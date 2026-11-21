package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearConfig;
import cn.blockforge.generated.awesomespears.SpearHitScan;
import com.mojang.datafixers.util.Either;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProjectileUtil.class)
public abstract class ProjectileUtilMixin {
    /** 移动目标补判 */
    @Inject(method = "getHitEntitiesAlong(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/component/AttackRange;Ljava/util/function/Predicate;Lnet/minecraft/world/level/ClipContext$Block;)Lcom/mojang/datafixers/util/Either;",
            at = @At("RETURN"), cancellable = true)
    private static void awesome_spears$augmentHits(Entity attacker, AttackRange range, Predicate<Entity> canHit, ClipContext.Block blockMode,
                                                   CallbackInfoReturnable<Either<BlockHitResult, Collection<EntityHitResult>>> cir) {
        Either<BlockHitResult, Collection<EntityHitResult>> vanilla = cir.getReturnValue();
        if (vanilla != null && SpearConfig.isEnabled(SpearConfig.Option.HIT_MOVING_TARGETS)) {
            cir.setReturnValue(SpearHitScan.augment(attacker, range, canHit, blockMode, vanilla));
        }
    }
}
