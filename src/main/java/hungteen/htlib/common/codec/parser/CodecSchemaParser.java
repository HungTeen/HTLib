package hungteen.htlib.common.codec.parser;

import com.mojang.serialization.Codec;
import hungteen.htlib.common.codec.parser.handler.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Codec → Schema 树 的解析入口与编排器。
 *
 * <p>解析流程（对任意 codec 对象）：</p>
 * <ol>
 *   <li>{@link CodecUnwrapper#unwrap} 剥掉外层包装（MapCodecCodec / Recursive 包装），拿到真实结构 codec；</li>
 *   <li>查解析缓存、标记递归路径（防环）；</li>
 *   <li>按注册顺序遍历 {@link CodecSchemaHandler} 责任链，第一个 {@code supports} 命中的负责产出 Schema；
 *       <b>未命中任何 handler</b> 时，用 {@link CodecUnwrapper#scanBaseCodec} 在对象图里找第一个
 *       基础结构 codec（穿透 xmap / flatXmap / validate 等变换包装）递归重新分发；</li>
 *   <li>仍失败 → 产出 {@code UNKNOWN} 节点（仅带原始类名）。</li>
 * </ol>
 *
 * <p>静态入口：{@link #parse(Codec)}。解析期间的子 codec 解析统一经
 * {@link ParseContext#resolve} 回调到本类，保证缓存与防环全程共享。</p>
 *
 * @author PangTeen
 * @program examplemod-template-26.1
 * @create 2026/8/8 22:22
 **/
public final class CodecSchemaParser {

    /** 责任链：越特定的形态越靠前。 */
    private final List<CodecSchemaHandler> handlers = new ArrayList<>();

    public CodecSchemaParser() {

        handlers.add(new RegistryCodecHandler());

        handlers.add(new RecordCodecHandler());

        handlers.add(new ListCodecHandler());

        handlers.add(new MapCodecHandler());

        handlers.add(new EnumCodecHandler());

        handlers.add(new PrimitiveCodecHandler());
    }

    /**
     * 解析单个 codec 为 Schema（核心分发逻辑）。
     *
     * <p>先解包 → 缓存 / 防环 → 责任链分发；责任链未命中时退回"扫描基础结构"再分发。</p>
     *
     * @param codec   任意 codec 对象
     * @param context 共享上下文
     * @return 解析出的 Schema；无法识别时返回 UNKNOWN 节点
     */
    private DataSchema parse(Object codec, ParseContext context) {

        Object unwrapped = CodecUnwrapper.unwrap(codec);

        if (unwrapped == null) {
            return new DataSchema(SchemaType.UNKNOWN);
        }

        DataSchema cached = context.getCached(unwrapped);

        if (cached != null) {
            return cached;
        }

        if (!context.enter(unwrapped)) {
            return new DataSchema(SchemaType.UNKNOWN);
        }

        try {

            for (CodecSchemaHandler handler : handlers) {

                if (handler.supports(unwrapped)) {

                    DataSchema schema = handler.parse(unwrapped, context);

                    context.cache(unwrapped, schema);

                    return schema;
                }
            }

            /*
             * 变换包装（xmap / flatXmap / validate 等）：扫描基础结构 codec 重新分发。
             */
            Object base = CodecUnwrapper.scanBaseCodec(unwrapped);

            if (base != null && base != unwrapped) {

                DataSchema schema = parse(base, context);

                context.cache(unwrapped, schema);

                return schema;
            }

            return unknown(unwrapped);

        } finally {

            context.leave(unwrapped);
        }
    }

    /** 兜底：产出 UNKNOWN 节点，仅记录原始实现类名。 */
    private DataSchema unknown(Object codec) {

        DataSchema schema = new DataSchema(SchemaType.UNKNOWN);

        schema.javaType(codec.getClass());

        return schema;
    }

    /**
     * 静态入口：解析任意 {@code Codec<?>} 为 Schema 树。
     *
     * @param codec 目标 codec
     * @return 根节点 Schema（不会为 null）
     */
    public static DataSchema parse(Codec<?> codec) {

        CodecSchemaParser parser = new CodecSchemaParser();

        ParseContext context = new ParseContext();

        context.resolver(target -> parser.parse(target, context));

        return parser.parse(codec, context);
    }
}
