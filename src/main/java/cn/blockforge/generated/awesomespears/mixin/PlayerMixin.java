package cn.blockforge.generated.awesomespears.mixin;

import cn.blockforge.generated.awesomespears.SpearPlayerState;
import cn.blockforge.generated.awesomespears.SpearTuning;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Player.class)
public abstract class PlayerMixin implements SpearPlayerState {
    @Unique
    private int awesome_spears$pendingLungeTick = SpearTuning.NO_PENDING_TICK;

    @Override
    public int awesome_spears$pendingLungeTick() {
        return this.awesome_spears$pendingLungeTick;
    }

    @Override
    public void awesome_spears$setPendingLungeTick(int tick) {
        this.awesome_spears$pendingLungeTick = tick;
    }
}
