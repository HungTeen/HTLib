package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.client.gui.screen.codec.Rect;
import hungteen.htlib.client.gui.screen.codec.ViewerStyle;
import hungteen.htlib.client.gui.screen.codec.node.EditorFormNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 表单控件基类：与 {@link EditorFormNode} 一一对应，封装字段标签、输入框、增删按钮等控件，
 * 并负责悬浮提示的渲染。布局采用"首次创建 + 之后重摆"的增量模式，滚动/平移不会重建控件。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/5 22:50
 */
public abstract class EditorWidget {

    /** 表单行高。 */
    public static final int ROW_HEIGHT = 13;
    public static final int CONTROL_WIDTH = 64;
    public static final int CONTROL_MIN_WIDTH = 40;
    /** 结构型子结构缩进。 */
    public static final int INDENT = 6;
    /** 标签与控件之间的间距。 */
    public static final int LABEL_GAP = 5;
    /** 折叠按钮宽度及其与标签的间距。 */
    public static final int TOGGLE_WIDTH = 12;
    public static final int TOGGLE_GAP = 3;
    /** 行右缘留白（避开滚动条）。 */
    public static final int RIGHT_MARGIN = 8;
    /** 右侧按钮列最宽占用（[＋20][－20][▾12] + 间距），所有行内控件避开该区。 */
    public static final int BUTTON_ZONE = 58;

    protected final EditorHost host;
    private final EditorFormNode node;
    protected int x;
    protected int y;
    protected int width;
    protected boolean hidden = false;
    private boolean created = false;
    private boolean inline = false;
    private String inlineLabel = "";
    private int lastHeight = 0;
    private Button collapseToggle;
    private boolean collapsed = false;
    private final List<AbstractWidget> owned = new ArrayList<>();

    protected EditorWidget(EditorHost host, EditorFormNode node) {
        this.host = host;
        this.node = node;
        node.validate();
    }

    public final EditorFormNode node() {
        return node;
    }

    /**
     * 布局入口：首次调用时创建控件，之后仅重摆位置。返回占用的总高度。
     */
    public final int layout(int x, int y, int width) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.hidden = false;
        if (!created) {
            created = true;
            create();
            // 重建后强制从节点回填状态，保证切换模式/销毁重建后值不丢
            refreshFromNode();
        }
        this.lastHeight = doLayout();
        return this.lastHeight;
    }

    /** 上次布局的实际高度（折叠状态会影响该值）。 */
    public final int height() {
        return lastHeight;
    }

    /** 一次性创建控件并注册到宿主（dispose 后可重新创建）。 */
    protected abstract void create();

    /** 摆放控件与子控件（可反复调用），返回占用高度。 */
    protected abstract int doLayout();

    /** 每帧渲染标签等自绘内容（宿主裁剪区内调用）；容器负责递归子控件。 */
    public final void render(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        if (hidden) {
            return;
        }
        doRender(graphics, font, mouseX, mouseY);
    }

    protected void doRender(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
    }

    /** 渲染悬浮提示（画在全部控件之上）；返回是否命中本子树。 */
    public boolean renderTooltip(GuiGraphics graphics, Font font, double mouseX, double mouseY) {
        return renderSelfTooltip(graphics, font, mouseX, mouseY);
    }

    protected final boolean renderSelfTooltip(GuiGraphics graphics, Font font, double mouseX, double mouseY) {
        if (hidden) {
            return false;
        }
        Rect zone = tooltipZone();
        if (zone == null || zone.y() < host.topOffset()) {
            return false;
        }
        if (mouseX < zone.x() || mouseX > zone.x() + zone.width() || mouseY < zone.y()
            || mouseY > zone.y() + zone.height()) {
            return false;
        }
        List<Component> lines = node.tooltipLines();
        if (lines.isEmpty()) {
            return false;
        }
        graphics.renderComponentTooltip(font, lines, (int) mouseX, (int) mouseY);
        return true;
    }

    /** 悬浮提示热区：仅字段名那一块；返回 null 表示无提示。 */
    protected Rect tooltipZone() {
        return new Rect(x, y, labelWidth(), ROW_HEIGHT);
    }

    /** 节点数据变化（load/切换变体）后，把节点状态同步回控件。 */
    public void refreshFromNode() {
    }

    /** 整体隐藏（父控件折叠/切换变体时调用；重新布局时自动恢复）。 */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        if (hidden) {
            for (AbstractWidget w : owned) {
                w.visible = false;
            }
        }
    }

    /** 销毁：从宿主移除控件并复位状态；节点丢弃时调用，再次布局时控件会重建。 */
    public final void dispose() {
        for (AbstractWidget w : owned) {
            host.removeWidget(w);
        }
        owned.clear();
        created = false;
        onDispose();
    }

    protected void onDispose() {
    }

    // -------------------------------------------------
    // 折叠（有子结构的行）
    // -------------------------------------------------

    /** 该行是否可折叠（默认否，有子结构的容器覆写）。 */
    protected boolean collapsible() {
        return false;
    }

    /** 折叠按钮占用的行首宽度（不可折叠为 0）。 */
    protected final int toggleShift() {
        return collapsible() ? TOGGLE_WIDTH + TOGGLE_GAP : 0;
    }

    /** 创建折叠按钮（在 create 中调用）。 */
    protected final Button createCollapseToggle() {
        collapseToggle = createButton(collapsed ? "+" : "-", TOGGLE_WIDTH, b -> {
            collapsed = !collapsed;
            b.setMessage(Component.literal(collapsed ? "+" : "-"));
            host.relayout();
        });
        return collapseToggle;
    }

    /** 摆放折叠按钮（行右缘），按钮文案随状态切换。 */
    protected final void placeCollapseToggle() {
        if (collapseToggle != null) {
            place(collapseToggle, rightEdge() - TOGGLE_WIDTH, y);
        }
    }

    protected final boolean isCollapsed() {
        return collapsed;
    }

    protected final void expand() {
        collapsed = false;
    }

    /**
     * 内联布局（可选字段的标量行：左侧绘制外层标签，右侧控件），返回行高。
     */
    public int layoutInline(int x, int y, int width, String rowLabel) {
        this.inline = true;
        this.inlineLabel = rowLabel == null ? "" : rowLabel;
        return layout(x, y, width);
    }

    protected final boolean rowVisible(int rowY) {
        return rowY >= host.topOffset();
    }

    /** 标签在行左侧按文本实际宽度渲染，不截断。 */
    protected final void drawLabel(GuiGraphics graphics, Font font, String text, int color) {
        graphics.drawString(font, text, x, y + 1, color);
    }

    /** 展示用的字段名（内联行用外层标签）。 */
    protected final String displayLabel() {
        return inline ? inlineLabel : node.label();
    }

    /** 标签实际宽度。 */
    protected final int labelWidth() {
        return host.font().width(displayLabel());
    }

    /** 行右缘（留白避开滚动条）。 */
    protected final int rightEdge() {
        return x + width - RIGHT_MARGIN;
    }

    /** 行内控件（输入框/选择器）起始 x：紧跟字段名右侧。 */
    protected final int controlX() {
        return x + labelWidth() + LABEL_GAP;
    }

    protected final int controlWidth() {
        int available = Math.max(0, width - RIGHT_MARGIN - BUTTON_ZONE - labelWidth() - LABEL_GAP);
        return Math.max(CONTROL_MIN_WIDTH, Math.min(available, CONTROL_WIDTH));
    }

    protected final void place(AbstractWidget widget, int wx, int wy) {
        widget.setX(wx);
        widget.setY(wy);
        widget.visible = !hidden && rowVisible(wy);
    }

    protected final EditBox createEditBox(String value, Consumer<String> onChanged) {
        EditBox box = new EditBox(host.font(), 0, 0, 1, 12, Component.literal(""));
        box.setValue(value);
        box.setResponder(onChanged);
        host.addWidget(box);
        owned.add(box);
        return box;
    }

    protected final Button createButton(String text, int buttonWidth, Button.OnPress action) {
        Button button = Button.builder(Component.literal(text), action).bounds(0, 0, buttonWidth, 11).build();
        host.addWidget(button);
        owned.add(button);
        return button;
    }

    /** 创建行内补全选择器并注册到宿主（候选面板画在最上层）。 */
    protected final TypeSelector createSelector(int width, List<String> options, String current,
        Consumer<String> onSelect) {
        TypeSelector selector = new TypeSelector(host.screen(), this::attachWidget, 0, 0, width, ROW_HEIGHT - 2, 0,
            options, onSelect);
        selector.addToScreen(host.font());
        host.registerSelector(selector);
        return selector;
    }

    /** 从宿主移除选择器及其输入框。 */
    protected final void removeSelector(TypeSelector selector) {
        if (selector != null) {
            host.unregisterSelector(selector);
            selector.dispose(host::removeWidget);
        }
    }

    private void attachWidget(AbstractWidget widget) {
        host.addWidget(widget);
        owned.add(widget);
    }

    protected final int labelColor() {
        return node.isInvalid() ? ViewerStyle.COLOR_ERROR : ViewerStyle.COLOR_TEXT;
    }

    protected final void applyErrorColor(EditBox box) {
        box.setTextColor(node.isInvalid() ? ViewerStyle.COLOR_ERROR_VALUE : 0xFFFFFFFF);
    }

    /** 同步子节点与子控件：为新增节点创建控件，销毁已移除节点的控件。 */
    protected static void syncChildren(List<EditorFormNode> nodes, List<EditorWidget> widgets, EditorHost host) {
        widgets.removeIf(w -> {
            if (!nodes.contains(w.node())) {
                w.dispose();
                return true;
            }
            return false;
        });
        for (EditorFormNode n : nodes) {
            if (widgets.stream().noneMatch(w -> w.node() == n)) {
                widgets.add(n.widget(host));
            }
        }
    }
}
