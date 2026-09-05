package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import hungteen.htlib.client.gui.widget.codec.EditorHost;
import hungteen.htlib.client.gui.widget.codec.EditorWidget;
import hungteen.htlib.client.gui.widget.codec.OptionalWidget;
import hungteen.htlib.common.codec.parse.SchemaType;

/**
 * 可选节点：按内层形态紧凑展示。
 * <ul>
 *   <li>内层<b>标量</b>（数字/字符串/枚举/布尔/Holder…）：字段名 + 内层控件内联到<b>同一行</b>；</li>
 *   <li>内层<b>结构型</b>（RECORD/LIST/MAP/UNION…）：字段名一行标题 + 折叠按钮 [+]/[-]，展开才渲染内层。</li>
 * </ul>
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
public final class OptionalNode extends EditorFormNode {

    private EditorFormNode optionalInner;

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

    @Override
    public int height() {
        if (optionalInner == null) {
            return EditorWidget.ROW_HEIGHT;
        }
        if (structuralInner()) {
            return EditorWidget.ROW_HEIGHT + optionalInner.height();
        }
        return EditorWidget.ROW_HEIGHT;
    }

    @Override
    protected EditorWidget createWidget(EditorHost host) {
        return new OptionalWidget(host, this);
    }

    public EditorFormNode inner() {
        return optionalInner;
    }

    /** 内层是否结构型（多行、可折叠）。 */
    public boolean structuralInner() {
        JsonObject inner = elementSchema();
        return inner != null && isStructuralType(typeOf(inner));
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

    private static boolean isStructuralType(SchemaType type) {
        return switch (type) {
            case RECORD, LIST, SET, MAP, OPTIONAL, UNION, EITHER -> true;
            default -> false;
        };
    }
}
