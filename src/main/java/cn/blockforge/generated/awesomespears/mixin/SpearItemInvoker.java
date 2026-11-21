package cn.blockforge.generated.awesomespears.mixin;

import net.minecraft.spearcore.item.SpearItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** 借用 Spear Core 记录的玩家真实位移（服务器上的玩家速度不准） */
@Mixin(SpearItem.class)
public interface SpearItemInvoker {
    @Invoker(value = "getKnownMovement", remap = false)
    static Vec3 awesome_spears$knownMovement(Entity entity) {
        throw new AssertionError();
    }
}
