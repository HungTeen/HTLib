package hungteen.htlib.common.codec.parse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import hungteen.htlib.common.codec.parse.schema.DataSchema;
import hungteen.htlib.common.codec.parse.schema.FieldSchema;
import hungteen.htlib.common.network.EditorPacketLimits;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class SchemaReferencesTest {
    @BeforeAll
    static void bootstrap() {
        net.minecraft.SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    void legacySchemaIsReturnedUnchanged() {
        JsonObject legacy = json("{\"type\":\"STRING\"}");
        assertSame(legacy, SchemaReferences.expand(legacy));
    }

    @Test
    void compactPreservesLegacyFieldsDefaultsAndVariantNames() {
        DataSchema value = new DataSchema(SchemaType.STRING);
        value.defaultValue(new ValueInfo.Static("base"));
        DataSchema record = new DataSchema(SchemaType.RECORD);
        FieldSchema left = new FieldSchema("left", value, true, "left");
        left.defaultValue(new ValueInfo.Static("first"));
        FieldSchema right = new FieldSchema("right", value, false, "right");
        right.defaultValue(new ValueInfo.Static("second"));
        record.fields().add(left);
        record.fields().add(right);
        DataSchema first = record.copy();
        first.variantName("first");
        DataSchema second = record.copy();
        second.variantName("second");
        DataSchema union = new DataSchema(SchemaType.UNION);
        union.variantKey("kind");
        union.variants().add(first);
        union.variants().add(second);

        JsonObject compact = SchemaPrinter.toCompactJson(union);
        assertEquals(3, compact.getAsJsonObject("$defs").size(), "Variant names must not duplicate identical definitions");
        JsonObject expanded = SchemaReferences.expand(compact);
        assertEquals(SchemaPrinter.toJson(union), expanded);
        assertEquals("first", expanded.getAsJsonArray("variants").get(0).getAsJsonObject().get("name").getAsString());
        assertEquals("second", expanded.getAsJsonArray("variants").get(1).getAsJsonObject().get("name").getAsString());
        assertEquals("base", value.defaultValue() instanceof ValueInfo.Static info ? info.value() : null);
    }

    @Test
    void repeatedSubtreesRemainSharedInsteadOfExpandingExponentially() {
        DataSchema schema = new DataSchema(SchemaType.STRING);
        for (int depth = 0; depth < 30; depth++) {
            DataSchema parent = new DataSchema(SchemaType.RECORD);
            parent.fields().add(new FieldSchema("left", schema, true, "left"));
            parent.fields().add(new FieldSchema("right", schema, true, "right"));
            schema = parent;
        }
        JsonObject compact = SchemaPrinter.toCompactJson(schema);
        assertEquals(31, compact.getAsJsonObject("$defs").size());
        assertTrue(compact.toString().length() < 12000);
        JsonObject expanded = SchemaReferences.expand(compact);
        JsonObject left = expanded.getAsJsonArray("fields").get(0).getAsJsonObject().getAsJsonObject("schema");
        JsonObject right = expanded.getAsJsonArray("fields").get(1).getAsJsonObject().getAsJsonObject("schema");
        assertSame(left.get("fields"), right.get("fields"), "Reference overlays must share their child graph");
    }

    @Test
    void defaultsContainingRefAreDataNotSchemaReferences() {
        JsonObject compact = json("""
            {"$ref":"#/$defs/root","$defs":{"root":{"type":"STRING",
              "default":{"$ref":"external:data","$defs":{"data":1}}}}}
            """);
        JsonObject expanded = SchemaReferences.expand(compact);
        assertSame(compact.getAsJsonObject("$defs").getAsJsonObject("root").get("default"), expanded.get("default"));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "{\"$ref\":\"#/$defs/missing\",\"$defs\":{}}",
        "{\"$ref\":\"https://example.invalid/schema\",\"$defs\":{}}",
        "{\"$ref\":4,\"$defs\":{}}",
        "{\"$ref\":\"#/$defs/a\",\"$defs\":{\"a\":{\"$ref\":\"#/$defs/a\"}}}",
        "{\"$ref\":\"#/$defs/a\",\"$defs\":{\"a\":{\"$ref\":\"#/$defs/b\"},\"b\":{\"$ref\":\"#/$defs/a\"}}}",
        "{\"$ref\":\"#/$defs/a\",\"$defs\":{\"a\":{\"type\":\"LIST\",\"element\":{\"$ref\":\"#/$defs/a\"}}}}",
        "{\"$ref\":\"#/$defs/a\",\"$defs\":{\"a\":{\"$ref\":\"#/$defs/b\",\"type\":\"INT\"},\"b\":{\"type\":\"STRING\"}}}",
        "{\"$ref\":\"#/$defs/a\",\"$defs\":{\"a\":3}}",
        "{\"$ref\":\"#/$defs/a\",\"$defs\":{\"a\":{\"type\":\"LIST\",\"element\":3}}}"
    })
    void malformedOrCyclicReferencesAreRejected(String input) {
        assertThrows(IllegalArgumentException.class, () -> SchemaReferences.expand(json(input)));
    }

    @Test
    void serializerRejectsCyclicSchemas() {
        DataSchema cycle = new DataSchema(SchemaType.LIST);
        cycle.element(cycle);
        assertThrows(IllegalArgumentException.class, () -> SchemaPrinter.toCompactJson(cycle));
    }

    @Test
    void configuredFeatureFitsTheCompactNetworkBudget() {
        JsonObject compact = SchemaPrinter.toCompactJson(CodecSchemaParser.parse(ConfiguredFeature.DIRECT_CODEC));
        String encoded = compact.toString();
        int bytes = encoded.getBytes(StandardCharsets.UTF_8).length;
        System.out.println("ConfiguredFeature compact: chars=" + encoded.length() + ", UTF8=" + bytes
            + ", definitions=" + compact.getAsJsonObject("$defs").size());
        assertTrue(EditorPacketLimits.fitsSchema(encoded), "ConfiguredFeature compact UTF-8 bytes: " + bytes);
        JsonObject expanded = SchemaReferences.expand(compact);
        assertEquals("UNION", expanded.get("type").getAsString());
        JsonObject tree = expanded.getAsJsonArray("variants").asList().stream()
            .map(value -> value.getAsJsonObject()).filter(value -> value.get("name").getAsString().equals("minecraft:tree"))
            .findFirst().orElseThrow();
        assertEquals("RECORD", tree.get("type").getAsString());
        assertEquals("config", tree.getAsJsonArray("fields").get(0).getAsJsonObject().get("name").getAsString());
    }

    private static JsonObject json(String value) {
        return JsonParser.parseString(value).getAsJsonObject();
    }
}
