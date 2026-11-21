package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearTuning;
import net.minecraft.world.item.enchantment.effects.ApplyEntityImpulse;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 关掉「朝准星突进」时，突进只保留水平分量（等同原版 lunge.json 的 [1,0,1]）。
 * 必须在加速度生效前改：26.3 会在这里立刻把速度发给玩家，事后再改就来不及了。
 */
@Mixin(ApplyEntityImpulse.class)
public abstract class ApplyEntityImpulseMixin {
    @ModifyArg(method = "apply",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;addDeltaMovement(Lnet/minecraft/world/phys/Vec3;)V"))
    private Vec3 awesome_spears$flattenLunge(Vec3 impulse) {
        return SpearTuning.flattenLungeImpulse ? new Vec3(impulse.x, 0.0, impulse.z) : impulse;
    }
}
