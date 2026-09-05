package hungteen.htlib.client.gui.widget.codec;

import hungteen.htlib.common.network.NetworkHandler;
import hungteen.htlib.common.network.RequestRegistryEntriesPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 注册表条目的客户端缓存：数据包/自定义注册表不同步到客户端，
 * Holder 字段通过服务端枚举（{@link RegistryEntriesCache#acquire}），结果在此缓存并回调监听者。
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/6 21:30
 */
public final class RegistryEntriesCache {

    private static final Map<String, List<String>> ENTRIES = new ConcurrentHashMap<>();
    private static final Map<String, List<Consumer<List<String>>>> LISTENERS = new ConcurrentHashMap<>();
    private static final Set<String> REQUESTED = ConcurrentHashMap.newKeySet();

    private RegistryEntriesCache() {
    }

    public static List<String> entries(String registryName) {
        List<String> list = ENTRIES.get(registryName);
        return list == null ? List.of() : list;
    }

    /** 获取条目：有缓存立即回调；否则请求服务端并挂监听（结果到达后回调）。 */
    public static void acquire(String registryName, Consumer<List<String>> onEntries) {
        List<String> cached = ENTRIES.get(registryName);
        if (cached != null) {
            onEntries.accept(cached);
            return;
        }
        LISTENERS.computeIfAbsent(registryName, k -> new ArrayList<>()).add(onEntries);
        if (REQUESTED.add(registryName)) {
            NetworkHandler.sendToServer(new RequestRegistryEntriesPacket(registryName));
        }
    }

    /** 移除监听（控件销毁时调用）。 */
    public static void release(String registryName, Consumer<List<String>> onEntries) {
        List<Consumer<List<String>>> listeners = LISTENERS.get(registryName);
        if (listeners != null) {
            listeners.remove(onEntries);
        }
    }

    /** 响应包回调：写缓存并通知全部监听者。 */
    public static void update(String registryName, List<String> entries) {
        ENTRIES.put(registryName, entries);
        List<Consumer<List<String>>> listeners = LISTENERS.remove(registryName);
        if (listeners != null) {
            for (Consumer<List<String>> listener : listeners) {
                listener.accept(entries);
            }
        }
    }
}
