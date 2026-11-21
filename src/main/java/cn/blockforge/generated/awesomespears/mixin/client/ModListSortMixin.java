package cn.blockforge.generated.awesomespears.mixin.client;

import cn.blockforge.generated.awesomespears.client.ModListNames;
import net.minecraftforge.forgespi.language.IModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** 模组列表按名称排序时也用翻译名，排序位置与显示的名字一致。 */
@Mixin(targets = "net.minecraftforge.client.gui.ModListScreen$SortType", remap = false)
public abstract class ModListSortMixin {
    @Redirect(method = "compare(Lnet/minecraftforge/forgespi/language/IModInfo;Lnet/minecraftforge/forgespi/language/IModInfo;)I",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/forgespi/language/IModInfo;getDisplayName()Ljava/lang/String;"))
    private String awesomeSpears$translatedName(IModInfo info) {
        return ModListNames.displayName(info, info.getDisplayName());
    }
}
