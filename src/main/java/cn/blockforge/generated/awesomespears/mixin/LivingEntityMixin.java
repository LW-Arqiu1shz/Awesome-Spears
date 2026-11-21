package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearSideTrigger;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    /**
     * 服务端：刺击后马上右键蓄力。
     * 客户端玩家在这里还没被标记为「正在使用物品」，改由 LocalPlayerMixin 在它自己的 startUsingItem 末尾处理。
     */
    @Inject(method = "startUsingItem", at = @At("RETURN"))
    private void awesome_spears$lungeOnChargeStart(InteractionHand hand, CallbackInfo ci) {
        if ((Object) this instanceof Player player && !player.level().isClientSide) {
            SpearSideTrigger.onChargeStart(player);
        }
    }
}
