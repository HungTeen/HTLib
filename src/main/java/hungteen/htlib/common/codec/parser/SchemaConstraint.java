package hungteen.htlib.common.codec.parser;

/**
 * Schema 上的取值约束（sealed 接口，所有约束都是不可变 record）。
 *
 * <p>由解析过程从 codec 中尽力提取（数值范围、列表长度等），供编辑器做输入校验：
 * 例如给数值字段配置 min/max、给字符串字段配置正则、给列表配置长度上限。</p>
 *
 * <p>子类型：</p>
 * <ul>
 *   <li>{@link Min} / {@link Max}：单侧边界；</li>
 *   <li>{@link Range}：闭区间 [min, max]；</li>
 *   <li>{@link Length}：字符串/字节长度区间；</li>
 *   <li>{@link Size}：列表/集合元素个数区间；</li>
 *   <li>{@link Pattern}：字符串正则约束。</li>
 * </ul>
 *
 * @author PangTeen
 * @program examplemod-template-26.1
 * @create 2026/8/8 22:19
 **/
public sealed interface SchemaConstraint
    permits SchemaConstraint.Min, SchemaConstraint.Max, SchemaConstraint.Range, SchemaConstraint.Length,
    SchemaConstraint.Size, SchemaConstraint.Pattern {

    /** 数值下限：取值必须 ≥ value。 */
    record Min(Number value) implements SchemaConstraint {
    }

    /** 数值上限：取值必须 ≤ value。 */
    record Max(Number value) implements SchemaConstraint {
    }

    /** 数值闭区间：取值必须在 [min, max] 内。 */
    record Range(Number min, Number max) implements SchemaConstraint {
    }

    /** 长度区间（字符串/字节序列）：长度必须在 [min, max] 内。 */
    record Length(int min, int max) implements SchemaConstraint {
    }

    /** 元素个数区间（列表/集合）：元素数量必须在 [min, max] 内。 */
    record Size(int min, int max) implements SchemaConstraint {
    }

    /** 字符串正则约束：值必须匹配 regex。 */
    record Pattern(String regex) implements SchemaConstraint {
    }
}