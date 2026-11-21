package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearConfig;
import cn.blockforge.generated.awesomespears.SpearPlayerState;
import cn.blockforge.generated.awesomespears.SpearTuning;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Player.class})
public abstract class PlayerMixin implements SpearPlayerState {
   @Unique
   private int awesome_spears$pendingLungeTick = Integer.MIN_VALUE;
   @Unique
   private Vec3 awesome_spears$motionBeforeChargeHit;
   @Unique
   private boolean awesome_spears$sprintingBeforeChargeHit;

   @Override
   public int awesome_spears$pendingLungeTick() {
      return this.awesome_spears$pendingLungeTick;
   }

   @Override
   public void awesome_spears$setPendingLungeTick(int tick) {
      this.awesome_spears$pendingLungeTick = tick;
   }

   @Redirect(
      method = {"stabAttack"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/world/entity/Entity;hurtOrSimulate(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
      )}
   )
   private boolean awesome_spears$halfDamageAtPointBlank(Entity target, DamageSource source, float amount) {
      Player self = (Player)(Object)this;
      if (SpearConfig.isEnabled(SpearConfig.Option.HIT_POINT_BLANK_HALF_DAMAGE)
         && SpearTuning.isSpear(self.getMainHandItem()) && SpearTuning.isPointBlank(self, target)) {
         amount *= 0.5F;
      }

      return target.hurtOrSimulate(source, amount);
   }

   @Inject(
      method = {"causeExtraKnockback"},
      at = {@At("HEAD")}
   )
   private void awesome_spears$snapshotChargeMomentum(
      Entity target, float strength, Vec3 targetMotion, DamageSource source, float damage, boolean dismount, CallbackInfo ci
   ) {
      this.awesome_spears$motionBeforeChargeHit = null;
      Player self = (Player)(Object)this;
      if (!self.level().isClientSide() && SpearTuning.isChargingSpear(self)
         && SpearConfig.isEnabled(SpearConfig.Option.CHARGE_KEEP_MOMENTUM)) {
         this.awesome_spears$motionBeforeChargeHit = self.getDeltaMovement();
         this.awesome_spears$sprintingBeforeChargeHit = self.isSprinting();
      }
   }

   @Inject(
      method = {"causeExtraKnockback"},
      at = {@At("RETURN")}
   )
   private void awesome_spears$keepChargeMomentum(
      Entity target, float strength, Vec3 targetMotion, DamageSource source, float damage, boolean dismount, CallbackInfo ci
   ) {
      Vec3 before = this.awesome_spears$motionBeforeChargeHit;
      if (before != null) {
         this.awesome_spears$motionBeforeChargeHit = null;
         Player self = (Player)(Object)this;
         self.setDeltaMovement(before);
         if (this.awesome_spears$sprintingBeforeChargeHit) {
            self.setSprinting(true);
         }
      }
   }
}
