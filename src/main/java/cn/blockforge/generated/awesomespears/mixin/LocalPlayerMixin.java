package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearSideTrigger;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 客户端：刺击后马上右键蓄力，补上突进的位移（要等 LocalPlayer 标记完「正在使用物品」） */
@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "startUsingItem", at = @At("RETURN"))
    private void awesome_spears$lungeOnChargeStart(InteractionHand hand, CallbackInfo ci) {
        SpearSideTrigger.onChargeStart((LocalPlayer) (Object) this);
    }
}
