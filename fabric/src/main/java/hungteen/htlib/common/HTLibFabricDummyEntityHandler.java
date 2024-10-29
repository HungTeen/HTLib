package hungteen.htlib.common;

import hungteen.htlib.common.world.entity.DummyEntityManager;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.InteractionResult;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 21:07
 **/
public class HTLibFabricDummyEntityHandler {

    public static void registerDummyEntityEvents(){
        login();
        rightClickBlock();
        leftClickBlock();
        rightClickEntity();
        leftClickEntity();
    }

    public static void login() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            DummyEntityManager manager = DummyEntityManager.get(handler.getPlayer().serverLevel());
            manager.finalize(handler.getPlayer());
            manager.initialize(handler.getPlayer());
        });
    }

    public static void rightClickEntity() {
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            boolean blocked = DummyEntityManager.getCollisionEntities(world).anyMatch(dummyEntity -> {
                if (dummyEntity.requireBlock(player, entity.getBoundingBox())) {
                    return true;
                }
                return false;
            });
            return blocked ? InteractionResult.FAIL : InteractionResult.PASS;
        });
    }

    public static void rightClickBlock() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            boolean blocked = DummyEntityManager.getCollisionEntities(world).anyMatch(dummyEntity -> {
                if (dummyEntity.requireBlock(player, hitResult.getBlockPos())) {
                    return true;
                }
                return false;
            });
            return blocked ? InteractionResult.FAIL : InteractionResult.PASS;
        });
    }

    public static void leftClickBlock() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            boolean blocked = DummyEntityManager.getCollisionEntities(world).anyMatch(dummyEntity -> {
                if (dummyEntity.requireBlock(player, pos)) {
                    return true;
                }
                return false;
            });
            return blocked ? InteractionResult.FAIL : InteractionResult.PASS;
        });
    }

    /**
     * 防止玩家在边界攻击另一边的实体。
     */
    public static void leftClickEntity() {
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            boolean blocked = DummyEntityManager.getCollisionEntities(world).anyMatch(dummyEntity -> {
                if (dummyEntity.requireBlock(player, entity.getBoundingBox())) {
                    return true;
                }
                return false;
            });
            return blocked ? InteractionResult.FAIL : InteractionResult.PASS;
        });
    }

}
