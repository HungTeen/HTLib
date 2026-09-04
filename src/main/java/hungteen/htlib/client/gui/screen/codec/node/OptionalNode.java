package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import hungteen.htlib.client.gui.screen.codec.CodecEditorScreen;
import hungteen.htlib.common.codec.parse.SchemaType;

/**
 * 可选节点：不再有"启用/禁用"行，按内层形态紧凑展示。
 * <ul>
 *   <li>内层<b>标量</b>（数字/字符串/枚举/布尔/Holder…）：字段名 + 内层控件内联到<b>同一行</b>；</li>
 *   <li>内层<b>结构型</b>（RECORD/LIST/MAP/UNION…）：字段名一行标题 + 折叠按钮 [+]/[-]，展开才渲染内层。</li>
 * </ul>
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
final class OptionalNode extends EditorFormNode {

    private EditorFormNode optionalInner;
    private boolean expanded = false;

    OptionalNode(JsonObject schema, String label, JsonElement value) {
        super(schema, label, value);
    }

    @Override
    protected void init() {
        JsonObject inner = elementSchema();
        if (inner != null) {
            optionalInner = EditorFormNode.create(inner, "", value);
        }
    }

    /** 内层是否结构型（多行、可折叠）。 */
    private boolean structuralInner() {
        JsonObject inner = elementSchema();
        return inner != null && isStructuralType(typeOf(inner));
    }

    @Override
    public int height() {
        if (optionalInner == null) {
            return ROW_HEIGHT;
        }
        if (structuralInner()) {
            return ROW_HEIGHT + (expanded ? optionalInner.height() : 0);
        }
        return ROW_HEIGHT;
    }

    @Override
    public int buildControls(CodecEditorScreen screen, int x, int y, int width) {
        validateValue();
        if (optionalInner == null) {
            return y;
        }
        if (structuralInner()) {
            if (screen.formRowVisible(y)) {
                screen.addButton(x + LABEL_WIDTH, y, 12, expanded ? "-" : "+", b -> {
                    expanded = !expanded;
                    // 只重排布局（不重建树、不 reload），否则 rebuildForm 会新建节点导致 expanded 重置。
                    screen.rebuild();
                });
                y += ROW_HEIGHT;
            }
            if (expanded) {
                y = optionalInner.buildControls(screen, x + INDENT, y, width - INDENT);
            }
        } else {
            y = optionalInner.buildInlineControl(screen, x, y, width);
        }
        return y;
    }

    @Override
    public int collectLabels(CodecEditorScreen screen, int x, int y, int width) {
        validateValue();
        if (optionalInner == null) {
            return y;
        }
        if (structuralInner()) {
            int cx = x + LABEL_WIDTH;
            int cw = Math.max(CONTROL_MIN_WIDTH, Math.min(width - LABEL_WIDTH, CONTROL_WIDTH));
            fieldLabel(screen, x, y);
            screen.addTooltipRow(x, y, cx + cw - x, ROW_HEIGHT, tooltipLines());
            y += ROW_HEIGHT;
            if (expanded) {
                y = optionalInner.collectLabels(screen, x + INDENT, y, width - INDENT);
            }
        } else {
            y = optionalInner.collectInlineLabels(screen, x, y, width, label);
        }
        return y;
    }

    @Override
    public JsonElement collect() {
        return optionalInner == null ? JsonNull.INSTANCE : optionalInner.collect();
    }

    @Override
    public void load(JsonElement json) {
        this.value = json == null ? JsonNull.INSTANCE : json;
        JsonObject inner = elementSchema();
        optionalInner = inner == null ? null : EditorFormNode.create(inner, "", this.value);
    }

    @Override
    public boolean isOptionalAndAbsent() {
        return false;
    }

    private static boolean isStructuralType(SchemaType type) {
        return switch (type) {
            case RECORD, LIST, SET, MAP, OPTIONAL, UNION, EITHER -> true;
            default -> false;
        };
    }
}