package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.client.gui.screen.codec.EditorHost;
import hungteen.htlib.client.gui.screen.codec.node.EditorFormNode;
import hungteen.htlib.client.gui.screen.codec.node.UnionNode;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * 联合（dispatch）控件：字段标签 + 类型补全选择器 + 当前分支的子控件。
 * 非当前分支的子控件整体隐藏，切换分支时复用（不销毁重建）。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/5 22:50
 */
public final class UnionWidget extends EditorWidget {

    private final UnionNode node;
    private final EditorWidget[] variantWidgets;
    private TypeSelector selector;

    public UnionWidget(EditorHost host, UnionNode node) {
        super(host, node);
        this.node = node;
        this.variantWidgets = new EditorWidget[node.unionNames().size()];
    }

    @Override
    protected void create() {
        if (node.unionNames().isEmpty()) {
            return;
        }
        if (collapsible()) {
            createCollapseToggle();
        }
        selector = createSelector(SELECTOR_WIDTH, node.unionNames(), node.currentVariantName(), name -> {
            int idx = node.unionNames().indexOf(name);
            if (idx >= 0) {
                node.selectVariant(idx);
                EditorWidget widget = variantWidgets[idx];
                if (widget != null) {
                    widget.refreshFromNode();
                }
                host.relayout();
            }
        });
    }

    @Override
    protected boolean collapsible() {
        return !node.unionNames().isEmpty();
    }

    @Override
    protected int doLayout() {
        placeCollapseToggle();
        if (selector != null) {
            selector.setPosition(controlX(), y);
            selector.setVisible(!hidden && rowVisible(y));
        }
        if (isCollapsed()) {
            for (EditorWidget widget : variantWidgets) {
                if (widget != null) {
                    widget.setHidden(true);
                }
            }
            return ROW_HEIGHT;
        }
        for (int i = 0; i < variantWidgets.length; i++) {
            if (i != node.variantIndex() && variantWidgets[i] != null) {
                variantWidgets[i].setHidden(true);
            }
        }
        int height = ROW_HEIGHT;
        EditorFormNode selected = node.selectedVariant();
        if (selected != null) {
            int idx = node.variantIndex();
            if (variantWidgets[idx] == null) {
                variantWidgets[idx] = selected.widget(host);
            }
            height += variantWidgets[idx].layout(x - INDENT, y + ROW_HEIGHT, width + INDENT);
        }
        return height;
    }

    @Override
    protected void doRender(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        drawLabel(graphics, font, displayLabel(), labelColor());
        EditorWidget widget = selectedWidget();
        if (widget != null) {
            widget.render(graphics, font, mouseX, mouseY);
        }
    }

    @Override
    public boolean renderTooltip(GuiGraphics graphics, Font font, double mouseX, double mouseY) {
        if (renderSelfTooltip(graphics, font, mouseX, mouseY)) {
            return true;
        }
        EditorWidget widget = selectedWidget();
        return widget != null && widget.renderTooltip(graphics, font, mouseX, mouseY);
    }

    @Override
    public void refreshFromNode() {
        if (selector != null) {
            selector.setValue(node.currentVariantName());
        }
        EditorWidget widget = selectedWidget();
        if (widget != null) {
            widget.refreshFromNode();
        }
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        if (hidden) {
            for (EditorWidget widget : variantWidgets) {
                if (widget != null) {
                    widget.setHidden(true);
                }
            }
        }
    }

    @Override
    protected void onDispose() {
        removeSelector(selector);
        for (EditorWidget widget : variantWidgets) {
            if (widget != null) {
                widget.dispose();
            }
        }
    }

    private EditorWidget selectedWidget() {
        int idx = node.variantIndex();
        return idx >= 0 && idx < variantWidgets.length ? variantWidgets[idx] : null;
    }
}
