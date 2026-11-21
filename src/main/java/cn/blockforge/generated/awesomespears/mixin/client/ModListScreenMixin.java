package cn.blockforge.generated.awesomespears.mixin.client;

import cn.blockforge.generated.awesomespears.client.ModListNames;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.forgespi.language.IModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** 模组列表界面：名称、描述用翻译；搜索过滤同时匹配翻译名与原名。 */
@Mixin(value = ModListScreen.class, remap = false)
public abstract class ModListScreenMixin {
    @Redirect(method = {"init", "updateCache"},
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/forgespi/language/IModInfo;getDisplayName()Ljava/lang/String;"))
    private String awesomeSpears$translatedName(IModInfo info) {
        return ModListNames.displayName(info, info.getDisplayName());
    }

    /** Forge 1.20.1 不翻译模组描述，这里补上。 */
    @Redirect(method = "updateCache",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/forgespi/language/IModInfo;getDescription()Ljava/lang/String;"))
    private String awesomeSpears$translatedDescription(IModInfo info) {
        return ModListNames.description(info, info.getDescription());
    }

    /** reloadMods 里的搜索过滤 lambda。require = 0：其它小版本（如 NeoForge 1.20.1）的 lambda 编号可能不同，挂不上时只会少掉"双名搜索"。 */
    @Redirect(method = "lambda$reloadMods$10", require = 0,
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/forgespi/language/IModInfo;getDisplayName()Ljava/lang/String;"))
    private String awesomeSpears$searchBothNames(IModInfo info) {
        return ModListNames.searchName(info, info.getDisplayName());
    }
}
