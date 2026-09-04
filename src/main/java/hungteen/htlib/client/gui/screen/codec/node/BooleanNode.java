package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import hungteen.htlib.client.gui.screen.codec.CodecEditorScreen;

/**
 * 布尔节点：单行 true/false 切换按钮。
 *
 */
final class BooleanNode extends EditorFormNode {

    BooleanNode(JsonObject schema, String label, JsonElement value) {
        super(schema, label, value);
    }

    @Override
    protected void init() {
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
            boolean cur = value != null && value.isJsonPrimitive() && value.getAsBoolean();
            screen.addButton(cx, y, cw, String.valueOf(cur), b -> {
                value = new JsonPrimitive(!cur);
                screen.rebuild();
            });
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
        boolean cur = value != null && value.isJsonPrimitive() && value.getAsBoolean();
        return new JsonPrimitive(cur);
    }

    @Override
    public void load(JsonElement json) {
        value = json == null ? JsonNull.INSTANCE : json;
    }
}