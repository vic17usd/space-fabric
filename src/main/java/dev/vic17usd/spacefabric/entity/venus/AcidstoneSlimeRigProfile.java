package dev.vic17usd.spacefabric.entity.venus;

public final class AcidstoneSlimeRigProfile {
   public static final float WIDTH = 1.4F;
   public static final float HEIGHT = 1.72F;
   public static final float EYE_HEIGHT = 1.12F;
   public static final String MEDIUM = "submerged";
   private static final String[] KEYS = new String[]{"avoidance_bounce", "land_bounce"};
   private static final long[] LENGTHS = new long[]{1800L, 1600L};
   private static final long[] STARTS = new long[]{-1L, -1L};
   private static final long[] ENDS = new long[]{-1L, -1L};

   private AcidstoneSlimeRigProfile() {
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
