package hungteen.htlib.common.network.packet;

import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:28
 **/
public record PlaySoundPacket(Holder<SoundEvent> sound) implements PlayToClientPacket {

    public static final CustomPacketPayload.Type<PlaySoundPacket> TYPE = new CustomPacketPayload.Type<>(HTLibHelper.prefix("play_sound"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlaySoundPacket> STREAM_CODEC = StreamCodec.composite(
            SoundEvent.STREAM_CODEC,
            PlaySoundPacket::sound,
            PlaySoundPacket::new
    );

    @Override
    public void process(ClientPacketContext context) {
        context.player().playSound(sound.value(), 1F, 1F);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
