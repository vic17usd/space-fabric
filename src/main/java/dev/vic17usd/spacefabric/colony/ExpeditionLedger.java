package dev.vic17usd.spacefabric.colony;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.vic17usd.spacefabric.SpaceFabric;
import java.util.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.*;

/** Each natural expedition has one reward per player and mission type across the world. */
public final class ExpeditionLedger extends SavedData {
    public record Claim(String player,int mission) {
        public static final Codec<Claim> CODEC=RecordCodecBuilder.create(i->i.group(Codec.STRING.fieldOf("player").forGetter(Claim::player),Codec.intRange(1,3).fieldOf("mission").forGetter(Claim::mission)).apply(i,Claim::new));
    }
    public static final Codec<ExpeditionLedger> CODEC=Claim.CODEC.listOf().fieldOf("claims").xmap(ExpeditionLedger::new,l->List.copyOf(l.claims)).codec();
    public static final SavedDataType<ExpeditionLedger> TYPE=new SavedDataType<>(SpaceFabric.id("venus_expeditions"),ExpeditionLedger::new,CODEC,DataFixTypes.LEVEL);
    private final Set<Claim> claims=new LinkedHashSet<>();
    public ExpeditionLedger() { }
    private ExpeditionLedger(List<Claim> claims){this.claims.addAll(claims);}
    public static ExpeditionLedger get(ServerLevel level){return level.getServer().getDataStorage().computeIfAbsent(TYPE);}
    public boolean claimed(UUID player,int mission){return claims.contains(new Claim(player.toString(),mission));}
    public boolean claim(UUID player,int mission){if(mission<1||mission>3)return false;boolean added=claims.add(new Claim(player.toString(),mission));if(added)setDirty();return added;}
}
