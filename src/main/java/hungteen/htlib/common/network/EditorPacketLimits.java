package hungteen.htlib.common.network;

import java.nio.charset.StandardCharsets;

/** 编辑器请求需保持在 Minecraft 1.20.1 的 32767 字节服务端载荷限制内。 */
public final class EditorPacketLimits {

    public static final int MAX_ID_LENGTH = 256;
    public static final int MAX_JSON_LENGTH = 32767;
    // 为三个 ID、UUID、字符串长度和消息编号留出空间。
    public static final int MAX_JSON_BYTES = 30000;
    // 客户端载荷限制为 1 MiB，另需预留 ID、错误信息和通道头的空间。
    static final int MAX_SCHEMA_LENGTH = 900000;
    static final int MAX_SCHEMA_BYTES = 900000;
    static final int MAX_RESULT_LENGTH = 2048;

    private EditorPacketLimits() {
    }

    public static boolean fitsJson(String json) {
        return json != null && json.length() <= MAX_JSON_LENGTH
            && json.getBytes(StandardCharsets.UTF_8).length <= MAX_JSON_BYTES;
    }

    static boolean fitsSchema(String json) {
        return json != null && json.length() <= MAX_SCHEMA_LENGTH
            && json.getBytes(StandardCharsets.UTF_8).length <= MAX_SCHEMA_BYTES;
    }

    public static String resultMessage(String message) {
        if (message == null) {
            return "Unknown error";
        }
        return message.length() > MAX_RESULT_LENGTH ? message.substring(0, MAX_RESULT_LENGTH - 1) + "…" : message;
    }
}
