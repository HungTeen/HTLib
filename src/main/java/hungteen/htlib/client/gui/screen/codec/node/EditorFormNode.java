package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import hungteen.htlib.client.gui.screen.codec.SchemaTooltip;
import hungteen.htlib.client.gui.screen.codec.ViewerStyle;
import hungteen.htlib.client.gui.screen.codec.EditorHost;
import hungteen.htlib.client.gui.widget.codec.EditorWidget;
import hungteen.htlib.common.codec.parse.SchemaKeys;
import hungteen.htlib.common.codec.parse.SchemaType;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

/**
 * 表单节点基类：纯粹的"数据/状态"层，持有 schema、当前值与校验结果， 通过 {@link #widget(EditorHost)} 与视图层的 {@link EditorWidget} 一一对应。
 *
 * @author PangTeen
 * @program HTLib
 * @create 2026/8/10 14:10
 **/
public abstract class EditorFormNode {

    protected final JsonObject schema;
    protected final String label;
    protected JsonElement value;
    protected boolean invalid = false;
    protected String errorMessage = "";
    /** 是否必填（schema 声明；显示时字段名后带红色星号）。 */
    protected boolean required = false;

    private EditorWidget widget;

    protected EditorFormNode(JsonObject schema, String label, JsonElement value) {
        this.schema = schema;
        this.label = label == null ? "" : label;
        this.value = value == null || value.isJsonNull() ? defaultFor(schema) : value;
        this.required =
            this.schema != null && this.schema.has(SchemaKeys.REQUIRED) && this.schema.get(SchemaKeys.REQUIRED)
                .getAsBoolean();
        if (this.value == null) {
            this.value = JsonNull.INSTANCE;
        }
        // 注意：不能在构造函数里调用虚方法 init()——子类字段此时尚未初始化（多态构造陷阱），
        // 由工厂在构造完成后再调用。
    }

    /** 工厂：按 schema 类型创建对应节点子类（构造完成后调用 init 填充子结构）。 */
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
    // Widget 桥接：每个节点对应一个控件（惰性创建）
    // -------------------------------------------------

    public final boolean hasWidget() {
        return widget != null;
    }

    public final EditorWidget widget(EditorHost host) {
        if (widget == null) {
            widget = createWidget(host);
        }
        return widget;
    }

    /** 创建与本节点配套的控件（每种节点一个 Widget）。 */
    protected abstract EditorWidget createWidget(EditorHost host);

    // -------------------------------------------------
    // 供控件层访问的数据 API
    // -------------------------------------------------

    public String label() {
        return label;
    }

    public SchemaType type() {
        return typeOf(schema);
    }

    public JsonElement value() {
        return value;
    }

    public boolean isInvalid() {
        return invalid;
    }

    public boolean isRequired() {
        return required;
    }

    public String errorMessage() {
        return errorMessage;
    }

    public void setValue(JsonElement v) {
        this.value = v == null ? JsonNull.INSTANCE : v;
        validateValue();
    }

    /** 标量输入框文本 → 节点值（解析 + 校验）。 */
    public void setValueText(String text) {
        this.value = parseInput(text, type());
        validateValue();
    }

    /** 节点值的文本展示形式（字符串不加引号）。 */
    public String valueText() {
        return textOf(value);
    }

    /** 重新校验约束（控件创建/刷新时调用）。 */
    public void validate() {
        validateValue();
    }

    // -------------------------------------------------
    // 数据模型（子类实现）
    // -------------------------------------------------

    /** 从 schema + value 解析子结构（由工厂在构造完成后调用）。 */
    protected abstract void init();

    /** 预估占用的总行高（用于滚动范围）。 */
    public abstract int height();

    /** 从控件树收集 JSON 值。 */
    public abstract JsonElement collect();

    /** 把 JSON 值加载回节点。 */
    public abstract void load(JsonElement json);

    /** 校验当前值是否满足约束；无约束时不变。 */
    protected void validateValue() {
    }

    /** 该节点是否"可选且当前未启用"（RECORD 收集时省略键）。 */
    public boolean isOptionalAndAbsent() {
        return false;
    }

    // -------------------------------------------------
    // 悬浮提示
    // -------------------------------------------------

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

    // -------------------------------------------------
    // schema 工具
    // -------------------------------------------------

    /** schema "type" → {@link SchemaType}（序列化名与枚举名一致）。 */
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

    protected static String textOf(JsonElement e) {
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
        JsonElement declared = declaredDefault(schema);
        if (declared != null) {
            return declared;
        }
        return switch (typeOf(schema)) {
//            case RECORD, MAP -> new JsonObject();
//            case LIST, SET -> new com.google.gson.JsonArray();
            case BOOLEAN -> new com.google.gson.JsonPrimitive(false);
            default -> JsonNull.INSTANCE;
        };
    }

    /** schema 声明的默认值：Encoded 元素直接用（深拷贝），Static 字符串按字段类型还原。 */
    private static JsonElement declaredDefault(JsonObject schema) {
        if (schema == null || !schema.has(SchemaKeys.DEFAULT)) {
            return null;
        }
        JsonElement d = schema.get(SchemaKeys.DEFAULT);
        if (d == null || d.isJsonNull()) {
            return null;
        }
        if (d.isJsonPrimitive() && d.getAsJsonPrimitive().isString()) {
            String text = d.getAsString();
            return switch (typeOf(schema)) {
                case BOOLEAN -> new com.google.gson.JsonPrimitive(Boolean.parseBoolean(text));
                case BYTE -> new com.google.gson.JsonPrimitive((byte)parseDouble(text));
                case SHORT -> new com.google.gson.JsonPrimitive((short)parseDouble(text));
                case INT -> new com.google.gson.JsonPrimitive((int)parseDouble(text));
                case LONG -> new com.google.gson.JsonPrimitive((long)parseDouble(text));
                case FLOAT -> new com.google.gson.JsonPrimitive((float)parseDouble(text));
                case DOUBLE -> new com.google.gson.JsonPrimitive(parseDouble(text));
                case STRING, RESOURCE_LOCATION, COMPONENT, ENUM, HOLDER, HOLDER_SET, REGISTRY, UNKNOWN ->
                    new com.google.gson.JsonPrimitive(text);
                case RECORD, LIST, SET, MAP, OPTIONAL, UNION, EITHER -> parseJsonOrNull(text);
            };
        }
        return d.deepCopy();
    }

    private static JsonElement parseJsonOrNull(String text) {
        try {
            return com.google.gson.JsonParser.parseString(text);
        } catch (Exception e) {
            return null;
        }
    }

    protected static JsonElement parseInput(String text, SchemaType type) {
        if (text == null || text.isEmpty()) {
            // 啥也没有
            return JsonNull.INSTANCE;
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
