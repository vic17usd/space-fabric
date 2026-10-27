package dev.vic17usd.spacefabric.entity.venus;

public final class ObsidianLavafangRigProfile {
   public static final float WIDTH = 1.35F;
   public static final float HEIGHT = 1.5F;
   public static final float EYE_HEIGHT = 1.25F;
   public static final String MEDIUM = "grounded";
   private static final String[] KEYS = new String[]{"primary_attack"};
   private static final long[] LENGTHS = new long[]{1100L};
   private static final long[] STARTS = new long[]{480L};
   private static final long[] ENDS = new long[]{620L};

   private ObsidianLavafangRigProfile() {
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
