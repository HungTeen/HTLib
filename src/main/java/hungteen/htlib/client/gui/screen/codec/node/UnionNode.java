package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hungteen.htlib.client.gui.screen.codec.ViewerStyle;
import hungteen.htlib.client.gui.widget.codec.EditorHost;
import hungteen.htlib.client.gui.widget.codec.EditorWidget;
import hungteen.htlib.client.gui.widget.codec.UnionWidget;
import hungteen.htlib.common.codec.parse.SchemaKeys;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 联合（dispatch）节点：类型下拉 + 当前分支的字段。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
public final class UnionNode extends EditorFormNode {

    private static final String DEFAULT_VARIANT_KEY = "type";

    private final List<String> unionNames = new ArrayList<>();
    private final List<EditorFormNode> unionVariants = new ArrayList<>();
    private int unionIndex = 0;

    UnionNode(JsonObject schema, String label, JsonElement value) {
        super(schema, DEFAULT_VARIANT_KEY, value);
    }

    @Override
    protected void init() {
        if (!schema.has(SchemaKeys.VARIANTS)) {
            return;
        }
        JsonArray variants = schema.getAsJsonArray(SchemaKeys.VARIANTS);
        for (JsonElement v : variants) {
            JsonObject vo = v.getAsJsonObject();
            unionNames.add(vo.has(SchemaKeys.NAME) ? vo.get(SchemaKeys.NAME).getAsString()
                : ("分支" + unionNames.size()));
            unionVariants.add(EditorFormNode.create(vo, "", JsonNull.INSTANCE));
        }
        if (!unionVariants.isEmpty()) {
            unionIndex = matchUnionVariant(value);
            unionVariants.get(unionIndex).load(value);
        }
    }

    @Override
    public int height() {
        return unionVariants.isEmpty() ? EditorWidget.ROW_HEIGHT
            : EditorWidget.ROW_HEIGHT + unionVariants.get(unionIndex).height();
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

    public EditorFormNode selectedVariant() {
        return unionVariants.isEmpty() ? null : unionVariants.get(unionIndex);
    }

    /** 切换到指定变体（重置该变体的值）。 */
    public void selectVariant(int idx) {
        if (idx < 0 || idx >= unionVariants.size()) {
            return;
        }
        unionIndex = idx;
        unionVariants.get(idx).load(JsonNull.INSTANCE);
    }

    @Override
    public JsonElement collect() {
        if (unionVariants.isEmpty()) {
            return JsonNull.INSTANCE;
        }
        JsonElement inner = unionVariants.get(unionIndex).collect();
        String variantKey = schema.has(SchemaKeys.VARIANT_KEY)
            ? schema.get(SchemaKeys.VARIANT_KEY).getAsString() : DEFAULT_VARIANT_KEY;
        String variantName = unionIndex < unionNames.size() ? unionNames.get(unionIndex) : "";
        // 用 {分派键: 变体名, ...变体字段} 组装，符合 dispatch codec 的 JSON 结构。
        JsonObject obj = new JsonObject();
        obj.add(variantKey, new JsonPrimitive(variantName));
        if (inner.isJsonObject()) {
            for (var e : inner.getAsJsonObject().entrySet()) {
                obj.add(e.getKey(), e.getValue());
            }
        }
        return obj;
    }

    @Override
    public void load(JsonElement json) {
        this.value = json == null ? JsonNull.INSTANCE : json;
        if (!unionVariants.isEmpty()) {
            unionIndex = matchUnionVariant(value);
            unionVariants.get(unionIndex).load(value);
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
     * 2) 否则匹配第一个能解析出对象的变体；
     * 3) 兜底 0。
     */
    private int matchUnionVariant(JsonElement value) {
        if (value != null && value.isJsonObject() && schema.has(SchemaKeys.VARIANT_KEY)) {
            JsonElement typeVal = value.getAsJsonObject().get(schema.get(SchemaKeys.VARIANT_KEY).getAsString());
            if (typeVal != null && typeVal.isJsonPrimitive()) {
                String name = typeVal.getAsString();
                int idx = unionNames.indexOf(name);
                if (idx >= 0) {
                    return idx;
                }
            }
        }
        for (int i = 0; i < unionVariants.size(); i++) {
            unionVariants.get(i).load(value);
            if (unionVariants.get(i).collect().isJsonObject()) {
                return i;
            }
        }
        return 0;
    }
}
