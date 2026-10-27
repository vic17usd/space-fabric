package dev.vic17usd.spacefabric.entity.fauna;

public final class RedDuneIronmawRigProfile {
   public static final float WIDTH = 3.7F;
   public static final float HEIGHT = 8.7F;
   public static final float EYE_HEIGHT = 7.7F;
   public static final String MEDIUM = "grounded";
   private static final String[] KEYS = new String[]{"primary_attack", "burrow"};
   private static final long[] LENGTHS = new long[]{2400L, 1800L};
   private static final long[] STARTS = new long[]{1100L, -1L};
   private static final long[] ENDS = new long[]{1100L, -1L};

   private RedDuneIronmawRigProfile() {
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
