package dev.vic17usd.spacefabric.entity.venus;

public final class CopperAcidstingerDroneRigProfile {
   public static final float WIDTH = 0.567187F;
   public static final float HEIGHT = 1.05F;
   public static final float EYE_HEIGHT = 0.72F;
   public static final String MEDIUM = "airborne";
   private static final String[] KEYS = new String[]{"primary_attack", "flee"};
   private static final long[] LENGTHS = new long[]{600L, 800L};
   private static final long[] STARTS = new long[]{200L, -1L};
   private static final long[] ENDS = new long[]{200L, -1L};

   private CopperAcidstingerDroneRigProfile() {
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
