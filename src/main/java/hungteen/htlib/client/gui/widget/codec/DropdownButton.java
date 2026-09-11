package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.client.gui.screen.codec.DropdownUtil;
import hungteen.htlib.client.gui.screen.codec.Rect;
import hungteen.htlib.client.gui.screen.codec.ViewerStyle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * 无输入框的下拉按钮：点击弹出选项面板，选中后触发回调。
 * 与 {@link TypeSelector} 同一套面板绘制风格（DropdownUtil + 高 Z 层），
 * 适合工具条动作菜单（如保存 / 校验）。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/6 22:30
 */
public final class DropdownButton {

    private static final int ROW_H = ViewerStyle.FIELD_ROW;
    private static final int PANEL_MARGIN = ViewerStyle.DROPDOWN_PANEL_MARGIN;
    private static final int BUTTON_HEIGHT = 14;
    private static final int PANEL_GAP = 2;

    private final Consumer<AbstractWidget> addWidget;
    private final Consumer<AbstractWidget> removeWidget;
    private final List<String> options;
    private final Consumer<Integer> onSelect;
    private final int width;
    private final int buttonHeight;
    /** 固定按钮文案（为空则显示当前选中项）。 */
    private final String title;
    private Button button;
    private int x;
    private int y;
    private boolean open = false;
    private int current = 0;

    public DropdownButton(Consumer<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget,
        String title, int width, List<String> options, int initialIndex, Consumer<Integer> onSelect) {
        this.addWidget = addWidget;
        this.removeWidget = removeWidget;
        this.title = title;
        this.options = List.copyOf(options);
        this.current = Math.max(0, Math.min(initialIndex, options.size() - 1));
        this.onSelect = onSelect;
        this.width = width;
        this.buttonHeight = 13;
    }

    public DropdownButton(Consumer<AbstractWidget> addWidget, Consumer<AbstractWidget> removeWidget,
        String title, int width, int buttonHeight, List<String> options, int initialIndex, Consumer<Integer> onSelect) {
        this.addWidget = addWidget;
        this.removeWidget = removeWidget;
        this.title = title;
        this.options = List.copyOf(options);
        this.current = Math.max(0, Math.min(initialIndex, options.size() - 1));
        this.onSelect = onSelect;
        this.width = width;
        this.buttonHeight = buttonHeight;
    }

    /** 创建按钮并注册到宿主（幂等）。 */
    public Button addToScreen(Font font) {
        if (button == null) {
            button = Button.builder(Component.literal(currentText()), b -> open = !open)
                .bounds(x, y, width, buttonHeight).build();
            addWidget.accept(button);
        }
        return button;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if (button != null) {
            button.setX(x);
            button.setY(y);
        }
    }

    public void dispose() {
        if (button != null) {
            removeWidget.accept(button);
            button = null;
        }
        open = false;
    }

    public boolean isOpen() {
        return open;
    }

    public void close() {
        open = false;
    }

    private String currentText() {
        if (title != null) {
            return title;
        }
        return current >= 0 && current < options.size() ? options.get(current) : "";
    }

    private void select(int index) {
        open = false;
        if (index < 0 || index >= options.size()) {
            return;
        }
        current = index;
        if (button != null && title == null) {
            button.setMessage(Component.literal(currentText()));
        }
        onSelect.accept(index);
    }

    private boolean insidePanel(double mouseX, double mouseY) {
        int top = y + buttonHeight + PANEL_GAP;
        return mouseX >= x && mouseX <= x + width
            && mouseY >= top && mouseY <= top + options.size() * ROW_H;
    }

    /** 绘制选项面板（宿主在全部控件之后调用；面板在按钮下方展开）。 */
    public void paintPanel(GuiGraphics graphics, Font font, double mouseX, double mouseY) {
        if (!open || options.isEmpty()) {
            return;
        }
        int top = y + buttonHeight + PANEL_GAP;
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, TypeSelector.PANEL_Z);
        try {
            DropdownUtil.drawPanel(graphics, new Rect(x - PANEL_MARGIN, top - PANEL_MARGIN,
                width + PANEL_MARGIN * 2, options.size() * ROW_H + PANEL_MARGIN * 2));
            for (int i = 0; i < options.size(); i++) {
                int rowY = top + i * ROW_H;
                boolean hover = mouseX >= x && mouseX <= x + width && mouseY >= rowY && mouseY < rowY + ROW_H;
                if (hover) {
                    graphics.fill(x, rowY, x + width, rowY + ROW_H, ViewerStyle.COLOR_SELECT);
                }
                graphics.drawString(font, options.get(i), x + 2, rowY + (ROW_H - 9) / 2,
                    hover ? 0xFFFFFF : ViewerStyle.COLOR_TYPE);
            }
        } finally {
            graphics.pose().popPose();
        }
    }

    /**
     * 点击处理：面板内 → 选择并消费；展开时点击其他任何位置（含按钮自身）→ 只收起（保证不重复弹出）。
     * 关闭态不消费，按钮弹开交给 vanilla 的 onPress。
     */
    public boolean mouseClicked(double mouseX, double mouseY) {
        if (!open || options.isEmpty()) {
            return false;
        }
        if (insidePanel(mouseX, mouseY)) {
            select((int) ((mouseY - (y + buttonHeight + PANEL_GAP)) / ROW_H));
            return true;
        }
        close();
        return true;
    }
}
