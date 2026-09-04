package hungteen.htlib.common.codec.parse;

import com.google.gson.JsonElement;

/**
 * 字段默认值 / 值来源信息的三种形态（sealed 接口）。
 *
 * <p>记录"字段缺失时的行为"：是完全没有默认值、还是固定常量、还是只能通过编码 JSON 得到。</p>
 *
 * <p>子类型：</p>
 * <ul>
 *   <li>{@link Absent}：字段必填，缺失即失败；</li>
 *   <li>{@link Static}：有固定默认值（如 {@code optionalFieldOf("x", 5)} 的 5）；</li>
 *   <li>{@link Encoded}：通过编码对象（JsonElement）得到默认值（如组件默认值）。</li>
 * </ul>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:20
 **/
public sealed interface ValueInfo permits ValueInfo.Absent, ValueInfo.Static, ValueInfo.Encoded {

    /** 无默认值：字段必须显式提供（必填）。 */
    record Absent() implements ValueInfo {
    }

    /** 固定默认值：缺失时使用该 Java 对象值。 */
    record Static(Object value) implements ValueInfo {
    }

    /** 编码形态的默认值：缺失时使用该 JSON 元素。 */
    record Encoded(JsonElement value) implements ValueInfo {
    }
}
