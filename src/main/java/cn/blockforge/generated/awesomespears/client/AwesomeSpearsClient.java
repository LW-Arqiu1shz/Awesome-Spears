package cn.blockforge.generated.awesomespears.client;

import cn.blockforge.generated.awesomespears.AwesomeSpears;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

/** 仅客户端加载：把配置界面挂到 NeoForge 模组列表的「配置」按钮上。专用服务器不会加载此类。 */
@Mod(value = AwesomeSpears.MOD_ID, dist = Dist.CLIENT)
public final class AwesomeSpearsClient {
    public AwesomeSpearsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (modContainer, parent) -> new AwesomeSpearsConfigScreen(parent));
    }
}
