package cn.blockforge.generated.awesomespears.client;

import cn.blockforge.generated.awesomespears.SpearConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 原版风格的配置界面（与 Awesome Tridents 26.x 的配置界面相同，按 1.20.1 的界面接口改写）：
 * 顶部搜索框，下方是一条连续滚动的选项列表，左侧选项名，右侧开关，子选项缩进显示在所属大类下方。
 *
 * <p>每个选项对应一个固定的列表条目与开关按钮，在 {@link #init()} 时一次建好。
 * 切换开关只更新按钮的文案与可用状态，不会把正在被点击的按钮从控件树里移除；
 * 搜索只替换列表里显示哪些条目，条目对象本身复用。</p>
 */
public final class AwesomeSpearsConfigScreen extends Screen {
    private static final List<SpearConfig.Option> ALL_OPTIONS = SpearConfig.Option.ALL;

    private static final int SEARCH_TOP = 39;
    private static final int SEARCH_HEIGHT = 20;
    private static final int LIST_GAP = 6;
    /** 列表底部到窗口底部的空间，放「完成」按钮。 */
    private static final int FOOTER_HEIGHT = 34;
    private static final int ITEM_HEIGHT = 24;
    private static final int ROW_HEIGHT = 20;
    private static final int TOGGLE_WIDTH = 64;
    /** 子项标签相对大类的缩进量（缩进引导线画在行左侧 + 5 处）。 */
    private static final int CHILD_INDENT = 16;
    /** 行右侧给滚动条留出的空间。 */
    private static final int SCROLLBAR_SPACE = 10;

    private final Screen parent;
    private final Map<SpearConfig.Option, OptionEntry> entries = new EnumMap<>(SpearConfig.Option.class);

    private EditBox searchBox;
    private OptionList optionList;
    private String searchQuery = "";
    private int leftMargin;
    private boolean emptyResult;
    private int listTop;

    public AwesomeSpearsConfigScreen(Screen parent) {
        super(Component.translatable("config.awesome_spears.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        // 窗口缩放时 init 会被重新调用，保留之前的滚动位置。
        double previousScroll = this.optionList != null ? this.optionList.getScrollAmount() : 0.0;
        this.leftMargin = Math.max(12, this.width / 16);

        this.searchBox = new EditBox(this.font, this.leftMargin, SEARCH_TOP,
                this.width - this.leftMargin * 2, SEARCH_HEIGHT,
                Component.translatable("config.awesome_spears.search"));
        this.searchBox.setHint(Component.translatable("config.awesome_spears.search_hint"));
        this.searchBox.setMaxLength(100);
        this.searchBox.setValue(this.searchQuery);
        this.searchBox.setResponder(value -> {
            this.searchQuery = value;
            applyFilter();
            this.optionList.setScrollAmount(0.0);
        });
        this.addRenderableWidget(this.searchBox);

        this.listTop = SEARCH_TOP + SEARCH_HEIGHT + LIST_GAP;
        int listHeight = Math.max(ITEM_HEIGHT, this.height - FOOTER_HEIGHT - this.listTop);
        this.optionList = new OptionList(this.minecraft, this.width, listHeight, this.listTop);
        this.entries.clear();
        for (SpearConfig.Option option : ALL_OPTIONS) {
            this.entries.put(option, new OptionEntry(option));
        }
        this.addRenderableWidget(this.optionList);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> onClose())
                .bounds(this.width / 2 - 100, this.height - 27, 200, 20)
                .build());

        applyFilter();
        refreshStates();
        this.optionList.setScrollAmount(previousScroll);
    }

    /** 搜索结果保留父项上下文：父项命中时带出全部子项；只有子项命中时带出父项与命中的子项。 */
    private List<SpearConfig.Option> getFilteredOptions() {
        String query = this.searchQuery.strip().toLowerCase(Locale.ROOT);
        if (query.isEmpty()) {
            return ALL_OPTIONS;
        }

        List<SpearConfig.Option> result = new ArrayList<>();
        for (SpearConfig.Option option : ALL_OPTIONS) {
            if (option.parent() != null) {
                continue;
            }

            boolean parentMatches = matches(option, query);
            List<SpearConfig.Option> matchingChildren = ALL_OPTIONS.stream()
                    .filter(child -> child.parent() == option && (parentMatches || matches(child, query)))
                    .toList();

            if (parentMatches || !matchingChildren.isEmpty()) {
                result.add(option);
                result.addAll(matchingChildren);
            }
        }
        return result;
    }

    private static boolean matches(SpearConfig.Option option, String query) {
        String label = Component.translatable(option.labelKey()).getString().toLowerCase(Locale.ROOT);
        String tooltip = Component.translatable(option.tooltipKey()).getString().toLowerCase(Locale.ROOT);
        return label.contains(query) || tooltip.contains(query);
    }

    /** 按搜索词替换列表中显示的条目（条目对象复用，不新建按钮）。 */
    private void applyFilter() {
        List<OptionEntry> shown = new ArrayList<>();
        for (SpearConfig.Option option : getFilteredOptions()) {
            shown.add(this.entries.get(option));
        }
        this.emptyResult = shown.isEmpty();
        this.optionList.show(shown);
    }

    /** 刷新所有开关的文案与可用状态（父项切换会影响子项是否可编辑）。 */
    private void refreshStates() {
        for (OptionEntry entry : this.entries.values()) {
            entry.refresh();
        }
    }

    private static boolean isEditable(SpearConfig.Option option) {
        return option.parent() == null || SpearConfig.isEnabled(option.parent());
    }

    private static Component toggleText(SpearConfig.Option option, boolean editable) {
        if (!editable) {
            return Component.translatable("config.awesome_spears.parent_disabled").withStyle(ChatFormatting.GRAY);
        }
        return SpearConfig.rawValue(option)
                ? Component.translatable("config.awesome_spears.enabled").withStyle(ChatFormatting.GREEN)
                : Component.translatable("config.awesome_spears.disabled").withStyle(ChatFormatting.RED);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 1.20.1 的 Screen.render 不画背景，要先自己画
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 13, 0xFFFFFF);
        if (this.emptyResult) {
            graphics.drawCenteredString(this.font, Component.translatable("config.awesome_spears.search_empty"),
                    this.width / 2, this.listTop + 10, 0xAAAAAA);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    /** 连续滚动的选项列表；行宽铺满左右边距，右侧留出滚动条位置。 */
    private final class OptionList extends ContainerObjectSelectionList<OptionEntry> {
        OptionList(Minecraft minecraft, int width, int height, int y) {
            super(minecraft, width, height, y, y + height, ITEM_HEIGHT);
        }

        void show(List<OptionEntry> shown) {
            this.replaceEntries(shown);
        }

        @Override
        public int getRowWidth() {
            return Math.max(TOGGLE_WIDTH + 40,
                    AwesomeSpearsConfigScreen.this.width - AwesomeSpearsConfigScreen.this.leftMargin * 2 - SCROLLBAR_SPACE);
        }

        @Override
        protected int getScrollbarPosition() {
            return this.getRowRight() + 4;
        }
    }

    /** 一行：左侧选项名（子项缩进 + 引导线），右侧开关。 */
    private final class OptionEntry extends ContainerObjectSelectionList.Entry<OptionEntry> {
        private final SpearConfig.Option option;
        private final Button toggle;

        OptionEntry(SpearConfig.Option option) {
            this.option = option;
            this.toggle = Button.builder(Component.empty(), button -> {
                        if (!isEditable(this.option)) {
                            return;
                        }
                        SpearConfig.set(this.option, !SpearConfig.rawValue(this.option));
                        refreshStates();
                    })
                    .bounds(0, 0, TOGGLE_WIDTH, ROW_HEIGHT)
                    .build();
            this.toggle.setTooltip(Tooltip.create(Component.translatable(option.tooltipKey())));
        }

        void refresh() {
            boolean editable = isEditable(this.option);
            this.toggle.active = editable;
            this.toggle.setMessage(toggleText(this.option, editable));
        }

        @Override
        public void render(GuiGraphics graphics, int index, int top, int left, int width, int height,
                           int mouseX, int mouseY, boolean hovering, float partialTick) {
            int middle = top + height / 2;
            boolean child = this.option.parent() != null;
            boolean parentOff = child && !SpearConfig.isEnabled(this.option.parent());

            if (child) {
                graphics.fill(left + 5, top + 1, left + 6, top + height - 1, parentOff ? 0x55777777 : 0x66999999);
            }
            int color = parentOff ? 0x888888 : child ? 0xE0E0E0 : 0xFFFFFF;
            int labelX = left + (child ? CHILD_INDENT : 0);
            graphics.drawString(AwesomeSpearsConfigScreen.this.font, Component.translatable(this.option.labelKey()),
                    labelX, middle - 4, color);

            int toggleX = left + width - TOGGLE_WIDTH;
            this.toggle.setPosition(toggleX, middle - ROW_HEIGHT / 2);
            this.toggle.render(graphics, mouseX, mouseY, partialTick);

            // 标签区域也可悬停显示完整说明，不必把鼠标精准移到右侧开关上。
            if (hovering && mouseX >= left && mouseX < toggleX - 6) {
                AwesomeSpearsConfigScreen.this.setTooltipForNextRenderPass(Component.translatable(this.option.tooltipKey()));
            }
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return List.of(this.toggle);
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return List.of(this.toggle);
        }
    }
}
