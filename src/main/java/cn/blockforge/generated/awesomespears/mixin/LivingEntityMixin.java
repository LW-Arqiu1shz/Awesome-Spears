package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearConfig;
import cn.blockforge.generated.awesomespears.SpearPlayerState;
import cn.blockforge.generated.awesomespears.SpearTuning;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.item.component.KineticWeapon;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow
    protected int useItemRemaining;

    /** 贴脸命中（最小距离改为 0）、判定加宽 */
    @Inject(method = "getAttackRangeWith", at = @At("RETURN"), cancellable = true)
    private void awesome_spears$widenSpearHitbox(ItemStack stack, CallbackInfoReturnable<AttackRange> cir) {
        if (!SpearTuning.isSpear(stack)) {
            return;
        }
        AttackRange vanilla = cir.getReturnValue();
        boolean pointBlank = SpearConfig.isEnabled(SpearConfig.Option.HIT_POINT_BLANK);
        boolean wider = SpearConfig.isEnabled(SpearConfig.Option.HIT_WIDER_BOX);
        if (vanilla != null && (pointBlank || wider)) {
            cir.setReturnValue(new AttackRange(
                    pointBlank ? 0.0F : vanilla.minReach(), vanilla.maxReach(),
                    pointBlank ? 0.0F : vanilla.minCreativeReach(), vanilla.maxCreativeReach(),
                    wider ? SpearTuning.HITBOX_MARGIN : vanilla.hitboxMargin(), vanilla.mobFactor()));
        }
    }

    /** 刺击后马上右键蓄力：补上突进，并跳过蓄力起手（服务端结算，位移会同步给客户端） */
    @Inject(method = "startUsingItem", at = @At("RETURN"))
    private void awesome_spears$lungeOnChargeStart(InteractionHand hand, CallbackInfo ci) {
        if (!((Object) this instanceof ServerPlayer player) || player.getUsedItemHand() != InteractionHand.MAIN_HAND
                || !SpearTuning.afterStabEnabled()) {
            return;
        }
        SpearPlayerState state = (SpearPlayerState) player;
        if (!SpearTuning.isSideTriggerWindow(player, state.awesome_spears$pendingLungeTick())) {
            return;
        }
        ItemStack stack = player.getUseItem();
        KineticWeapon kinetic = stack.get(DataComponents.KINETIC_WEAPON);
        if (kinetic != null) {
            int duration = stack.getUseDuration(player);
            this.useItemRemaining = Math.min(this.useItemRemaining, duration - kinetic.delayTicks());
        }
        EnchantmentHelper.doPostPiercingAttackEffects(player.level(), player);
        state.awesome_spears$setPendingLungeTick(SpearTuning.NO_PENDING_TICK);
    }
}
