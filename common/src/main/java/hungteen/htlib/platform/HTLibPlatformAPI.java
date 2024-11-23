package hungteen.htlib.platform;

import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.api.util.Platform;
import hungteen.htlib.api.util.helper.ServiceHelper;
import hungteen.htlib.common.impl.registry.HTVanillaRegistry;
import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * 一个用来兼容不同平台方法的接口，在不同的平台有不同的实现。
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/22 8:56
 */
public interface HTLibPlatformAPI {

    HTLibPlatformAPI INSTANCE = ServiceHelper.findService(HTLibPlatformAPI.class);

    /**
     * Obtain the Mod API, either a valid implementation if mod is present, else
     * a dummy instance instead if mod is absent.
     */
    static HTLibPlatformAPI get() {
        return INSTANCE;
    }

    /* Basic Information */

    /**
     * @return 不同实现平台。
     */
    Platform getPlatform();

    /**
     * 对应 mod 是否已加载。
     */
    boolean isModLoaded(String modId);

    /**
     * 是否为物理客户端。
     */
    boolean isPhysicalClient();

    /**
     * @return info list of all loaded mods.
     */
    List<? extends HTModInfo> getModInfoList();

    /**
     * @param modId 对应 mod 的 id。
     * @return 对应 mod 的容器。
     */
    Optional<? extends HTModContainer> getModContainer(String modId);

    /* Event Related */

    boolean postDummyEntityCreateEvent(Level level, DummyEntity dummyEntity);

    /* Network Related */

    /**
     * @param msg Packet to be sent to server.
     */
    void sendToServer(CustomPacketPayload msg);

    /**
     * @param msg Packet to be sent to client.
     */
    void sendToClient(ServerLevel level, CustomPacketPayload msg);

    /**
     * @param serverPlayer The player to send the packet to.
     * @param msg Packet to be sent to client.
     */
    void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload msg);

    /**
     * @param level client level to send packet to.
     * @param vec client position to send packet to.
     * @param dis distance to the position.
     * @param msg Packet to be sent to client.
     */
    void sendToClient(ServerLevel level, @Nullable ServerPlayer player, Vec3 vec, double dis, CustomPacketPayload msg);

    /**
     * Send packet to all players who tracking the entity.
     * @param entity the tracked entity.
     * @param msg Packet to be sent to client.
     */
    void sendToClientTrackingPlayerAndSelf(Entity entity, CustomPacketPayload msg);

    /* Registry Related */

    /**
     * 不同平台根据自己的特性创建独有的 Codec 工厂。
     */
    HTCodecRegistry.HTCodecRegistryFactory createCodecRegistryFactory();

    /**
     * 不同平台根据自己的特性创建独有的 Custom 工厂。
     */
    HTCustomRegistry.HTCustomRegistryFactory createCustomRegistryFactory();

    /**
     * 不同平台根据自己的特性创建独有的 Vanilla 工厂。
     */
    HTVanillaRegistry.HTVanillaRegistryFactory createVanillaRegistryFactory();

}
