package hungteen.htlib.common.codec.parser;

/**
 * 记录中的一个字段（RECORD 节点的子节点）。
 *
 * <p>携带字段在 JSON 中的键名、字段值 Schema、是否必填、完整路径与默认值。
 * 由 {@link FieldParser} 解析字段级 MapCodec 生成。</p>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:21
 **/
public final class FieldSchema {

    /** JSON 键名。 */
    private final String name;

    /** 字段值对应的 Schema（可能被 OPTIONAL 包装）。 */
    private final DataSchema schema;

    /** 是否必填（可选字段为 false）。 */
    private final boolean required;

    /** 从根到该字段的完整点路径（如 {@code a.b.c}），供编辑器定位。 */
    private final String path;

    /** 默认值信息（缺省时 {@link ValueInfo.Absent}）。 */
    private ValueInfo defaultValue = new ValueInfo.Absent();

    public FieldSchema(String name, DataSchema schema, boolean required, String path) {
        this.name = name;
        this.schema = schema;
        this.required = required;
        this.path = path;
    }

    /** JSON 键名。 */
    public String name() {
        return name;
    }

    /** 字段值 Schema。 */
    public DataSchema schema() {
        return schema;
    }

    /** 是否必填。 */
    public boolean required() {
        return required;
    }

    /** 从根到字段的完整点路径。 */
    public String path() {
        return path;
    }

    /** 默认值信息。 */
    public ValueInfo defaultValue() {
        return defaultValue;
    }

    /** 设置默认值信息。 */
    public void defaultValue(ValueInfo value) {
        this.defaultValue = value;
    }
}
