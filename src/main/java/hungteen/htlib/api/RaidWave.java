package hungteen.htlib.api;

import com.mojang.serialization.Codec;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 */
public abstract class RaidWave {

    /**
     * Get the type of wave.
     * @return wave type.
     */
    protected abstract IRaidWaveType<?> getType();
    public interface IRaidWaveType<P extends RaidWave> {

        /**
         * Get the method to codec wave.
         * @return codec method.
         */
        Codec<P> codec();

    }
}
