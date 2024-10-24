package hungteen.htlib.common.network.packets;

import hungteen.htlib.common.network.HTPlayToClientPayload;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 16:45
 **/
public abstract class PlayToClientPacket<T extends PlayToClientPacket<T>> implements CustomPacketPayload, HTPlayToClientPayload {

    @Override
    public abstract @NotNull Type<T> type();

    public abstract StreamCodec<RegistryFriendlyByteBuf, T> streamCodec();

}
