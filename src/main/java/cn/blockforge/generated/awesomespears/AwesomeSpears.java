package cn.blockforge.generated.awesomespears;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1.21.1 没有原版长矛，本版本改的是前置模组 Spear Core（矛核心）的长矛。
 * 改动与 26.x 版一致：判定加宽、贴脸半伤、蓄力击退、撞到不减速、突进只在蓄力中左键刺击时触发。
 */
@Mod(AwesomeSpears.MOD_ID)
public final class AwesomeSpears {
    public static final String MOD_ID = "awesome_spears";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public AwesomeSpears(IEventBus modBus) {
        SpearConfig.load();
        LOGGER.info("Awesome Spears 已加载（Spear Core 适配）：长矛判定加宽、贴脸半伤、突进只在蓄力中左键刺击时触发");
    }
}
