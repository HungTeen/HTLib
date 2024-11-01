package hungteen.htlib.common;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 21:07
 **/
@EventBusSubscriber(modid = HTLibAPI.MOD_ID)
public class HTLibNeoDummyEntityHandler {

    @SubscribeEvent
    public static void tick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            DummyEntityManager.get(serverLevel).tick();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level() instanceof ServerLevel serverLevel && event.getEntity() instanceof ServerPlayer serverPlayer) {
//            PlayerCapabilityManager.syncToClient(event.getEntity());
            DummyEntityManager.get(serverLevel).initialize(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            DummyEntityManager.get(serverPlayer.serverLevel()).finalize(serverPlayer);
        }
    }

//    @SubscribeEvent
//    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
//        if(EntityHelper.isServer(event.getEntity())) {
//            PlayerCapabilityManager.syncToClient(event.getEntity());
//        }
//    }
//
//    @SubscribeEvent
//    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
//        if(EntityHelper.isServer(event.getEntity())) {
//            PlayerCapabilityManager.syncToClient(event.getEntity());
//        }
//    }
//
//    @SubscribeEvent
//    public static void onPlayerClone(PlayerEvent.Clone event) {
//        PlayerCapabilityManager.cloneData(event.getOriginal(), event.getEntity(), event.isWasDeath());
//    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void interactAt(PlayerInteractEvent.EntityInteractSpecific event) {
        DummyEntityManager.getCollisionEntities(event.getLevel()).forEach(dummyEntity -> {
            if (dummyEntity.requireBlock(event.getEntity(), event.getTarget().getBoundingBox())) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void interact(PlayerInteractEvent.EntityInteract event) {
        DummyEntityManager.getCollisionEntities(event.getLevel()).forEach(dummyEntity -> {
            if (dummyEntity.requireBlock(event.getEntity(), event.getTarget().getBoundingBox())) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        DummyEntityManager.getCollisionEntities(event.getLevel()).forEach(dummyEntity -> {
            if (dummyEntity.requireBlock(event.getEntity(), event.getHitVec().getBlockPos())) {
                event.setCanceled(true);
            }
        });
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        DummyEntityManager.getCollisionEntities(event.getLevel()).forEach(dummyEntity -> {
            if (dummyEntity.requireBlock(event.getEntity(), event.getPos())) {
                event.setCanceled(true);
            }
        });
    }

    /**
     * 防止玩家在边界攻击另一边的实体。
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void hurt(LivingIncomingDamageEvent event) {
        if (event.getSource().getDirectEntity() != null) {
            DummyEntityManager.getCollisionEntities(event.getEntity().level()).forEach(dummyEntity -> {
                if (dummyEntity.requireBlock(event.getEntity(), event.getSource().getDirectEntity().getBoundingBox())) {
                    event.setCanceled(true);
                }
            });
        }
    }

}
