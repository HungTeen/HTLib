package hungteen.htlib.impl.wave;

import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.common.world.raid.WaveComponent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-29 09:08
 **/
public abstract class BaseWave extends WaveComponent {

    private PlaceComponent spawnPlacement;
    private final int prepareDuration;
    private final int waveDuration;
    private final boolean canSkip;

    public BaseWave(PlaceComponent spawnPlacement, int prepareDuration, int waveDuration, boolean canSkip) {
        this.spawnPlacement = spawnPlacement;
        this.prepareDuration = prepareDuration;
        this.waveDuration = waveDuration;
        this.canSkip = canSkip;
    }

    @Override
    public PlaceComponent getSpawnPlacement() {
        return spawnPlacement;
    }

    @Override
    public int getPrepareDuration() {
        return prepareDuration;
    }

    @Override
    public int getWaveDuration() {
        return waveDuration;
    }

    @Override
    public boolean canSkip() {
        return canSkip;
    }
}
