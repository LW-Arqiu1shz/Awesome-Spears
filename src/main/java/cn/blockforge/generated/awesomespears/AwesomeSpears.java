package cn.blockforge.generated.awesomespears;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AwesomeSpears implements ModInitializer {
   public static final String MOD_ID = "awesome_spears";
   public static final Logger LOGGER = LoggerFactory.getLogger("awesome_spears");

   public void onInitialize() {
      SpearConfig.load();
      LOGGER.info("Awesome Spears 已加载：长矛判定加宽、贴脸半伤、Lunge 只在蓄力中左键刺击时触发");
   }
}
