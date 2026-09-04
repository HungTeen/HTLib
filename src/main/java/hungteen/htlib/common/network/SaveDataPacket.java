package hungteen.htlib.common.network;

import hungteen.htlib.common.codec.parse.CodecEditorManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * 客户端 → 服务端：保存编辑结果到指定路径。
 *
 * @author PangTeen
 * @program: HTLib
 * @create 2026/8/10 14:07
 **/
public class SaveDataPacket {

    private final String registryName;
    private final String targetPath;
    private final String json;

    public SaveDataPacket(String registryName, String targetPath, String json) {
        this.registryName = registryName;
        this.targetPath = targetPath;
        this.json = json;
    }

    public SaveDataPacket(FriendlyByteBuf buffer) {
        this.registryName = buffer.readUtf();
        this.targetPath = buffer.readUtf();
        this.json = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(registryName);
        buffer.writeUtf(targetPath);
        buffer.writeUtf(json);
    }

    public static class Handler {
        public static void onMessage(SaveDataPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                var sender = ctx.get().getSender();
                if (sender == null) {
                    return;
                }
                Optional<Path> saved = CodecEditorManager.saveJson(sender.server, message.targetPath, message.json);
                saved.ifPresentOrElse(
                        path -> sender.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "已保存 " + message.registryName + " → " + path)),
                        () -> sender.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "保存失败，请检查路径与 JSON 合法性"))
                );
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
