package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hungteen.htlib.client.gui.screen.codec.EditorHost;
import hungteen.htlib.client.gui.widget.codec.EditorWidget;
import hungteen.htlib.client.gui.widget.codec.RecordWidget;
import hungteen.htlib.common.codec.parse.SchemaKeys;

import java.util.ArrayList;
import java.util.List;

/**
 * 记录/对象节点：字段集合，逐字段递归编辑。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
public final class RecordNode extends EditorFormNode {

    private final List<EditorFormNode> recordChildren = new ArrayList<>();

    RecordNode(JsonObject schema, String label, JsonElement value) {
        super(schema, label, value);
    }

    @Override
    protected void init() {
        if (!schema.has(SchemaKeys.FIELDS)) {
            return;
        }
        for (JsonElement f : schema.getAsJsonArray(SchemaKeys.FIELDS)) {
            JsonObject field = f.getAsJsonObject();
            String name = field.get(SchemaKeys.NAME).getAsString();
            JsonObject fieldSchema = field.getAsJsonObject(SchemaKeys.SCHEMA);
            // 兼容旧格式：字段级 default 合并进字段 schema，子节点才能读到默认值
            if (!fieldSchema.has(SchemaKeys.DEFAULT) && field.has(SchemaKeys.DEFAULT)) {
                fieldSchema = fieldSchema.deepCopy();
                fieldSchema.add(SchemaKeys.DEFAULT, field.get(SchemaKeys.DEFAULT));
            }
            recordChildren.add(EditorFormNode.create(fieldSchema, name, fieldValue(name)));
        }
    }

    @Override
    public int height() {
        int h = 0;
        for (EditorFormNode c : recordChildren) {
            h += c.height();
        }
        return h;
    }

    @Override
    protected EditorWidget createWidget(EditorHost host) {
        return new RecordWidget(host, this);
    }

    public List<EditorFormNode> children() {
        return recordChildren;
    }

    @Override
    public JsonElement collect() {
        JsonObject obj = new JsonObject();
        for (EditorFormNode child : recordChildren) {
            // 可选枚举且"不使用"：省略该键
            if (child.isOptionalAndAbsent()) {
                continue;
            }
            obj.add(child.label(), child.collect());
        }
        return obj;
    }

    @Override
    public void load(JsonElement json) {
        this.value = json == null || json.isJsonNull() ? defaultFor(schema) : json;
        if (this.value == null) {
            this.value = new JsonObject();
        }
        for (EditorFormNode child : recordChildren) {
            child.load(fieldValue(child.label()));
        }
    }
}
