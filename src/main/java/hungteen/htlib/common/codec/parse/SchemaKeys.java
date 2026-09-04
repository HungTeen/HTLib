package hungteen.htlib.common.codec.parse;

/**
 * Schema JSON 的键名常量（与 {@link SchemaPrinter} 输出约定一致）。
 */
public final class SchemaKeys {

    private SchemaKeys() {
    }

    /** 节点形态（对应 {@link SchemaType} 的 name）。 */
    public static final String TYPE = "type";
    /** 推断的 Java 类型全限定名。 */
    public static final String JAVA_TYPE = "javaType";
    /** 默认值。 */
    public static final String DEFAULT = "default";
    /** 约束描述数组。 */
    public static final String CONSTRAINTS = "constraints";
    /** 注册表引用信息。 */
    public static final String REGISTRY = "registry";
    /** 是否允许标签引用。 */
    public static final String ALLOW_TAG = "allowTag";

    /** RECORD 字段数组。 */
    public static final String FIELDS = "fields";
    /** 字段名。 */
    public static final String NAME = "name";
    /** 字段是否必填。 */
    public static final String REQUIRED = "required";
    /** 字段完整路径。 */
    public static final String PATH = "path";
    /** 字段值 schema。 */
    public static final String SCHEMA = "schema";

    /** LIST / SET / OPTIONAL 的元素，MAP 的值。 */
    public static final String ELEMENT = "element";
    /** MAP 的键。 */
    public static final String KEY = "key";
    /** MAP 的值。 */
    public static final String VALUE = "value";

    /** UNION 分派键名。 */
    public static final String VARIANT_KEY = "variantKey";
    /** UNION 变体数组。 */
    public static final String VARIANTS = "variants";

    /** 枚举值数组。 */
    public static final String ENUM_VALUES = "enumValues";
    /** 枚举序列化名。 */
    public static final String SERIALIZED_NAME = "serializedName";
}
