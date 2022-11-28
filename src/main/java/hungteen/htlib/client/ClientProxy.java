package hungteen.htlib.client;

import hungteen.htlib.CommonProxy;
import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:29
 **/
public class ClientProxy extends CommonProxy {

    public static final Minecraft MC = Minecraft.getInstance();
    private static final HashMap<Integer, DummyEntity> DUMMY_ENTITY_MAP = new HashMap<>();

    @Override
    public void addDummyEntity(DummyEntity entity) {
        DUMMY_ENTITY_MAP.putIfAbsent(entity.getEntityID(), entity);
    }

    @Override
    public void removeDummyEntity(int entityID) {
        DUMMY_ENTITY_MAP.remove(entityID);
    }

    @Override
    public List<DummyEntity> getDummyEntities() {
        return DUMMY_ENTITY_MAP.values().stream().toList();
    }

    @Override
    public Player getPlayer() {
        return MC.player;
    }
}
