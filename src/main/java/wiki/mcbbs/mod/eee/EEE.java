package wiki.mcbbs.mod.eee;

import xland.mcmod.enchlevellangpatch.api.EnchantmentLevelLangPatch;

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
        EnchantmentLevelLangPatch.registerPatch(s -> true, (translationStorage, key) -> {
            final String s1 = translationStorage.getOrDefault(key, key);
            String k = LRU_CACHE.get(s1);
            if (k != null) return k;

            final char[] chars = s1.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                final char c = chars[i];
                if (!Character.isWhitespace(c) && WHITELIST.indexOf(c) < 0) {
                    chars[i] = 'e';
                }
            }
            k = String.valueOf(chars).intern();
            LRU_CACHE.put(s1, k);
            return k;
        });
        // Config override is no longer required, because LangPatch applies configs in the very end.
    }
}
