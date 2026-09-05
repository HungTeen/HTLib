package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.client.gui.screen.codec.DropdownUtil;
import hungteen.htlib.client.gui.screen.codec.Rect;
import hungteen.htlib.client.gui.screen.codec.ViewerStyle;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;

/**
 * 数据包类型输入框：像聊天栏命令一样，边输入边自动补全，无需点击按钮展开。
 *
 * <p>输入文字 → 下方弹出匹配候选项；鼠标悬停/↑↓ 移动高亮，Enter/Tab 或点击候选项完成选中。
 * 面板背景、滚动由宿主每帧调用 {@link #paintPanel} 绘制在主体裁剪区内。</p>
 */
public final class TypeSelector {

    private static final int ROW_H = 16;
    private static final int PANEL_MARGIN = ViewerStyle.DROPDOWN_PANEL_MARGIN;
    private static final int PANEL_GAP = 2;
    private static final int SCROLL_STEP = 3;

    private final Screen host;
    private final Consumer<AbstractWidget> addWidget;
    private final Consumer<String> onSelect;
    private final List<String> allNames;
    private final int listWidth;
    private int listLeft, listRight;
    private final int height;
    private int listTop;
    private int boxY;

    private EditBox box;
    private boolean ignoreResponder = false;
    private boolean open = false;
    private final List<String> suggestions = new ArrayList<>();
    private int suggestIndex = 0;
    private int scroll = 0;
    private String current = "";

public TypeSelector(Screen host, Consumer<AbstractWidget> addWidget, int x, int y, int width, int height, int listTop,
    List<String> names, Consumer<String> onSelect) {
        this.host = host;
        this.addWidget = addWidget;
        this.onSelect = onSelect;
        this.allNames = List.copyOf(names);
        this.listWidth = width;
        this.listLeft = x;
        this.listRight = x + width;
        this.height = height;
        this.listTop = listTop;
        this.boxY = y;
    }

    /** 创建编辑框并加入屏幕（font 在 render 阶段才可用；幂等）。 */
    public EditBox addToScreen(Font font) {
        if (box == null) {
            box = new EditBox(font, listLeft, boxY, listRight - listLeft, this.height, Component.translatable("htlib.screen.type"));
            box.setMaxLength(128);
            box.setSuggestion(I18n.get("htlib.screen.type_hint"));
            box.setResponder(this::onTextChanged);
        }
        addWidget.accept(box);
        return box;
    }

    /** 重摆输入框与候选面板位置（宿主布局时调用；面板在输入框下方展开，不遮挡输入框）。 */
    public void setPosition(int x, int y) {
        this.listLeft = x;
        this.listRight = x + this.listWidth;
        this.boxY = y;
        this.listTop = y + this.height + PANEL_GAP;
        if (box != null) {
            box.setX(x);
            box.setY(y);
        }
    }

    /** 行不可见时隐藏输入框并收起候选面板。 */
    public void setVisible(boolean visible) {
        if (box != null) {
            box.visible = visible;
        }
        if (!visible) {
            open = false;
            suggestions.clear();
        }
    }

    /** 从宿主移除输入框（控件树销毁时调用；之后可重新 addToScreen 重建）。 */
    public void dispose(Consumer<AbstractWidget> removeWidget) {
        if (box != null) {
            removeWidget.accept(box);
            box = null;
        }
        open = false;
        suggestions.clear();
    }

    /** 聚焦输入框（屏幕刚打开时调用，可直接打字）。 */
    public void focus() {
        if (box != null) {
            box.setFocused(true);
            host.setFocused(box);
        }
    }

    /** 回填当前值（不触发补全）。 */
    public void setValue(String name) {
        current = StringUtils.defaultString(name);
        close();
        if (box != null) {
            ignoreResponder = true;
            box.setValue(current);
            ignoreResponder = false;
            if (StringUtils.isBlank(current)) {
                box.setSuggestion(I18n.get("htlib.screen.type_hint"));
            } else {
                box.setSuggestion(null);
            }
        }
    }

    public boolean isOpen() {
        return open;
    }

    /**
     * 输入变化 → 重算补全候选项。
     */
    private void onTextChanged(String text) {
        if (ignoreResponder) {
            return;
        }
        current = StringUtils.defaultString(text);
        String q = current.trim().toLowerCase(Locale.ROOT);
        suggestions.clear();
        if (q.isEmpty()) {
            open = false;
            box.setSuggestion(I18n.get("htlib.screen.type_hint"));
            return;
        }
        box.setSuggestion(null);
        // 前缀匹配优先，其次包含匹配
        List<String> starts = new ArrayList<>();
        List<String> contains = new ArrayList<>();
        for (String name : allNames) {
            String lower = name.toLowerCase(Locale.ROOT);
            if (lower.startsWith(q)) {
                starts.add(name);
            } else if (lower.contains(q)) {
                contains.add(name);
            }
        }
        suggestions.addAll(starts);
        suggestions.addAll(contains);
        open = !suggestions.isEmpty();
        suggestIndex = suggestions.indexOf(current);
        if (suggestIndex < 0) {
            suggestIndex = 0;
        }
        scroll = 0;
        ensureVisible();
    }

    /** 绘制补全面板（宿主在全部控件之后调用；面板右缘不越过 rightLimit）。 */
    public void paintPanel(GuiGraphics graphics, Font font, double mouseX, double mouseY, int rightLimit) {
        if (!open || suggestions.isEmpty()) {
            return;
        }
        int bottom = host.height - ViewerStyle.BOTTOM_PADDING;
        int maxRows = Math.max(0, (bottom - listTop) / ROW_H);
        if (maxRows <= 0) {
            return;
        }
        int right = Math.min(listRight, rightLimit);
        if (right - listLeft < 20) {
            return;
        }
        scroll = DropdownUtil.clampScroll(scroll, suggestions.size(), maxRows);

        DropdownUtil.drawPanel(graphics, new Rect(listLeft - PANEL_MARGIN, listTop - PANEL_MARGIN,
            (right - listLeft) + PANEL_MARGIN * 2, bottom - listTop + PANEL_MARGIN * 2));

        int end = Math.min(suggestions.size(), scroll + maxRows);
        for (int i = scroll; i < end; i++) {
            int y = listTop + (i - scroll) * ROW_H;
            if (y >= bottom) {
                break;
            }
            // 鼠标悬停跟随高亮
            if (mouseX >= listLeft && mouseX <= right && mouseY >= y && mouseY < y + ROW_H) {
                suggestIndex = i;
            }
            boolean highlight = i == suggestIndex;
            if (highlight) {
                graphics.fill(listLeft, y, right, y + ROW_H, ViewerStyle.COLOR_SELECT);
            }
            graphics.drawString(font, suggestions.get(i), listLeft + 2, y + (ROW_H - 9) / 2,
                highlight ? 0xFFFFFF : ViewerStyle.COLOR_TYPE);
        }

        if (suggestions.size() > maxRows) {
            int trackX = right - ViewerStyle.SCROLLBAR_WIDTH - ViewerStyle.SCROLLBAR_RIGHT_MARGIN;
            DropdownUtil.drawScrollbar(graphics,
                new Rect(trackX, listTop, ViewerStyle.SCROLLBAR_WIDTH, bottom - listTop),
                suggestions.size(), maxRows, scroll);
        }
    }

    /** 点击：命中候选行 → 选中并消费；面板外 → 关闭并把点击放行给底层控件。返回是否消费。 */
    public boolean mouseClicked(double mouseX, double mouseY, int rightLimit) {
        if (!open || suggestions.isEmpty()) {
            return false;
        }
        int right = Math.min(listRight, rightLimit);
        int bottom = host.height - ViewerStyle.BOTTOM_PADDING;
        if (right - listLeft < 20) {
            close();
            return false;
        }
        if (mouseX >= listLeft && mouseX <= right && mouseY >= listTop && mouseY <= bottom) {
            int row = (int) ((mouseY - listTop) / ROW_H);
            if (row >= 0 && row < (bottom - listTop) / ROW_H) {
                selectAt(scroll + row);
            }
            return true;
        }
        close();
        return false;
    }

    /** 滚轮：滚动候选项。返回是否消费。 */
    public boolean mouseScrolled(double delta) {
        if (!open) {
            return false;
        }
        int maxRows = Math.max(0, (host.height - ViewerStyle.BOTTOM_PADDING - listTop) / ROW_H);
        scroll = DropdownUtil.clampScroll(scroll - (int) delta * SCROLL_STEP, suggestions.size(), maxRows);
        return true;
    }

    /** 键盘：↑↓ 移动高亮，Enter/Tab 完成补全。返回是否消费。 */
    public boolean keyPressed(int keyCode) {
        if (!open && !isFocused()) {
            return false;
        }
        if (keyCode == GLFW_KEY_UP) {
            openAllIfClosed();
            if (suggestIndex > 0) {
                suggestIndex--;
            }
            ensureVisible();
            return true;
        }
        if (keyCode == GLFW_KEY_DOWN) {
            openAllIfClosed();
            if (suggestIndex < suggestions.size() - 1) {
                suggestIndex++;
            }
            ensureVisible();
            return true;
        }
        if (keyCode == GLFW_KEY_ENTER || keyCode == GLFW_KEY_KP_ENTER) {
            if (open && suggestIndex >= 0 && suggestIndex < suggestions.size()) {
                selectAt(suggestIndex);
            } else {
                selectExact(current);
            }
            return true;
        }
        if (keyCode == GLFW_KEY_TAB) {
            if (open && suggestIndex >= 0 && suggestIndex < suggestions.size()) {
                selectAt(suggestIndex);
            }
            return true;
        }
        return false;
    }

    /** 空文本时按 ↑↓：列出全部以浏览。 */
    private void openAllIfClosed() {
        if (!open) {
            suggestions.clear();
            suggestions.addAll(allNames);
            open = true;
            suggestIndex = 0;
            scroll = 0;
        }
    }

    private void selectAt(int idx) {
        if (idx < 0 || idx >= suggestions.size()) {
            return;
        }
        String name = suggestions.get(idx);
        close();
        onSelect.accept(name);
        setValue(name);
    }

    private void selectExact(String text) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        for (String name : allNames) {
            if (name.equals(text.trim())) {
                close();
                onSelect.accept(name);
                setValue(name);
                return;
            }
        }
    }

    private void ensureVisible() {
        int maxRows = Math.max(0, (host.height - ViewerStyle.BOTTOM_PADDING - listTop) / ROW_H);
        if (suggestIndex < scroll) {
            scroll = suggestIndex;
        } else if (suggestIndex >= scroll + maxRows) {
            scroll = suggestIndex - maxRows + 1;
        }
        scroll = DropdownUtil.clampScroll(scroll, suggestions.size(), maxRows);
    }

    private void close() {
        open = false;
        suggestions.clear();
        suggestIndex = 0;
        scroll = 0;
    }

    private boolean isFocused() {
        return box != null && box.isFocused();
    }
}