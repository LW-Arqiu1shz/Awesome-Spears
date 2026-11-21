package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearConfig;
import net.minecraft.world.item.component.KineticWeapon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(KineticWeapon.class)
public abstract class KineticWeaponMixin {
    /** 冲锋击退 */
    @ModifyArg(method = "damageEntities",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;stabAttack(Lnet/minecraft/world/entity/EquipmentSlot;Lnet/minecraft/world/entity/Entity;FZZZ)Z"),
            index = 4)
    private boolean awesome_spears$restoreChargeKnockback(boolean vanilla) {
        return vanilla || SpearConfig.isEnabled(SpearConfig.Option.CHARGE_KNOCKBACK);
    }
}
