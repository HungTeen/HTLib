package hungteen.htlib.common.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import hungteen.htlib.common.registry.HTRegistryManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 10:13
 **/
public class DataPackPacket {

    private static final Logger LOGGER = LogUtils.getLogger();
    private String type;
    private ResourceLocation location;
    private JsonElement data;

    public DataPackPacket(String type, ResourceLocation location, JsonElement data) {
        this.type = type;
        this.location = location;
        this.data = data;
    }

    public DataPackPacket(FriendlyByteBuf buffer) {
        this.type = buffer.readUtf();
        this.location = new ResourceLocation(buffer.readUtf());
        this.data = JsonParser.parseString(buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.type);
        buffer.writeUtf(this.location.toString());
        buffer.writeUtf(this.data.toString());
    }

    public static class Handler {
        public static void onMessage(DataPackPacket message, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                HTRegistryManager.get(message.type).ifPresent(registry -> {
                    registry.getCodec().parse(JsonOps.INSTANCE, message.data)
                            .resultOrPartial(LOGGER::error)
                            .ifPresent(value -> {
                                registry.outerRegister(message.location, value);
                            });
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }

}
