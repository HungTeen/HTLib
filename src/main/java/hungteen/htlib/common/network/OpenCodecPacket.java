package hungteen.htlib.common.network;

import hungteen.htlib.client.gui.screen.codec.CodecEditorScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/4 22:59
 **/
public class OpenCodecPacket {

    /** 打开哪个界面。 */
    public enum Mode {
        VIEWER, EDITOR
    }

    private final List<ResourceLocation> registryNames;
    private final String preselect;
    private final Mode mode;

    public OpenCodecPacket(List<ResourceLocation> registryNames, String preselect, Mode mode) {
        this.registryNames = registryNames;
        this.preselect = preselect;
        this.mode = mode;
    }

    public OpenCodecPacket(FriendlyByteBuf buffer) {
        int size = buffer.readVarInt();
        this.registryNames = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            registryNames.add(buffer.readResourceLocation());
        }
        this.preselect = buffer.readUtf();
        this.mode = buffer.readBoolean() ? Mode.EDITOR : Mode.VIEWER;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(registryNames.size());
        for (ResourceLocation name : registryNames) {
            buffer.writeResourceLocation(name);
        }
        buffer.writeUtf(preselect);
        buffer.writeBoolean(mode == Mode.EDITOR);
    }

    public static class Handler {
        public static void onMessage(OpenCodecPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                if (message.mode == Mode.EDITOR) {
                    CodecEditorScreen.open(message.registryNames);
                } else {
                    // TODO 还需要吗？
//                    CodecViewerScreen.open(message.registryNames, message.preselect);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
