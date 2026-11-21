package cn.blockforge.generated.awesomespears;

import cn.blockforge.generated.awesomespears.mixin.LivingEntityAccessor;
import net.minecraft.spearcore.item.SpearItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * 刺击后马上右键蓄力：补上突进，并跳过蓄力起手。
 * 两端都要跑：客户端负责玩家自己的位移，服务端负责耐久和音效（与 Spear Core 的突进做法一致）。
 * 必须在「已开始使用物品」之后调用（客户端玩家要等 LocalPlayer.startUsingItem 跑完才算开始使用）。
 */
public final class SpearSideTrigger {
    private SpearSideTrigger() {
    }

    public static void onChargeStart(Player player) {
        if (!SpearTuning.afterStabEnabled() || !player.isUsingItem() || player.getUsedItemHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        SpearPlayerState state = (SpearPlayerState) player;
        if (!SpearTuning.isSideTriggerWindow(player, state.awesome_spears$pendingLungeTick())) {
            return;
        }
        ItemStack stack = player.getUseItem();
        if (stack.getItem() instanceof SpearItem spear) {
            LivingEntityAccessor access = (LivingEntityAccessor) player;
            int duration = stack.getUseDuration(player);
            access.awesome_spears$setUseItemRemaining(
                    Math.min(access.awesome_spears$getUseItemRemaining(), duration - spear.getDelayTicks()));
        }
        SpearItem.jerotesLungeForwardMaybe(player);
        state.awesome_spears$setPendingLungeTick(SpearTuning.NO_PENDING_TICK);
    }
}
