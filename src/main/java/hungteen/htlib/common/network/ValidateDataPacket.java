package hungteen.htlib.common.network;

import hungteen.htlib.common.codec.parse.CodecEditorManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * 客户端 → 服务端：校验编辑结果是否符合注册表 codec，结果由 {@link EditorResultPacket} 回传。
 *
 * <p>Holder 引用需要 {@code RegistryOps} 才能解析，而客户端注册表不全（见
 * {@link CodecEditorManager#validate}），故校验放在服务端做。</p>
 *
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/12
 **/
public class ValidateDataPacket {

    private final String registryName;
    private final String json;

    public ValidateDataPacket(String registryName, String json) {
        this.registryName = registryName;
        this.json = json;
    }

    public ValidateDataPacket(FriendlyByteBuf buffer) {
        this.registryName = buffer.readUtf();
        this.json = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(registryName);
        buffer.writeUtf(json);
    }

    public static void onMessage(ValidateDataPacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer sender = ctx.get().getSender();
            if (sender == null) {
                return;
            }
            Optional<String> error = CodecEditorManager.validate(sender.server, message.registryName, message.json);
            NetworkHandler.sendToClient(sender,
                new EditorResultPacket(EditorResultPacket.ACTION_VALIDATE, error.isEmpty(), error.orElse("")));
        });
        ctx.get().setPacketHandled(true);
    }
}
