package hungteen.htlib.common.network;

import hungteen.htlib.client.gui.widget.codec.EditorResultCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/** A response belongs to one request, even when documents or screens have changed. */
public class EditorResultPacket {
    public static final String ACTION_SAVE = "save";
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
