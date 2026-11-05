package hungteen.htlib.common.codec.parse;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.codec.parse.schema.DataSchema;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class RegistryCodecRecognitionTest {
    private static ResourceKey<net.minecraft.core.Registry<String>> REFERENCE;
    private static Codec<Holder<String>> HOLDER;

    @BeforeAll
    static void bootstrap() {
        net.minecraft.SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        REFERENCE = ResourceKey.createRegistryKey(new ResourceLocation("test", "reference"));
        HOLDER = RegistryFileCodec.create(REFERENCE, Codec.STRING);
    }

    private record ReferenceRecord(Holder<String> reference) {
        private static final Codec<ReferenceRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            HOLDER.fieldOf("reference").forGetter(ReferenceRecord::reference)
        ).apply(instance, ReferenceRecord::new));
    }

    @Test
    void wrappedRecordDoesNotBecomeItsNestedHolder() {
        DataSchema schema = CodecSchemaParser.parse(ReferenceRecord.CODEC.xmap(Function.identity(), Function.identity()));
        assertEquals(SchemaType.RECORD, schema.type());
        assertEquals(SchemaType.HOLDER, schema.fields().get(0).schema().type());
        assertEquals(REFERENCE, schema.fields().get(0).schema().registry().registry());
    }

    @Test
    void wrappedListDoesNotBecomeItsElementHolder() {
        DataSchema schema = CodecSchemaParser.parse(HOLDER.listOf().xmap(Function.identity(), Function.identity()));
        assertEquals(SchemaType.LIST, schema.type());
        assertEquals(SchemaType.HOLDER, schema.element().type());
    }

    @Test
    void wrappedHolderRetainsItsOwnRegistry() {
        DataSchema schema = CodecSchemaParser.parse(HOLDER.xmap(Function.identity(), Function.identity()));
        assertEquals(SchemaType.HOLDER, schema.type());
        assertEquals(REFERENCE, schema.registry().registry());
    }

    @Test
    void byNameCodecUsesCapturedRegistryWithoutInspectingItsEntries() {
        assertEquals(Registries.FEATURE, CodecUnwrapper.registryKey(BuiltInRegistries.FEATURE.byNameCodec()));
        assertEquals(Registries.FEATURE, CodecUnwrapper.registryKey(BuiltInRegistries.FEATURE.holderByNameCodec()));
    }

    @Test
    void unrelatedRegistryCaptureDoesNotChangeDelegateShape() {
        assertEquals(SchemaType.STRING, CodecSchemaParser.parse(capturingRegistry(Codec.STRING)).type());
        assertEquals(SchemaType.RECORD, CodecSchemaParser.parse(capturingRegistry(ReferenceRecord.CODEC)).type());
        assertEquals(SchemaType.LIST, CodecSchemaParser.parse(capturingRegistry(HOLDER.listOf())).type());
        var incidentalCodec = HOLDER;
        Codec<String> string = Codec.STRING.xmap(value -> {
            assertNotNull(incidentalCodec);
            return value;
        }, Function.identity());
        assertEquals(SchemaType.STRING, CodecSchemaParser.parse(string).type());
    }

    private static <T> Codec<T> capturingRegistry(Codec<T> codec) {
        var registry = BuiltInRegistries.FEATURE;
        return codec.xmap(value -> {
            assertNotNull(registry.key());
            return value;
        }, Function.identity());
    }

    @Test
    void holderSetWrappersKeepTagsAndRegistry() {
        var codec = RegistryCodecs.homogeneousList(REFERENCE, Codec.STRING);
        for (Codec<?> wrapped : List.of(codec, codec.xmap(Function.identity(), Function.identity()),
            codec.flatXmap(DataResult::success, DataResult::success), codec.withLifecycle(Lifecycle.experimental()))) {
            DataSchema schema = CodecSchemaParser.parse(wrapped);
            assertEquals(SchemaType.HOLDER_SET, schema.type());
            assertEquals(REFERENCE, schema.registry().registry());
            assertTrue(schema.registry().allowTag());
        }
    }

    @Test
    void fixedRegistryWrappersKeepTheirShape() {
        var codec = RegistryFixedCodec.create(REFERENCE);
        for (Codec<?> wrapped : List.of(codec, codec.xmap(Function.identity(), Function.identity()),
            codec.withLifecycle(Lifecycle.experimental()))) {
            DataSchema schema = CodecSchemaParser.parse(wrapped);
            assertEquals(SchemaType.REGISTRY, schema.type());
            assertEquals(REFERENCE, schema.registry().registry());
        }
    }

    @Test
    void enumWrappersKeepTheirValues() {
        var codec = CodecSchemaParserTest.DemoType.CODEC;
        for (Codec<?> wrapped : List.of(codec.xmap(Function.identity(), Function.identity()),
            codec.withLifecycle(Lifecycle.experimental()))) {
            DataSchema schema = CodecSchemaParser.parse(wrapped);
            assertEquals(SchemaType.ENUM, schema.type());
            assertEquals(List.of("one", "two", "three"),
                schema.enumValues().stream().map(value -> value.serializedName()).toList());
        }
    }

    @Test
    void stringCallbackCapturingEnumValuesRemainsString() {
        var incidentalValues = CodecSchemaParserTest.DemoType.values();
        Codec<String> codec = Codec.STRING.xmap(value -> {
            assertEquals(3, incidentalValues.length);
            return value;
        }, Function.identity());
        assertEquals(SchemaType.STRING, CodecSchemaParser.parse(codec).type());
        assertNull(CodecUnwrapper.enumClassOf(codec));
        assertTrue(CodecUnwrapper.enumValues(codec).isEmpty());
    }

    @Test
    void unsupportedContainersDoNotFallBackToAnInternalHolder() {
        for (Codec<?> codec : List.of(Codec.either(HOLDER, Codec.STRING), Codec.pair(HOLDER, Codec.INT))) {
            assertEquals(SchemaType.UNKNOWN, CodecSchemaParser.parse(codec).type());
        }
    }

    @Test
    void vanillaConfiguredFeatureBranchesAreNotHolderReferences() {
        // No SchemaRegistryAccess is installed: this must also work outside a world.
        DataSchema schema = CodecSchemaParser.parse(ConfiguredFeature.DIRECT_CODEC);
        assertEquals(SchemaType.UNION, schema.type());
        assertEquals(Registries.FEATURE, schema.key().registry().registry());
        for (String name : List.of("minecraft:tree", "minecraft:block_pile", "minecraft:desert_well")) {
            DataSchema branch = schema.variants().stream().filter(value -> name.equals(value.variantName()))
                .findFirst().orElseThrow();
            assertEquals(SchemaType.RECORD, branch.type(), name);
            var config = branch.fields().stream().filter(field -> field.name().equals("config"))
                .findFirst().orElseThrow();
            assertTrue(config.required());
            if (name.equals("minecraft:tree") || name.equals("minecraft:block_pile")) {
                assertEquals(SchemaType.RECORD, config.schema().type(), name);
                String expectedField = name.equals("minecraft:tree") ? "trunk_provider" : "state_provider";
                assertTrue(config.schema().fields().stream().anyMatch(field -> field.name().equals(expectedField)), name);
            }
        }
    }
}
