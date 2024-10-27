package hungteen.htlib.platform;

import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.HTLibNeoNetworkHandler;
import hungteen.htlib.common.event.DummyEntityEvent;
import hungteen.htlib.common.impl.registry.HTNeoCodecRegistryImpl;
import hungteen.htlib.common.impl.registry.HTNeoCustomRegistry;
import hungteen.htlib.common.impl.registry.HTNeoVanillaRegistry;
import hungteen.htlib.common.impl.registry.HTVanillaRegistry;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.util.NeoHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 15:13
 **/
public class HTLibNeoAPI implements HTLibPlatformAPI {

    @Override
    public Platform getPlatform() {
        return Platform.NEOFORGE;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return NeoHelper.isModLoaded(modId);
    }

    @Override
    public boolean isPhysicalClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    @Override
    public List<? extends HTModInfo> getModInfoList() {
        return ModList.get().getMods().stream().map(HTNeoModInfo::new).toList();
    }

    @Override
    public Optional<? extends HTModContainer> getModContainer(String modId) {
        return ModList.get().getModContainerById(modId).map(HTNeoModContainer::new);
    }

    @Override
    public boolean postDummyEntityCreateEvent(Level level, DummyEntity dummyEntity) {
        return NeoForge.EVENT_BUS.post(new DummyEntityEvent.DummyEntitySpawnEvent(level, dummyEntity)).isCanceled();
    }

    @Override
    public void sendToServer(CustomPacketPayload msg) {
        HTLibNeoNetworkHandler.sendToServer(msg);
    }

    @Override
    public void sendToClient(CustomPacketPayload msg) {
        HTLibNeoNetworkHandler.sendToClient(msg);
    }

    @Override
    public void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload msg) {
        HTLibNeoNetworkHandler.sendToClient(serverPlayer, msg);
    }

    @Override
    public void sendToClient(ServerLevel level, @Nullable ServerPlayer player, Vec3 vec, double dis, CustomPacketPayload msg) {
        HTLibNeoNetworkHandler.sendToNearByClient(level, player, vec, dis, msg);
    }

    @Override
    public HTCodecRegistry.HTCodecRegistryFactory createCodecRegistryFactory() {
        return HTNeoCodecRegistryImpl::new;
    }

    @Override
    public HTCustomRegistry.HTCustomRegistryFactory createCustomRegistryFactory() {
        return HTNeoCustomRegistry::new;
    }

    @Override
    public HTVanillaRegistry.HTVanillaRegistryFactory createVanillaRegistryFactory() {
        return HTNeoVanillaRegistry::new;
    }

}
