package cn.blockforge.generated.awesomespears;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(
   modid = "awesome_spears"
)
public final class SpearSmokeTest {
   private SpearSmokeTest() {
   }

   @SubscribeEvent
   public static void onServerStarted(ServerStartedEvent event) {
      MinecraftServer server = event.getServer();

      try {
         Registry<Enchantment> enchantments = server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
         boolean lunge = enchantments.getOptional(Enchantments.LUNGE).isPresent();
         int spearItems = 0;

         for (Holder<Item> ignored : server.registryAccess().lookupOrThrow(Registries.ITEM).getTagOrEmpty(ItemTags.SPEARS)) {
            spearItems++;
         }

         long ownRegistrations = server.registryAccess()
            .lookupOrThrow(Registries.ITEM)
            .keySet()
            .stream()
            .filter(id -> "awesome_spears".equals(id.getNamespace()))
            .count();
         boolean mixinApplied = SpearPlayerState.class.isAssignableFrom(Player.class);
         boolean pass = lunge && spearItems > 0 && ownRegistrations == 0L && mixinApplied;
         AwesomeSpears.LOGGER
            .info(
               "Awesome Spears 冒烟测试：Lunge附魔={} 长矛标签物品={} 本模组注册项={} Player状态mixin={} => {}",
               new Object[]{lunge, spearItems, ownRegistrations, mixinApplied, pass ? "PASS" : "FAIL"}
            );
      } catch (Throwable var9) {
         AwesomeSpears.LOGGER.warn("Awesome Spears 冒烟测试未能完成", var9);
      }
   }
}
