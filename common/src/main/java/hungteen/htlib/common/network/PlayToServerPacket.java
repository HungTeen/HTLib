package hungteen.htlib.common.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 16:45
 **/
public abstract class PlayToServerPacket<T extends PlayToServerPacket<T>> implements CustomPacketPayload, HTPlayToServerPayload {

    @Override
    public abstract @NotNull Type<T> type();

    public abstract StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

}
