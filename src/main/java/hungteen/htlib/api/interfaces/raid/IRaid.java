package hungteen.htlib.api.interfaces.raid;

import hungteen.htlib.api.interfaces.IDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;

import java.util.List;
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
     * Remove raider from the raid.
     * @param raider Raider to remove.
     */
    void removeRaider(Entity raider);

    boolean isDefeated();

    boolean isLost();

    Component getTitle();

    /**
     * Get getSpawnEntities placement by priority.
     * @return Function (Spawn -> Placement).
     */
    Function<ISpawnComponent, IPositionComponent> getPlaceComponent();

    /**
     * Get candidate positions of the given placement, which is calculated at runtime
     * and will not be persisted. Used by placements like {@code RayTracePosition},
     * which refresh candidates before spawning entities.
     * @return Candidate positions of the placement.
     */
    List<BlockPos> getCandidatePositions();
}
