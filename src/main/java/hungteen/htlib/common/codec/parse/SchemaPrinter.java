package hungteen.htlib.common.codec.parse;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hungteen.htlib.common.codec.parse.schema.DataSchema;
import hungteen.htlib.common.codec.parse.schema.EnumValueSchema;
import hungteen.htlib.common.codec.parse.schema.FieldSchema;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 把 {@link DataSchema} 树输出成可读文本 / JSON 格式。
 *
 * <p>三种输出：</p>
 * <ul>
 *   <li>{@link #print(DataSchema)}：缩进文本，适合聊天框直接展示（字段、类型、必填、默认值、范围、枚举、注册表引用）；</li>
 *   <li>{@link #toJson(DataSchema)}：JSON 结构，适合写文件供外部工具解析（如文档生成、编辑器 schema）。</li>
 *   <li>{@link #toCompactJson(DataSchema)}：网络传输用 JSON，重复子结构以引用表示。</li>
 * </ul>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/10 12:05
 **/
public final class SchemaPrinter {

    // 网络输出复用同一节点，也合并结构相同、仅分支名不同的浅拷贝。
    private final JsonObject definitions = new JsonObject();
    private final Map<DataSchema, String> ids = new IdentityHashMap<>();
    private final Map<String, String> equivalentDefinitions = new HashMap<>();

    private SchemaPrinter() {
    }

    /** 枚举值超过该数量时截断（避免大枚举刷屏）。 */
    private static final int MAX_ENUM_VALUES = 40;

    /** 字段默认值/约束等附加文本的最大长度，超长截断。 */
    private static final int MAX_EXTRA_TEXT = 80;

    // -------------------------------------------------
    // 文本输出
    // -------------------------------------------------

    /** 输出缩进文本（根节点），末尾带换行。 */
    public static String print(DataSchema schema) {
        StringBuilder sb = new StringBuilder();
        appendNode(schema, 0, sb);
        return sb.toString();
    }

    private static void appendNode(DataSchema schema, int depth, StringBuilder sb) {

        if (schema == null) {
            pad(depth, sb).append("null\n");
            return;
        }

        pad(depth, sb).append(schema.type());

        if (schema.javaType() != null) {
            sb.append(" (").append(schema.javaType().getSimpleName()).append(")");
        }

        if (schema.registry() != null) {
            sb.append(" → ").append(schema.registry().registry().location());
            sb.append(schema.registry().allowTag() ? " (+tag)" : " (no-tag)");
        }

        appendDefault(sb, schema.defaultValue());

        if (!schema.constraints().isEmpty()) {
            sb.append(" 约束[");
            List<SchemaConstraint> constraints = schema.constraints().constraints();
            for (int i = 0; i < constraints.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(constraints.get(i));
            }
            sb.append("]");
        }

        sb.append("\n");

        switch (schema.type()) {
            case RECORD -> {
                for (FieldSchema field : schema.fields()) {
                    pad(depth + 1, sb).append(field.name())
                        .append(field.required() ? " (必填)" : " (可选)");
                    appendDefault(sb, field.defaultValue());
                    sb.append("\n");
                    appendNode(field.schema(), depth + 2, sb);
                }
            }
            case LIST, SET, OPTIONAL -> {
                pad(depth + 1, sb).append("元素:\n");
                appendNode(schema.element(), depth + 2, sb);
            }
            case MAP -> {
                pad(depth + 1, sb).append("键:\n");
                appendNode(schema.key(), depth + 2, sb);
                pad(depth + 1, sb).append("值:\n");
                appendNode(schema.element(), depth + 2, sb);
            }
            case ENUM -> {
                List<EnumValueSchema> values = schema.enumValues();
                int shown = Math.min(values.size(), MAX_ENUM_VALUES);
                for (int i = 0; i < shown; i++) {
                    EnumValueSchema value = values.get(i);
                    pad(depth + 1, sb).append(value.serializedName());
                    if (!value.serializedName().equals(value.name())) {
                        sb.append(" (").append(value.name()).append(")");
                    }
                    sb.append("\n");
                }
                if (values.size() > shown) {
                    pad(depth + 1, sb).append("… 共 ").append(values.size()).append(" 个\n");
                }
            }
            case UNION, EITHER -> {
                if (schema.variantKey() != null) {
                    pad(depth + 1, sb).append("分派键: ").append(schema.variantKey()).append("\n");
                }
                if (schema.key() != null) {
                    pad(depth + 1, sb).append("类型值:\n");
                    appendNode(schema.key(), depth + 2, sb);
                }
                List<DataSchema> variants = schema.variants();
                int shown = Math.min(variants.size(), MAX_ENUM_VALUES);
                for (int i = 0; i < shown; i++) {
                    DataSchema variant = variants.get(i);
                    pad(depth + 1, sb).append(variant.variantName() != null ? variant.variantName() : ("分支[" + i + "]"));
                    sb.append(":\n");
                    appendNode(variant, depth + 2, sb);
                }
                if (variants.size() > shown) {
                    pad(depth + 1, sb).append("… 共 ").append(variants.size()).append(" 个分支\n");
                }
            }
            default -> {
            }
        }
    }

    private static StringBuilder pad(int depth, StringBuilder sb) {
        sb.append("  ".repeat(depth));
        return sb;
    }

    private static void appendDefault(StringBuilder sb, ValueInfo info) {
        if (info instanceof ValueInfo.Static staticInfo) {
            sb.append(" =").append(truncate(String.valueOf(staticInfo.value())));
        } else if (info instanceof ValueInfo.Encoded encodedInfo) {
            sb.append(" =").append(truncate(String.valueOf(encodedInfo.value())));
        }
    }

    /** 超长附加文本截断（保留尾部，指示省略）。 */
    private static String truncate(String text) {
        if (text != null && text.length() > MAX_EXTRA_TEXT) {
            return text.substring(0, MAX_EXTRA_TEXT) + "…";
        }
        return text;
    }

    // -------------------------------------------------
    // JSON 输出
    // -------------------------------------------------

    /** 把 schema 树输出成 JSON（根节点）。 */
    public static JsonObject toJson(DataSchema schema) {
        JsonObject root = new JsonObject();
        appendJsonNode(schema, root);
        return root;
    }

    /** 网络格式中每个共享子结构只输出一次。 */
    public static JsonObject toCompactJson(DataSchema schema) {
        SchemaPrinter printer = new SchemaPrinter();
        JsonObject root = new JsonObject();
        printer.appendReference(schema, root);
        root.add("$defs", printer.definitions);
        return root;
    }

    private void appendReference(DataSchema node, JsonObject ref) {
        String id = ids.get(node);
        if (id == null) {
            if (ids.containsKey(node)) {
                throw new IllegalArgumentException("Cyclic schema graph");
            }
            id = "s" + ids.size();
            ids.put(node, null); // 正在生成此定义。
            JsonObject definition = new JsonObject();
            appendJsonNode(node, definition, this::appendReference);
            // 子节点已经是引用，此处只比较当前层的定义。
            String existing = equivalentDefinitions.putIfAbsent(definition.toString(), id);
            if (existing == null) {
                definitions.add(id, definition);
            } else {
                id = existing;
            }
            ids.put(node, id);
        }
        ref.addProperty("$ref", "#/$defs/" + id);
    }

    private static void appendJsonNode(DataSchema schema, JsonObject obj) {
        appendJsonNode(schema, obj, SchemaPrinter::appendJsonNode);
    }

    private static void appendJsonNode(DataSchema schema, JsonObject obj,
                                       BiConsumer<DataSchema, JsonObject> appendChild) {

        if (schema == null) {
            obj.addProperty("type", "null");
            return;
        }

        obj.addProperty("type", schema.type().name());

        if (schema.javaType() != null) {
            obj.addProperty("javaType", schema.javaType().getName());
        }

        if (schema.registry() != null) {
            JsonObject registry = new JsonObject();
            registry.addProperty("registry", schema.registry().registry().location().toString());
            registry.addProperty("allowTag", schema.registry().allowTag());
            obj.add("registry", registry);
        }

        appendJsonDefault(obj, schema.defaultValue());

        if (!schema.constraints().isEmpty()) {
            JsonArray constraints = new JsonArray();
            for (SchemaConstraint constraint : schema.constraints().constraints()) {
                constraints.add(constraint.toString());
            }
            obj.add("constraints", constraints);
        }

        switch (schema.type()) {
            case RECORD -> {
                JsonArray fields = new JsonArray();
                for (FieldSchema field : schema.fields()) {
                    JsonObject fieldObj = new JsonObject();
                    fieldObj.addProperty("name", field.name());
                    fieldObj.addProperty("required", field.required());
                    fieldObj.addProperty("path", field.path());
                    appendJsonDefault(fieldObj, field.defaultValue());
                    JsonObject fieldSchema = new JsonObject();
                    appendChild.accept(field.schema(), fieldSchema);
                    // 默认值与必填标记同时挂到字段 schema 上，客户端节点才能读到
                    appendJsonDefault(fieldSchema, field.defaultValue());
                    fieldSchema.addProperty("required", field.required());
                    fieldObj.add("schema", fieldSchema);
                    fields.add(fieldObj);
                }
                obj.add("fields", fields);
            }
            case LIST, SET, OPTIONAL -> {
                JsonObject element = new JsonObject();
                appendChild.accept(schema.element(), element);
                obj.add("element", element);
            }
            case MAP -> {
                JsonObject key = new JsonObject();
                appendChild.accept(schema.key(), key);
                obj.add("key", key);
                JsonObject value = new JsonObject();
                appendChild.accept(schema.element(), value);
                obj.add("value", value);
            }
            case ENUM -> {
                JsonArray values = new JsonArray();
                for (EnumValueSchema value : schema.enumValues()) {
                    JsonObject valueObj = new JsonObject();
                    valueObj.addProperty("name", value.name());
                    valueObj.addProperty("serializedName", value.serializedName());
                    values.add(valueObj);
                }
                obj.add("enumValues", values);
            }
            case UNION, EITHER -> {
                if (schema.variantKey() != null) {
                    obj.addProperty("variantKey", schema.variantKey());
                }
                if (schema.key() != null) {
                    JsonObject key = new JsonObject();
                    appendChild.accept(schema.key(), key);
                    obj.add("typeKey", key);
                }
                JsonArray variants = new JsonArray();
                for (DataSchema variant : schema.variants()) {
                    JsonObject variantObj = new JsonObject();
                    if (variant.variantName() != null) {
                        variantObj.addProperty("name", variant.variantName());
                    }
                    appendChild.accept(variant, variantObj);
                    variants.add(variantObj);
                }
                obj.add("variants", variants);
            }
            default -> {
            }
        }
    }

    private static void appendJsonDefault(JsonObject obj, ValueInfo info) {
        if (info instanceof ValueInfo.Static staticInfo) {
            obj.addProperty("default", String.valueOf(staticInfo.value()));
        } else if (info instanceof ValueInfo.Encoded encodedInfo) {
            obj.add("default", encodedInfo.value());
        }
    }
}
