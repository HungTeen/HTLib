package hungteen.htlib.api.interfaces.raid;

import hungteen.htlib.api.interfaces.IDummyEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
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

    /**
     * 玩家中已被袭击进度条跟踪的保卫者。 Defenders tracked by the raid bar.
     * @return Players following the raid.
     */
    default List<ServerPlayer> getPlayers() {
        return List.of();
    }

    /**
     * 当前已关联到本袭击的袭击者。 Raiders linked to this raid.
     * @return Linked raiders.
     */
    default List<Entity> getRaiders() {
        return List.of();
    }
}
