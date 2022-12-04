package hungteen.htlib.impl.result;

import hungteen.htlib.common.world.raid.IResultComponent;
import hungteen.htlib.util.interfaces.IRaid;
import hungteen.htlib.util.interfaces.IResultComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 22:49
 *
 * {@link net.minecraft.advancements.AdvancementRewards}
 * TODO 支持进度的奖励。
 **/
public class AdvancementReward implements IResultComponent {

    @Override
    public void apply(IRaid raid, ServerLevel level, int tick) {

    }

    @Override
    public void applyToDefender(IRaid raid, Entity defender, int tick) {

    }

    @Override
    public void applyToRaider(IRaid raid, Entity raider, int tick) {

    }

    @Override
    public IResultComponentType<?> getType() {
        return null;
    }
}
