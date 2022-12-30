package hungteen.htlib.api.interfaces.raid;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 22:37
 **/
public interface IResultComponent {

    /**
     * Whether the result is used for victory.
     * @return True if the result is used for victory
     */
    default boolean forVictory(){
        return false;
    }

    /**
     * Whether the result is used for loss.
     * @return True if the result is used for loss.
     */
    default boolean forLoss(){
        return false;
    }

    /**
     * Global tick, 全局触发。
     * @param raid Current raid.
     * @param level Current level.
     * @param tick Current tick.
     */
    void apply(IRaid raid, ServerLevel level, int tick);

    /**
     * Tick for defender, 对保卫者触发事件。
     * @param raid Current raid.
     * @param defender Specific defender.
     * @param tick Current tick.
     */
    void applyToDefender(IRaid raid, Entity defender, int tick);

    /**
     * Tick for raider, 对袭击者触发事件。
     * @param raid Current raid.
     * @param raider Specific raider.
     * @param tick Current tick.
     */
    void applyToRaider(IRaid raid, Entity raider, int tick);

    /**
     * Get Serializer.
     * @return Serialize type.
     */
    IResultComponentType<?> getType();

}
