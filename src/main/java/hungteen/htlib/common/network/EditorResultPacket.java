package hungteen.htlib.common.network;

import hungteen.htlib.client.gui.widget.codec.EditorResultCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 服务端 → 客户端：编辑动作的结果（保存 / 校验）。
 *
 * <p>{@link #action} 和 {@link #requestId} 把结果还给发起请求的那个动作，避免过期响应影响新请求；
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
    private final UUID requestId;
    private final boolean success;
    private final String message;

    public EditorResultPacket(String action, UUID requestId, boolean success, String message) {
        this.action = action;
        this.requestId = requestId;
        this.success = success;
        this.message = EditorPacketLimits.resultMessage(message);
    }

    public EditorResultPacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(32), buffer.readUUID(), buffer.readBoolean(), buffer.readUtf(EditorPacketLimits.MAX_RESULT_LENGTH));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(action, 32);
        buffer.writeUUID(requestId);
        buffer.writeBoolean(success);
        buffer.writeUtf(message, EditorPacketLimits.MAX_RESULT_LENGTH);
    }

    public static void onMessage(EditorResultPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
            () -> () -> EditorResultCache.complete(message.action, message.requestId, message.success, message.message)));
        ctx.get().setPacketHandled(true);
    }
}
