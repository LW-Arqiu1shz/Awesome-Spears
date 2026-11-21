package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearTuning;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.PiercingWeapon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PiercingWeapon.class)
public abstract class PiercingWeaponMixin {
    /** 蓄力中左键：只突进，不刺击 */
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void awesome_spears$handleLungeStab(LivingEntity attacker, EquipmentSlot slot, CallbackInfo ci) {
        if (attacker instanceof ServerPlayer player && slot == EquipmentSlot.MAINHAND && SpearTuning.braceLungeEnabled()) {
            ItemStack stack = player.getMainHandItem();
            if (SpearTuning.isSpear(stack) && SpearTuning.lungeLevel(stack) > 0 && SpearTuning.canLunge(player)) {
                player.postPiercingAttack();
                ci.cancel();
            }
        }
    }
}
