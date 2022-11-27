package hungteen.htlib.common.world.raid;

import hungteen.htlib.util.interfaces.IRaidComponentType;

import java.util.List;
import java.util.Optional;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 */
public abstract class RaidComponent {

    /**
     * Get the wave list.
     * @return list of waves.
     */
    public abstract List<WaveComponent> getRaidWaves();

    /**
     * Determines the wave spawn placement type.
     * @return the least high priority spawn placement.
     */
    public abstract Optional<PlaceComponent> getSpawnPlacement();

    /**
     * Get the type of raid.
     * @return raid type.
     */
    public abstract IRaidComponentType<?> getType();

}
