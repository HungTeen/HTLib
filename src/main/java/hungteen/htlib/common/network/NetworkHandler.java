package hungteen.htlib.common.network;

import hungteen.htlib.util.helper.HTLibHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:26
 **/
public class NetworkHandler {

    private static int id = 0;

    private static SimpleChannel CHANNEL;

    public static void init() {

        CHANNEL =
            NetworkRegistry.ChannelBuilder.named(HTLibHelper.prefix("networking")).networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true).serverAcceptedVersions(s -> true).simpleChannel();

        CHANNEL.registerMessage(getId(), PlaySoundPacket.class, PlaySoundPacket::encode, PlaySoundPacket::new,
            PlaySoundPacket.Handler::onMessage);
        CHANNEL.registerMessage(getId(), DummyEntityPacket.class, DummyEntityPacket::encode, DummyEntityPacket::new,
            DummyEntityPacket.Handler::onMessage);
        CHANNEL.registerMessage(getId(), SyncDatapackPacket.class, SyncDatapackPacket::encode, SyncDatapackPacket::new,
            SyncDatapackPacket.Handler::onMessage);
        CHANNEL.registerMessage(getId(), OpenCodecPacket.class, OpenCodecPacket::encode, OpenCodecPacket::new,
            OpenCodecPacket.Handler::onMessage);
        CHANNEL.registerMessage(getId(), SaveDataPacket.class, SaveDataPacket::encode, SaveDataPacket::new,
            SaveDataPacket.Handler::onMessage);
        CHANNEL.registerMessage(getId(), ValidateDataPacket.class, ValidateDataPacket::encode, ValidateDataPacket::new,
            ValidateDataPacket::onMessage);
        CHANNEL.registerMessage(getId(), EditorResultPacket.class, EditorResultPacket::encode, EditorResultPacket::new,
            EditorResultPacket::onMessage);
        CHANNEL.registerMessage(getId(), RequestRegistryEntriesPacket.class, RequestRegistryEntriesPacket::encode,
            RequestRegistryEntriesPacket::new, RequestRegistryEntriesPacket::onMessage);
        CHANNEL.registerMessage(getId(), RegistryEntriesResponsePacket.class, RegistryEntriesResponsePacket::encode,
            RegistryEntriesResponsePacket::new, RegistryEntriesResponsePacket::onMessage);
        CHANNEL.registerMessage(getId(), RequestEntrySchemaPacket.class, RequestEntrySchemaPacket::encode,
            RequestEntrySchemaPacket::new, RequestEntrySchemaPacket::onMessage);
        CHANNEL.registerMessage(getId(), EntrySchemaResponsePacket.class, EntrySchemaResponsePacket::encode,
            EntrySchemaResponsePacket::new, EntrySchemaResponsePacket::onMessage);
    }

    public static <MSG> void sendToServer(MSG msg) {
        CHANNEL.sendToServer(msg);
    }

    public static <MSG> void sendToClient(MSG msg) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static <MSG> void sendToClient(ServerPlayer serverPlayer, MSG msg) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> serverPlayer), msg);
    }

    public static <MSG> void sendToNearByClient(Level world, Vec3 vec, double dis, MSG msg) {
        CHANNEL.send(PacketDistributor.NEAR.with(() -> {
            return new PacketDistributor.TargetPoint(vec.x, vec.y, vec.z, dis, world.dimension());
        }), msg);
    }

    private static int getId() {
        return id++;
    }
}
