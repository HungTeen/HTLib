package hungteen.htlib.platform;

import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.event.DummyEntityEvent;
import hungteen.htlib.common.impl.registry.HTFabricCodecRegistryImpl;
import hungteen.htlib.common.impl.registry.HTFabricCustomRegistry;
import hungteen.htlib.common.impl.registry.HTFabricVanillaRegistry;
import hungteen.htlib.common.impl.registry.HTVanillaRegistry;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.util.helper.PlayerHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/28 9:22
 **/
public class HTLibFabricAPI implements HTLibPlatformAPI{

    @Override
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isPhysicalClient() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public List<? extends HTModInfo> getModInfoList() {
        return FabricLoader.getInstance().getAllMods().stream().map(HTFabricModInfo::new).toList();
    }

    @Override
    public Optional<? extends HTModContainer> getModContainer(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).map(HTFabricModContainer::new);
    }

    @Override
    public boolean postDummyEntityCreateEvent(Level level, DummyEntity dummyEntity) {
        return DummyEntityEvent.DUMMY_SPAWN.invoker().handle(level, dummyEntity);
    }

    @Override
    public void sendToServer(CustomPacketPayload msg) {
        ClientPlayNetworking.send(msg);
    }

    @Override
    public void sendToClient(ServerLevel level, CustomPacketPayload msg) {
        PlayerHelper.getServerPlayers(level).forEach(player -> sendToClient(player, msg));
    }

    @Override
    public void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload msg) {
        ServerPlayNetworking.send(serverPlayer, msg);
    }

    @Override
    public void sendToClient(ServerLevel level, @Nullable ServerPlayer player, Vec3 vec, double dis, CustomPacketPayload msg) {
        PlayerHelper.getServerPlayers(level).stream().filter(p -> p.distanceToSqr(vec) <= dis * dis && ! p.equals(player)).forEach(p -> sendToClient(p, msg));
    }

    @Override
    public HTCodecRegistry.HTCodecRegistryFactory createCodecRegistryFactory() {
        return HTFabricCodecRegistryImpl::new;
    }

    @Override
    public HTCustomRegistry.HTCustomRegistryFactory createCustomRegistryFactory() {
        return HTFabricCustomRegistry::new;
    }

    @Override
    public HTVanillaRegistry.HTVanillaRegistryFactory createVanillaRegistryFactory() {
        return HTFabricVanillaRegistry::new;
    }
}
