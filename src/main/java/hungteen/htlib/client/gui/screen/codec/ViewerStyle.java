package hungteen.htlib.client.gui.screen.codec;

/**
 * 查看器的布局尺寸、配色与行为参数集中处。
 *
 * <p>调整界面外观/间距/滚动步长/提示时长时只改这里即可，避免散落的魔法数字。</p>
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
public final class ViewerStyle {

    private ViewerStyle() {
    }

    // ---- 布局 ----
    /** 顶部工具条高度，字段树从这之下开始。 */
    public static final int TOP_OFFSET = 30;
    /** 字段行高。 */
    public static final int ROW_HEIGHT = 14;
    /** 每级缩进宽度。 */
    public static final int INDENT = 12;
    /** 折叠按钮宽/高。 */
    public static final int TOGGLE_WIDTH = 12;
    public static final int TOGGLE_BUTTON_HEIGHT = 12;
    /** 折叠按钮与标签之间的间距。 */
    public static final int TOGGLE_GAP = 3;
    /** 根节点主干参考线相对 indent 的偏移。 */
    public static final int GUIDE_ROOT_X = 4;
    /** UNION 类型循环按钮尺寸。 */
    public static final int TYPE_BUTTON_WIDTH = 140;
    public static final int CYCLE_BUTTON_HEIGHT = 14;
    /** 标签与 UNION 按钮之间的最小间距 / 标签最小宽度。 */
    public static final int TYPE_BUTTON_GAP = 10;
    public static final int MIN_LABEL_WIDTH = 40;
    /** 滚动条宽度与距右边缘的间距。 */
    public static final int SCROLLBAR_WIDTH = 3;
    public static final int SCROLLBAR_RIGHT_MARGIN = 4;
    /** 下拉框行高与行间距。 */
    public static final int FIELD_ROW = 16;
    public static final int FIELD_GAP = 4;
    public static final int DROPDOWN_BOTTOM_PADDING = 10;
    /** 左右留白 / 底部留白 / 状态提示距底部高度。 */
    public static final int LEFT_PADDING = 8;
    public static final int RIGHT_PADDING = 20;
    public static final int BOTTOM_PADDING = 20;
    public static final int STATUS_BOTTOM = 16;
    /** 滚轮滚动步长（像素）。 */
    public static final int SCROLL_STEP = 12;
    /** 顶部下拉框按钮位置与尺寸。 */
    public static final int TYPE_DROPDOWN_Y = 5;
    public static final int TYPE_DROPDOWN_WIDTH = 180;
    public static final int TYPE_DROPDOWN_HEIGHT = 18;
    /** 下拉搜索框高度、与列表的间距、滚轮每格滚动行数。 */
    public static final int DROPDOWN_SEARCH_HEIGHT = 16;
    public static final int DROPDOWN_SEARCH_GAP = 4;
    public static final int DROPDOWN_SCROLL_STEP = 3;
    /** 下拉面板背景相对内容的外扩边距。 */
    public static final int DROPDOWN_PANEL_MARGIN = 2;
    /** 补全面板右缘与屏幕右边缘的保护距离（避开右侧按钮列与滚动条）。 */
    public static final int PANEL_RIGHT_GUARD = 66;

    // ---- 配色 ----
    /** 字段名文字（misode 风格淡蓝）。 */
    public static final int COLOR_TEXT = 0x9CDCFE;
    /** 层级参考线。 */
    public static final int COLOR_GUIDE = 0x505050;
    /** 状态提示。 */
    public static final int COLOR_STATUS = 0xFFFFAA;
    /** 滚动条轨道 / 滑块。 */
    public static final int COLOR_SCROLL_TRACK = 0x30000000;
    public static final int COLOR_SCROLL_THUMB = 0xFFA0A0A0;
    /** 下拉/补全面板背景 / 边框。背景必须不透明，遮住其下方的字段。 */
    public static final int COLOR_DROPDOWN_BG = 0xFF101010;
    public static final int COLOR_DROPDOWN_BORDER = 0xFF6E6E6E;
    /** 补全面板选中行高亮。 */
    public static final int COLOR_SELECT = 0x50FFFFFF;

    // ---- 悬浮提示配色 ----
    /** 类型 / 次要信息。 */
    public static final int COLOR_TYPE = 0xD0D0D0;
    /** 必填 / 可选。 */
    public static final int COLOR_REQUIRED = 0xFF6B6B;
    public static final int COLOR_OPTIONAL = 0x9E9E9E;
    /** 约束范围。 */
    public static final int COLOR_CONSTRAINT = 0xFFD54F;
    /** 默认值。 */
    public static final int COLOR_DEFAULT = 0x7CFC7C;
    /** 注册表引用。 */
    public static final int COLOR_REGISTRY = 0x6ECBFF;
    /** 枚举 / 类型列表。 */
    public static final int COLOR_ENUM = 0xD4A9FF;
    /** 校验失败（编辑器报错文字）。 */
    public static final int COLOR_ERROR = 0xFFFF5A5A;
    /** 校验失败时输入框文字颜色。 */
    public static final int COLOR_ERROR_VALUE = 0xFFFF4040;

    // ---- 行为 ----
    /** 状态提示自动消失时长（毫秒）。 */
    public static final long STATUS_TIMEOUT_MS = 3000L;
    /** 完成类提示（加载/保存）的消失时长（毫秒）。 */
    public static final long INFO_TIMEOUT_MS = 8000L;
}
