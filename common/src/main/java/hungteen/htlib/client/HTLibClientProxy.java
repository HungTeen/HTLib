package hungteen.htlib.client;

import hungteen.htlib.client.util.ClientHelper;
import hungteen.htlib.common.HTLibProxy;
import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:29
 **/
public class HTLibClientProxy extends HTLibProxy {

    private static final HashMap<Integer, DummyEntity> DUMMY_ENTITY_MAP = new HashMap<>();

    @Override
    public Optional<Player> getClientPlayer() {
        return Optional.ofNullable(ClientHelper.player());
    }

    @Override
    public void runOnClient(Supplier<Runnable> task) {
        task.get().run();
    }

    /* 虚拟实体相关 */

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
    public void clearDummyEntities() {
        DUMMY_ENTITY_MAP.clear();
    }
}
