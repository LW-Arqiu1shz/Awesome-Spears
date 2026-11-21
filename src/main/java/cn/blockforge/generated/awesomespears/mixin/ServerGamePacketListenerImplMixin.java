package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearPlayerState;
import cn.blockforge.generated.awesomespears.SpearTuning;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket.Action;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ServerGamePacketListenerImpl.class})
public abstract class ServerGamePacketListenerImplMixin {
   @Shadow
   public ServerPlayer player;

   @Inject(
      method = {"handlePlayerAction"},
      at = {@At("HEAD")}
   )
   private void awesome_spears$rememberStab(ServerboundPlayerActionPacket packet, CallbackInfo ci) {
      if (packet.getAction() == Action.STAB && !this.player.isUsingItem() && SpearTuning.lungeLevel(this.player.getMainHandItem()) > 0) {
         ((SpearPlayerState)this.player).awesome_spears$setPendingLungeTick(this.player.tickCount);
      }
   }
}
