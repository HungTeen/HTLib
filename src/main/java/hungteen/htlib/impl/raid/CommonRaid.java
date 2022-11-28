package hungteen.htlib.impl.raid;

import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.common.world.raid.WaveComponent;
import hungteen.htlib.util.interfaces.IRaidComponentType;

import java.util.List;
import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-28 23:37
 **/
public class CommonRaid extends BaseRaid {
    @Override
    public List<WaveComponent> getRaidWaves() {
        return null;
    }

    @Override
    public Optional<PlaceComponent> getSpawnPlacement() {
        return Optional.empty();
    }

    @Override
    public IRaidComponentType<?> getType() {
        return null;
    }
}
