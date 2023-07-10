package hungteen.htlib.common.event;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.capability.PlayerCapabilityManager;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import hungteen.htlib.util.helper.registry.EntityHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 21:07
 **/
@Mod.EventBusSubscriber(modid = HTLib.MOD_ID)
public class HTPlayerEvents {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void login(PlayerEvent.PlayerLoggedInEvent event){
        if(event.getEntity().level() instanceof ServerLevel && event.getEntity() instanceof ServerPlayer){
            PlayerCapabilityManager.syncToClient(event.getEntity());
            DummyEntityManager.get((ServerLevel) event.getEntity().level()).syncToClient((ServerPlayer) event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if(EntityHelper.isServer(event.getEntity())) {
            PlayerCapabilityManager.syncToClient(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if(EntityHelper.isServer(event.getEntity())) {
            PlayerCapabilityManager.syncToClient(event.getEntity());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        PlayerCapabilityManager.cloneData(event.getOriginal(), event.getEntity(), event.isWasDeath());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void interactAt(PlayerInteractEvent.EntityInteractSpecific event){
        DummyEntityManager.getCollisionEntities(event.getLevel()).forEach(dummyEntity -> {
            if(dummyEntity.requireBlock(event.getEntity(), event.getTarget().getBoundingBox())){
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void interact(PlayerInteractEvent.EntityInteract event){
        DummyEntityManager.getCollisionEntities(event.getLevel()).forEach(dummyEntity -> {
            if(dummyEntity.requireBlock(event.getEntity(), event.getTarget().getBoundingBox())){
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event){
        DummyEntityManager.getCollisionEntities(event.getLevel()).forEach(dummyEntity -> {
            if(dummyEntity.requireBlock(event.getEntity(), event.getHitVec().getBlockPos())){
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event){
        DummyEntityManager.getCollisionEntities(event.getLevel()).forEach(dummyEntity -> {
            if(dummyEntity.requireBlock(event.getEntity(), event.getPos())){
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void hurt(LivingHurtEvent event){
        if(event.getSource().getDirectEntity() != null){
            DummyEntityManager.getCollisionEntities(event.getEntity().level()).forEach(dummyEntity -> {
                if(dummyEntity.requireBlock(event.getEntity(), event.getSource().getDirectEntity().getBoundingBox())){
                    event.setCanceled(true);
                }
            });
        }
    }

}
