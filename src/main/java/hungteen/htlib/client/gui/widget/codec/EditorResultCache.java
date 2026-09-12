package hungteen.htlib.client.gui.widget.codec;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * 保存 / 校验结果的客户端回调表：请求发出前按动作挂上回调，
 * 服务端响应（{@link hungteen.htlib.common.network.EditorResultPacket}）到达后触发并移除。
 *
 * <p>按动作分别持有：校验与保存的响应各回各的回调，同一动作重复请求时后者覆盖前者。</p>
 *
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/12
 **/
public final class EditorResultCache {

    private static final Map<String, BiConsumer<Boolean, String>> LISTENERS = new ConcurrentHashMap<>();

    private EditorResultCache() {
    }

    /** 挂上某动作的待响应回调（发请求前调用）。 */
    public static void await(String action, BiConsumer<Boolean, String> onResult) {
        LISTENERS.put(action, onResult);
    }

    /** 移除某动作的待响应回调（界面关闭时调用）。 */
    public static void release(String action) {
        LISTENERS.remove(action);
    }

    /** 响应包回调：通知对应动作的等待者（结果一次性）。 */
    public static void complete(String action, boolean success, String message) {
        BiConsumer<Boolean, String> listener = LISTENERS.remove(action);
        if (listener != null) {
            listener.accept(success, message);
        }
    }
}
