package hungteen.htlib.common.world.raid;

import hungteen.htlib.util.interfaces.IRaid;
import hungteen.htlib.util.interfaces.IResultComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 22:37
 **/
public interface IResultComponent {

    /**
     * 全局触发。
     */
    void applyToDefender(IRaid raid, ServerLevel level, int tick);

    /**
     * 对保卫者触发事件。
     */
    void applyToDefender(IRaid raid, ServerPlayer player, int tick);

    /**
     *
     * @return
     */
    IResultComponentType<?> getType();

}
