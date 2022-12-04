package hungteen.htlib.common.world.raid;

import hungteen.htlib.util.interfaces.IRaid;
import hungteen.htlib.util.interfaces.IResultComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 22:37
 **/
public interface IResultComponent {

    default boolean forVictory(){
        return false;
    }

    default boolean forLoss(){
        return false;
    }

    /**
     * 全局触发。
     */
    void apply(IRaid raid, ServerLevel level, int tick);

    /**
     * 对保卫者触发事件。
     */
    void applyToDefender(IRaid raid, Entity defender, int tick);

    /**
     * 对袭击者触发事件。
     */
    void applyToRaider(IRaid raid, Entity raider, int tick);

    /**
     *
     * @return
     */
    IResultComponentType<?> getType();

}
