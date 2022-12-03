package hungteen.htlib.util.interfaces;

import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.common.world.raid.ISpawnComponent;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 10:25
 **/
public interface IRaid extends IDummyEntity{

    boolean addRaider(Entity raider);

    /**
     * Get getSpawnEntities placement by priority.
     * @return Function (Spawn -> Placement).
     */
    Function<ISpawnComponent, PlaceComponent> getPlaceComponent();
}
