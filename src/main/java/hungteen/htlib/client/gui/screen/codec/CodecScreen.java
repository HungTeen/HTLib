package hungteen.htlib.client.gui.screen.codec;

import hungteen.htlib.client.gui.screen.HTScreen;
import hungteen.htlib.client.gui.widget.codec.TypeSelector;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 **/
public abstract class CodecScreen extends HTScreen {

    protected final List<ResourceLocation> registryNames;
    protected String selected = "";
    protected String status = "";
    protected long statusUntil = Long.MAX_VALUE;

    /** 顶部类型补全输入框（init 时由 {@link #createTypeSelector()} 创建）。 */
    protected TypeSelector typeSelector;
    /** 行内枚举/UNION 的补全输入框（节点在建控件时注册）。 */
    protected final List<TypeSelector> selectors = new ArrayList<>();

    protected CodecScreen(List<ResourceLocation> registryNames) {
        this.registryNames = new ArrayList<>(registryNames);
    }

    /** 收到某注册表的 schema：统一转发给当前打开的 Codec 界面。 */
    public static void onSchema(String registryName, String schemaJson) {
        if (Minecraft.getInstance().screen instanceof CodecScreen screen && screen.selected.equals(registryName)) {
            screen.applySchema(schemaJson);
        }
    }

    /**
     * 创建顶部类型补全输入框。
     */
    protected abstract TypeSelector createTypeSelector();

    /**
     * 全量重建界面。
     */
    protected abstract void rebuild();

    /**
     * 应用收到的 schema。
     */
    protected abstract void applySchema(String schemaJson);

    /**
     * 主体区域绘制（负责 scissor 管理），在裁剪区内的内容逐一画出。
     */
    protected abstract void renderFormRegion(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    /**
     * super.render 之后的覆盖层（滚动条 / 悬浮提示）。
     */
    protected abstract void renderOverlays(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    /**
     * 补全面板已处理后的剩余滚动。
     */
    protected abstract boolean onScroll(double mouseX, double mouseY, double delta);

    /**
     * 补全面板已处理后的剩余点击（如右键平移、UNION 下拉关闭）。
     */
    protected boolean onMouseClick(double mouseX, double mouseY, int button) {
        return false;
    }

    /** 进入界面后的额外初始化（如预选注册表请求 schema）。 */
    protected void onInit() {
    }

    @Override
    protected void init() {
        typeSelector = createTypeSelector();
        rebuild();
        if (typeSelector != null) {
            typeSelector.focus();
        }
        onInit();
    }

    /** 短暂状态提示（自动消失）。 */
    protected void setStatus(String text) {
        this.status = text;
        this.statusUntil = Util.getMillis() + ViewerStyle.STATUS_TIMEOUT_MS;
    }

    /** 持久状态提示（不自动消失）。 */
    protected void setStatusPersistent(String text) {
        this.status = text;
        this.statusUntil = Long.MAX_VALUE;
    }

    private void tickStatus() {
        if (!status.isEmpty() && Util.getMillis() >= statusUntil) {
            status = "";
        }
    }

    /** 注册一个行内补全输入框（由表单控件创建，面板画在最上层）。 */
    public void registerSelector(TypeSelector selector) {
        selectors.add(selector);
    }

    /** 注销一个行内补全输入框。 */
    public void unregisterSelector(TypeSelector selector) {
        selectors.remove(selector);
    }

    /** 是否有补全面板展开（展开期间视为临时模态：隐藏字段 tooltip、拦截面板外点击）。 */
    protected boolean anyPanelOpen() {
        if (typeSelector != null && typeSelector.isOpen()) {
            return true;
        }
        for (TypeSelector s : selectors) {
            if (s.isOpen()) {
                return true;
            }
        }
        return false;
    }

    /** 补全面板允许的最大右缘（默认不限制；子类可限制以避开右侧按钮列）。 */
    protected int panelRightLimit() {
        return Integer.MAX_VALUE;
    }

    /** 顶部 + 行内所有补全面板（画在最上层）。 */
    protected void paintSelectors(GuiGraphics graphics, double mouseX, double mouseY) {
        int limit = panelRightLimit();
        if (typeSelector != null) {
            typeSelector.paintPanel(graphics, this.font, mouseX, mouseY, limit);
        }
        for (TypeSelector s : selectors) {
            s.paintPanel(graphics, this.font, mouseX, mouseY, limit);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics);
        tickStatus();

        renderFormRegion(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        if (!status.isEmpty()) {
            graphics.drawString(this.font, status, ViewerStyle.LEFT_PADDING,
                this.height - ViewerStyle.STATUS_BOTTOM, ViewerStyle.COLOR_STATUS);
        }
        renderOverlays(graphics, mouseX, mouseY, partialTick);
        // 补全面板画在最上层（不应被字段/控件遮挡）
        paintSelectors(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (typeSelector != null && typeSelector.mouseScrolled(delta)) {
            return true;
        }
        for (TypeSelector s : selectors) {
            if (s.mouseScrolled(delta)) {
                return true;
            }
        }
        return onScroll(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (typeSelector != null && typeSelector.keyPressed(keyCode)) {
            return true;
        }
        for (TypeSelector s : selectors) {
            if (s.keyPressed(keyCode)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int selectorLimit = panelRightLimit();
        boolean panelOpen = anyPanelOpen();
        if (typeSelector != null && typeSelector.mouseClicked(mouseX, mouseY, selectorLimit)) {
            return true;
        }
        for (TypeSelector s : selectors) {
            if (s.mouseClicked(mouseX, mouseY, selectorLimit)) {
                return true;
            }
        }
        // 面板展开时，面板外的点击只用于收起面板，不透传给下方的按钮/输入框
        if (panelOpen) {
            setFocused(null);
            return true;
        }
        if (onMouseClick(mouseX, mouseY, button)) {
            return true;
        }
        if (super.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        // 点击空白区域：取消输入框聚焦
        setFocused(null);
        return false;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
