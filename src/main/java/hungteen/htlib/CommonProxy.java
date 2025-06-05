package hungteen.htlib;

import hungteen.htlib.common.world.entity.DummyEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:29
 **/
public class CommonProxy {

    public void addDummyEntity(DummyEntity entity) {
    }

    public void removeDummyEntity(int entityID) {
    }

    public List<DummyEntity> getDummyEntities() {
        return List.of();
    }

    public Optional<DummyEntity> getDummyEntity(int id) {
        return Optional.empty();
    }

    public Player getPlayer(){
        return null;
    }

    public Optional<Level> getLevel() {
        return Optional.empty();
    }

    public void clearDummyEntities() {

    }
}
