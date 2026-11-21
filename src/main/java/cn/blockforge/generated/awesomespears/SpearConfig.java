package cn.blockforge.generated.awesomespears;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Awesome Spears 的配置（config/awesome_spears.json），做法与 Awesome Tridents 一致。
 * 所有选项默认开启；子选项的原始值会保留，但只有父选项开启时才生效。
 * 关闭某项 = 恢复这一项的原版（1.21.1 / 1.20.1 为 Spear Core 原本）行为。
 */
public final class SpearConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve("awesome_spears.json");
    private static final Map<Option, Boolean> VALUES = new EnumMap<>(Option.class);

    static {
        resetToDefaults();
    }

    private SpearConfig() {
    }

    /** 展示顺序即声明顺序：大类在前，它的子项紧随其后。 */
    public enum Option {
        LUNGE("lunge", "lunge", null),
        LUNGE_NO_HUNGER("lungeNoHunger", "lunge_no_hunger", LUNGE),
        LUNGE_IN_AIR("lungeInAir", "lunge_in_air", LUNGE),
        LUNGE_IN_WATER("lungeInWater", "lunge_in_water", LUNGE),
        LUNGE_FOLLOW_LOOK("lungeFollowLook", "lunge_follow_look", LUNGE),
        LUNGE_WHILE_BRACING("lungeWhileBracing", "lunge_while_bracing", LUNGE),
        LUNGE_AFTER_STAB("lungeAfterStab", "lunge_after_stab", LUNGE),
        CHARGE("charge", "charge", null),
        CHARGE_KNOCKBACK("chargeKnockback", "charge_knockback", CHARGE),
        CHARGE_KEEP_MOMENTUM("chargeKeepMomentum", "charge_keep_momentum", CHARGE),
        HIT("hit", "hit", null),
        HIT_POINT_BLANK("hitPointBlank", "hit_point_blank", HIT),
        HIT_WIDER_BOX("hitWiderBox", "hit_wider_box", HIT),
        HIT_MOVING_TARGETS("hitMovingTargets", "hit_moving_targets", HIT),
        HIT_POINT_BLANK_HALF_DAMAGE("hitPointBlankHalfDamage", "hit_point_blank_half_damage", HIT);

        public static final List<Option> ALL = List.of(values());

        private final String key;
        private final String langId;
        private final Option parent;

        Option(String key, String langId, Option parent) {
            this.key = key;
            this.langId = langId;
            this.parent = parent;
        }

        public String labelKey() {
            return "config.awesome_spears.option." + langId;
        }

        public String tooltipKey() {
            return "config.awesome_spears.tooltip." + langId;
        }

        public Option parent() {
            return parent;
        }
    }

    /** 在模组初始化时加载配置文件；不存在时自动生成全为 true 的默认配置。 */
    public static synchronized void load() {
        resetToDefaults();
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                JsonElement parsed = JsonParser.parseReader(reader);
                if (parsed.isJsonObject()) {
                    JsonObject root = parsed.getAsJsonObject();
                    for (Option option : Option.values()) {
                        JsonElement value = root.get(option.key);
                        if (value != null && value.isJsonPrimitive() && value.getAsJsonPrimitive().isBoolean()) {
                            VALUES.put(option, value.getAsBoolean());
                        }
                    }
                }
            } catch (Exception exception) {
                AwesomeSpears.LOGGER.error("无法读取 Awesome Spears 配置文件，已使用默认配置：{}", CONFIG_PATH, exception);
                resetToDefaults();
            }
        }
        save();
    }

    private static void resetToDefaults() {
        VALUES.clear();
        for (Option option : Option.values()) {
            VALUES.put(option, true);
        }
    }

    /** 返回选项在文件中的原始值；子选项即使被父选项关闭也会保留自己的值。 */
    public static synchronized boolean rawValue(Option option) {
        return VALUES.getOrDefault(option, true);
    }

    /** 返回选项的实际生效值；父选项关闭时，其所有子选项均视为关闭。 */
    public static synchronized boolean isEnabled(Option option) {
        if (!VALUES.getOrDefault(option, true)) {
            return false;
        }
        return option.parent == null || isEnabled(option.parent);
    }

    /** 立即更新并持久化一个选项。 */
    public static synchronized void set(Option option, boolean enabled) {
        VALUES.put(option, enabled);
        save();
    }

    private static void save() {
        JsonObject root = new JsonObject();
        for (Option option : Option.values()) {
            root.addProperty(option.key, VALUES.getOrDefault(option, true));
        }

        Path temporary = CONFIG_PATH.resolveSibling(CONFIG_PATH.getFileName() + ".tmp");
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(temporary)) {
                GSON.toJson(root, writer);
            }
            try {
                Files.move(temporary, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ignored) {
                Files.move(temporary, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            AwesomeSpears.LOGGER.error("无法保存 Awesome Spears 配置文件：{}", CONFIG_PATH, exception);
        }
    }
}
