package cn.blockforge.generated.awesomespears;

import cn.blockforge.generated.awesomespears.mixin.SpearItemInvoker;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.spearcore.item.SpearItem;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * 在 Spear Core 的判定结果上补一遍：目标判定箱按它这一 tick 的位移前后拉长，
 * 打移动中的目标不用自己留提前量。只补 Spear Core 漏掉的目标，不会重复结算。
 */
public final class SpearHitScan {
    private static final double CANDIDATE_PADDING = 2.5;

    private SpearHitScan() {
    }

    public static void augment(LivingEntity attacker, SpearItem spear, float margin, Predicate<Entity> canHit,
                               List<EntityHitResult> results) {
        if (!(attacker.level() instanceof ServerLevel level) || !SpearTuning.holdsSpear(attacker)) {
            return;
        }
        Vec3 look = headLookAngle(attacker);
        Vec3 eye = attacker.getEyePosition();
        Vec3 min = eye.add(look.scale(spear.effectiveMinRange(attacker)));
        double lead = Math.max(0.0, SpearItemInvoker.awesome_spears$knownMovement(attacker).dot(look));
        Vec3 max = eye.add(look.scale(spear.effectiveMaxRange(attacker) + lead));
        BlockHitResult blockHit = level.clip(new ClipContext(eye, max, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, attacker));
        if (blockHit.getType() != HitResult.Type.MISS) {
            if (eye.distanceToSqr(blockHit.getLocation()) < eye.distanceToSqr(min)) {
                return;
            }
            max = blockHit.getLocation();
        }

        AABB scanBox = AABB.ofSize(min, margin, margin, margin).expandTowards(max.subtract(min)).inflate(CANDIDATE_PADDING);
        Set<Integer> taken = new HashSet<>();
        for (EntityHitResult hit : results) {
            taken.add(hit.getEntity().getId());
        }
        int before = results.size();
        // 原版 getEntities 已经包含末影龙的各个部位
        for (Entity target : level.getEntities(attacker, scanBox, canHit)) {
            addIfHit(level, attacker, target, min, max, margin, taken, results);
        }
        if (results.size() != before) {
            final Vec3 start = min;
            results.sort((a, b) -> Double.compare(a.getLocation().distanceToSqr(start), b.getLocation().distanceToSqr(start)));
        }
    }

    private static void addIfHit(ServerLevel level, Entity attacker, Entity target, Vec3 min, Vec3 max, float margin,
                                 Set<Integer> taken, List<EntityHitResult> out) {
        if (target == attacker || taken.contains(target.getId())) {
            return;
        }
        AABB box = sweptBox(target, margin);
        Vec3 hit;
        if (box.contains(min)) {
            hit = min;
        } else {
            Optional<Vec3> clip = box.clip(min, max);
            if (clip.isEmpty()) {
                return;
            }
            hit = clip.get();
        }
        Vec3 center = box.getCenter();
        BlockHitResult sight = level.clip(new ClipContext(hit, center, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, attacker));
        if (sight.getType() != HitResult.Type.MISS) {
            center = sight.getLocation();
        }
        if (!box.clip(hit, center).isEmpty()) {
            taken.add(target.getId());
            out.add(new EntityHitResult(target, hit));
        }
    }

    private static AABB sweptBox(Entity target, float margin) {
        Vec3 move = SpearItemInvoker.awesome_spears$knownMovement(target);
        if (move.lengthSqr() > 4.0) {
            move = move.normalize().scale(2.0);
        }
        return target.getBoundingBox().expandTowards(move).expandTowards(move.scale(-1.0)).inflate(margin);
    }

    /** 与 Spear Core 一致：按头部朝向算视线 */
    private static Vec3 headLookAngle(Entity entity) {
        float xRad = entity.getXRot() * Mth.DEG_TO_RAD;
        float yRad = -entity.getYHeadRot() * Mth.DEG_TO_RAD;
        float cosY = Mth.cos(yRad);
        float sinY = Mth.sin(yRad);
        float cosX = Mth.cos(xRad);
        float sinX = Mth.sin(xRad);
        return new Vec3(sinY * cosX, -sinX, cosY * cosX);
    }
}
