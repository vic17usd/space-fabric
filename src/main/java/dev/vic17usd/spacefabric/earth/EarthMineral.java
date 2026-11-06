package dev.vic17usd.spacefabric.earth;

/** Generated from tools/earth_minerals.json by generate_earth_resources.py. */
public enum EarthMineral {
    BAUXITE("bauxite","aluminum","stone",12,8,32,128),
    CASSITERITE("cassiterite","tin","stone",7,6,0,96),
    SPHALERITE("sphalerite","zinc","stone",8,7,-32,64),
    GALENA("galena","lead","iron",7,5,-48,32),
    ARGENTITE("argentite","silver","iron",5,3,-56,16),
    PENTLANDITE("pentlandite","nickel","iron",6,4,-48,24),
    ILMENITE("ilmenite","titanium","diamond",4,3,-60,0),
    FLUORITE("fluorite",null,"stone",6,5,-32,48);
    public final String path, metal, tool;
    public final int veinSize, attempts, minY, maxY;
    EarthMineral(String path,String metal,String tool,int veinSize,int attempts,int minY,int maxY) {
        this.path=path;this.metal=metal;this.tool=tool;this.veinSize=veinSize;
        this.attempts=attempts;this.minY=minY;this.maxY=maxY;
    }
    public boolean isMetal() { return metal!=null; }
    public String oreId(boolean deep) { return (deep ? "deepslate_" : "")+path+"_ore"; }
    public String rawId() { return "raw_"+path; }
    public String productId() { return isMetal() ? metal+"_ingot" : path+"_crystal"; }
    public String storageId() { return (isMetal() ? metal : path)+"_block"; }
}
