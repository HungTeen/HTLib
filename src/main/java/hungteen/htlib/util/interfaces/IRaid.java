package hungteen.htlib.util.interfaces;

import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.common.world.raid.SpawnComponent;

import java.util.function.Function;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 10:25
 **/
public interface IRaid extends IDummyEntity{

    /**
     * Get spawn placement by priority.
     * @return Function (Spawn -> Placement).
     */
    Function<SpawnComponent, PlaceComponent> getPlaceComponent();
}
