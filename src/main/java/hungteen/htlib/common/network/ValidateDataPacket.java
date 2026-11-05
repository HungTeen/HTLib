package hungteen.htlib.common.network;

import hungteen.htlib.common.codec.parse.CodecEditorManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

/** Validate against the server's full registries, without writing a file. */
public class ValidateDataPacket {
    private final UUID requestId;
    private final String registryName;
    private final String json;

    public ValidateDataPacket(UUID requestId, String registryName, String json) {
        this.requestId = requestId;
        this.registryName = registryName;
        this.json = json;
    }

    public ValidateDataPacket(FriendlyByteBuf buffer) {
        this(buffer.readUUID(), buffer.readUtf(EditorPacketLimits.MAX_ID_LENGTH), buffer.readUtf(EditorPacketLimits.MAX_JSON_LENGTH));
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(requestId);
        buffer.writeUtf(registryName, EditorPacketLimits.MAX_ID_LENGTH);
        buffer.writeUtf(json, EditorPacketLimits.MAX_JSON_LENGTH);
    }

    public static void onMessage(ValidateDataPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var sender = ctx.get().getSender();
            if (sender == null) return;
            Optional<String> error = sender.hasPermissions(2)
                ? CodecEditorManager.validate(sender.server, message.registryName, message.json)
                : Optional.of("需要权限等级 2 才能编辑数据包");
            NetworkHandler.sendToClient(sender, new EditorResultPacket(EditorResultPacket.ACTION_VALIDATE,
                message.requestId, error.isEmpty(), error.orElse("")));
        });
        ctx.get().setPacketHandled(true);
    }
}
