package hungteen.htlib.impl.result;

import hungteen.htlib.common.world.raid.IResultComponent;
import hungteen.htlib.util.interfaces.IRaid;
import hungteen.htlib.util.interfaces.IResultComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-03 22:49
 **/
public class AdvancementReward implements IResultComponent {

//    public static final Codec<AdvancementRewards> REWARDS_CODEC = Codec.STRING.flatXmap()
//
//    public static final Codec<AdvancementReward> CODEC = RecordCodecBuilder.<AdvancementReward>mapCodec(instance -> instance.group(
//
//    ));

    @Override
    public void applyToDefender(IRaid raid, ServerLevel level, int tick) {

    }

    @Override
    public void applyToDefender(IRaid raid, ServerPlayer player, int tick) {

    }

    @Override
    public IResultComponentType<?> getType() {
        return null;
    }
}
