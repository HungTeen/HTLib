package hungteen.htlib.common;

import hungteen.htlib.common.network.packet.DummyEntityPacket;
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
        PayloadTypeRegistry.playS2C().register(DummyEntityPacket.TYPE, DummyEntityPacket.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(SyncDatapackPacket.TYPE, SyncDatapackPacket.STREAM_CODEC);
    }

    static void initClient(){
        ClientPlayNetworking.registerGlobalReceiver(PlaySoundPacket.TYPE, FabricHelper.wrapClientHandler(PlaySoundPacket::process));
        ClientPlayNetworking.registerGlobalReceiver(SyncDatapackPacket.TYPE, FabricHelper.wrapClientHandler(SyncDatapackPacket::process));
        ClientPlayNetworking.registerGlobalReceiver(DummyEntityPacket.TYPE, FabricHelper.wrapClientHandler(DummyEntityPacket::process));
    }

}
