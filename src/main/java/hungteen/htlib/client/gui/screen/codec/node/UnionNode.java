package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import hungteen.htlib.client.gui.screen.codec.ViewerStyle;
import hungteen.htlib.client.gui.screen.codec.EditorHost;
import hungteen.htlib.client.gui.widget.codec.EditorWidget;
import hungteen.htlib.client.gui.widget.codec.UnionWidget;
import hungteen.htlib.common.codec.parse.SchemaKeys;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联合（dispatch）节点：类型下拉 + 当前分支的字段。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
public final class UnionNode extends EditorFormNode {

    private static final String DEFAULT_VARIANT_KEY = "type";

    private List<String> unionNames = List.of();
    private JsonArray variantSchemas = new JsonArray();
    /** 只创建切换到的分支，并保留各自的草稿。 */
    private final Map<Integer, EditorFormNode> unionVariants = new HashMap<>();
    private int unionIndex = 0;

    UnionNode(JsonObject schema, String label, JsonElement value) {
        super(schema, label, value);
    }

    @Override
    protected void init() {
        if (!schema.has(SchemaKeys.VARIANTS)) {
            return;
        }
        variantSchemas = schema.getAsJsonArray(SchemaKeys.VARIANTS);
        // 只读取当前层的名称。用整个 schema 作缓存键会递归计算共享子结构的哈希。
        List<String> names = new ArrayList<>();
        for (JsonElement variant : variantSchemas) {
            JsonObject entry = variant.getAsJsonObject();
            names.add(entry.has(SchemaKeys.NAME) ? entry.get(SchemaKeys.NAME).getAsString() : "分支" + names.size());
        }
        unionNames = List.copyOf(names);
        if (!variantSchemas.isEmpty()) {
            unionIndex = matchUnionVariant(value);
            selectedVariant().load(value);
        }
    }

    @Override
    public int height() {
        EditorFormNode selected = selectedVariant();
        return selected == null ? EditorWidget.ROW_HEIGHT : EditorWidget.ROW_HEIGHT + selected.height();
    }

    @Override
    protected EditorWidget createWidget(EditorHost host) {
        return new UnionWidget(host, this);
    }

    public List<String> unionNames() {
        return unionNames;
    }

    public int variantIndex() {
        return unionIndex;
    }

    public String currentVariantName() {
        return unionIndex >= 0 && unionIndex < unionNames.size() ? unionNames.get(unionIndex) : "";
    }

    public String discriminatorKey() {
        return schema.has(SchemaKeys.VARIANT_KEY)
            ? schema.get(SchemaKeys.VARIANT_KEY).getAsString() : DEFAULT_VARIANT_KEY;
    }

    public EditorFormNode selectedVariant() {
        if (variantSchemas.isEmpty()) {
            return null;
        }
        return unionVariants.computeIfAbsent(unionIndex,
            index -> EditorFormNode.create(variantSchemas.get(index).getAsJsonObject(), "", JsonNull.INSTANCE));
    }

    /** 切换到指定变体，保留之前编辑的分支草稿。 */
    public void selectVariant(int idx) {
        if (idx < 0 || idx >= variantSchemas.size()) {
            return;
        }
        unionIndex = idx;
    }

    @Override
    protected List<EditorFormNode> validationChildren() {
        EditorFormNode selected = selectedVariant();
        return selected == null ? List.of() : List.of(selected);
    }

    @Override
    public JsonElement collect() {
        EditorFormNode selected = selectedVariant();
        if (selected == null) {
            return JsonNull.INSTANCE;
        }
        JsonElement inner = selected.collect();
        // 用 {分派键: 变体名, ...变体字段} 组装，符合 dispatch codec 的 JSON 结构。
        JsonObject obj = new JsonObject();
        obj.addProperty(discriminatorKey(), currentVariantName());
        if (inner.isJsonObject()) {
            for (var e : inner.getAsJsonObject().entrySet()) {
                if (e.getValue() != null && !e.getValue().isJsonNull()) {
                    obj.add(e.getKey(), e.getValue());
                }
            }
        }
        return obj;
    }

    @Override
    public void load(JsonElement json) {
        this.value = json == null ? JsonNull.INSTANCE : json;
        if (!variantSchemas.isEmpty()) {
            unionIndex = matchUnionVariant(value);
            selectedVariant().load(value);
        }
    }

    @Override
    protected void appendTypeInfo(List<Component> lines) {
        if (schema.has(SchemaKeys.VARIANT_KEY)) {
            lines.add(line(I18n.get("htlib.tooltip.variant_key", schema.get(SchemaKeys.VARIANT_KEY).getAsString()),
                ViewerStyle.COLOR_TYPE));
        }
        if (!unionNames.isEmpty()) {
            lines.add(line(I18n.get("htlib.tooltip.types", unionNames.size(), String.join(", ", unionNames)),
                ViewerStyle.COLOR_ENUM));
        }
    }

    /**
     * 探测当前值匹配哪个变体：
     * 1) 若值有分派键且其值与某个变体名相同，匹配该变体；
     * 2) 否则按 schema 选择第一个对象结构，不创建或重置其它分支；
     * 3) 兜底 0。
     */
    private int matchUnionVariant(JsonElement value) {
        if (value != null && value.isJsonObject()) {
            JsonElement typeVal = value.getAsJsonObject().get(discriminatorKey());
            if (typeVal != null && typeVal.isJsonPrimitive()) {
                String name = typeVal.getAsString();
                int idx = unionNames.indexOf(name);
                if (idx >= 0) {
                    return idx;
                }
            }
        }
        for (int i = 0; i < variantSchemas.size(); i++) {
            switch (typeOf(variantSchemas.get(i).getAsJsonObject())) {
                case RECORD, MAP, UNION, EITHER -> { return i; }
                default -> { }
            }
        }
        return 0;
    }
}
