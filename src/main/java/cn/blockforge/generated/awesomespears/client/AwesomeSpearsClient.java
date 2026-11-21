package cn.blockforge.generated.awesomespears.client;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

/** 仅客户端调用：把配置界面挂到 Forge 模组列表的「配置」按钮上。 */
public final class AwesomeSpearsClient {
    private AwesomeSpearsClient() {
    }

    public static void register() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> new AwesomeSpearsConfigScreen(parent)));
    }
}
