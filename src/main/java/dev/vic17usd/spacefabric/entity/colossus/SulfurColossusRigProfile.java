package dev.vic17usd.spacefabric.entity.colossus;

public final class SulfurColossusRigProfile {
   public static final float WIDTH = 4.25F;
   public static final float HEIGHT = 3.55F;
   public static final float EYE_HEIGHT = 3.02F;
   public static final String MEDIUM = "grounded";
   private static final String[] KEYS = new String[]{"primary_attack", "death", "ground_slam", "stagger", "venting", "volcanic_burst"};
   private static final long[] LENGTHS = new long[]{2400L, 4000L, 4000L, 1200L, 5000L, 5000L};
   private static final long[] STARTS = new long[]{1000L, -1L, 2000L, -1L, 3000L, 3000L};
   private static final long[] ENDS = new long[]{1000L, -1L, 2000L, -1L, 3000L, 3000L};

   private SulfurColossusRigProfile() {
   }

   public static int actionIndex(String key) {
      for (int i = 0; i < KEYS.length; i++) {
         if (KEYS[i].equals(key)) {
            return i;
         }
      }

      return -1;
   }

   public static long actionDurationMillis(String key) {
      int index = actionIndex(key);
      return index < 0 ? -1L : LENGTHS[index];
   }

   public static long effectStartMillis(String key) {
      int index = actionIndex(key);
      return index < 0 ? -1L : STARTS[index];
   }

   public static long effectEndMillis(String key) {
      int index = actionIndex(key);
      return index < 0 ? -1L : ENDS[index];
   }
}
