package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import hungteen.htlib.client.gui.screen.codec.SchemaTooltip;
import hungteen.htlib.client.gui.screen.codec.ViewerStyle;
import hungteen.htlib.client.gui.screen.codec.EditorHost;
import hungteen.htlib.client.gui.widget.codec.EditorWidget;
import hungteen.htlib.client.gui.widget.codec.EnumWidget;
import hungteen.htlib.common.codec.parse.SchemaKeys;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 枚举节点：选中某个常量。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
public final class EnumNode extends EditorFormNode {

    /** enum 值列表按 schema 共享（同一 schema 的所有枚举下拉共用同一个列表对象）。 */
    private static final Map<JsonObject, List<String>> SHARED_ENUM_VALUES =
        Collections.synchronizedMap(new WeakHashMap<>());

    private List<String> enumValues = List.of();
    private int enumIndex = 0;

    EnumNode(JsonObject schema, String label, JsonElement value) {
        super(schema, label, value);
    }

    @Override
    protected void init() {
        this.enumValues = sharedEnumValues(schema);
        String current = value != null && value.isJsonPrimitive() ? value.getAsString() : "";
        enumIndex = Math.max(0, enumValues.indexOf(current));
    }

    /** 构建并共享枚举值列表（schema 相等即复用，列表不可变）。 */
    private static List<String> sharedEnumValues(JsonObject schema) {
        List<String> cached = SHARED_ENUM_VALUES.get(schema);
        if (cached != null) {
            return cached;
        }
        java.util.List<String> built = new ArrayList<>();
        if (schema.has(SchemaKeys.ENUM_VALUES)) {
            for (JsonElement e : schema.getAsJsonArray(SchemaKeys.ENUM_VALUES)) {
                built.add(e.getAsJsonObject().get(SchemaKeys.SERIALIZED_NAME).getAsString());
            }
        }
        List<String> unmodifiable = List.copyOf(built);
        SHARED_ENUM_VALUES.put(schema, unmodifiable);
        return unmodifiable;
    }

    @Override
    public int height() {
        return EditorWidget.ROW_HEIGHT;
    }

    @Override
    protected EditorWidget createWidget(EditorHost host) {
        return new EnumWidget(host, this);
    }

    public List<String> enumValues() {
        return enumValues;
    }

    public int enumIndex() {
        return enumIndex;
    }

    /** 选中指定常量。 */
    public void setEnumValue(String name) {
        enumIndex = Math.max(0, enumValues.indexOf(name));
    }

    @Override
    public JsonElement collect() {
        return enumIndex >= 0 && enumIndex < enumValues.size()
            ? new com.google.gson.JsonPrimitive(enumValues.get(enumIndex)) : JsonNull.INSTANCE;
    }

    @Override
    public void load(JsonElement json) {
        String current;
        if (json != null && json.isJsonPrimitive()) {
            current = json.getAsString();
        } else {
            // 无值重置时回到 schema 声明的默认枚举名（如有）
            current = SchemaTooltip.defaultText(schema);
        }
        enumIndex = Math.max(0, enumValues.indexOf(current));
    }

    @Override
    protected void appendTypeInfo(List<Component> lines) {
        if (enumValues.isEmpty()) {
            return;
        }
        StringBuilder shown = new StringBuilder();
        int limit = Math.min(enumValues.size(), 20);
        for (int i = 0; i < limit; i++) {
            if (i > 0) {
                shown.append(", ");
            }
            shown.append(enumValues.get(i));
        }
        if (enumValues.size() > limit) {
            shown.append(I18n.get("htlib.tooltip.enum_more", enumValues.size()));
        }
        lines.add(line(I18n.get("htlib.tooltip.enum", shown.toString()), ViewerStyle.COLOR_ENUM));
    }
}
