package hungteen.htlib.common.network;

import java.nio.charset.StandardCharsets;

/** Keep editor requests within Minecraft 1.20.1's 32767-byte serverbound custom payload. */
public final class EditorPacketLimits {
    public static final int MAX_ID_LENGTH = 256;
    public static final int MAX_JSON_LENGTH = 32767;
    // Also leaves room for three IDs, a UUID, string lengths and the packet discriminator.
    public static final int MAX_JSON_BYTES = 30000;
    public static final int MAX_SCHEMA_LENGTH = 262144;
    public static final int MAX_RESULT_LENGTH = 2048;

    private EditorPacketLimits() {
    }

    public static boolean fitsJson(String json) {
        return json != null && json.length() <= MAX_JSON_LENGTH
            && json.getBytes(StandardCharsets.UTF_8).length <= MAX_JSON_BYTES;
    }

    public static String resultMessage(String message) {
        if (message == null) return "Unknown error";
        return message.length() > MAX_RESULT_LENGTH ? message.substring(0, MAX_RESULT_LENGTH - 1) + "…" : message;
    }
}
