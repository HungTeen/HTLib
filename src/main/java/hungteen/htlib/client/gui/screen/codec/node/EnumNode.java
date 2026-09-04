package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hungteen.htlib.client.gui.screen.codec.CodecEditorScreen;
import hungteen.htlib.client.gui.screen.codec.ViewerStyle;
import hungteen.htlib.common.codec.parse.SchemaKeys;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 枚举节点：单行循环按钮，选中某个常量。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:50
 */
final class EnumNode extends EditorFormNode {

    private final List<String> enumValues = new ArrayList<>();
    private int enumIndex = 0;

    EnumNode(JsonObject schema, String label, JsonElement value) {
        super(schema, label, value);
    }

    @Override
    protected void init() {
        if (schema.has(SchemaKeys.ENUM_VALUES)) {
            for (JsonElement e : schema.getAsJsonArray(SchemaKeys.ENUM_VALUES)) {
                enumValues.add(e.getAsJsonObject().get(SchemaKeys.SERIALIZED_NAME).getAsString());
            }
        }
        String current = value != null && value.isJsonPrimitive() ? value.getAsString() : "";
        enumIndex = Math.max(0, enumValues.indexOf(current));
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
            String current = enumIndex >= 0 && enumIndex < enumValues.size() ? enumValues.get(enumIndex) : "";
            screen.addSelector(cx, y, cw,  ROW_HEIGHT - 2, enumValues, current, i -> enumIndex = i);
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
        int cx = x + LABEL_WIDTH;
        int cw = Math.max(CONTROL_MIN_WIDTH, Math.min(width - LABEL_WIDTH, CONTROL_WIDTH));
        if (screen.formRowVisible(y)) {
            String current = enumIndex >= 0 && enumIndex < enumValues.size() ? enumValues.get(enumIndex) : "";
            screen.addSelector(cx, y, cw, ROW_HEIGHT - 2, enumValues, current, i -> enumIndex = i);
        }
        return y + ROW_HEIGHT;
    }

    @Override
    public int enumIndex() {
        return enumIndex;
    }

    @Override
    public JsonElement collect() {
        return enumIndex >= 0 && enumIndex < enumValues.size()
            ? new JsonPrimitive(enumValues.get(enumIndex)) : JsonNull.INSTANCE;
    }

    @Override
    public void load(JsonElement json) {
        String current = json != null && json.isJsonPrimitive() ? json.getAsString() : "";
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