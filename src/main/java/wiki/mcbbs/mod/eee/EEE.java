package wiki.mcbbs.mod.eee;

import xland.mcmod.enchlevellangpatch.api.EnchantmentLevelLangPatch;
import xland.mcmod.enchlevellangpatch.api.EnchantmentLevelLangPatchConfig;

import java.util.LinkedHashMap;
import java.util.Map;

public class EEE {
    private static final String WHITELIST = "()（）,.!，。！:：;；?？'‘’\"“”[]【】{}「」『』";
    private static final int MAX_SIZE = 2 << 16;    // 128K
    private static final LinkedHashMap<String, String> LRU_CACHE = new LinkedHashMap<String, String>(32767, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
            return size() > MAX_SIZE;
        }
    };

    public static void init() {
        EnchantmentLevelLangPatch patch = (translationStorage, key) -> {
            final String s = translationStorage.getOrDefault(key, key);
            String k = LRU_CACHE.get(s);
            if (k != null) return k;

            final char[] chars = s.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                final char c = chars[i];
                if (!Character.isWhitespace(c) && WHITELIST.indexOf(c) < 0) {
                    chars[i] = 'e';
                }
            }
            k = String.valueOf(chars).intern();
            LRU_CACHE.put(s, k);
            return k;
        };
        EnchantmentLevelLangPatch.registerPatch(s->true, patch);
        EnchantmentLevelLangPatch.registerEnchantmentPatch("eeemod:eee", patch);
        EnchantmentLevelLangPatch.registerPotionPatch("eeemod:eee", patch);
        EnchantmentLevelLangPatchConfig.setCurrentEnchantmentHooks(patch);
        EnchantmentLevelLangPatchConfig.setCurrentPotionHooks(patch);
    }
}
