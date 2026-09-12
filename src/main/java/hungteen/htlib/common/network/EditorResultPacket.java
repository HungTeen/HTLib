package hungteen.htlib.common.network;

import hungteen.htlib.client.gui.widget.codec.EditorResultCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：编辑动作的结果（保存 / 校验）。
 *
 * <p>{@link #action} 把结果还给发起请求的那个动作（同一动作只保留最后一次请求的回调），
 * {@link #message} 在成功时是保存路径、失败时是错误原因，由界面按动作决定文案。</p>
 *
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/12
 **/
public class EditorResultPacket {

    /** {@link SaveDataPacket} 的结果。 */
    public static final String ACTION_SAVE = "save";
    /** {@link ValidateDataPacket} 的结果。 */
    public static final String ACTION_VALIDATE = "validate";

    private final String action;
    private final boolean success;
    private final String message;

    public EditorResultPacket(String action, boolean success, String message) {
        this.action = action;
        this.success = success;
        this.message = message;
    }

    public EditorResultPacket(FriendlyByteBuf buffer) {
        this.action = buffer.readUtf();
        this.success = buffer.readBoolean();
        this.message = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(action);
        buffer.writeBoolean(success);
        buffer.writeUtf(message);
    }

    public static void onMessage(EditorResultPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> EditorResultCache.complete(message.action, message.success, message.message));
        ctx.get().setPacketHandled(true);
    }
}
