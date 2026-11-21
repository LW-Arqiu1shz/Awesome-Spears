package cn.blockforge.generated.awesomespears;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class SpearTuning {
   public static final double CLOSE_RANGE = 2.0;
   public static final float HITBOX_MARGIN = 0.5F;
   public static final int MIN_BRACE_TICKS = 4;
   public static final int STAB_QUEUE_TICKS = 10;
   public static final int SIDE_TRIGGER_WINDOW_TICKS = 3;
   public static final int NO_PENDING_TICK = Integer.MIN_VALUE;

   private SpearTuning() {
   }

   public static boolean isSpear(ItemStack stack) {
      return !stack.isEmpty() && stack.typeHolder().is(ItemTags.SPEARS);
   }

   public static boolean holdsSpear(Entity entity) {
      if (entity instanceof LivingEntity living && (isSpear(living.getMainHandItem()) || isSpear(living.getUseItem()))) {
         return true;
      }

      return false;
   }

   public static int lungeLevel(ItemStack stack) {
      ItemEnchantments enchantments = stack.getEnchantments();
      if (enchantments.isEmpty()) {
         return 0;
      } else {
         for (Holder<Enchantment> holder : enchantments.keySet()) {
            if (holder.is(Enchantments.LUNGE)) {
               return enchantments.getLevel(holder);
            }
         }

         return 0;
      }
   }

   public static boolean holdsLungeSpear(Player player) {
      return lungeLevel(player.getItemBySlot(EquipmentSlot.MAINHAND)) > 0 || lungeLevel(player.getItemBySlot(EquipmentSlot.OFFHAND)) > 0;
   }

   public static boolean isBracing(Player player) {
      return player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND && player.getTicksUsingItem() >= 4 && isSpear(player.getUseItem());
   }

   public static boolean isChargingSpear(Player player) {
      if (!player.isUsingItem()) {
         return false;
      } else {
         ItemStack stack = player.getUseItem();
         return isSpear(stack) && stack.has(DataComponents.KINETIC_WEAPON);
      }
   }

   public static boolean isChargingLungeSpear(Player player) {
      if (player.isUsingItem() && player.getUsedItemHand() == InteractionHand.MAIN_HAND) {
         ItemStack stack = player.getUseItem();
         return isSpear(stack) && lungeLevel(stack) > 0;
      } else {
         return false;
      }
   }

   public static boolean canBraceStab(Player player) {
      return isBracingLungeSpear(player) && !player.cannotAttackWithItem(player.getUseItem(), 0);
   }

   public static boolean isBracingLungeSpear(Player player) {
      return isBracing(player) && lungeLevel(player.getUseItem()) > 0;
   }

   public static boolean canLunge(Player player) {
      if (!isBracing(player)) {
         return false;
      } else {
         ItemStack stack = player.getUseItem();
         return lungeLevel(stack) > 0 && !stack.nextDamageWillBreak();
      }
   }

   public static boolean isSideTriggerWindow(Player player, int pendingTick) {
      if (pendingTick == Integer.MIN_VALUE) {
         return false;
      } else {
         int elapsed = player.tickCount - pendingTick;
         return elapsed >= 0 && elapsed <= 3 && isChargingLungeSpear(player) && !player.getUseItem().nextDamageWillBreak();
      }
   }

   /** 配置「蓄力时突进」：开启时突进只在蓄力中左键（或刺击后马上右键）触发；关闭时恢复原版，每次刺击都突进 */
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
      double halfLength = box.getXsize() * 0.5 * Math.abs(look.x) + box.getYsize() * 0.5 * Math.abs(look.y) + box.getZsize() * 0.5 * Math.abs(look.z);
      return alongView + halfLength < 2.0;
   }

   /** 本次突进只保留水平分量（「朝准星突进」关闭时，由 EnchantmentHelperMixin 设置） */
   public static boolean flattenLungeImpulse;
}
