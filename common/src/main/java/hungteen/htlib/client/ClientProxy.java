package hungteen.htlib.client;

import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:29
 **/
public class ClientProxy {

    private static final HashMap<Integer, DummyEntity> DUMMY_ENTITY_MAP = new HashMap<>();

    public static void addDummyEntity(DummyEntity entity) {
        DUMMY_ENTITY_MAP.putIfAbsent(entity.getEntityID(), entity);
    }

    public static void removeDummyEntity(int entityID) {
        DUMMY_ENTITY_MAP.remove(entityID);
    }

    public static List<DummyEntity> getDummyEntities() {
        return DUMMY_ENTITY_MAP.values().stream().toList();
    }

    public static List<DummyEntity> getDummyEntities(Level level) {
        return DUMMY_ENTITY_MAP.values().stream()
                .filter(entity -> entity.getLevel().equals(level))
                .toList();
    }

    public static Optional<DummyEntity> getDummyEntity(int id) {
        return Optional.ofNullable(DUMMY_ENTITY_MAP.getOrDefault(id, null));
    }

    public static void clearDummyEntities() {
        DUMMY_ENTITY_MAP.clear();
    }
}