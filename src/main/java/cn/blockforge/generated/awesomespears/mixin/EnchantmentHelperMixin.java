package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearConfig;
import cn.blockforge.generated.awesomespears.SpearPlayerState;
import cn.blockforge.generated.awesomespears.SpearTuning;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 突进（Lunge）附魔在这里结算。本模组的 lunge.json 去掉了原版的饥饿、鞘翅、水中限制，并改成朝准星方向突进；
 * 配置里关掉对应选项时，在这里把原版的限制补回来（只水平突进由 ApplyEntityImpulseMixin 处理）。
 */
@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    /** 原版突进要求饥饿值至少为 7（创造模式不限） */
    @Unique
    private static final int VANILLA_MIN_FOOD = 7;
    /** 原版突进每级消耗 4 点饥饿度 */
    @Unique
    private static final float VANILLA_EXHAUSTION_PER_LEVEL = 4.0F;
    @Unique
    private static boolean awesome_spears$lungePassed;

    @Inject(method = "doPostPiercingAttackEffects", at = @At("HEAD"), cancellable = true)
    private static void awesome_spears$gateLunge(ServerLevel level, LivingEntity entity, CallbackInfo ci) {
        awesome_spears$lungePassed = false;
        SpearTuning.flattenLungeImpulse = false;
        if (!(entity instanceof Player player) || !SpearTuning.holdsLungeSpear(player)) {
            return;
        }
        if (SpearTuning.braceLungeEnabled()) {
            int pending = ((SpearPlayerState) player).awesome_spears$pendingLungeTick();
            if (!SpearTuning.lungeAllowed(player, pending)) {
                ci.cancel();
                return;
            }
        }
        if (!SpearConfig.isEnabled(SpearConfig.Option.LUNGE_NO_HUNGER) && !player.isCreative()
                && player.getFoodData().getFoodLevel() < VANILLA_MIN_FOOD) {
            ci.cancel();
            return;
        }
        if ((!SpearConfig.isEnabled(SpearConfig.Option.LUNGE_IN_AIR) && player.isFallFlying())
                || (!SpearConfig.isEnabled(SpearConfig.Option.LUNGE_IN_WATER) && player.isInWater())) {
            ci.cancel();
            return;
        }
        awesome_spears$lungePassed = true;
        SpearTuning.flattenLungeImpulse = !SpearConfig.isEnabled(SpearConfig.Option.LUNGE_FOLLOW_LOOK);
    }

    @Inject(method = "doPostPiercingAttackEffects", at = @At("RETURN"))
    private static void awesome_spears$restoreVanillaLunge(ServerLevel level, LivingEntity entity, CallbackInfo ci) {
        boolean passed = awesome_spears$lungePassed;
        awesome_spears$lungePassed = false;
        SpearTuning.flattenLungeImpulse = false;
        // 骑乘时 lunge.json 本身不会突进
        if (!passed || !(entity instanceof Player player) || player.isPassenger()) {
            return;
        }
        if (!SpearConfig.isEnabled(SpearConfig.Option.LUNGE_NO_HUNGER)) {
            int levels = SpearTuning.lungeLevel(player.getItemBySlot(EquipmentSlot.MAINHAND))
                    + SpearTuning.lungeLevel(player.getItemBySlot(EquipmentSlot.OFFHAND));
            player.causeFoodExhaustion(VANILLA_EXHAUSTION_PER_LEVEL * levels);
        }
    }
}
