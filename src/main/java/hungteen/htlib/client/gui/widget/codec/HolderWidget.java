package hungteen.htlib.client.gui.widget.codec;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import hungteen.htlib.client.gui.screen.codec.EditorHost;
import hungteen.htlib.client.gui.screen.codec.node.EditorFormNode;
import hungteen.htlib.client.gui.screen.codec.node.HolderNode;
import hungteen.htlib.common.codec.parse.SchemaType;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * 注册表引用控件（HOLDER）：支持两种模式切换。
 * <ul>
 *   <li><b>引用模式</b>：单个 ID 输入框（原版注册表用补全下拉，数据包注册表条目由服务端枚举）；</li>
 *   <li><b>展开模式</b>：加载注册表条目结构的 schema 构建内联子表单
 *   （{@code RegistryFileCodec} 原生支持 ID 与内联对象两种 JSON 形态）。</li>
 * </ul>
 * 模式切换按钮位于字段名之后、输入框之前；按钮只对可内联的 HOLDER 类型显示。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/5 22:50
 */
public final class HolderWidget extends EditorWidget {

    private static final int MODE_BUTTON_WIDTH = 32;
    private static final int MODE_BUTTON_GAP = 3;

    private final HolderNode node;
    private List<String> entries = List.of();
    private String requestedRegistry;
    private String requestedEntrySchema;
    private JsonObject entrySchema;
    private EditorFormNode inlineNode;
    private TypeSelector selector;
    private EditBox box;
    private Button modeButton;

    public HolderWidget(EditorHost host, HolderNode node) {
        super(host, node);
        this.node = node;
    }

    @Override
    protected void create() {
        entries = node.entries();
        String registryName = node.registryName();
        if (entries.isEmpty() && registryName != null) {
            entries = RegistryEntriesCache.entries(registryName);
        }
        if (supportsInline()) {
            acquireEntrySchema();
        }
        buildControl();
        if (registryName != null && entries.isEmpty()) {
            requestedRegistry = registryName;
            RegistryEntriesCache.acquire(registryName, this::onEntries);
        }
    }

    @Override
    protected int doLayout() {
        if (supportsInline() && modeButton != null) {
            place(modeButton, controlX(), y);
            modeButton.setMessage(Component.literal(currentModeText()));
        }
        // 展开模式：行内只有 label + 模式按钮，子表单缩进布局在下方
        if (inlineMode()) {
            if (inlineNode != null) {
                return ROW_HEIGHT + inlineNode.widget(host).layout(x + INDENT, y + ROW_HEIGHT, width - INDENT);
            }
            return ROW_HEIGHT;
        }
        int cx = refControlX();
        if (box != null) {
            box.setWidth(refControlWidth());
            place(box, cx, y);
        } else if (selector != null) {
            selector.setPosition(cx, y);
            selector.setVisible(!hidden && rowVisible(y));
        }
        return ROW_HEIGHT;
    }

    @Override
    protected void doRender(GuiGraphics graphics, Font font, int mouseX, int mouseY) {
        drawLabel(graphics, font, displayLabel(), labelColor());
        if (inlineMode() && inlineNode != null) {
            // 子表单的值是权威，同步回节点（父级 collect 用）
            node.setValue(inlineNode.collect());
            inlineNode.widget(host).render(graphics, font, mouseX, mouseY);
        }
    }

    @Override
    public boolean renderTooltip(GuiGraphics graphics, Font font, double mouseX, double mouseY) {
        if (renderSelfTooltip(graphics, font, mouseX, mouseY)) {
            return true;
        }
        if (inlineMode() && inlineNode != null) {
            return inlineNode.widget(host).renderTooltip(graphics, font, mouseX, mouseY);
        }
        return false;
    }

    @Override
    public void refreshFromNode() {
        if (inlineMode()) {
            if (inlineNode != null) {
                inlineNode.load(node.value());
                inlineNode.widget(host).refreshFromNode();
            }
            return;
        }
        if (box != null) {
            box.setValue(node.valueText());
            applyErrorColor(box);
        } else if (selector != null) {
            selector.setValue(node.valueText());
        }
    }

    @Override
    public void setHidden(boolean hidden) {
        super.setHidden(hidden);
        if (hidden && inlineNode != null && inlineNode.hasWidget()) {
            inlineNode.widget(host).setHidden(true);
        }
    }

    @Override
    protected void onDispose() {
        removeSelector(selector);
        if (requestedRegistry != null) {
            RegistryEntriesCache.release(requestedRegistry, this::onEntries);
        }
        if (requestedEntrySchema != null) {
            EntrySchemaCache.release(requestedEntrySchema, this::onEntrySchema);
        }
        if (inlineNode != null) {
            inlineNode.widget(host).dispose();
        }
    }

    // -------------------------------------------------
    // 引用 / 展开模式
    // -------------------------------------------------

    /** 是否提供展开按钮（仅 HOLDER 且有注册表信息）。 */
    private boolean supportsInline() {
        return node.type() == SchemaType.HOLDER && node.registryName() != null;
    }

    /** 展开模式：值为内联对象。 */
    private boolean inlineMode() {
        return node.value() != null && node.value().isJsonObject();
    }

    private String currentModeText() {
        return I18n.get(inlineMode() ? "htlib.screen.inline" : "htlib.screen.ref");
    }

    /** 切换引用 / 展开：值形态随模式变化，控件树按新模式重建。 */
    private void toggleMode() {
        if (inlineMode()) {
            node.setValue(new JsonPrimitive(""));
        } else {
            node.setValue(new JsonObject());
            acquireEntrySchema();
        }
        dispose();
        host.relayout();
    }

    private void acquireEntrySchema() {
        if (entrySchema != null) {
            return;
        }
        String registryName = node.registryName();
        if (registryName == null) {
            return;
        }
        String cached = EntrySchemaCache.schema(registryName);
        if (cached != null) {
            onEntrySchema(cached);
            return;
        }
        requestedEntrySchema = registryName;
        EntrySchemaCache.acquire(registryName, this::onEntrySchema);
    }

    /** 条目结构 schema 到达：构建内联子表单并重排。 */
    private void onEntrySchema(String schemaJson) {
        if (!isCreated() || entrySchema != null || inlineNode != null) {
            return;
        }
        try {
            entrySchema = JsonParser.parseString(schemaJson).getAsJsonObject();
            inlineNode = EditorFormNode.root(entrySchema);
            inlineNode.load(node.value().isJsonObject() ? node.value() : com.google.gson.JsonNull.INSTANCE);
            host.relayout();
        } catch (Exception ignored) {
        }
    }

    /** 服务端条目到达：把手填输入框升级为补全选择器（仅引用模式）。 */
    private void onEntries(List<String> received) {
        if (!isCreated() || received.isEmpty() || selector != null || inlineMode()) {
            return;
        }
        entries = received;
        if (box != null) {
            removeOwned(box);
            box = null;
        }
        selector = createSelector(refControlWidth(), entries, node.valueText(),
            name -> node.setValue(new JsonPrimitive(name)));
        selector.setValue(node.valueText());
    }

    // -------------------------------------------------
    // 引用模式行控件
    // -------------------------------------------------

    private void buildControl() {
        if (supportsInline()) {
            modeButton = createButton(currentModeText(), MODE_BUTTON_WIDTH, b -> toggleMode());
        }
        if (inlineMode()) {
            return;
        }
        if (!entries.isEmpty()) {
            selector = createSelector(refControlWidth(), entries, node.valueText(),
                name -> node.setValue(new JsonPrimitive(name)));
        } else {
            box = createEditBox(node.valueText(), s -> {
                node.setValueText(s);
                applyErrorColor(box);
            });
            applyErrorColor(box);
        }
    }

    /** 模式按钮占位后的引用控件起始 x。 */
    private int refControlX() {
        return controlX() + (supportsInline() ? MODE_BUTTON_WIDTH + MODE_BUTTON_GAP : 0);
    }

    private int refControlWidth() {
        return Math.max(0, controlWidth() - (supportsInline() ? MODE_BUTTON_WIDTH + MODE_BUTTON_GAP : 0));
    }}
