package dev.vic17usd.spacefabric.entity.sanctum;

public final class BasaltBosswardenRigProfile {
   public static final float WIDTH = 1.194919F;
   public static final float HEIGHT = 3.3F;
   public static final float EYE_HEIGHT = 2.8F;
   public static final String MEDIUM = "grounded";
   private static final String[] KEYS = new String[]{"primary_attack", "boss_rage", "shield_block"};
   private static final long[] LENGTHS = new long[]{1800L, 1000L, 1200L};
   private static final long[] STARTS = new long[]{820L, -1L, -1L};
   private static final long[] ENDS = new long[]{860L, -1L, -1L};

   private BasaltBosswardenRigProfile() {
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
