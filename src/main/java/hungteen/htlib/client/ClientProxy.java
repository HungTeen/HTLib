package hungteen.htlib.client;

import hungteen.htlib.CommonProxy;
import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
    public List<DummyEntity> getDummyEntities(Level level) {
        return DUMMY_ENTITY_MAP.values().stream()
                .filter(entity -> entity.getLevel().equals(level))
                .toList();
    }

    @Override
    public Optional<DummyEntity> getDummyEntity(int id) {
        return Optional.ofNullable(DUMMY_ENTITY_MAP.getOrDefault(id, null));
    }

    @Override
    public Player getPlayer() {
        return MC.player;
    }

    @Override
    public void clearDummyEntities() {
        DUMMY_ENTITY_MAP.clear();
    }
}
