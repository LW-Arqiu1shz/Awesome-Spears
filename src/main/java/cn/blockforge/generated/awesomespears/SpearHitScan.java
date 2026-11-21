package cn.blockforge.generated.awesomespears;

import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.item.component.AttackRange;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public final class SpearHitScan {
   private static final double PREDICT_TICKS = 1.0;
   private static final double MAX_PREDICT = 2.0;
   private static final double CANDIDATE_PADDING = 2.5;

   private SpearHitScan() {
   }

   public static Either<BlockHitResult, Collection<EntityHitResult>> augment(
      Entity attacker, AttackRange range, Predicate<Entity> canHit, Block blockMode, Either<BlockHitResult, Collection<EntityHitResult>> vanilla
   ) {
      if (attacker.level() instanceof ServerLevel level) {
         if (!SpearTuning.holdsSpear(attacker)) {
            return vanilla;
         } else {
            Collection<EntityHitResult> already = vanilla.right().orElse(List.of());
            List<EntityHitResult> extra = scan(level, attacker, range, canHit, blockMode, already);
            if (extra.isEmpty()) {
               return vanilla;
            } else {
               List<EntityHitResult> merged = new ArrayList<>(already.size() + extra.size());
               merged.addAll(already);
               merged.addAll(extra);
               return Either.right(merged);
            }
         }
      } else {
         return vanilla;
      }
   }

   private static List<EntityHitResult> scan(
      ServerLevel level, Entity attacker, AttackRange range, Predicate<Entity> canHit, Block blockMode, Collection<EntityHitResult> already
   ) {
      Vec3 look = attacker.getHeadLookAngle();
      Vec3 eye = attacker.getEyePosition();
      Vec3 min = eye.add(look.scale((double)range.effectiveMinRange(attacker)));
      double lead = Math.max(0.0, attacker.getKnownMovement().dot(look));
      Vec3 max = eye.add(look.scale((double)range.effectiveMaxRange(attacker) + lead));
      BlockHitResult blockHit = level.clipIncludingBorder(new ClipContext(eye, max, blockMode, Fluid.NONE, attacker));
      if (blockHit.getType() != Type.MISS) {
         if (eye.distanceToSqr(blockHit.getLocation()) < eye.distanceToSqr(min)) {
            return List.of();
         }

         max = blockHit.getLocation();
      }

      float margin = range.hitboxMargin();
      AABB scanBox = AABB.ofSize(min, (double)margin, (double)margin, (double)margin).expandTowards(max.subtract(min)).inflate(2.5);
      Set<Integer> taken = new HashSet<>();

      for (EntityHitResult hit : already) {
         taken.add(hit.getEntity().getId());
      }

      List<EntityHitResult> hits = new ArrayList<>();

      for (Entity candidate : level.getEntities(attacker, scanBox, canHit)) {
         addIfHit(level, attacker, candidate, min, max, margin, blockMode, taken, hits);
      }

      for (Object raw : level.dragonParts()) {
         if (raw instanceof EnderDragonPart) {
            EnderDragonPart part = (EnderDragonPart)raw;
            if (canHit.test(part)) {
               addIfHit(level, attacker, part, min, max, margin, blockMode, taken, hits);
            }
         }
      }

      return hits;
   }

   private static void addIfHit(
      ServerLevel level, Entity attacker, Entity target, Vec3 min, Vec3 max, float margin, Block blockMode, Set<Integer> taken, List<EntityHitResult> out
   ) {
      if (target != attacker && !taken.contains(target.getId())) {
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
         BlockHitResult sight = level.clipIncludingBorder(new ClipContext(hit, center, blockMode, Fluid.NONE, attacker));
         if (sight.getType() != Type.MISS) {
            center = sight.getLocation();
         }

         if (!box.clip(hit, center).isEmpty()) {
            taken.add(target.getId());
            out.add(new EntityHitResult(target, hit));
         }
      }
   }

   private static AABB sweptBox(Entity target, float margin) {
      Vec3 move = target.getKnownMovement().scale(1.0);
      if (move.lengthSqr() > 4.0) {
         move = move.normalize().scale(2.0);
      }

      return target.getBoundingBox().expandTowards(move).expandTowards(move.scale(-1.0)).inflate((double)margin);
   }
}
