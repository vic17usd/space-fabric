package dev.vic17usd.spacefabric.entity.venus;

public final class SulfurwingCopperstingerRigProfile {
   public static final float WIDTH = 3.2F;
   public static final float HEIGHT = 4.5F;
   public static final float EYE_HEIGHT = 2.8F;
   public static final String MEDIUM = "airborne";
   private static final String[] KEYS = new String[]{"primary_attack"};
   private static final long[] LENGTHS = new long[]{1600L};
   private static final long[] STARTS = new long[]{800L};
   private static final long[] ENDS = new long[]{800L};

   private SulfurwingCopperstingerRigProfile() {
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
