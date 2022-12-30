package hungteen.htlib.api.interfaces.raid;

import hungteen.htlib.api.interfaces.IDummyEntity;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 10:25
 **/
public interface IRaid extends IDummyEntity {

    /**
     * Add raider to the raid.
     * @param raider Raider to add.
     * @return True if successful, false otherwise.
     */
    boolean addRaider(Entity raider);

    /**
     * Get getSpawnEntities placement by priority.
     * @return Function (Spawn -> Placement).
     */
    Function<ISpawnComponent, IPlaceComponent> getPlaceComponent();
}
