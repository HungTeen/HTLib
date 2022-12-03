package hungteen.htlib.impl.result;

import hungteen.htlib.common.world.raid.IResultComponent;
import hungteen.htlib.util.interfaces.IRaid;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 22:43
 **/
public class ItemReward implements IResultComponent {
    @Override
    public void applyToDefender(IRaid raid, ServerLevel level, int tick) {

    }

    @Override
    public void applyToDefender(IRaid raid, ServerPlayer player, int tick) {

    }
}
