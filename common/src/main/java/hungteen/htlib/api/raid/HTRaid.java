package hungteen.htlib.api.raid;

import hungteen.htlib.util.HTDummyEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.function.Function;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 10:25
 **/
public interface HTRaid extends HTDummyEntity {

    /**
     * Add raider to the raid.
     * @param raider Raider to add.
     * @return True if successful, false otherwise.
     */
    boolean addRaider(Entity raider);

    boolean isDefeated();

    boolean isLost();

    Component getTitle();

    /**
     * Get getSpawnEntities placement by priority.
     * @return Function (Spawn -> Placement).
     */
    Function<SpawnComponent, PositionComponent> getPlaceComponent();
}
