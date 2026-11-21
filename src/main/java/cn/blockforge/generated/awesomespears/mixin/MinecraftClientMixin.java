package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearTuning;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.PiercingWeapon;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
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

   @Inject(
      method = {"handleKeybinds"},
      at = {@At("HEAD")}
   )
   private void awesome_spears$stabWhileBracing(CallbackInfo ci) {
      if (this.player != null && this.gameMode != null) {
         if (!SpearTuning.braceLungeEnabled() || !SpearTuning.isBracingLungeSpear(this.player)) {
            this.awesome_spears$queuedStab = 0;
         } else {
            while (this.options.keyAttack.consumeClick()) {
               this.awesome_spears$queuedStab = 10;
            }

            if (this.awesome_spears$queuedStab > 0) {
               this.awesome_spears$queuedStab--;
               if (SpearTuning.canBraceStab(this.player)) {
                  this.awesome_spears$queuedStab = 0;
                  ItemStack stack = this.player.getMainHandItem();
                  PiercingWeapon weapon = (PiercingWeapon)stack.get(DataComponents.PIERCING_WEAPON);
                  if (weapon != null) {
                     this.gameMode.piercingAttack(stack.getAttackAnimation(), weapon);
                  }
               }
            }
         }
      }
   }
}
