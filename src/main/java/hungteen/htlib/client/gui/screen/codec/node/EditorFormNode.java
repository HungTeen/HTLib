package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import hungteen.htlib.client.gui.screen.codec.CodecEditorScreen;
import hungteen.htlib.client.gui.screen.codec.SchemaTooltip;
import hungteen.htlib.client.gui.screen.codec.ViewerStyle;
import hungteen.htlib.common.codec.parse.SchemaKeys;
import hungteen.htlib.common.codec.parse.SchemaType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * 表单节点基类：调度工厂 + 公共布局/工具。
 *
 * <p>采用策略/模板方法模式，<b>每种 schema 类型一个子类</b>：{@link RecordNode}、{@link ListSetNode}、
 * {@link MapNode}、{@link OptionalNode}、{@link UnionNode}、{@link EnumNode}、{@link BooleanNode}、
 * {@link ScalarNode}。子类只需实现 init/height/buildControls/collectLabels/collect/load。</p>
 *
 * <p>控件由 {@link #buildControls} 在重建时创建；标签与悬浮提示由 {@link #collectLabels} <b>每帧</b>重建
 * （校验红字/报错即时生效，且不丢输入焦点）。</p>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/10 14:10
 **/
public abstract class EditorFormNode {

    /** 表单行高。 */
    public static final int ROW_HEIGHT = 13;
    static final int LABEL_WIDTH = 60;
    static final int CONTROL_WIDTH = 64;
    static final int CONTROL_MIN_WIDTH = 40;
    /** 结构型子结构缩进。 */
    static final int INDENT = 6;

    protected final JsonObject schema;
    protected final String label;
    protected JsonElement value;
    protected boolean invalid = false;
    protected String errorMessage = "";

    /** 左值编辑框（标量用，load 时回填）。 */
    protected net.minecraft.client.gui.components.EditBox editBox;

    protected EditorFormNode(JsonObject schema, String label, JsonElement value) {
        this.schema = schema;
        this.label = label == null ? "" : label;
        this.value = value == null ? defaultFor(schema) : value;
        if (this.value == null) {
            this.value = JsonNull.INSTANCE;
        }
        // 注意：不能在构造函数里调用虚方法 init()——子类字段此时尚未初始化（多态构造陷阱），
        // 由工厂在构造完成后再调用。
    }

    /**
     * 工厂：按 schema 类型创建对应节点子类（构造完成后调用 init 填充子结构）。
     */
    public static EditorFormNode create(JsonObject schema, String label, JsonElement value) {
        EditorFormNode node = switch (typeOf(schema)) {
            case RECORD -> new RecordNode(schema, label, value);
            case LIST, SET -> new ListSetNode(schema, label, value);
            case MAP -> new MapNode(schema, label, value);
            case OPTIONAL -> new OptionalNode(schema, label, value);
            case UNION, EITHER -> new UnionNode(schema, label, value);
            case ENUM -> new EnumNode(schema, label, value);
            case BOOLEAN -> new BooleanNode(schema, label, value);
            case HOLDER, HOLDER_SET -> new HolderNode(schema, label, value);
            default -> new ScalarNode(schema, label, value);
        };
        node.init();
        return node;
    }

    /** 根节点便捷构造。 */
    public static EditorFormNode root(JsonObject schema) {
        return create(schema, "", JsonNull.INSTANCE);
    }

    // -------------------------------------------------
    // 子类必须实现的模板方法
    // -------------------------------------------------

    /** 从 schema + value 解析子结构。 */
    protected abstract void init();

    /** 预估占用的总行高（用于滚动范围）。 */
    public abstract int height();

    /** 创建控件（仅建控件，不画标签），返回下一个可用 y。 */
    public abstract int buildControls(CodecEditorScreen screen, int x, int y, int width);

    /** 只重建标签与悬浮提示（不建控件），每帧调用；布局推进与 {@link #buildControls} 一致。 */
    public abstract int collectLabels(CodecEditorScreen screen, int x, int y, int width);

    /** 从控件收集 JSON 值。 */
    public abstract JsonElement collect();

    /** 把 JSON 值加载回控件。 */
    public abstract void load(JsonElement json);

    // -------------------------------------------------
    // 子类可选覆写
    // -------------------------------------------------

    /** 校验当前值是否满足约束；无约束时不变。 */
    protected void validateValue() {
    }

    /** 该节点是否"可选且当前未启用"（RECORD 收集时省略键）。 */
    public boolean isOptionalAndAbsent() {
        return false;
    }

    /** 当前枚举下标（可选枚举折叠用）。 */
    public int enumIndex() {
        return 0;
    }

    /** UNION 变体名列表（类型下拉用）。 */
    public List<String> unionNames() {
        return List.of();
    }

    /** 切换到指定 UNION 变体。 */
    public void selectVariant(int idx) {
    }

    /** 用外部行标签渲染单行控件（可选字段内联标量用）；默认编辑框。 */
    public int buildInlineControl(CodecEditorScreen screen, int x, int y, int width) {
        buildScalarControl(screen, x, y, width);
        return y + ROW_HEIGHT;
    }

    /** {@link #buildInlineControl} 的标签/提示版本。 */
    public int collectInlineLabels(CodecEditorScreen screen, int x, int y, int width, String rowLabel) {
        int cx = x + LABEL_WIDTH;
        int cw = Math.max(CONTROL_MIN_WIDTH, Math.min(width - LABEL_WIDTH, CONTROL_WIDTH));
        validateValue();
        fieldLabelText(screen, x, y, rowLabel, fieldLabelColor());
        screen.addTooltipRow(x, y, cx + cw - x, ROW_HEIGHT, tooltipLines());
        return y + ROW_HEIGHT;
    }

    // -------------------------------------------------
    // 公共工具
    // -------------------------------------------------

    /** 单行标量控件（编辑框 + 校验），供标量节点与可选内联共用。 */
    protected void buildScalarControl(CodecEditorScreen screen, int x, int y, int width) {
        validateValue();
        int cx = x + LABEL_WIDTH;
        int cw = Math.max(CONTROL_MIN_WIDTH, Math.min(width - LABEL_WIDTH, CONTROL_WIDTH));
        if (screen.formRowVisible(y)) {
            editBox = screen.addEditBox(cx, y, cw, textOf(value), s -> {
                value = parseInput(s, type());
                validateValue();
                if (editBox != null) {
                    editBox.setTextColor(invalid ? ViewerStyle.COLOR_ERROR_VALUE : 0xFFFFFFFF);
                }
            });
            if (invalid) {
                editBox.setTextColor(ViewerStyle.COLOR_ERROR_VALUE);
            }
        }
    }

    /** 校验失败时文字变红。 */
    protected int fieldLabelColor() {
        return invalid ? ViewerStyle.COLOR_ERROR : ViewerStyle.COLOR_TEXT;
    }

    /** 字段名标签：超宽时省略号截断；默认用本节点 label（空则类型名）。 */
    protected void fieldLabel(CodecEditorScreen screen, int x, int y) {
        fieldLabelText(screen, x, y, label.isEmpty() ? type().name() : label, fieldLabelColor());
    }

    /** 用给定文本绘制字段名；与同行控件文字同一纵线。 */
    protected void fieldLabelText(CodecEditorScreen screen, int x, int y, String text, int color) {
        screen.drawLabelEllipsis(x, y + 1, text, LABEL_WIDTH, color);
    }

    /** 悬浮提示：类型/注册表/约束/默认值 + 子类附加信息 + 校验报错。 */
    public List<Component> tooltipLines() {
        List<Component> lines = new ArrayList<>();
        MutableComponent head = Component.literal(label.isEmpty() ? type().name() : label)
            .withStyle(st -> st.withColor(ViewerStyle.COLOR_TEXT));
        head.append(Component.literal(": " + type().name()).withStyle(st -> st.withColor(ViewerStyle.COLOR_TYPE)));
        if (schema.has(SchemaKeys.JAVA_TYPE)) {
            head.append(Component.literal(" (" + schema.get(SchemaKeys.JAVA_TYPE).getAsString() + ")")
                .withStyle(st -> st.withColor(ViewerStyle.COLOR_TYPE)));
        }
        lines.add(head);

        if (schema.has(SchemaKeys.REGISTRY)) {
            JsonObject reg = schema.getAsJsonObject(SchemaKeys.REGISTRY);
            String t = I18n.get("htlib.tooltip.registry", reg.get(SchemaKeys.REGISTRY).getAsString());
            if (reg.has(SchemaKeys.ALLOW_TAG) && reg.get(SchemaKeys.ALLOW_TAG).getAsBoolean()) {
                t += I18n.get("htlib.tooltip.tag");
            }
            lines.add(line(t, ViewerStyle.COLOR_REGISTRY));
        }

        String cc = SchemaTooltip.formatConstraints(schema);
        if (!cc.isEmpty()) {
            lines.add(line(I18n.get("htlib.tooltip.constraints", cc), ViewerStyle.COLOR_CONSTRAINT));
        }
        String def = SchemaTooltip.defaultText(schema);
        if (!def.isEmpty()) {
            lines.add(line(I18n.get("htlib.tooltip.default", def), ViewerStyle.COLOR_DEFAULT));
        }

        appendTypeInfo(lines);

        if (invalid) {
            lines.add(line(errorMessage, ViewerStyle.COLOR_ERROR));
        }
        return lines;
    }

    /** 子类向悬浮提示追加类型专属行。 */
    protected void appendTypeInfo(List<Component> lines) {
    }

    protected static Component line(String text, int color) {
        return Component.literal(text).withStyle(style -> style.withColor(color));
    }

    /** schema "type" → {@link SchemaType}（序列化名与枚举名一致）。 */
    protected SchemaType type() {
        return typeOf(schema);
    }

    protected static SchemaType typeOf(JsonObject s) {
        if (s == null || !s.has(SchemaKeys.TYPE)) {
            return SchemaType.UNKNOWN;
        }
        try {
            return SchemaType.valueOf(s.get(SchemaKeys.TYPE).getAsString());
        } catch (IllegalArgumentException e) {
            return SchemaType.UNKNOWN;
        }
    }

    protected JsonObject elementSchema() {
        return schema.has(SchemaKeys.ELEMENT) ? schema.getAsJsonObject(SchemaKeys.ELEMENT) : null;
    }

    protected JsonObject keySchema() {
        return schema.has(SchemaKeys.KEY) ? schema.getAsJsonObject(SchemaKeys.KEY) : null;
    }

    protected JsonObject valueSchema() {
        return schema.has(SchemaKeys.VALUE) ? schema.getAsJsonObject(SchemaKeys.VALUE) : null;
    }

    protected JsonElement fieldValue(String name) {
        if (value != null && value.isJsonObject() && value.getAsJsonObject().has(name)) {
            return value.getAsJsonObject().get(name);
        }
        return null;
    }

    protected String textOf(JsonElement e) {
        if (e == null || e.isJsonNull()) {
            return "";
        }
        // 字符串不加引号：JsonPrimitive.toString() 会带引号，回填输入框后再解析会造成引号翻倍
        if (e.isJsonPrimitive() && e.getAsJsonPrimitive().isString()) {
            return e.getAsString();
        }
        return e.toString();
    }

    protected static JsonElement defaultFor(JsonObject schema) {
        return switch (typeOf(schema)) {
            case RECORD -> new JsonObject();
            case LIST, SET -> new com.google.gson.JsonArray();
            case MAP -> new JsonObject();
            case OPTIONAL -> JsonNull.INSTANCE;
            case BOOLEAN -> new com.google.gson.JsonPrimitive(false);
            case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE -> new com.google.gson.JsonPrimitive(0);
            default -> new com.google.gson.JsonPrimitive("");
        };
    }

    protected static JsonElement parseInput(String text, SchemaType type) {
        if (text == null || text.isEmpty()) {
            return switch (type) {
                case STRING, RESOURCE_LOCATION, COMPONENT, UNKNOWN, HOLDER, HOLDER_SET, REGISTRY ->
                    new com.google.gson.JsonPrimitive("");
                default -> JsonNull.INSTANCE;
            };
        }
        return switch (type) {
            case INT -> new com.google.gson.JsonPrimitive((int)parseDouble(text));
            case LONG -> new com.google.gson.JsonPrimitive((long)parseDouble(text));
            case FLOAT -> new com.google.gson.JsonPrimitive((float)parseDouble(text));
            case DOUBLE -> new com.google.gson.JsonPrimitive(parseDouble(text));
            case BYTE -> new com.google.gson.JsonPrimitive((byte)parseDouble(text));
            case SHORT -> new com.google.gson.JsonPrimitive((short)parseDouble(text));
            default -> new com.google.gson.JsonPrimitive(text);
        };
    }

    protected static double parseDouble(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}