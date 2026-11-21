package cn.blockforge.generated.awesomespears.mixin.client;

import cn.blockforge.generated.awesomespears.client.ModListNames;
import net.minecraftforge.forgespi.language.IModInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** 模组列表左侧每一行的名称（以及旁白朗读）改用本模组的翻译名。 */
@Mixin(targets = "net.minecraftforge.client.gui.widget.ModListWidget$ModEntry", remap = false)
public abstract class ModListEntryMixin {
    @Redirect(method = "*",
            at = @At(value = "INVOKE", target = "Lnet/minecraftforge/forgespi/language/IModInfo;getDisplayName()Ljava/lang/String;"))
    private String awesomeSpears$translatedName(IModInfo info) {
        return ModListNames.displayName(info, info.getDisplayName());
    }
}
