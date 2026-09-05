package hungteen.htlib.client.gui.screen.codec;

import net.minecraft.client.gui.GuiGraphics;

/**
 * 下拉面板的公共绘制：带边框的暗色背景、竖条滚动条、滚动限位。
 */
public final class DropdownUtil {

    /** 绘制带边框的暗色面板。 */
    public static void drawPanel(GuiGraphics graphics, Rect rect) {
        graphics.fill(rect.x(), rect.y(), rect.x() + rect.width(), rect.y() + rect.height(),
            ViewerStyle.COLOR_DROPDOWN_BG);
        graphics.hLine(rect.x(), rect.x() + rect.width(), rect.y(), ViewerStyle.COLOR_DROPDOWN_BORDER);
        graphics.hLine(rect.x(), rect.x() + rect.width(), rect.y() + rect.height(), ViewerStyle.COLOR_DROPDOWN_BORDER);
        graphics.vLine(rect.x(), rect.y(), rect.y() + rect.height(), ViewerStyle.COLOR_DROPDOWN_BORDER);
        graphics.vLine(rect.x() + rect.width(), rect.y(), rect.y() + rect.height(), ViewerStyle.COLOR_DROPDOWN_BORDER);
    }

    /** 滚动偏移限位到 [0, 条目数-可见行]。 */
    public static int clampScroll(int scroll, int itemCount, int visibleRows) {
        return Math.max(0, Math.min(scroll, Math.max(0, itemCount - visibleRows)));
    }

    /**
     * 绘制竖条滚动条（轨道 + 滑块），画在列表按钮之上。
     *
     * @param track        滚动条区域（竖条位置与高度）
     * @param itemCount    条目总数
     * @param visibleRows  一屏可见条目数
     * @param scroll       当前滚动偏移（条目数）
     */
    public static void drawScrollbar(GuiGraphics graphics, Rect track, int itemCount, int visibleRows, int scroll) {
        int maxScroll = Math.max(0, itemCount - visibleRows);
        if (maxScroll <= 0) {
            return;
        }
        graphics.fill(track.x() - 1, track.y(), track.x() + track.width() + 1, track.y() + track.height(),
            ViewerStyle.COLOR_SCROLL_TRACK);
        int thumbH = Math.max(ViewerStyle.ROW_HEIGHT, track.height() * visibleRows / Math.max(1, itemCount));
        int thumbY = track.y() + (int)((long)scroll * (track.height() - thumbH) / maxScroll);
        graphics.fill(track.x(), thumbY, track.x() + track.width(), thumbY + thumbH,
            ViewerStyle.COLOR_SCROLL_THUMB);
    }
}