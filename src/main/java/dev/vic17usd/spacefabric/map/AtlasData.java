package dev.vic17usd.spacefabric.map;

import com.google.gson.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import java.util.zip.*;

/** Personal, dimension-scoped surface atlas. No world access, networking or chunk requests. */
public final class AtlasData {
    public static final int CELL=4, TILE=32, MAX_WAYPOINTS=128, MAX_CACHED_TILES=3072;
    private static final int MAGIC=0x53464131, LIMIT=29_999_984;
    private static final Gson JSON=new GsonBuilder().setPrettyPrinting().create();
    public record Waypoint(UUID id,String name,int x,int y,int z,int color,boolean visible) {
        public Waypoint {
            Objects.requireNonNull(id);Objects.requireNonNull(name);
            name=name.codePoints().filter(c->!Character.isISOControl(c)&&c!=0xA7).limit(48)
                    .collect(StringBuilder::new,StringBuilder::appendCodePoint,StringBuilder::append).toString().strip();
            if(name.isEmpty()||Math.abs((long)x)>LIMIT||Math.abs((long)z)>LIMIT||y < -2048||y>2047||color<0||color>0xFFFFFF)
                throw new IllegalArgumentException("Invalid map waypoint");
        }
    }
    private static final class TileData {
        final int[] colors=new int[TILE*TILE];final short[] heights=new short[TILE*TILE];boolean dirty;
    }
    private final Path directory,regions;
    private final String dimension;
    private final LinkedHashMap<Long,TileData> cache=new LinkedHashMap<>(32,.75F,true);
    private final Set<Long> known=new HashSet<>(),bad=new HashSet<>();
    private final List<Waypoint> waypoints=new ArrayList<>();
    private UUID selected;
    private int readBudget=24;
    private long revision;
    private IOException error;
    private int lastX,lastY,lastZ;
    private boolean positionDirty;
    private boolean deferredReads;

    public AtlasData(Path worldDirectory,String dimension) throws IOException {
        if(!validDimension(dimension))throw new IllegalArgumentException("Invalid dimension");
        this.dimension=dimension;directory=worldDirectory.resolve(key(dimension));regions=directory.resolve("regions");
        Files.createDirectories(regions);
        Path metadata=directory.resolve("markers.json");
        if(Files.exists(metadata))loadMarkers(metadata);else saveMarkers();
        try(var files=Files.newDirectoryStream(regions,"*.sfa")) {
            for(var file:files) {
                String[] pair=file.getFileName().toString().replace(".sfa","").split("_");
                try {if(pair.length==2)known.add(tileKey(Integer.parseInt(pair[0]),Integer.parseInt(pair[1])));}catch(NumberFormatException ignored) { }
            }
        }
    }
    public static boolean validDimension(String value){return value!=null&&value.length()<=256&&value.matches("[a-z0-9_.-]+:[a-z0-9_./-]+");}
    public static String key(String value) {
        try{return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));}
        catch(NoSuchAlgorithmException impossible){throw new IllegalStateException(impossible);}
    }
    public static String sessionKey(String worldIdentity,UUID player){return key(worldIdentity+"\n"+player);}
    public String dimension(){return dimension;}
    public long revision(){return revision;}
    public IOException error(){return error;}
    public int lastX(){return lastX;}public int lastY(){return lastY;}public int lastZ(){return lastZ;}
    public void position(int x,int y,int z){if(x!=lastX||y!=lastY||z!=lastZ){lastX=x;lastY=y;lastZ=z;positionDirty=true;}}
    public void beginTick(){readBudget=24;}
    public void beginView(){deferredReads=false;}
    public boolean deferredReads(){return deferredReads;}
    public List<Waypoint> waypoints(){return List.copyOf(waypoints);}
    public Optional<Waypoint> selected(){return waypoints.stream().filter(p->p.id().equals(selected)).findFirst();}
    public void select(UUID id) throws IOException {
        if(id!=null&&waypoints.stream().noneMatch(p->p.id().equals(id)))throw new IllegalArgumentException("Unknown waypoint");
        selected=id;saveMarkers();revision++;
    }
    public void put(Waypoint point) throws IOException {
        int index=-1;for(int i=0;i<waypoints.size();i++)if(waypoints.get(i).id().equals(point.id()))index=i;
        if(index<0){if(waypoints.size()>=MAX_WAYPOINTS)throw new IOException("Waypoint limit reached");waypoints.add(point);}
        else waypoints.set(index,point);
        selected=point.id();saveMarkers();revision++;
    }
    public void delete(UUID id) throws IOException {
        waypoints.removeIf(p->p.id().equals(id));if(Objects.equals(selected,id))selected=null;saveMarkers();revision++;
    }
    private static long tileKey(int x,int z){return ((long)x<<32)|(z&0xFFFFFFFFL);}
    private Path tilePath(long key){return regions.resolve(((int)(key>>32))+"_"+((int)key)+".sfa");}
    private TileData tile(int cellX,int cellZ,boolean writing) throws IOException {
        long key=tileKey(Math.floorDiv(cellX,TILE),Math.floorDiv(cellZ,TILE));
        var tile=cache.get(key);if(tile!=null)return tile;
        if(bad.contains(key))return null;
        if(!writing&&!known.contains(key))return null;
        if(known.contains(key)&&!writing&&readBudget<=0){deferredReads=true;return null;}
        tile=new TileData();
        if(known.contains(key)) {
            readBudget--;
            try(var input=new DataInputStream(new GZIPInputStream(Files.newInputStream(tilePath(key))))) {
                if(input.readInt()!=MAGIC)throw new IOException("Invalid atlas tile");
                for(int i=0;i<tile.colors.length;i++){tile.colors[i]=input.readInt();tile.heights[i]=input.readShort();}
                if(input.read()!=-1)throw new IOException("Oversized atlas tile");
            }catch(IOException failure){bad.add(key);error=failure;return null;}
        }
        if(cache.size()>=MAX_CACHED_TILES){var oldest=cache.entrySet().iterator().next();saveTile(oldest.getKey(),oldest.getValue());cache.remove(oldest.getKey());}
        cache.put(key,tile);return tile;
    }
    private static int index(int x,int z){return Math.floorMod(z,TILE)*TILE+Math.floorMod(x,TILE);}
    public int color(int worldX,int worldZ) {
        int x=Math.floorDiv(worldX,CELL),z=Math.floorDiv(worldZ,CELL);
        try {var tile=tile(x,z,false);return tile==null?0:tile.colors[index(x,z)];}
        catch(IOException failure){error=failure;return 0;}
    }
    public int height(int worldX,int worldZ,int fallback) {
        int x=Math.floorDiv(worldX,CELL),z=Math.floorDiv(worldZ,CELL);
        try {var tile=tile(x,z,false);int i=index(x,z);return tile==null||tile.colors[i]==0?fallback:tile.heights[i];}
        catch(IOException failure){error=failure;return fallback;}
    }
    public void record(int worldX,int worldZ,int height,int argb) throws IOException {
        if(height < -2048||height>2047)throw new IllegalArgumentException("Invalid surface altitude");
        int x=Math.floorDiv(worldX,CELL),z=Math.floorDiv(worldZ,CELL);var tile=tile(x,z,true);if(tile==null)return;
        int i=index(x,z),color=argb|0xFF000000;
        if(tile.colors[i]!=color||tile.heights[i]!=height){tile.colors[i]=color;tile.heights[i]=(short)height;tile.dirty=true;revision++;}
    }
    private void saveTile(long key,TileData tile) throws IOException {
        if(!tile.dirty)return;
        var path=tilePath(key);var temporary=path.resolveSibling(path.getFileName()+".tmp");
        try(var output=new DataOutputStream(new GZIPOutputStream(Files.newOutputStream(temporary)))) {
            output.writeInt(MAGIC);for(int i=0;i<tile.colors.length;i++){output.writeInt(tile.colors[i]);output.writeShort(tile.heights[i]);}
        }
        replace(temporary,path);known.add(key);tile.dirty=false;
    }
    public void flush(int maximum) throws IOException {
        if(maximum<=0)return;
        int count=0;for(var entry:cache.entrySet())if(entry.getValue().dirty){saveTile(entry.getKey(),entry.getValue());if(++count>=maximum)break;}
        if(positionDirty)saveMarkers();
    }
    public static void writeJson(Path path,JsonElement data) throws IOException {
        Files.createDirectories(path.getParent());var temporary=path.resolveSibling(path.getFileName()+".tmp");
        Files.writeString(temporary,JSON.toJson(data),StandardCharsets.UTF_8);replace(temporary,path);
    }
    private static void replace(Path source,Path destination) throws IOException {
        try{Files.move(source,destination,StandardCopyOption.REPLACE_EXISTING,StandardCopyOption.ATOMIC_MOVE);}
        catch(AtomicMoveNotSupportedException ignored){Files.move(source,destination,StandardCopyOption.REPLACE_EXISTING);}
    }
    private void saveMarkers() throws IOException {
        var root=new JsonObject();root.addProperty("version",1);root.addProperty("dimension",dimension);
        var list=new JsonArray();
        for(var p:waypoints){var j=new JsonObject();j.addProperty("id",p.id().toString());j.addProperty("name",p.name());j.addProperty("x",p.x());j.addProperty("y",p.y());j.addProperty("z",p.z());j.addProperty("color",p.color());j.addProperty("visible",p.visible());list.add(j);}
        root.add("waypoints",list);if(selected!=null)root.addProperty("selected",selected.toString());
        root.addProperty("last_x",lastX);root.addProperty("last_y",lastY);root.addProperty("last_z",lastZ);
        writeJson(directory.resolve("markers.json"),root);
        positionDirty=false;
    }
    private void loadMarkers(Path path) throws IOException {
        try {
            if(Files.size(path)>262144)throw new IOException("Oversized waypoint file");
            var root=JsonParser.parseString(Files.readString(path)).getAsJsonObject();
            if(root.get("version").getAsInt()!=1||!dimension.equals(root.get("dimension").getAsString()))throw new IOException("Wrong atlas dimension or version");
            var list=root.getAsJsonArray("waypoints");if(list.size()>MAX_WAYPOINTS)throw new IOException("Oversized waypoint list");
            var ids=new HashSet<UUID>();
            for(var entry:list){var j=entry.getAsJsonObject();var point=new Waypoint(UUID.fromString(j.get("id").getAsString()),j.get("name").getAsString(),j.get("x").getAsInt(),j.get("y").getAsInt(),j.get("z").getAsInt(),j.get("color").getAsInt(),j.get("visible").getAsBoolean());if(!ids.add(point.id()))throw new IOException("Duplicate waypoint");waypoints.add(point);}
            if(root.has("selected")){var id=UUID.fromString(root.get("selected").getAsString());if(ids.contains(id))selected=id;}
            lastX=root.has("last_x")?root.get("last_x").getAsInt():0;lastY=root.has("last_y")?root.get("last_y").getAsInt():0;lastZ=root.has("last_z")?root.get("last_z").getAsInt():0;
        }catch(RuntimeException malformed){throw new IOException("Cannot read atlas waypoints",malformed);}
    }
    public static List<String> visited(Path root) throws IOException {
        if(!Files.isDirectory(root))return List.of();var result=new TreeSet<String>();
        try(var paths=Files.newDirectoryStream(root)) {
            for(var directory:paths){var path=directory.resolve("markers.json");if(!Files.isRegularFile(path)||Files.size(path)>262144)continue;
                try{var dim=JsonParser.parseString(Files.readString(path)).getAsJsonObject().get("dimension").getAsString();if(validDimension(dim))result.add(dim);}catch(RuntimeException ignored) { }
            }
        }
        return List.copyOf(result);
    }
}
