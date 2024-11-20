package hungteen.htlib.util.helper;

import hungteen.htlib.platform.HTLibPlatformAPI;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/11/19 23:02
 **/
public interface NetworkHelper {

    static void sendToServer(CustomPacketPayload msg){
        HTLibPlatformAPI.get().sendToServer(msg);
    }

    static void sendToClient(ServerLevel level, CustomPacketPayload msg){
        HTLibPlatformAPI.get().sendToClient(level, msg);
    }

    static void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload msg){
        HTLibPlatformAPI.get().sendToClient(serverPlayer, msg);
    }

    static void sendToClient(ServerLevel level, @Nullable ServerPlayer player, Vec3 vec, double dis, CustomPacketPayload msg){
        HTLibPlatformAPI.get().sendToClient(level, player, vec, dis, msg);
    }

    static void sendToClientTrackingPlayerAndSelf(Entity entity, CustomPacketPayload msg){
        HTLibPlatformAPI.get().sendToClientTrackingPlayerAndSelf(entity, msg);
    }

}
