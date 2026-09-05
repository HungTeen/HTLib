package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hungteen.htlib.client.gui.screen.codec.EditorHost;
import hungteen.htlib.client.gui.widget.codec.BooleanWidget;
import hungteen.htlib.client.gui.widget.codec.EditorWidget;

/**
 * 布尔节点：单行 true/false 切换按钮。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
public final class BooleanNode extends EditorFormNode {

    BooleanNode(JsonObject schema, String label, JsonElement value) {
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
        return new BooleanWidget(host, this);
    }

    /** 切换 true/false。 */
    public void toggle() {
        setValue(new JsonPrimitive(!isCurrentTrue()));
    }

    public boolean isCurrentTrue() {
        return value != null && value.isJsonPrimitive() && value.getAsBoolean();
    }

    @Override
    public JsonElement collect() {
        return new JsonPrimitive(isCurrentTrue());
    }

    @Override
    public void load(JsonElement json) {
        value = json == null || json.isJsonNull() ? defaultFor(schema) : json;
    }
}
