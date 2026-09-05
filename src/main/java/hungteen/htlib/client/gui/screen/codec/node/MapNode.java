package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hungteen.htlib.client.gui.screen.codec.EditorHost;
import hungteen.htlib.client.gui.widget.codec.EditorWidget;
import hungteen.htlib.client.gui.widget.codec.MapWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * 映射节点：头部增删（删最后一项），键/值各占一行递归编辑。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
public final class MapNode extends EditorFormNode {

    private final List<EditorFormNode> mapKeys = new ArrayList<>();
    private final List<EditorFormNode> mapValues = new ArrayList<>();

    MapNode(JsonObject schema, String label, JsonElement value) {
        super(schema, label, value);
    }

    @Override
    protected void init() {
        JsonObject k = keySchema();
        JsonObject v = valueSchema();
        if (k == null || v == null) {
            return;
        }
        if (value.isJsonObject()) {
            for (var entry : value.getAsJsonObject().entrySet()) {
                mapKeys.add(EditorFormNode.create(k, "", new JsonPrimitive(entry.getKey())));
                mapValues.add(EditorFormNode.create(v, "", entry.getValue()));
            }
        }
    }

    @Override
    public int height() {
        int h = EditorWidget.ROW_HEIGHT;
        for (int i = 0; i < mapKeys.size(); i++) {
            h += mapKeys.get(i).height() + mapValues.get(i).height();
        }
        return h;
    }

    @Override
    protected EditorWidget createWidget(EditorHost host) {
        return new MapWidget(host, this);
    }

    public List<EditorFormNode> keys() {
        return mapKeys;
    }

    public List<EditorFormNode> values() {
        return mapValues;
    }

    public void addEntry() {
        mapKeys.add(EditorFormNode.create(keySchema(), "", new JsonPrimitive("")));
        mapValues.add(EditorFormNode.create(valueSchema(), "", JsonNull.INSTANCE));
    }

    public void removeLastEntry() {
        if (!mapKeys.isEmpty()) {
            mapKeys.remove(mapKeys.size() - 1);
            mapValues.remove(mapValues.size() - 1);
        }
    }

    @Override
    public JsonElement collect() {
        JsonObject obj = new JsonObject();
        for (int i = 0; i < Math.min(mapKeys.size(), mapValues.size()); i++) {
            JsonElement k = mapKeys.get(i).collect();
            JsonElement v = mapValues.get(i).collect();
            if (k.isJsonPrimitive()) {
                obj.add(k.getAsString(), v);
            }
        }
        return obj;
    }

    @Override
    public void load(JsonElement json) {
        mapKeys.clear();
        mapValues.clear();
        JsonObject k = keySchema();
        JsonObject v = valueSchema();
        if (k != null && v != null && json != null && json.isJsonObject()) {
            for (var entry : json.getAsJsonObject().entrySet()) {
                mapKeys.add(EditorFormNode.create(k, "", new JsonPrimitive(entry.getKey())));
                mapValues.add(EditorFormNode.create(v, "", entry.getValue()));
            }
        }
    }
}
