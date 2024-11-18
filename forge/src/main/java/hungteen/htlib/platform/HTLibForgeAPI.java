package hungteen.htlib.platform;

import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.HTLibForgeNetworkHandler;
import hungteen.htlib.common.event.DummyEntityEvent;
import hungteen.htlib.common.impl.registry.HTForgeCodecRegistryImpl;
import hungteen.htlib.common.impl.registry.HTForgeCustomRegistry;
import hungteen.htlib.common.impl.registry.HTForgeVanillaRegistry;
import hungteen.htlib.common.impl.registry.HTVanillaRegistry;
import hungteen.htlib.common.world.entity.DummyEntity;
import hungteen.htlib.util.ForgeHelper;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/22 15:13
 **/
public class HTLibForgeAPI implements HTLibPlatformAPI {

    @Override
    public Platform getPlatform() {
        return Platform.FORGE;
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ForgeHelper.isModLoaded(modId);
    }

    @Override
    public boolean isPhysicalClient() {
        return FMLLoader.getDist() == Dist.CLIENT;
    }

    @Override
    public List<? extends HTModInfo> getModInfoList() {
        return ModList.get().getMods().stream().map(HTForgeModInfo::new).toList();
    }

    @Override
    public Optional<? extends HTModContainer> getModContainer(String modId) {
        return ModList.get().getModContainerById(modId).map(HTForgeModContainer::new);
    }

    @Override
    public boolean postDummyEntityCreateEvent(Level level, DummyEntity dummyEntity) {
        return MinecraftForge.EVENT_BUS.post(new DummyEntityEvent.DummyEntitySpawnEvent(level, dummyEntity));
    }

    @Override
    public void sendToServer(CustomPacketPayload msg) {
        HTLibForgeNetworkHandler.sendToServer(msg);
    }

    @Override
    public void sendToClient(ServerLevel level, CustomPacketPayload msg) {
        HTLibForgeNetworkHandler.sendToClient(msg);
    }

    @Override
    public void sendToClient(ServerPlayer serverPlayer, CustomPacketPayload msg) {
        HTLibForgeNetworkHandler.sendToClient(serverPlayer, msg);
    }

    @Override
    public void sendToClient(ServerLevel level, @Nullable ServerPlayer player, Vec3 vec, double dis, CustomPacketPayload msg) {
        HTLibForgeNetworkHandler.sendToNearByClient(level, vec, dis, msg);
    }

    @Override
    public HTCodecRegistry.HTCodecRegistryFactory createCodecRegistryFactory() {
        return HTForgeCodecRegistryImpl::new;
    }

    @Override
    public HTCustomRegistry.HTCustomRegistryFactory createCustomRegistryFactory() {
        return HTForgeCustomRegistry::new;
    }

    @Override
    public HTVanillaRegistry.HTVanillaRegistryFactory createVanillaRegistryFactory() {
        return HTForgeVanillaRegistry::new;
    }

}
