package dev.vic17usd.spacefabric.planet;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistrationInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** Usa los codecs reales de 26.1 sin abrir un servidor ni aceptar su EULA. */
class WorldgenCodecTest {
    private static HolderLookup.Provider vanilla;
    private static HolderLookup.Provider withPlanetTypes;

    @BeforeAll
    static void initializeRegistries() throws IOException {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        vanilla = VanillaRegistries.createLookup();
        var types = new MappedRegistry<DimensionType>(Registries.DIMENSION_TYPE, Lifecycle.stable());
        vanilla.lookupOrThrow(Registries.DIMENSION_TYPE).listElements().forEach(holder ->
                types.register(holder.key(), holder.value(), RegistrationInfo.BUILT_IN));
        for (Planet planet : Planet.values()) {
            DimensionType type = decodeType(planet);
            types.register(ResourceKey.create(Registries.DIMENSION_TYPE,
                    Identifier.fromNamespaceAndPath("space_fabric", planet.path())),
                    type, RegistrationInfo.BUILT_IN);
        }
        types.freeze();
        withPlanetTypes = HolderLookup.Provider.create(Stream.concat(
                vanilla.listRegistries().filter(registry -> !registry.key().equals(Registries.DIMENSION_TYPE)),
                Stream.of(types)));
    }

    @ParameterizedTest
    @EnumSource(Planet.class)
    void dimensionTypeIsAcceptedByMinecraft(Planet planet) throws IOException {
        DimensionType type = decodeType(planet);
        assertEquals(384, type.height());
        assertEquals(-64, type.minY());
    }

    @ParameterizedTest
    @EnumSource(Planet.class)
    void dimensionAndGeneratorResolveAgainstRealRegistries(Planet planet) throws IOException {
        LevelStem stem = LevelStem.CODEC.parse(withPlanetTypes.createSerializationContext(JsonOps.INSTANCE),
                resource("dimension/" + planet.path())).getOrThrow();
        assertNotNull(stem.generator());
        assertEquals("space_fabric:" + planet.path(), stem.type().unwrapKey().orElseThrow().identifier().toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"decompression", "crushing_pressure", "hypoxia"})
    void damageTypeIsAcceptedByMinecraft(String path) throws IOException {
        DamageType type = DamageType.DIRECT_CODEC.parse(JsonOps.INSTANCE,
                resource("damage_type/" + path)).getOrThrow();
        assertEquals("space_fabric." + path, type.msgId());
    }

    private static DimensionType decodeType(Planet planet) throws IOException {
        return DimensionType.DIRECT_CODEC.parse(vanilla.createSerializationContext(JsonOps.INSTANCE),
                resource("dimension_type/" + planet.path())).getOrThrow();
    }

    private static JsonElement resource(String path) throws IOException {
        String location = "/data/space_fabric/" + path + ".json";
        var stream = WorldgenCodecTest.class.getResourceAsStream(location);
        assertNotNull(stream, location);
        try (var reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader);
        }
    }
}
