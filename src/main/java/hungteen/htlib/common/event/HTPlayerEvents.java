package hungteen.htlib.common.event;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.world.entity.DummyEntityManager;
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
        if(event.getEntity().level instanceof ServerLevel && event.getEntity() instanceof ServerPlayer){
            DummyEntityManager.get((ServerLevel) event.getEntity().level).syncToClient((ServerPlayer) event.getEntity());
        }
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
            DummyEntityManager.getCollisionEntities(event.getEntity().getLevel()).forEach(dummyEntity -> {
                if(dummyEntity.requireBlock(event.getEntity(), event.getSource().getDirectEntity().getBoundingBox())){
                    event.setCanceled(true);
                }
            });
        }
    }

}
