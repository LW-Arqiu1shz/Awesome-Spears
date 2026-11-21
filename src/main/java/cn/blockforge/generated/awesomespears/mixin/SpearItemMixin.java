package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearConfig;
import cn.blockforge.generated.awesomespears.SpearHitScan;
import cn.blockforge.generated.awesomespears.SpearPlayerState;
import cn.blockforge.generated.awesomespears.SpearTuning;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.spearcore.item.SpearItem;
import net.minecraft.spearcore.util.SpearRules;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Spear Core 的长矛逻辑全在 SpearItem 里，26.x 版对原版长矛做的改动在这里一一对应。
 */
@Mixin(SpearItem.class)
public abstract class SpearItemMixin {
    @Unique
    private static Vec3 awesome_spears$motionBeforeChargeHit;
    @Unique
    private static boolean awesome_spears$sprintingBeforeChargeHit;

    // ========== 突进：只在蓄力中左键刺击时触发（或刺击后马上右键蓄力） ==========

    @Inject(remap = false, method = "jerotesLungeForwardMaybe", at = @At("HEAD"), cancellable = true)
    private static void awesome_spears$gateLunge(LivingEntity user, CallbackInfo ci) {
        if (!(user instanceof Player player) || !SpearTuning.braceLungeEnabled()) {
            return;
        }
        if (SpearTuning.lungeLevel(player, player.getMainHandItem()) <= 0) {
            return;
        }
        SpearPlayerState state = (SpearPlayerState) player;
        if (SpearTuning.lungeAllowed(player, state.awesome_spears$pendingLungeTick())) {
            return;
        }
        // 普通刺击不突进；记下时间，紧接着右键蓄力时再补上
        if (!player.isUsingItem()) {
            state.awesome_spears$setPendingLungeTick(player.tickCount);
        }
        ci.cancel();
    }

    /** 不吃饭：饥饿值不再挡突进 */
    @Redirect(remap = false, method = "jerotesLungeForwardMaybe",
            at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraft/spearcore/util/SpearRules;lungeBlockedByHunger(I)Z"))
    private static boolean awesome_spears$ignoreHunger(int foodLevel) {
        return !SpearConfig.isEnabled(SpearConfig.Option.LUNGE_NO_HUNGER) && SpearRules.lungeBlockedByHunger(foodLevel);
    }

    /** 鞘翅飞行中也能突进 */
    @Redirect(remap = false, method = "jerotesLungeForwardMaybe",
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/entity/LivingEntity;isFallFlying()Z"))
    private static boolean awesome_spears$allowWhileFlying(LivingEntity user) {
        return !SpearConfig.isEnabled(SpearConfig.Option.LUNGE_IN_AIR) && user.isFallFlying();
    }

    /** 水里也能突进 */
    @Redirect(remap = false, method = "jerotesLungeForwardMaybe",
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/entity/LivingEntity;isInWater()Z"))
    private static boolean awesome_spears$allowInWater(LivingEntity user) {
        return !SpearConfig.isEnabled(SpearConfig.Option.LUNGE_IN_WATER) && user.isInWater();
    }

    /** 突进不消耗饥饿 */
    @Redirect(remap = false, method = "jerotesLungeForwardMaybe",
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/entity/player/Player;causeFoodExhaustion(F)V"))
    private static void awesome_spears$noExhaustion(Player player, float amount) {
        if (!SpearConfig.isEnabled(SpearConfig.Option.LUNGE_NO_HUNGER)) {
            player.causeFoodExhaustion(amount);
        }
    }

    /** 朝准星方向突进（包括上下），力度沿用 Spear Core */
    @Redirect(remap = false, method = "jerotesLungeForwardMaybe",
            at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraft/spearcore/item/SpearItem;rushAttack(Lnet/minecraft/world/entity/LivingEntity;F)Z"))
    private static boolean awesome_spears$rushAlongLook(LivingEntity user, float strength) {
        if (!SpearConfig.isEnabled(SpearConfig.Option.LUNGE_FOLLOW_LOOK)) {
            return SpearItem.rushAttack(user, strength);
        }
        Vec3 look = user.getLookAngle();
        user.push(look.x * strength, look.y * strength, look.z * strength);
        return true;
    }

    /** 蓄力中左键：只突进，不刺击 */
    @Inject(remap = false, method = "performStabAttack", at = @At("HEAD"), cancellable = true)
    private void awesome_spears$braceStab(LivingEntity attacker, ItemStack stack, EquipmentSlot slot, boolean playSound,
                                          CallbackInfoReturnable<Boolean> cir) {
        if (attacker instanceof ServerPlayer player && slot == EquipmentSlot.MAINHAND && SpearTuning.braceLungeEnabled()
                && SpearTuning.lungeLevel(player, stack) > 0 && SpearTuning.canLunge(player)) {
            SpearItem.jerotesLungeForwardMaybe(player);
            cir.setReturnValue(false);
        }
    }

    // ========== 蓄力击退 ==========

    @ModifyArg(remap = false, method = {"onUseTick", "m_5929_"},
            at = @At(value = "INVOKE", remap = false, target = "Lnet/minecraft/spearcore/item/SpearItem;stabAttack(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/Entity;FZZZLnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/spearcore/event/SpearDamageEvent$AttackType;)Z"),
            index = 4)
    private boolean awesome_spears$restoreChargeKnockback(boolean vanilla) {
        return vanilla || SpearConfig.isEnabled(SpearConfig.Option.CHARGE_KNOCKBACK);
    }

    // ========== 判定加宽：贴脸也能打到，判定箱更宽，补判移动目标 ==========

    @Inject(remap = false, method = "effectiveMinRange", at = @At("RETURN"), cancellable = true)
    private void awesome_spears$noMinRange(Entity entity, CallbackInfoReturnable<Float> cir) {
        if (SpearConfig.isEnabled(SpearConfig.Option.HIT_POINT_BLANK)) {
            cir.setReturnValue(0.0F);
        }
    }

    @ModifyVariable(remap = false, method = "getHitEntitiesAlong", at = @At("HEAD"), argsOnly = true)
    private static float awesome_spears$widenMargin(float margin) {
        return SpearConfig.isEnabled(SpearConfig.Option.HIT_WIDER_BOX) ? Math.max(margin, SpearTuning.HITBOX_MARGIN) : margin;
    }

    @Inject(remap = false, method = "getHitEntitiesAlong", at = @At("RETURN"))
    private static void awesome_spears$augmentHits(LivingEntity user, SpearItem spear, float margin, Predicate<Entity> predicate,
                                                   CallbackInfoReturnable<List<EntityHitResult>> cir) {
        List<EntityHitResult> hits = cir.getReturnValue();
        if (hits != null && SpearConfig.isEnabled(SpearConfig.Option.HIT_MOVING_TARGETS)) {
            SpearHitScan.augment(user, spear, margin, predicate, hits);
        }
    }

    // ========== 贴脸半伤 ==========

    @Redirect(remap = false, method = {"stabAttack", "performStabAttack"},
            at = @At(value = "INVOKE", remap = true, target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"))
    private boolean awesome_spears$halfDamageAtPointBlank(Entity target, DamageSource source, float amount) {
        float dealt = amount;
        if (SpearConfig.isEnabled(SpearConfig.Option.HIT_POINT_BLANK_HALF_DAMAGE)
                && source.getEntity() instanceof Player player && SpearTuning.isSpear(player.getMainHandItem())
                && SpearTuning.isPointBlank(player, target)) {
            dealt *= 0.5F;
        }
        return target.hurt(source, dealt);
    }

    // ========== 蓄力撞到目标不减速、不停止疾跑 ==========

    @Inject(remap = false, method = "causeExtraKnockback", at = @At("HEAD"))
    private static void awesome_spears$snapshotChargeMomentum(LivingEntity attacker, Entity target, float strength, Vec3 preMotion,
                                                              CallbackInfo ci) {
        awesome_spears$motionBeforeChargeHit = null;
        if (attacker instanceof Player player && !player.level().isClientSide && SpearItem.isChargingSpear(player)
                && SpearConfig.isEnabled(SpearConfig.Option.CHARGE_KEEP_MOMENTUM)) {
            awesome_spears$motionBeforeChargeHit = player.getDeltaMovement();
            awesome_spears$sprintingBeforeChargeHit = player.isSprinting();
        }
    }

    @Inject(remap = false, method = "causeExtraKnockback", at = @At("RETURN"))
    private static void awesome_spears$keepChargeMomentum(LivingEntity attacker, Entity target, float strength, Vec3 preMotion,
                                                          CallbackInfo ci) {
        Vec3 before = awesome_spears$motionBeforeChargeHit;
        if (before != null) {
            awesome_spears$motionBeforeChargeHit = null;
            attacker.setDeltaMovement(before);
            if (awesome_spears$sprintingBeforeChargeHit) {
                attacker.setSprinting(true);
            }
        }
    }
}
