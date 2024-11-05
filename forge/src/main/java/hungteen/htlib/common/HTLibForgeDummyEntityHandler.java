package hungteen.htlib.common;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.common.world.entity.DummyEntityManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
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
@Mod.EventBusSubscriber(modid = HTLibAPI.MOD_ID)
public class HTLibForgeDummyEntityHandler {

    @SubscribeEvent
    public static void tick(TickEvent.LevelTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.level instanceof ServerLevel) {
            DummyEntityManager.get((ServerLevel) event.level).tick();
        }
    }

    @SubscribeEvent
    public static void login(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level() instanceof ServerLevel serverLevel && event.getEntity() instanceof ServerPlayer serverPlayer) {
            DummyEntityManager.get(serverLevel).initialize(serverPlayer);
        }
    }

    @Mod.EventBusSubscriber(modid = HTLibAPI.MOD_ID, value = Dist.CLIENT)
    public static class PlayerLoggedOutHandler {
        @SubscribeEvent
        public static void logout(ClientPlayerNetworkEvent.LoggingOut event) {
            HTLibProxy.get().clearDummyEntities();
        }
    }

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
    public static void hurt(LivingAttackEvent event) {
        if (event.getSource().getDirectEntity() != null) {
            DummyEntityManager.getCollisionEntities(event.getEntity().level()).forEach(dummyEntity -> {
                if (dummyEntity.requireBlock(event.getEntity(), event.getSource().getDirectEntity().getBoundingBox())) {
                    event.setCanceled(true);
                }
            });
        }
    }

}
