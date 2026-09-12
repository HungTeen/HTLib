package hungteen.htlib.client.gui.screen.codec;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hungteen.htlib.client.gui.screen.codec.node.EditorFormNode;
import hungteen.htlib.client.gui.widget.codec.EditorResultCache;
import hungteen.htlib.client.gui.widget.codec.EditorWidget;
import hungteen.htlib.client.gui.widget.codec.EntrySchemaCache;
import hungteen.htlib.client.gui.widget.codec.TypeSelector;
import hungteen.htlib.common.network.EditorResultPacket;
import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.network.SaveDataPacket;
import hungteen.htlib.common.network.ValidateDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Codec 编辑器界面。
 *
 * <p>顶部工具条：数据包类型下拉框（支持搜索）、表单/JSON 模式切换、保存路径与文件名、保存按钮。
 * 主体区域：表单或 JSON 文本视图。表单支持滚轮滚动与右键拖拽平移。</p>
 *
 * <p>控件由 {@link EditorWidget} 树持有：滚动/平移只触发 {@link #layoutForm()} 重摆，
 * 结构变化（增删行/折叠/切换变体）由控件回调 {@link EditorHost#relayout()} 增量更新； 仅在 schema 变更、模式切换、窗口 resize 时全量 {@link #rebuild()}。</p>
 *
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 **/
public class CodecEditorScreen extends CodecScreen implements EditorHost {

    private static final int TOP_OFFSET = 20;
    /** 顶部类型输入框宽度（编辑器工具条更紧凑）。 */
    private static final int TYPE_DROPDOWN_WIDTH = 150;

    private boolean jsonMode = false;
    private JsonObject schema;
    private EditorFormNode formRoot;
    private String datapackName;
    private String saveRegistryName;
    private hungteen.htlib.client.gui.widget.codec.DropdownButton saveDropdown;
    private int jsonScroll = 0;
    private int formScroll = 0;
    private int panX = 0;

    /* 右键拖拽平移 */
    private boolean panning = false;
    private double panLastX, panLastY;

    public CodecEditorScreen(List<ResourceLocation> registryNames) {
        super(registryNames);
    }

    /**
     * 打开编辑器界面（由 OpenCodecPacket 调用）。
     */
    public static void open(List<ResourceLocation> registryNames) {
        Minecraft.getInstance().setScreen(new CodecEditorScreen(registryNames));
    }

    /** 顶部类型补全输入框（候选为全部注册表名）。 */
    @Override
    protected TypeSelector createTypeSelector() {
        List<String> names = registryNames.stream().map(ResourceLocation::toString).toList();
        return new TypeSelector(this, this::addRenderableWidget, ViewerStyle.LEFT_PADDING, 4, TYPE_DROPDOWN_WIDTH, 13,
            TOP_OFFSET, names, this::select);
    }

    /**
     * 全量重建：销毁旧控件树 → 清空界面 → 工具条 → 布局表单。
     */
    @Override
    public void rebuild() {
        if (formRoot != null && formRoot.hasWidget()) {
            formRoot.widget(this).dispose();
        }
        clearWidgets();
        selectors.clear();

        // 顶部：数据包类型输入框（自动补全）
        typeSelector.setValue(selected);
        typeSelector.addToScreen(this.font);

        rebuildBody();
        layoutForm();
    }

    /** 增量布局：只重摆控件树，不销毁不重建（滚动/平移/结构变化都走这里）。 */
    private void layoutForm() {
        if (!jsonMode && formRoot != null) {
            formRoot.widget(this).layout(ViewerStyle.LEFT_PADDING + panX, TOP_OFFSET - formScroll,
                this.width - ViewerStyle.LEFT_PADDING * 2);
        }
    }

    /** 界面移除后不再接收保存/校验结果（响应可能晚于关闭到达）。 */
    @Override
    public void removed() {
        super.removed();
        EditorResultCache.release(EditorResultPacket.ACTION_SAVE);
        EditorResultCache.release(EditorResultPacket.ACTION_VALIDATE);
    }

    /**
     * 顶部工具条按钮（模式切换/路径/文件名/动作下拉）。
     */
    private void rebuildBody() {
        Button modeButton =
            Button.builder(Component.translatable(jsonMode ? "htlib.screen.form" : "htlib.screen.json"), b -> {
                jsonMode = !jsonMode;
                rebuild();
            }).bounds(166, 4, 70, 14).build();
        addRenderableWidget(modeButton);

        EditBox datapackNameField = new EditBox(this.font, 244, 4, 60, 13, Component.translatable("htlib.screen.path"));
        datapackNameField.setValue(saveRegistryName);
        datapackNameField.setSuggestion(I18n.get("htlib.screen.datapack_hint"));
        datapackNameField.setResponder(s -> {
            datapackName = s;
            datapackNameField.setSuggestion(StringUtils.isBlank(s) ? I18n.get("htlib.screen.datapack_hint") : null);
        });
        addRenderableWidget(datapackNameField);

        EditBox registryNameField = new EditBox(this.font, 314, 4, 60, 13, Component.translatable("htlib.screen.path"));
        registryNameField.setValue(saveRegistryName);
        registryNameField.setSuggestion(I18n.get("htlib.screen.path_hint"));
        registryNameField.setResponder(s -> {
            saveRegistryName = s;
            registryNameField.setSuggestion(StringUtils.isBlank(s) ? I18n.get("htlib.screen.path_hint") : null);
        });
        addRenderableWidget(registryNameField);

        // 动作下拉：保存 / 校验（按钮文案固定为"更多"）
        List<String> actions = List.of(I18n.get("htlib.screen.save"), I18n.get("htlib.screen.validate"));
        int actionsWidth = Math.max(40, actions.stream().mapToInt(this.font::width).max().orElse(40) + 8);
        saveDropdown =
            new hungteen.htlib.client.gui.widget.codec.DropdownButton(this::addRenderableWidget, this::removeWidget,
                I18n.get("htlib.screen.more"), actionsWidth, 14, actions, 0, index -> {
                if (index == 0) {
                    save();
                } else {
                    validate();
                }
            });
        saveDropdown.addToScreen(this.font);
        saveDropdown.setPosition(this.width - actionsWidth - 2, 4);
    }

    /**
     * 校验整个表单：交给服务端用完整 RegistryAccess 解析。
     *
     * <p>Holder 引用只有在 {@code RegistryOps} 下才会按注册名解析，而客户端注册表不全，
     * 单纯用 {@code JsonOps} 解析会把它当内联对象，报出误导性的字段错误。</p>
     */
    private void validate() {
        if (formRoot == null) {
            return;
        }
        EditorResultCache.await(EditorResultPacket.ACTION_VALIDATE, this::onValidateResult);
        setStatusPersistent(I18n.get("htlib.screen.validating"));
        NetworkHandler.sendToServer(new ValidateDataPacket(this.selected, collectJsonText()));
    }

    /** 校验结果：通过只提示成功，不通过给出 codec 的报错原因。 */
    private void onValidateResult(boolean success, String message) {
        setStatusFor(success ? I18n.get("htlib.screen.validate_ok") : I18n.get("htlib.screen.validate_failed", message),
            ViewerStyle.INFO_TIMEOUT_MS);
    }

    /**
     * 把 JSON 文本格式化（多行缩进）。
     */
    private static String prettyJson(String text) {
        try {
            return new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(JsonParser.parseString(text));
        } catch (Exception e) {
            return text;
        }
    }

    /**
     * 选中注册表 → 优先读取 schema 缓存，未命中才请求服务端（同一注册表只请求一次）。
     */
    public void select(String name) {
        this.selected = name;
        this.schema = null;
        this.formRoot = null;
        this.formScroll = 0;
        this.panX = 0;
        String cached = EntrySchemaCache.schema(name);
        if (cached != null && !cached.isEmpty()) {
            applySchema(cached);
            return;
        }
        setStatusPersistent(I18n.get("htlib.screen.request_schema", name));
        rebuild();
        EntrySchemaCache.acquire(name, json -> {
            // 回调时可能已切换/关闭界面：仅当仍是当前选中且屏幕存活时应用
            if (name.equals(selected) && screen() == Minecraft.getInstance().screen) {
                applySchema(json);
            }
        });
    }

    /**
     * 应用收到的 schema。
     */
    @Override
    protected void applySchema(String schemaJson) {
        try {
            this.schema = JsonParser.parseString(schemaJson).getAsJsonObject();
            this.formRoot = EditorFormNode.root(this.schema);
            this.formScroll = 0;
            this.panX = 0;
            setStatusFor(I18n.get("htlib.screen.loaded_schema", selected), ViewerStyle.INFO_TIMEOUT_MS);
            rebuild();
        } catch (Exception e) {
            setStatusPersistent(I18n.get("htlib.screen.schema_error", e.getMessage()));
        }
    }

    /**
     * 保存：发送到服务端，由服务端校验通过后落盘。
     */
    private void save() {
        String json = collectJsonText();
        if (StringUtils.isAnyBlank(this.selected, this.saveRegistryName, this.datapackName)) {
            setStatusFor(I18n.get("htlib.screen.missing_path"), ViewerStyle.INFO_TIMEOUT_MS);
            return;
        }
        if (!ResourceLocation.isValidResourceLocation(this.saveRegistryName)) {
            setStatusFor(I18n.get("htlib.screen.invalid_registry_name"), ViewerStyle.INFO_TIMEOUT_MS);
            return;
        }
        ResourceLocation registryName = ResourceLocation.tryParse(this.saveRegistryName);
        ResourceLocation registryType = ResourceLocation.tryParse(this.selected);
        if (ObjectUtils.anyNull(registryName, registryType)) {
            setStatusFor(I18n.get("htlib.screen.invalid_registry_name"), ViewerStyle.INFO_TIMEOUT_MS);
            return;
        }
        // 保存路径与所选数据包类型对齐：datapack:<pack>/data/<命名空间>/<注册表路径>
        String path =
            String.format("datapack:%s/%s/data/%s/%s/%s.json", this.datapackName, registryName.getNamespace(),
                registryType.getNamespace(), registryType.getPath(), registryName.getPath());
        EditorResultCache.await(EditorResultPacket.ACTION_SAVE, this::onSaveResult);
        setStatusPersistent(I18n.get("htlib.screen.saving"));
        NetworkHandler.sendToServer(new SaveDataPacket(selected, path, json));
    }

    /** 保存结果：成功给出落盘路径，失败给出原因（校验不通过 / 写入失败）。 */
    private void onSaveResult(boolean success, String message) {
        setStatusFor(success ? I18n.get("htlib.screen.saved", message) : I18n.get("htlib.screen.save_failed", message),
            ViewerStyle.INFO_TIMEOUT_MS);
    }

    private String collectJsonText() {
        if (formRoot != null) {
            return formRoot.collect().toString();
        }
        return "{}";
    }

    // -------------------------------------------------
    // 渲染
    // -------------------------------------------------

    @Override
    protected void renderFormRegion(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 主体区域裁剪在 TOP_OFFSET 之下：滚动/平移时不会盖住顶部工具条
        graphics.enableScissor(0, TOP_OFFSET, this.width, this.height);

        // 表单：控件树自绘标签（悬浮提示在 renderOverlays 画）
        if (!jsonMode && formRoot != null) {
            formRoot.widget(this).render(graphics, this.font, mouseX, mouseY);
        }

        // 多行滚动 JSON 视图
        if (jsonMode) {
            String text = formRoot != null ? formRoot.collect().toString() : "{}";
            List<String> lines = splitLines(prettyJson(text));
            int x = ViewerStyle.LEFT_PADDING;
            int y = TOP_OFFSET - jsonScroll;
            for (String line : lines) {
                if (y >= TOP_OFFSET - 4 && y < this.height - 30) {
                    graphics.drawString(this.font, line, x, y, 0xFFFFFF);
                }
                y += 10;
            }
        }

        graphics.disableScissor();
    }

    @Override
    protected void renderOverlays(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!jsonMode) {
            drawFormScrollbar(graphics);
            // 面板展开时隐藏字段悬浮提示，避免提示从面板下方探出
            if (formRoot != null && !anyPanelOpen()) {
                formRoot.widget(this).renderTooltip(graphics, this.font, mouseX, mouseY);
            }
        }
    }

    /** 表单右侧滚动条（仅内容超出可视区时显示）。 */
    private void drawFormScrollbar(GuiGraphics graphics) {
        if (formRoot == null) {
            return;
        }
        int content = formRoot.widget(this).height();
        int visible = this.height - TOP_OFFSET - ViewerStyle.BOTTOM_PADDING;
        int maxScroll = Math.max(0, content - visible);
        if (maxScroll <= 0) {
            return;
        }
        int trackX = this.width - ViewerStyle.SCROLLBAR_WIDTH - ViewerStyle.SCROLLBAR_RIGHT_MARGIN;
        int trackY = TOP_OFFSET;
        graphics.fill(trackX - 1, trackY, trackX + ViewerStyle.SCROLLBAR_WIDTH + 1, trackY + visible,
            ViewerStyle.COLOR_SCROLL_TRACK);
        int thumbH = Math.max(EditorWidget.ROW_HEIGHT, visible * visible / Math.max(1, content));
        int thumbY = trackY + (int)((long)formScroll * (visible - thumbH) / maxScroll);
        graphics.fill(trackX, thumbY, trackX + ViewerStyle.SCROLLBAR_WIDTH, thumbY + thumbH,
            ViewerStyle.COLOR_SCROLL_THUMB);
    }

    /** 按宽度折行。 */
    private static List<String> splitLines(String text) {
        List<String> lines = new ArrayList<>();
        for (String line : text.split("\n")) {
            if (line.length() <= 120) {
                lines.add(line);
            } else {
                for (int i = 0; i < line.length(); i += 120) {
                    lines.add(line.substring(i, Math.min(line.length(), i + 120)));
                }
            }
        }
        return lines;
    }

    // -------------------------------------------------
    // 滚动 / 平移
    // -------------------------------------------------

    /** 补全面板之外：表单/JSON 滚动。 */
    @Override
    protected boolean onScroll(double mouseX, double mouseY, double delta) {
        if (!jsonMode) {
            int content = formRoot == null ? 0 : formRoot.widget(this).height();
            int visible = this.height - TOP_OFFSET - ViewerStyle.BOTTOM_PADDING;
            formScroll = DropdownUtil.clampScroll(formScroll - (int)delta * ViewerStyle.SCROLL_STEP, content, visible);
            layoutForm();
            return true;
        }
        jsonScroll = (int)Math.max(0, jsonScroll - delta * 10);
        return true;
    }

    /** 补全面板之外：右键按下开始平移（表单/JSON 视图均可）。 */
    @Override
    protected boolean onMouseClick(double mouseX, double mouseY, int button) {
        if (button == 1) {
            panning = true;
            panLastX = mouseX;
            panLastY = mouseY;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (panning && button == 1) {
            // 内容跟随鼠标：鼠标下/右移 → 内容下/右移
            panX += (int)Math.round(mouseX - panLastX);
            int dy = (int)Math.round(mouseY - panLastY);
            if (!jsonMode) {
                int content = formRoot == null ? 0 : formRoot.widget(this).height();
                int visible = this.height - TOP_OFFSET - ViewerStyle.BOTTOM_PADDING;
                formScroll = DropdownUtil.clampScroll(formScroll - dy, content, visible);
            } else {
                jsonScroll = (int)Math.max(0, jsonScroll - dy);
            }
            panX = Math.max(-this.width, Math.min(0, panX));
            panLastX = mouseX;
            panLastY = mouseY;
            layoutForm();
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 1) {
            panning = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    /** 动作下拉的面板与字段补全面板一起画在最上层。 */
    @Override
    protected void paintSelectors(GuiGraphics graphics, double mouseX, double mouseY) {
        super.paintSelectors(graphics, mouseX, mouseY);
        if (saveDropdown != null) {
            saveDropdown.paintPanel(graphics, this.font, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 动作下拉展开期间：面板内选择，面板外只收起（不透传给下方控件）
        if (saveDropdown != null && saveDropdown.isOpen()) {
            saveDropdown.mouseClicked(mouseX, mouseY);
            setFocused(null);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (saveDropdown != null && saveDropdown.isOpen() && keyCode == org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE) {
            saveDropdown.close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    // -------------------------------------------------
    // EditorHost
    // -------------------------------------------------

    @Override
    public Screen screen() {
        return this;
    }

    @Override
    public Font font() {
        return this.font;
    }

    @Override
    public int screenWidth() {
        return this.width;
    }

    @Override
    public int topOffset() {
        return TOP_OFFSET;
    }

    @Override
    public void addWidget(AbstractWidget widget) {
        addRenderableWidget(widget);
    }

    @Override
    public void removeWidget(AbstractWidget widget) {
        super.removeWidget(widget);
    }

    @Override
    public void relayout() {
        layoutForm();
    }

    /** 补全面板右缘不越过右侧按钮列与滚动条区，避免面板盖住 [＋][－][▾] 按钮及其文字。 */
    @Override
    protected int panelRightLimit() {
        return this.width - ViewerStyle.PANEL_RIGHT_GUARD;
    }
}
