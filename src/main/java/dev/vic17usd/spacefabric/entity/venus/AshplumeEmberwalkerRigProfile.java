package dev.vic17usd.spacefabric.entity.venus;

public final class AshplumeEmberwalkerRigProfile {
   public static final float WIDTH = 0.9F;
   public static final float HEIGHT = 3.3F;
   public static final float EYE_HEIGHT = 3.05F;
   public static final String MEDIUM = "grounded";
   private static final String[] KEYS = new String[]{"flee_startle"};
   private static final long[] LENGTHS = new long[]{700L};
   private static final long[] STARTS = new long[]{-1L};
   private static final long[] ENDS = new long[]{-1L};

   private AshplumeEmberwalkerRigProfile() {
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
