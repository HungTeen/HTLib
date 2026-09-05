package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.network.RequestEntrySchemaPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 注册表"条目结构 schema"的客户端缓存：Holder 字段展开模式（内联定义）需要条目 codec 的字段结构，
 * 通过服务端枚举（{@link #acquire}）获取，结果缓存并回调监听者。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/6 21:50
 */
public final class EntrySchemaCache {

    private static final Map<String, String> SCHEMAS = new ConcurrentHashMap<>();
    private static final Map<String, List<Consumer<String>>> LISTENERS = new ConcurrentHashMap<>();
    private static final Set<String> REQUESTED = ConcurrentHashMap.newKeySet();

    private EntrySchemaCache() {
    }

    public static String schema(String registryName) {
        return SCHEMAS.get(registryName);
    }

    /** 获取条目结构 schema：有缓存立即回调；否则请求服务端并挂监听（结果到达后回调）。 */
    public static void acquire(String registryName, Consumer<String> onSchema) {
        String cached = SCHEMAS.get(registryName);
        if (cached != null) {
            onSchema.accept(cached);
            return;
        }
        LISTENERS.computeIfAbsent(registryName, k -> new ArrayList<>()).add(onSchema);
        if (REQUESTED.add(registryName)) {
            NetworkHandler.sendToServer(new RequestEntrySchemaPacket(registryName));
        }
    }

    /** 移除监听（控件销毁时调用）。 */
    public static void release(String registryName, Consumer<String> onSchema) {
        List<Consumer<String>> listeners = LISTENERS.get(registryName);
        if (listeners != null) {
            listeners.remove(onSchema);
        }
    }

    /** 响应包回调：写缓存并通知全部监听者。 */
    public static void update(String registryName, String schemaJson) {
        SCHEMAS.put(registryName, schemaJson);
        List<Consumer<String>> listeners = LISTENERS.remove(registryName);
        if (listeners != null) {
            for (Consumer<String> listener : listeners) {
                listener.accept(schemaJson);
            }
        }
    }
}
