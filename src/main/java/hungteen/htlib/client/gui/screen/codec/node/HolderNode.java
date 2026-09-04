package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hungteen.htlib.client.gui.screen.codec.CodecEditorScreen;
import hungteen.htlib.common.codec.parse.SchemaKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 注册表引用节点（HOLDER / HOLDER_SET）：用补全输入框选条目（客户端本地枚举，不靠 schema 下发）。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
final class HolderNode extends EditorFormNode {

    /** 可补全的注册表条目名（客户端本地枚举）。 */
    private final List<String> entries = new ArrayList<>();

    HolderNode(JsonObject schema, String label, JsonElement value) {
        super(schema, label, value);
    }

    @Override
    protected void init() {
        if (schema.has(SchemaKeys.REGISTRY)) {
            String name = schema.getAsJsonObject(SchemaKeys.REGISTRY).get(SchemaKeys.REGISTRY).getAsString();
            entries.addAll(clientRegistryEntries(name));
        }
    }

    private String current() {
        return value != null && value.isJsonPrimitive() ? value.getAsString() : "";
    }

    @Override
    public int height() {
        return ROW_HEIGHT;
    }

    @Override
    public int buildControls(CodecEditorScreen screen, int x, int y, int width) {
        int cx = x + LABEL_WIDTH;
        int cw = Math.max(CONTROL_MIN_WIDTH, Math.min(width - LABEL_WIDTH, CONTROL_WIDTH));
        if (screen.formRowVisible(y)) {
            if (entries.isEmpty()) {
                // 枚举不到条目（如 HTLib 自定义注册表）：退回普通输入框，允许手填。
                buildScalarControl(screen, x, y, width);
            } else {
                screen.addSelector(cx, y, cw, ROW_HEIGHT - 2, entries, current(), i -> {
                    if (i >= 0 && i < entries.size()) {
                        value = new JsonPrimitive(entries.get(i));
                    }
                });
            }
        }
        return y + ROW_HEIGHT;
    }

    @Override
    public int collectLabels(CodecEditorScreen screen, int x, int y, int width) {
        int cx = x + LABEL_WIDTH;
        int cw = Math.max(CONTROL_MIN_WIDTH, Math.min(width - LABEL_WIDTH, CONTROL_WIDTH));
        fieldLabel(screen, x, y);
        screen.addTooltipRow(x, y, cx + cw - x, ROW_HEIGHT, tooltipLines());
        return y + ROW_HEIGHT;
    }

    @Override
    public int buildInlineControl(CodecEditorScreen screen, int x, int y, int width) {
        return buildControls(screen, x, y, width);
    }

    @Override
    public JsonElement collect() {
        return value == null ? JsonNull.INSTANCE : value;
    }

    @Override
    public void load(JsonElement json) {
        value = json == null ? JsonNull.INSTANCE : json;
    }

    /** 客户端本地枚举注册表条目：内置注册表走 {@link BuiltInRegistries}，数据驱动走当前关卡 RegistryAccess。 */
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
        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            @SuppressWarnings({"unchecked", "rawtypes"})
            Optional<? extends Registry<?>> opt =
                mc.level.registryAccess().registry((ResourceKey) ResourceKey.createRegistryKey(name));
            if (opt.isPresent()) {
                for (var entry : opt.get().entrySet()) {
                    result.add(entry.getKey().location().toString());
                }
            }
        }
        return result;
    }
}