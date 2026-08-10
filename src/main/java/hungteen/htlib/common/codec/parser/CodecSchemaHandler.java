package hungteen.htlib.common.codec.parser;

import hungteen.htlib.common.codec.parser.handler.*;

/**
 * Codec Schema 解析器的责任链接口。
 *
 * <p>每种"形态"的 codec 对应一个 handler 实现，各自负责识别并把 codec 转成 {@link DataSchema}。
 * {@link CodecSchemaParser} 按注册顺序逐个询问 {@link #supports}，命中后调用 {@link #parse}。</p>
 *
 * <p>现有实现（见 {@link CodecSchemaParser#CodecSchemaParser()}）：</p>
 * <ul>
 *   <li>{@link RegistryCodecHandler} —— 注册表引用（Holder / HolderSet / Registry）；</li>
 *   <li>{@link RecordCodecHandler} —— 记录（RecordCodecBuilder）；</li>
 *   <li>{@link ListCodecHandler} —— 列表（ListCodec）；</li>
 *   <li>{@link MapCodecHandler} —— 映射（Unbounded / Simple / Dispatched）；</li>
 *   <li>{@link EnumCodecHandler} —— 枚举；</li>
 *   <li>{@link PrimitiveCodecHandler} —— 基本类型（int / string / bool ...）。</li>
 * </ul>
 *
 * <p>顺序有讲究：更"特定"的形态靠前（如注册表、记录），基本类型兜底放最后，
 * 避免把包装结构误判成简单类型。</p>
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/8 22:22
 **/
public interface CodecSchemaHandler {

    /**
     * 是否支持解析该 codec。
     *
     * @param codec 已解包的目标 codec（可能为 null）
     * @return true 表示本 handler 能把它解析成 Schema
     */
    boolean supports(Object codec);

    /**
     * 把 codec 解析成 Schema。
     *
     * <p>只有在 {@link #supports} 返回 true 后才会被调用。解析子结构时通过
     * {@link ParseContext#resolve} 委托给解析器复用同一套流程。</p>
     *
     * @param codec   已解包的目标 codec
     * @param context 共享解析上下文（缓存 / 防环 / 路径 / 委托）
     * @return 解析出的 Schema（不得为 null）
     */
    DataSchema parse(Object codec, ParseContext context);
}
