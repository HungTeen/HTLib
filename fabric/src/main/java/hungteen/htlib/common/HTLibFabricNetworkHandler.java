package hungteen.htlib.common;

import hungteen.htlib.common.network.packet.DummyEntityInitPacket;
import hungteen.htlib.common.network.packet.DummyEntityPlayPacket;
import hungteen.htlib.common.network.packet.PlaySoundPacket;
import hungteen.htlib.common.network.packet.SyncDatapackPacket;
import hungteen.htlib.util.FabricHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/28 9:31
 **/
public interface HTLibFabricNetworkHandler {

    static void init(){
        PayloadTypeRegistry.playS2C().register(PlaySoundPacket.TYPE, PlaySoundPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(DummyEntityInitPacket.TYPE, DummyEntityInitPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(DummyEntityPlayPacket.TYPE, DummyEntityPlayPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncDatapackPacket.TYPE, SyncDatapackPacket.STREAM_CODEC);
    }

    static void initClient(){
        ClientPlayNetworking.registerGlobalReceiver(PlaySoundPacket.TYPE, FabricHelper.wrapClientHandler(PlaySoundPacket::process));
        ClientPlayNetworking.registerGlobalReceiver(DummyEntityInitPacket.TYPE, FabricHelper.wrapClientHandler(DummyEntityInitPacket::process));
        ClientPlayNetworking.registerGlobalReceiver(DummyEntityPlayPacket.TYPE, FabricHelper.wrapClientHandler(DummyEntityPlayPacket::process));
        ClientPlayNetworking.registerGlobalReceiver(SyncDatapackPacket.TYPE, FabricHelper.wrapClientHandler(SyncDatapackPacket::process));
    }

}
