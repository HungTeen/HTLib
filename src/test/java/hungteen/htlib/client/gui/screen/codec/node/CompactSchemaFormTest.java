package hungteen.htlib.client.gui.screen.codec.node;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hungteen.htlib.common.codec.parse.CodecSchemaParser;
import hungteen.htlib.common.codec.parse.SchemaPrinter;
import hungteen.htlib.common.codec.parse.SchemaReferences;
import net.minecraft.SharedConstants;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.locale.Language;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class CompactSchemaFormTest {
    @BeforeAll
    static void initializeGameTypesAndTranslations() throws ReflectiveOperationException {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        var setLanguage = I18n.class.getDeclaredMethod("setLanguage", Language.class);
        setLanguage.setAccessible(true);
        setLanguage.invoke(null, Language.getInstance());
    }

    @Test
    void openingSharedBranchesDoesNotExpandTheirUnselectedDescendants() {
        JsonObject schema = object("{\"type\":\"RECORD\",\"fields\":[]}");
        for (int depth = 0; depth < 14; depth++) {
            JsonObject union = object("{\"type\":\"UNION\",\"variantKey\":\"type\"}");
            JsonArray variants = new JsonArray();
            variants.add(object("{\"name\":\"leaf\",\"type\":\"RECORD\",\"fields\":[]}"));
            for (String name : new String[] {"left", "right"}) {
                JsonObject branch = object("{\"type\":\"RECORD\"}");
                branch.addProperty("name", name);
                JsonObject field = object("{\"name\":\"child\",\"required\":true}");
                field.add("schema", schema);
                JsonArray fields = new JsonArray();
                fields.add(field);
                branch.add("fields", fields);
                variants.add(branch);
            }
            union.add("variants", variants);
            schema = union;
        }
        JsonObject sharedDag = schema;
        assertTimeout(Duration.ofSeconds(3), () -> {
            UnionNode node = (UnionNode) EditorFormNode.root(sharedDag);
            assertEquals(object("{\"type\":\"leaf\"}"), node.collect());
            node.selectVariant(1);
            assertEquals(object("{\"type\":\"left\",\"child\":{\"type\":\"leaf\"}}"), node.collect());
            assertTrue(node.height() > 0);
            assertFalse(node.hasErrors());
            EditorFormNode draft = node.selectedVariant();
            node.selectVariant(2);
            node.collect();
            node.selectVariant(1);
            assertSame(draft, node.selectedVariant());
        });
    }

    @Test
    void fieldAttributeOverridesKeepSharedSchemaDescendants() {
        JsonObject expanded = SchemaReferences.expand(object("""
            {"$ref":"#/$defs/root","$defs":{
              "root":{"type":"RECORD","fields":[
                {"name":"first","required":true,"schema":{"$ref":"#/$defs/choice"}},
                {"name":"second","schema":{"$ref":"#/$defs/choice"}}]},
              "choice":{"type":"UNION","variants":[{"$ref":"#/$defs/leaf","name":"leaf"}]},
              "leaf":{"type":"RECORD","fields":[]}
            }}
            """));
        RecordNode node = (RecordNode) EditorFormNode.root(expanded);
        assertTrue(node.children().get(0).isRequired());
        assertFalse(node.children().get(1).isRequired());
        assertSame(node.children().get(0).schema.get("variants"), node.children().get(1).schema.get("variants"));
    }

    @Test
    void vanillaConfiguredFeatureTreeCanBeOpenedEditedAndRevisited() {
        String wire = SchemaPrinter.toCompactJson(CodecSchemaParser.parse(ConfiguredFeature.DIRECT_CODEC)).toString();
        JsonObject schema = SchemaReferences.expand(object(wire));
        assertTimeout(Duration.ofSeconds(5), () -> {
            UnionNode node = (UnionNode) EditorFormNode.root(schema);
            int tree = node.unionNames().indexOf("minecraft:tree");
            int blockPile = node.unionNames().indexOf("minecraft:block_pile");
            assertTrue(tree >= 0 && blockPile >= 0);
            node.selectVariant(tree);
            RecordNode config = (RecordNode) ((RecordNode) node.selectedVariant()).children().stream()
                .filter(field -> field.label().equals("config")).findFirst().orElseThrow();
            EditorFormNode forceDirt = config.children().stream()
                .filter(field -> field.label().equals("force_dirt")).findFirst().orElseThrow();
            if (forceDirt instanceof OptionalNode optional) {
                optional.setPresent(true);
                forceDirt = optional.inner();
            }
            forceDirt.setValueText("true");
            node.selectVariant(blockPile);
            assertTrue(node.selectedVariant() instanceof RecordNode);
            node.selectVariant(tree);
            JsonObject output = node.collect().getAsJsonObject();
            assertEquals("minecraft:tree", output.get("type").getAsString());
            assertTrue(output.getAsJsonObject("config").get("force_dirt").getAsBoolean());
        });
    }

    private static JsonObject object(String json) {
        return JsonParser.parseString(json).getAsJsonObject();
    }
}
