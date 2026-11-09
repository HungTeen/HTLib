package hungteen.htlib.common.network;

import hungteen.htlib.client.gui.screen.codec.CodecScreen;
import hungteen.htlib.client.gui.widget.codec.EntrySchemaCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：返回某个数据包注册表条目结构的 schema JSON。
 *
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/6 21:50
 **/
public class EntrySchemaResponsePacket {

    private final String registryName;
    private final String schemaJson;
    private final String error;

    public EntrySchemaResponsePacket(String registryName, String schemaJson) {
        this(registryName, schemaJson, "");
    }

    private EntrySchemaResponsePacket(String registryName, String schemaJson, String error) {
        requireSchemaBudget(schemaJson);
        this.registryName = registryName;
        this.schemaJson = schemaJson;
        this.error = error;
    }

    public static EntrySchemaResponsePacket failure(String registryName, String error) {
        return new EntrySchemaResponsePacket(registryName, "", EditorPacketLimits.resultMessage(error));
    }

    public EntrySchemaResponsePacket(FriendlyByteBuf buffer) {
        this(buffer.readUtf(EditorPacketLimits.MAX_ID_LENGTH), buffer.readUtf(EditorPacketLimits.MAX_SCHEMA_LENGTH),
            buffer.readUtf(EditorPacketLimits.MAX_RESULT_LENGTH));
    }

    public void encode(FriendlyByteBuf buffer) {
        requireSchemaBudget(schemaJson);
        buffer.writeUtf(registryName, EditorPacketLimits.MAX_ID_LENGTH);
        buffer.writeUtf(schemaJson, EditorPacketLimits.MAX_SCHEMA_LENGTH);
        buffer.writeUtf(error, EditorPacketLimits.MAX_RESULT_LENGTH);
    }

    private static void requireSchemaBudget(String json) {
        if (!EditorPacketLimits.fitsSchema(json)) {
            throw new IllegalArgumentException("Schema exceeds the 900000-byte UTF-8 budget");
        }
    }

    public static void onMessage(EntrySchemaResponsePacket message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            EntrySchemaCache.update(message.registryName, message.schemaJson);
            if (!message.error.isEmpty()) CodecScreen.onSchemaError(message.registryName, message.error);
        }));
        ctx.get().setPacketHandled(true);
    }
}
