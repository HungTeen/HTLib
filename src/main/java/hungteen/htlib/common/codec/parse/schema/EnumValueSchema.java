package hungteen.htlib.common.codec.parse.schema;

/**
 * 枚举的单个取值：Java 常量名 + 序列化名。
 *
 * <p>{@code serializedName} 是实际写入 JSON 的名字（来自 {@code getSerializedName()}），
 * 与 {@code name} 通常一致，但可能存在重命名映射（如 1.21 附魔 ID 变更）。</p>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:21
 **/
public record EnumValueSchema(String name, String serializedName) {
}
