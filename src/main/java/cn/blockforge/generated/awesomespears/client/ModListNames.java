package cn.blockforge.generated.awesomespears.client;

import cn.blockforge.generated.awesomespears.AwesomeSpears;
import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.forgespi.language.IModInfo;

/**
 * 这一版的模组列表只显示 mods.toml 里写死的 displayName，没有翻译入口。
 * mixin/client/ModList*Mixin 在读取名称处调用这里：只对本模组、且当前语言有
 * {@value #NAME_KEY} 翻译时替换为翻译名，其他模组原样返回。
 * 描述直接用 fml.menu.mods.info.description.&lt;modid&gt; 翻译键（Forge 1.20.1 本身不读，由 ModListScreenMixin 补上）。
 *
 * <p>此类必须位于 Mixin 配置包之外，供 Mixin 安全引用。</p>
 */
public final class ModListNames {
    public static final String NAME_KEY = "fml.menu.mods.info.displayname." + AwesomeSpears.MOD_ID;
    public static final String DESCRIPTION_KEY = "fml.menu.mods.info.description." + AwesomeSpears.MOD_ID;

    private ModListNames() {
    }

    /** 显示用：翻译名（没有译文时为原名）。 */
    public static String displayName(IModInfo info, String original) {
        if (AwesomeSpears.MOD_ID.equals(info.getModId()) && I18n.exists(NAME_KEY)) {
            return I18n.get(NAME_KEY);
        }
        return original;
    }

    /** 描述：翻译后的描述（没有译文时为原文）。 */
    public static String description(IModInfo info, String original) {
        if (AwesomeSpears.MOD_ID.equals(info.getModId()) && I18n.exists(DESCRIPTION_KEY)) {
            return I18n.get(DESCRIPTION_KEY);
        }
        return original;
    }

    /** 搜索用：翻译名 + 原名，中文界面下搜 "Awesome Spears" 或 "这长矛有力气" 都能找到；不会显示在界面上。 */
    public static String searchName(IModInfo info, String original) {
        String shown = displayName(info, original);
        return shown.equals(original) ? original : shown + " " + original;
    }
}
