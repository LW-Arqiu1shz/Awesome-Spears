package cn.blockforge.generated.awesomespears.client;

import cn.blockforge.generated.awesomespears.AwesomeSpears;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.modlist.DefaultModDisplayInfo;
import net.neoforged.neoforge.client.gui.modlist.ModDisplayInfo;

/**
 * 仅客户端加载（专用服务器不会加载此类）：
 * 把配置界面挂到模组列表的「配置」按钮上；模组列表里的名称按当前语言显示，
 * 描述沿用 NeoForge 默认的 {@code neoforge.screen.mods.info.description.<modid>} 翻译键。
 */
@Mod(value = AwesomeSpears.MOD_ID, dist = Dist.CLIENT)
public final class AwesomeSpearsClient {
    private static final String NAME_KEY = "neoforge.screen.mods.info.displayname." + AwesomeSpears.MOD_ID;

    public AwesomeSpearsClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class,
                (modContainer, parent) -> new AwesomeSpearsConfigScreen(parent));
        container.registerExtensionPoint(ModDisplayInfo.class, new DefaultModDisplayInfo(container) {
            @Override
            public Component displayName() {
                return Component.translatableWithFallback(NAME_KEY, container.getModInfo().getDisplayName());
            }
        });
    }
}
