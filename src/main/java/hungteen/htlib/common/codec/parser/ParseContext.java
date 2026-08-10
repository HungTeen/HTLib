package hungteen.htlib.common.codec.parser;

import java.util.*;
import java.util.function.Function;

/**
 * 一次解析的共享上下文：贯穿整个 handler 链。
 *
 * <p>职责：</p>
 * <ul>
 *   <li>schema 缓存 —— 同一 codec 对象只解析一次（{@link #cache}/{@link #getCached}）；</li>
 *   <li>递归防环 —— 用身份集合标记当前递归路径上的对象（{@link #enter}/{@link #leave}），
 *       只防"当前路径成环"，不做全局去重（同一 codec 可能被多处引用）；</li>
 *   <li>字段路径 —— 记录当前从根到所在节点的点路径（{@link #push}/{@link #pop}/{@link #path}），
 *       用于生成字段的完整定位路径；</li>
 *   <li>子解析委托 —— 由 {@link CodecSchemaParser} 注入的回调（{@link #resolver}/{@link #resolve}），
 *       handler 解析子 codec 时调用它复用同一套解析流程。</li>
 * </ul>
 *
 * @author PangTeen
 * @program examplemod-template-26.1
 * @create 2026/8/8 22:22
 **/
public final class ParseContext {

    /** codec 对象 → 已解析 Schema 的缓存（身份比较，同一实例共享）。 */
    private final IdentityHashMap<Object, DataSchema> cache = new IdentityHashMap<>();

    /** 当前字段路径栈（每段为字段名）。 */
    private final Deque<String> path = new ArrayDeque<>();

    /** 正在递归解析的对象（防循环引用）。 */
    private final Set<Object> active = Collections.newSetFromMap(new IdentityHashMap<>());

    /** 子 codec 的解析委托（由 CodecSchemaParser 注入）。 */
    private Function<Object, DataSchema> resolver;

    /** 取缓存中的已解析 Schema，未解析过返回 null。 */
    public DataSchema getCached(Object object) {
        return cache.get(object);
    }

    /** 缓存一个对象的解析结果。 */
    public void cache(Object object, DataSchema schema) {
        cache.put(object, schema);
    }

    /**
     * 标记进入该对象。
     *
     * @return false 表示该对象已在这条递归路径上（成环），应停止深入。
     */
    public boolean enter(Object object) {
        return active.add(object);
    }

    /** 标记离开该对象（从递归路径移除）。 */
    public void leave(Object object) {
        active.remove(object);
    }

    /** 进入一层路径（字段名入栈）。 */
    public void push(String name) {
        path.addLast(name);
    }

    /** 离开一层路径（字段名出栈）。 */
    public void pop() {
        path.removeLast();
    }

    /** 当前点路径（用 . 连接，空栈返回空串）。 */
    public String path() {
        return String.join(".", path);
    }

    /** 注入子 codec 解析委托。 */
    public void resolver(Function<Object, DataSchema> resolver) {
        this.resolver = resolver;
    }

    /** 委托解析子 codec（未注入时返回 null）。 */
    public DataSchema resolve(Object codec) {
        return resolver == null ? null : resolver.apply(codec);
    }
}
