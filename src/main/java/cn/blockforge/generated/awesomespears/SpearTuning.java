package cn.blockforge.generated.awesomespears;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.spearcore.item.SpearItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SpearTuning {
    public static final double CLOSE_RANGE = 2.0;
    public static final float HITBOX_MARGIN = 0.5F;
    public static final int MIN_BRACE_TICKS = 4;
    public static final int STAB_QUEUE_TICKS = 10;
    public static final int SIDE_TRIGGER_WINDOW_TICKS = 3;
    public static final int NO_PENDING_TICK = Integer.MIN_VALUE;

    /** Spear Core 的突进附魔 spearcore:lunge */
    public static final ResourceKey<Enchantment> LUNGE =
            ResourceKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("spearcore", "lunge"));

    private SpearTuning() {
    }

    public static boolean isSpear(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof SpearItem;
    }

    public static boolean holdsSpear(Entity entity) {
        return entity instanceof LivingEntity living && (isSpear(living.getMainHandItem()) || isSpear(living.getUseItem()));
    }

    /** 与 Spear Core 读取突进等级的方式一致 */
    public static int lungeLevel(Level level, ItemStack stack) {
        if (!isSpear(stack)) {
            return 0;
        }
        return level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolder(LUNGE)
                .map(stack::getEnchantmentLevel)
                .orElse(0);
    }

    public static int lungeLevel(Player player, ItemStack stack) {
        return lungeLevel(player.level(), stack);
    }

    public static boolean nextDamageWillBreak(ItemStack stack) {
        return stack.isDamageableItem() && stack.getDamageValue() >= stack.getMaxDamage() - 1;
    }

    public static boolean isBracing(Player player) {
        return player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND
                && player.getTicksUsingItem() >= MIN_BRACE_TICKS && isSpear(player.getUseItem());
    }

    public static boolean isChargingSpear(Player player) {
        return SpearItem.isChargingSpear(player);
    }

    public static boolean isChargingLungeSpear(Player player) {
        if (player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
            ItemStack stack = player.getUseItem();
            return isSpear(stack) && lungeLevel(player, stack) > 0;
        }
        return false;
    }

    public static boolean isBracingLungeSpear(Player player) {
        return isBracing(player) && lungeLevel(player, player.getUseItem()) > 0;
    }

    public static boolean canBraceStab(Player player) {
        return isBracingLungeSpear(player) && player.getUseItem().getItem() instanceof SpearItem spear
                && SpearItem.canPlayerPerformStab(player, spear);
    }

    public static boolean canLunge(Player player) {
        if (!isBracing(player)) {
            return false;
        }
        ItemStack stack = player.getUseItem();
        return lungeLevel(player, stack) > 0 && !nextDamageWillBreak(stack);
    }

    public static boolean isSideTriggerWindow(Player player, int pendingTick) {
        if (pendingTick == NO_PENDING_TICK) {
            return false;
        }
        int elapsed = player.tickCount - pendingTick;
        return elapsed >= 0 && elapsed <= SIDE_TRIGGER_WINDOW_TICKS && isChargingLungeSpear(player)
                && !nextDamageWillBreak(player.getUseItem());
    }

    /** 配置「蓄力时突进」：开启时突进只在蓄力中左键（或刺击后马上右键）触发；关闭时每次刺击都突进 */
    public static boolean braceLungeEnabled() {
        return SpearConfig.isEnabled(SpearConfig.Option.LUNGE_WHILE_BRACING);
    }

    /** 配置「刺击后接蓄力」：只在「蓄力时突进」开启时有意义 */
    public static boolean afterStabEnabled() {
        return braceLungeEnabled() && SpearConfig.isEnabled(SpearConfig.Option.LUNGE_AFTER_STAB);
    }

    public static boolean lungeAllowed(Player player, int pendingTick) {
        return canLunge(player) || afterStabEnabled() && isSideTriggerWindow(player, pendingTick);
    }

    public static boolean isPointBlank(Player player, Entity target) {
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getViewVector(1.0F);
        AABB box = target.getBoundingBox();
        double alongView = box.getCenter().subtract(eye).dot(look);
        double halfLength = box.getXsize() * 0.5 * Math.abs(look.x)
                + box.getYsize() * 0.5 * Math.abs(look.y)
                + box.getZsize() * 0.5 * Math.abs(look.z);
        return alongView + halfLength < CLOSE_RANGE;
    }
}
