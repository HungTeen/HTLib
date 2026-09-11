package hungteen.htlib.common.codec.parse;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.google.gson.JsonElement;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hungteen.htlib.common.codec.parse.schema.DataSchema;
import hungteen.htlib.common.codec.parse.schema.EnumValueSchema;
import hungteen.htlib.common.codec.parse.schema.FieldSchema;
import hungteen.htlib.common.codec.parse.schema.RegistrySchema;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.StringRepresentable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * CodecSchemaParser 各形态解析的单测：验证每种 Codec 都能解析出正确的 SchemaType 与结构信息。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/6 23:00
 */
class CodecSchemaParserTest {

    @BeforeAll
    static void bootstrap() {
        // ResourceLocation / 注册表相关类需要先完成原版 bootstrap
        net.minecraft.SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    private static DataSchema parse(Codec<?> codec) {
        return CodecSchemaParser.parse(codec);
    }

    private static String toJson(Codec<?> codec) {
        return SchemaPrinter.toJson(parse(codec)).toString();
    }

    private static FieldSchema findField(DataSchema schema, String name) {
        return schema.fields().stream().filter(f -> f.name().equals(name)).findFirst().orElse(null);
    }

    // -------------------------------------------------
    // RECORD（含 fields / required / optional 默认值）
    // -------------------------------------------------

    public record DemoRecord(int count, String name, boolean flag) {
        public static final Codec<DemoRecord> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("count").forGetter(DemoRecord::count),
            Codec.STRING.optionalFieldOf("name", "demo").forGetter(DemoRecord::name),
            Codec.BOOL.fieldOf("flag").forGetter(DemoRecord::flag)
        ).apply(instance, DemoRecord::new));
    }

    @Test
    void parseRecord() {
        DataSchema schema = parse(DemoRecord.CODEC);
        assertEquals(SchemaType.RECORD, schema.type());
        List<FieldSchema> fields = schema.fields();
        assertEquals(3, fields.size());

        FieldSchema count = findField(schema, "count");
        assertNotNull(count, "字段 count 应存在");
        assertEquals(SchemaType.INT, count.schema().type());
        assertTrue(count.required(), "fieldOf 应为必填");

        FieldSchema name = findField(schema, "name");
        assertNotNull(name, "字段 name 应存在");
        assertFalse(name.required(), "optionalFieldOf 应为可选");
        assertEquals(SchemaType.OPTIONAL, name.schema().type());
        assertEquals(SchemaType.STRING, name.schema().element().type());
        assertNotNull(name.defaultValue(), "optionalFieldOf 带默认值应记录 Static 默认");

        FieldSchema flag = findField(schema, "flag");
        assertNotNull(flag, "字段 flag 应存在");
        assertTrue(flag.required(), "fieldOf 应为必填");

        String json = toJson(DemoRecord.CODEC);
        assertTrue(json.contains("\"default\":\"demo\""), "默认值应出现在字段 schema 里：" + json);
    }

    // -------------------------------------------------
    // LIST / SET
    // -------------------------------------------------

    @Test
    void parseList() {
        DataSchema schema = parse(Codec.INT.listOf());
        assertEquals(SchemaType.LIST, schema.type());
        assertNotNull(schema.element());
        assertEquals(SchemaType.INT, schema.element().type());
    }

    @Test
    void parseSet() {
        DataSchema schema = parse(Codec.STRING.listOf());
        assertEquals(SchemaType.LIST, schema.type());
        assertEquals(SchemaType.STRING, schema.element().type());
    }

    // -------------------------------------------------
    // MAP
    // -------------------------------------------------

    @Test
    void parseMap() {
        DataSchema schema = parse(Codec.unboundedMap(Codec.STRING, Codec.INT));
        assertEquals(SchemaType.MAP, schema.type());
        assertEquals(SchemaType.STRING, schema.key().type());
        assertEquals(SchemaType.INT, schema.element().type());
    }

    // -------------------------------------------------
    // ENUM
    // -------------------------------------------------

    public enum DemoType implements StringRepresentable {
        ONE, TWO, THREE;

        @Override
        public String getSerializedName() {
            return name().toLowerCase();
        }

        public static final Codec<DemoType> CODEC = StringRepresentable.fromEnum(DemoType::values);
    }

    @Test
    void parseEnum() {
        DataSchema schema = parse(DemoType.CODEC);
        assertEquals(SchemaType.ENUM, schema.type());
        List<EnumValueSchema> values = schema.enumValues();
        assertEquals(3, values.size());
        assertEquals("one", values.get(0).serializedName());
        assertEquals("two", values.get(1).serializedName());
    }

    // -------------------------------------------------
    // 原语
    // -------------------------------------------------

    @Test
    void parsePrimitives() {
        assertEquals(SchemaType.BOOLEAN, parse(Codec.BOOL).type());
        assertEquals(SchemaType.INT, parse(Codec.INT).type());
        assertEquals(SchemaType.DOUBLE, parse(Codec.DOUBLE).type());
        assertEquals(SchemaType.STRING, parse(Codec.STRING).type());
    }

    // -------------------------------------------------
    // 注册表引用（HOLDER / HOLDER_SET）
    // -------------------------------------------------

    private static ResourceKey<net.minecraft.core.Registry<String>> demoKey() {
        return ResourceKey.createRegistryKey(new ResourceLocation("test", "demo"));
    }

    @Test
    void parseHolder() {
        DataSchema schema = parse(RegistryFileCodec.create(demoKey(), Codec.STRING));
        assertEquals(SchemaType.HOLDER, schema.type());
        RegistrySchema registry = schema.registry();
        assertNotNull(registry);
        assertEquals(new ResourceLocation("test", "demo"), registry.registry().location());
        assertFalse(registry.allowTag(), "HOLDER 不允许标签");
    }

    @Test
    void parseHolderSet() {
        DataSchema schema = parse(RegistryCodecs.homogeneousList(demoKey(), Codec.STRING));
        assertEquals(SchemaType.HOLDER_SET, schema.type());
        RegistrySchema registry = schema.registry();
        assertNotNull(registry);
        assertTrue(registry.allowTag(), "HOLDER_SET 允许标签");
    }

    // -------------------------------------------------
    // UNION（dispatch：键 + 各分支 codec）
    // -------------------------------------------------

    public enum AnimalKind implements StringRepresentable {
        DOG("dog"), CAT("cat");

        private final String name;

        AnimalKind(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return name;
        }

        public static final Codec<AnimalKind> CODEC = StringRepresentable.fromEnum(AnimalKind::values);
    }

    public interface Animal {
        AnimalKind type();

        Codec<Animal> CODEC = AnimalKind.CODEC.dispatch(Animal::type, kind -> switch (kind) {
            case DOG -> Dog.CODEC;
            case CAT -> Cat.CODEC;
        });
    }

    public static final class Dog implements Animal {
        public static final Codec<Dog> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("legs").forGetter(Dog::legs)
        ).apply(instance, Dog::new));
        private final int legs;

        public Dog(int legs) {
            this.legs = legs;
        }

        public int legs() {
            return legs;
        }

        @Override
        public AnimalKind type() {
            return AnimalKind.DOG;
        }
    }

    public static final class Cat implements Animal {
        public static final Codec<Cat> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("grumpy").forGetter(Cat::grumpy)
        ).apply(instance, Cat::new));
        private final boolean grumpy;

        public Cat(boolean grumpy) {
            this.grumpy = grumpy;
        }

        public boolean grumpy() {
            return grumpy;
        }

        @Override
        public AnimalKind type() {
            return AnimalKind.CAT;
        }
    }

    @Test
    void parseDispatch() {
        DataSchema schema = parse(Animal.CODEC);
        assertEquals(SchemaType.UNION, schema.type());
        assertEquals("type", schema.variantKey());
        assertEquals(2, schema.variants().size());
        assertEquals("dog", schema.variants().get(0).variantName());
        assertEquals(SchemaType.RECORD, schema.variants().get(0).type());
        assertEquals(SchemaType.RECORD, schema.variants().get(1).type());
    }

    // -------------------------------------------------
    // 变换包装（xmap 等穿透还原基础形态）
    // -------------------------------------------------

    @Test
    void parseThroughWrapper() {
        DataSchema schema = parse(Codec.DOUBLE.xmap(d -> d * 2, d -> d / 2));
        assertEquals(SchemaType.DOUBLE, schema.type(), "xmap 包装应穿透还原 DOUBLE");
    }

    @Test
    void parseThroughLazy() {
        DataSchema schema = parse(Codec.STRING.listOf().xmap(l -> l, l -> l));
        assertEquals(SchemaType.LIST, schema.type(), "双重包装应穿透还原 LIST");
    }

    // -------------------------------------------------
    // JSON 输出结构
    // -------------------------------------------------

    @Test
    void printJson() {
        JsonElement parsed = com.google.gson.JsonParser.parseString(toJson(DemoRecord.CODEC));
        assertTrue(parsed.isJsonObject(), "根输出应为 JSON 对象");
        String json = parsed.toString();
        assertTrue(json.contains("count") && json.contains("name") && json.contains("flag"),
            "字段名应出现在输出里：" + json);
        assertTrue(json.contains("RECORD"), "根形态应写入 type：" + json);
    }

    // -------------------------------------------------
    // dispatch 单条目与 JsonOps 编解码独立编译性检查
    // -------------------------------------------------

    @Test
    void encodeDecodeRoundTrip() {
        var result = Animal.CODEC.encodeStart(JsonOps.INSTANCE, new Dog(4));
        var decoded = result.flatMap(data -> Animal.CODEC.parse(JsonOps.INSTANCE, data));
        assertTrue(decoded.result().isPresent(), "dispatch codec 应能完成编码-解码往返");
        assertEquals(AnimalKind.DOG, decoded.result().get().type());
    }
}
