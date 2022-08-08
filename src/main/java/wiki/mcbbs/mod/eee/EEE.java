package wiki.mcbbs.mod.eee;

import xland.mcmod.enchlevellangpatch.api.EnchantmentLevelLangPatch;
import xland.mcmod.enchlevellangpatch.api.EnchantmentLevelLangPatchConfig;

public class EEE {
    private static final String WHITELIST = "()（）,.，。:：;；?？'‘’\"“”[]【】{}「」『』";

    public static void init() {
        EnchantmentLevelLangPatch patch = (translationStorage, key) -> {
            final String s = translationStorage.getOrDefault(key, key);
            final char[] chars = s.toCharArray();
            for (int i = 0; i < chars.length; i++) {
                final char c = chars[i];
                if (!Character.isWhitespace(c) && WHITELIST.indexOf(c) < 0) {
                    chars[i] = 'e';
                }
            }
            return String.valueOf(chars);
        };
        EnchantmentLevelLangPatch.registerPatch(s->true, patch);
        EnchantmentLevelLangPatch.registerEnchantmentPatch("eeemod:eee", patch);
        EnchantmentLevelLangPatch.registerPotionPatch("eeemod:eee", patch);
        EnchantmentLevelLangPatchConfig.setCurrentEnchantmentHooks(patch);
        EnchantmentLevelLangPatchConfig.setCurrentEnchantmentHooks(patch);
    }
}
