package cn.blockforge.generated.awesomespears.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/** Mod Menu 的可选配置入口；不安装 Mod Menu 时不会加载此类。 */
@Environment(EnvType.CLIENT)
public final class AwesomeSpearsModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return AwesomeSpearsConfigScreen::new;
    }
}
