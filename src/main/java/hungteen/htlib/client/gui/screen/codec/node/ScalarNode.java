package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import hungteen.htlib.client.gui.screen.codec.SchemaTooltip;
import hungteen.htlib.client.gui.screen.codec.EditorHost;
import hungteen.htlib.client.gui.widget.codec.EditorWidget;
import hungteen.htlib.client.gui.widget.codec.ScalarWidget;
import hungteen.htlib.common.codec.parse.SchemaKeys;
import net.minecraft.client.resources.language.I18n;

/**
 * 标量节点：STRING/数字/资源定位/组件等，只有一个输入框。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
public final class ScalarNode extends EditorFormNode {

    ScalarNode(JsonObject schema, String label, JsonElement value) {
        super(schema, label, value);
    }

    @Override
    protected void init() {
    }

    @Override
    public int height() {
        return EditorWidget.ROW_HEIGHT;
    }

    @Override
    protected EditorWidget createWidget(EditorHost host) {
        return new ScalarWidget(host, this);
    }

    @Override
    public JsonElement collect() {
        return value == null ? JsonNull.INSTANCE : value;
    }

    @Override
    public void load(JsonElement json) {
        value = json == null || json.isJsonNull() ? defaultFor(schema) : json;
    }

    @Override
    protected void validateValue() {
        invalid = false;
        errorMessage = "";
        if (!schema.has(SchemaKeys.CONSTRAINTS)) {
            return;
        }
        SchemaTooltip.Kind kind;
        String raw = "";
        switch (type()) {
            case BYTE, SHORT, INT, LONG, FLOAT, DOUBLE -> {
                kind = SchemaTooltip.Kind.NUMBER;
                raw = value != null && value.isJsonPrimitive() ? value.getAsString() : "";
            }
            case STRING, RESOURCE_LOCATION, COMPONENT -> {
                kind = SchemaTooltip.Kind.STRING;
                raw = value != null && value.isJsonPrimitive() ? value.getAsString() : "";
            }
            default -> {
                return;
            }
        }
        for (JsonElement c : schema.getAsJsonArray(SchemaKeys.CONSTRAINTS)) {
            String err = SchemaTooltip.checkConstraint(c.getAsString(), kind, raw, 0);
            if (err != null) {
                invalid = true;
                errorMessage = I18n.get("htlib.error.constraint", err);
                return;
            }
        }
    }
}
