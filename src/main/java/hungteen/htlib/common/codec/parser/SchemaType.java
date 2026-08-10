package hungteen.htlib.common.codec.parser;

/**
 * Schema 的"形态"判别枚举。
 *
 * <p>描述 {@link DataSchema} 在数据上呈现的结构种类，由解析出的 codec 形态决定。
 * 它只区分"长什么样"，具体值类型、范围等附加信息存于 DataSchema 的其他字段。</p>
 *
 * <p>取值分组：</p>
 * <ul>
 *   <li>标量：{@link #BOOLEAN}、{@link #BYTE}、{@link #SHORT}、{@link #INT}、{@link #LONG}、
 *       {@link #FLOAT}、{@link #DOUBLE}、{@link #STRING}、{@link #RESOURCE_LOCATION}、{@link #COMPONENT}；</li>
 *   <li>结构：{@link #RECORD}（对象/记录）、{@link #LIST}、{@link #SET}、{@link #MAP}；</li>
 *   <li>可选包装：{@link #OPTIONAL}（元素可缺省，内部持有一个元素 Schema）；</li>
 *   <li>注册表：{@link #HOLDER}（单个注册表引用）、{@link #HOLDER_SET}（标签/集合）、{@link #REGISTRY}；</li>
 *   <li>多态：{@link #ENUM}、{@link #UNION}（按键分派的多选一）、{@link #EITHER}（左/右二选一）；</li>
 *   <li>兜底：{@link #UNKNOWN}（无法识别结构）。</li>
 * </ul>
 *
 * @author PangTeen
 * @program examplemod-template-26.1
 * @create 2026/8/8 22:19
 **/
public enum SchemaType {

    /** 布尔值（true / false）。 */
    BOOLEAN,

    /** 8 位有符号整数。 */
    BYTE,

    /** 16 位有符号整数。 */
    SHORT,

    /** 32 位有符号整数。 */
    INT,

    /** 64 位有符号整数。 */
    LONG,

    /** 32 位浮点数。 */
    FLOAT,

    /** 64 位浮点数。 */
    DOUBLE,

    /** 字符串。 */
    STRING,

    /** 命名空间限定的资源定位符（如 minecraft:stone）。 */
    RESOURCE_LOCATION,

    /** 枚举：值取自固定常量集合。 */
    ENUM,

    /** 文本组件（Minecraft 聊天/显示文本结构）。 */
    COMPONENT,

    /** 记录/对象：字段集合，字段见 {@link DataSchema#fields()}。 */
    RECORD,

    /** 列表：元素见 {@link DataSchema#element()}。 */
    LIST,

    /** 集合：元素见 {@link DataSchema#element()}。 */
    SET,

    /** 映射：键见 {@link DataSchema#key()}，值见 {@link DataSchema#element()}。 */
    MAP,

    /** 可选包装：内部元素见 {@link DataSchema#element()}，缺失时取默认值。 */
    OPTIONAL,

    /** 单个注册表条目引用（Holder，如"某个附魔"）。 */
    HOLDER,

    /** 注册表条目集合（HolderSet，可含标签）。 */
    HOLDER_SET,

    /** 整个注册表本身（如"所有附魔"）。 */
    REGISTRY,

    /** 联合：按分派键选择某一子结构（DispatchedMapCodec）。 */
    UNION,

    /** 二选一：Left / Right 两种结构之一。 */
    EITHER,

    /** 无法识别的结构。 */
    UNKNOWN
}
