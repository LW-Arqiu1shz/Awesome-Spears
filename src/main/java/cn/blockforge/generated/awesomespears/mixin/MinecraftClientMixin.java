package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearTuning;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.spearcore.item.SpearItem;
import net.minecraft.spearcore.network.SpearStabAttackPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** 举矛蓄力时原版会吞掉左键，这里放出来：蓄力中左键 = 突进 */
@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Shadow
    public LocalPlayer player;
    @Shadow
    public MultiPlayerGameMode gameMode;
    @Shadow
    @Final
    public Options options;
    @Unique
    private int awesome_spears$queuedStab;

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void awesome_spears$stabWhileBracing(CallbackInfo ci) {
        if (this.player == null || this.gameMode == null) {
            return;
        }
        if (!SpearTuning.braceLungeEnabled() || !SpearTuning.isBracingLungeSpear(this.player)) {
            this.awesome_spears$queuedStab = 0;
            return;
        }
        while (this.options.keyAttack.consumeClick()) {
            this.awesome_spears$queuedStab = SpearTuning.STAB_QUEUE_TICKS;
        }
        if (this.awesome_spears$queuedStab > 0) {
            this.awesome_spears$queuedStab--;
            if (SpearTuning.canBraceStab(this.player)) {
                this.awesome_spears$queuedStab = 0;
                SpearStabAttackPacket.CHANNEL.sendToServer(new SpearStabAttackPacket());
                SpearItem.jerotesLungeForwardMaybe(this.player);
                this.player.resetAttackStrengthTicker();
            }
        }
    }
}
