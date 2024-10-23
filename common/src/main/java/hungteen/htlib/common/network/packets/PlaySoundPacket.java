package hungteen.htlib.common.network.packets;

import hungteen.htlib.common.network.ClientPacketContext;
import hungteen.htlib.common.network.HTPlayToClientPayload;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:28
 **/
public record PlaySoundPacket(Holder<SoundEvent> sound) implements HTPlayToClientPayload {

    public static final StreamCodec<RegistryFriendlyByteBuf, PlaySoundPacket> STREAM_CODEC = StreamCodec.composite(
            SoundEvent.STREAM_CODEC,
            PlaySoundPacket::sound,
            PlaySoundPacket::new
    );

    @Override
    public void process(ClientPacketContext context) {
        context.player().playSound(sound.value(), 1F, 1F);
    }

}
