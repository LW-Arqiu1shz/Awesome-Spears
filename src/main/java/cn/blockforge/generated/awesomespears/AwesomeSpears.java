package cn.blockforge.generated.awesomespears;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("awesome_spears")
public final class AwesomeSpears {
   public static final String MOD_ID = "awesome_spears";
   public static final Logger LOGGER = LoggerFactory.getLogger("awesome_spears");

   public AwesomeSpears(IEventBus modBus) {
      SpearConfig.load();
      LOGGER.info("Awesome Spears 已加载：长矛判定加宽、贴脸半伤、Lunge 只在蓄力中左键刺击时触发");
   }
}
