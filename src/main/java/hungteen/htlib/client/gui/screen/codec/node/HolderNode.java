package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import hungteen.htlib.client.gui.screen.codec.EditorHost;
import hungteen.htlib.client.gui.widget.codec.EditorWidget;
import hungteen.htlib.client.gui.widget.codec.HolderWidget;
import hungteen.htlib.client.gui.widget.codec.RegistryEntriesCache;
import hungteen.htlib.common.codec.parse.SchemaKeys;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * 注册表引用节点（HOLDER / HOLDER_SET）。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
public final class HolderNode extends EditorFormNode {

    /** 可补全的注册表条目名（客户端本地枚举）。 */
    private List<String> entries = List.of();

    HolderNode(JsonObject schema, String label, JsonElement value) {
        super(schema, label, value);
    }

    @Override
    protected void init() {
        if (schema.has(SchemaKeys.REGISTRY)) {
            String name = schema.getAsJsonObject(SchemaKeys.REGISTRY).get(SchemaKeys.REGISTRY).getAsString();
            // 条目列表走共享缓存（同一注册表只枚举一次，本地结果也写回缓存给所有 Holder 下拉复用）
            List<String> cached = RegistryEntriesCache.entries(name);
            if (!cached.isEmpty()) {
                entries = cached;
            } else {
                List<String> scanned = clientRegistryEntries(name);
                entries = scanned;
                if (!scanned.isEmpty()) {
                    RegistryEntriesCache.update(name, scanned);
                }
            }
        }
    }

    @Override
    public int height() {
        return EditorWidget.ROW_HEIGHT;
    }

    @Override
    protected EditorWidget createWidget(EditorHost host) {
        return new HolderWidget(host, this);
    }

    public void setEntries(List<String> entries) {
        this.entries = entries;
    }

    public List<String> entries() {
        return entries;
    }

    /** schema 声明的注册表名（无注册表信息时为 null）。 */
    public String registryName() {
        return schema.has(SchemaKeys.REGISTRY)
            ? schema.getAsJsonObject(SchemaKeys.REGISTRY).get(SchemaKeys.REGISTRY).getAsString() : null;
    }

    @Override
    public JsonElement collect() {
        return value == null ? JsonNull.INSTANCE : value;
    }

    @Override
    public void load(JsonElement json) {
        value = json == null || json.isJsonNull() ? defaultFor(schema) : json;
    }

    /**
     * 客户端本地枚举注册表条目：内置注册表走 {@link BuiltInRegistries}。
     */
    private static List<String> clientRegistryEntries(String registryName) {
        List<String> result = new ArrayList<>();
        ResourceLocation name = ResourceLocation.tryParse(registryName);
        if (name == null) {
            return result;
        }
        Registry<?> builtin = BuiltInRegistries.REGISTRY.get(name);
        if (builtin != null) {
            for (var entry : builtin.entrySet()) {
                result.add(entry.getKey().location().toString());
            }
            return result;
        }
        return result;
    }
}
