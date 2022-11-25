package hungteen.htlib.api;

import com.mojang.serialization.Codec;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/18 10:02
 */
public abstract class CustomRaid {

    /**
     * Get the type of raid.
     * @return raid type.
     */
    protected abstract ICustomRaidType<?> getType();
    public interface ICustomRaidType<P extends CustomRaid> {

        /**
         * Get the method to codec raid.
         * @return codec method.
         */
        Codec<P> codec();

    }
}
