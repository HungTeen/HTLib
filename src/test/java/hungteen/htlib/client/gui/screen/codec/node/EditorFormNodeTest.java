package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class EditorFormNodeTest {
    @BeforeAll
    static void initializeClientTranslationsWithoutStartingTheGame() throws ReflectiveOperationException {
        var setLanguage = I18n.class.getDeclaredMethod("setLanguage", Language.class);
        setLanguage.setAccessible(true);
        setLanguage.invoke(null, Language.getInstance());
    }

    @Test
    void optionalImportPreservesAbsenceAndExplicitFalseZeroAndEmptyList() {
        EditorFormNode node = EditorFormNode.root(schema("""
            {"type":"RECORD","fields":[
              {"name":"enabled","schema":{"type":"OPTIONAL","element":{"type":"BOOLEAN"}}},
              {"name":"count","schema":{"type":"OPTIONAL","element":{"type":"INT"}}},
              {"name":"items","schema":{"type":"OPTIONAL","element":{"type":"LIST","element":{"type":"STRING"}}}}
            ]}
            """));
        assertEquals(json("{}"), node.collect());
        assertFalse(node.hasErrors());
        JsonElement explicit = json("{\"enabled\":false,\"count\":0,\"items\":[]}");
        node.load(explicit);
        assertEquals(explicit, node.collect());
        node.load(json("{}"));
        assertEquals(json("{}"), node.collect());
    }

    @Test
    void optionalToggleKeepsDraftAndDeclaredDefaultWithoutEmittingItWhenAbsent() {
        OptionalNode node = (OptionalNode) EditorFormNode.create(schema("""
            {"type":"OPTIONAL","default":"9007199254740993","element":{"type":"LONG"}}
            """), "count", null);
        assertTrue(node.isOptionalAndAbsent());
        node.setPresent(true);
        assertEquals("9007199254740993", node.collect().toString());
        node.inner().setValueText("9007199254740995");
        node.setPresent(false);
        assertTrue(node.collect().isJsonNull());
        node.setPresent(true);
        assertEquals("9007199254740995", node.collect().toString());
    }

    @Test
    void integerInputAndDeclaredDefaultsKeepEveryLongDigit() {
        EditorFormNode node = EditorFormNode.root(schema("{\"type\":\"LONG\",\"default\":\"9223372036854775807\"}"));
        assertEquals("9223372036854775807", node.collect().toString());
        node.setValueText("9007199254740993");
        assertEquals("9007199254740993", node.collect().toString());
        node.setValueText("-9223372036854775808");
        assertEquals("-9223372036854775808", node.collect().toString());
        node.load(null);
        assertEquals("9223372036854775807", node.collect().toString());
    }

    @ParameterizedTest
    @CsvSource({"INT,abc", "INT,1.5", "INT,-", "BYTE,256", "SHORT,32768", "INT,2147483648",
        "LONG,9223372036854775808", "DOUBLE,NaN", "FLOAT,Infinity", "DOUBLE,1e400", "FLOAT,1e40"})
    void invalidNumbersRemainEditableAndBlockSubmission(String type, String input) {
        JsonObject schema = new JsonObject();
        schema.addProperty("type", type);
        EditorFormNode node = EditorFormNode.root(schema);
        node.setValueText(input);
        assertTrue(node.hasErrors());
        assertEquals(input, node.valueText());
        assertTrue(node.collect().getAsJsonPrimitive().isString());
        String corrected = type.equals("FLOAT") || type.equals("DOUBLE") ? "1.25" : "1";
        node.setValueText(corrected);
        assertFalse(node.hasErrors());
        assertTrue(node.collect().getAsJsonPrimitive().isNumber());
        assertEquals(corrected, node.collect().toString());
    }

    @Test
    void nestedDispatchRetainsItsRecordFieldNameAndCustomDiscriminator() {
        EditorFormNode node = EditorFormNode.root(schema("""
            {"type":"RECORD","fields":[{"name":"effect","schema":{
              "type":"UNION","variantKey":"kind","variants":[
                {"name":"demo:damage","type":"RECORD","fields":[{"name":"amount","schema":{"type":"INT"}}]}
              ]}}]}
            """));
        JsonElement expected = json("{\"effect\":{\"kind\":\"demo:damage\",\"amount\":7}}");
        node.load(expected);
        assertEquals(expected, node.collect());
    }

    @Test
    void dispatchSwitchKeepsEachBranchesDraft() {
        UnionNode node = (UnionNode) EditorFormNode.root(schema("""
            {"type":"UNION","variantKey":"type","variants":[
              {"name":"demo:first","type":"RECORD","fields":[{"name":"value","schema":{"type":"STRING"}}]},
              {"name":"demo:second","type":"RECORD","fields":[{"name":"other","schema":{"type":"STRING"}}]}
            ]}
            """));
        node.load(json("{\"type\":\"demo:first\",\"value\":\"keep me\"}"));
        node.selectVariant(1);
        ((RecordNode) node.selectedVariant()).children().get(0).setValueText("second draft");
        node.selectVariant(0);
        assertEquals("keep me", node.collect().getAsJsonObject().get("value").getAsString());
        node.selectVariant(1);
        assertEquals("second draft", node.collect().getAsJsonObject().get("other").getAsString());
    }

    @Test
    void mapEditingKeepsSeparateDraftEntriesUntilKeysAreUnique() {
        MapNode node = (MapNode) EditorFormNode.root(schema("""
            {"type":"MAP","key":{"type":"STRING"},"value":{"type":"STRING"}}
            """));
        node.addEntry();
        node.addEntry();
        assertEquals(2, node.keys().size());
        assertTrue(node.hasErrors(), "Duplicate blank keys must not be silently collapsed by save/validate");
        node.keys().get(0).setValueText("first");
        node.keys().get(1).setValueText("second");
        node.values().get(0).setValueText("one");
        node.values().get(1).setValueText("two");
        assertFalse(node.hasErrors());
        assertEquals(json("{\"first\":\"one\",\"second\":\"two\"}"), node.collect());
    }

    @Test
    void invalidNumericDraftSurvivesAndOnlyActiveOptionalFieldsBlockSubmission() {
        OptionalNode node = (OptionalNode) EditorFormNode.create(
            schema("{\"type\":\"OPTIONAL\",\"element\":{\"type\":\"INT\"}}"), "count", null);
        node.setPresent(true);
        node.inner().setValueText("not a number");
        assertEquals("not a number", node.inner().valueText());
        assertTrue(node.hasErrors());
        node.setPresent(false);
        assertFalse(node.hasErrors());
        node.setPresent(true);
        assertEquals("not a number", node.inner().valueText());
        assertTrue(node.hasErrors());
        node.inner().setValueText("4");
        assertFalse(node.hasErrors());
        assertEquals(json("4"), node.collect());
    }

    @Test
    void importedEmptyStringsSurviveTheSameTextRefreshUsedByWidgets() {
        RecordNode node = (RecordNode) EditorFormNode.root(schema("""
            {"type":"RECORD","fields":[
              {"name":"required","schema":{"type":"STRING"}},
              {"name":"optional","schema":{"type":"OPTIONAL","element":{"type":"STRING","default":"fallback"}}}
            ]}
            """));
        JsonElement expected = json("{\"required\":\"\",\"optional\":\"\"}");
        node.load(expected);
        EditorFormNode required = node.children().get(0);
        EditorFormNode optional = ((OptionalNode) node.children().get(1)).inner();
        required.setValueText(required.valueText());
        optional.setValueText(optional.valueText());
        assertEquals(expected, node.collect());
    }

    @Test
    void inlineHolderValidatesDraftsAndCollectsCurrentValuesWithoutRendering() {
        RecordNode root = (RecordNode) EditorFormNode.root(schema("""
            {"type":"RECORD","fields":[{"name":"holder","schema":{"type":"HOLDER"}}]}
            """));
        root.load(json("{\"holder\":{}}"));
        HolderNode holder = (HolderNode) root.children().get(0);
        JsonObject entrySchema = schema("""
            {"type":"MAP","key":{"type":"STRING"},"value":{"type":"STRING"}}
            """);
        MapNode inline = (MapNode) holder.inlineNode(entrySchema);
        inline.addEntry();
        inline.addEntry();
        inline.keys().get(0).setValueText("duplicate");
        inline.keys().get(1).setValueText("duplicate");
        inline.values().get(0).setValueText("one");
        inline.values().get(1).setValueText("two");
        assertTrue(root.hasErrors(), "Inline map duplicates must be rejected before collection overwrites a key");
        EditorFormNode reattached = holder.inlineNode(entrySchema);
        assertSame(inline, reattached, "Widget recreation must reattach the complete draft, not an old JSON snapshot");
        assertEquals(2, ((MapNode) reattached).keys().size());
        assertEquals("one", ((MapNode) reattached).values().get(0).valueText());
        assertEquals("two", ((MapNode) reattached).values().get(1).valueText());
        assertTrue(root.hasErrors());
        inline.keys().get(1).setValueText("second");
        assertFalse(root.hasErrors());
        assertEquals(json("{\"holder\":{\"duplicate\":\"one\",\"second\":\"two\"}}"), root.collect());

        root.load(json("{\"holder\":{\"imported\":\"new value\"}}"));
        assertEquals("imported", inline.keys().get(0).valueText());
        assertEquals(json("{\"holder\":{\"imported\":\"new value\"}}"), root.collect());
        inline.addEntry();
        inline.keys().get(1).setValueText("imported");
        assertTrue(root.hasErrors());
        holder.setValue(json("\"demo:reference\""));
        assertFalse(root.hasErrors(), "An inactive inline draft must not block reference mode");
        assertEquals(json("{\"holder\":\"demo:reference\"}"), root.collect());
    }

    private static JsonObject schema(String text) {
        return json(text).getAsJsonObject();
    }

    private static JsonElement json(String text) {
        return JsonParser.parseString(text);
    }
}
