package hungteen.htlib.common.codec.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Schema 树节点：描述一个 codec 解析出的数据形态及附加约束。
 *
 * <p>一棵树由多个节点组成，节点间的引用按 {@link SchemaType} 决定含义：</p>
 * <ul>
 *   <li>{@code RECORD}：{@link #fields()} 为字段列表；</li>
 *   <li>{@code LIST} / {@code SET} / {@code OPTIONAL}：{@link #element()} 为元素 Schema；</li>
 *   <li>{@code MAP}：{@link #key()} 为键 Schema、{@link #element()} 为值 Schema；</li>
 *   <li>{@code ENUM}：{@link #enumValues()} 为全部枚举值；</li>
 *   <li>注册表形态：{@link #registry()} 为注册表信息；</li>
 *   <li>其余：{@link #javaType()} 为推断出的 Java 类型，{@link #constraints()} 为取值约束。</li>
 * </ul>
 *
 * <p>解析过程见 {@link CodecSchemaParser}；节点由各 {@link CodecSchemaHandler} 填充。
 * 该对象会被解析缓存共享，因此字段级追加约束时必须先 {@link #copy()} 再修改。</p>
 *
 * @author PangTeen
 * @program examplemod-template-26.1
 * @create 2026/8/8 22:21
 **/
public class DataSchema {

    /** 形态（见 {@link SchemaType}）。 */
    private final SchemaType type;

    /** 推断出的 Java 值类型（尽力而为，可能为 null）。 */
    private Class<?> javaType;

    /** 默认值信息（缺省时 {@link ValueInfo.Absent}）。 */
    private ValueInfo defaultValue = new ValueInfo.Absent();

    /** 取值约束集合。 */
    private ConstraintSet constraints = new ConstraintSet();

    /** 注册表引用信息（仅注册表形态）。 */
    private RegistrySchema registry;

    /** 记录字段（仅 RECORD）。 */
    private final List<FieldSchema> fields = new ArrayList<>();

    /** 元素 Schema（LIST / SET / OPTIONAL 的元素、MAP 的值）。 */
    private DataSchema element;

    /** 键 Schema（仅 MAP）。 */
    private DataSchema key;

    /** 多态变体（UNION / EITHER）。 */
    private final List<DataSchema> variants = new ArrayList<>();

    /** 枚举值列表（仅 ENUM）。 */
    private final List<EnumValueSchema> enumValues = new ArrayList<>();

    /** 以指定形态创建节点。 */
    public DataSchema(SchemaType type) {
        this.type = type;
    }

    /** 节点形态。 */
    public SchemaType type() {
        return type;
    }

    /** 推断出的 Java 值类型。 */
    public Class<?> javaType() {
        return javaType;
    }

    /** 设置推断的 Java 值类型。 */
    public void javaType(Class<?> javaType) {
        this.javaType = javaType;
    }

    /** 默认值信息。 */
    public ValueInfo defaultValue() {
        return defaultValue;
    }

    /** 设置默认值信息。 */
    public void defaultValue(ValueInfo defaultValue) {
        this.defaultValue = defaultValue;
    }

    /** 取值约束集合。 */
    public ConstraintSet constraints() {
        return constraints;
    }

    /** 注册表引用信息（仅注册表形态）。 */
    public RegistrySchema registry() {
        return registry;
    }

    /** 设置注册表引用信息。 */
    public void registry(RegistrySchema registry) {
        this.registry = registry;
    }

    /** 记录字段列表（仅 RECORD）。 */
    public List<FieldSchema> fields() {
        return fields;
    }

    /** 枚举值列表（仅 ENUM）。 */
    public List<EnumValueSchema> enumValues() {
        return enumValues;
    }

    /** 元素 Schema（LIST / SET / OPTIONAL 的元素、MAP 的值）。 */
    public DataSchema element() {
        return element;
    }

    /** 设置元素 Schema。 */
    public void element(DataSchema element) {
        this.element = element;
    }

    /** 键 Schema（仅 MAP）。 */
    public DataSchema key() {
        return key;
    }

    /** 设置键 Schema。 */
    public void key(DataSchema key) {
        this.key = key;
    }

    /** 多态变体列表（UNION / EITHER）。 */
    public List<DataSchema> variants() {
        return variants;
    }

    /**
     * 浅拷贝：用于字段级加约束时避免污染解析缓存里的共享 schema。
     * element / key / fields 等子结构引用原对象。
     */
    public DataSchema copy() {
        DataSchema copy = new DataSchema(type);
        copy.javaType = javaType;
        copy.defaultValue = defaultValue;
        copy.registry = registry;
        copy.element = element;
        copy.key = key;
        copy.fields.addAll(fields);
        copy.variants.addAll(variants);
        copy.enumValues.addAll(enumValues);
        copy.constraints = constraints.copy();
        return copy;
    }
}