package hungteen.htlib.common.impl.wave;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.HTSimpleRegistry;
import hungteen.htlib.api.interfaces.raid.IWaveComponent;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.util.helper.HTLibHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 16:10
 */
public class HTWaveTypes {

    private static final HTSimpleRegistry<hungteen.htlib.api.interfaces.raid.WaveType<?>> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("wave_type"));

    public static final hungteen.htlib.api.interfaces.raid.WaveType<CommonWave> COMMON = register(new WaveType<>("common", CommonWave.CODEC));

    public static <T extends IWaveComponent> hungteen.htlib.api.interfaces.raid.WaveType<T> register(hungteen.htlib.api.interfaces.raid.WaveType<T> type) {
        return registry().register(type);
    }

    public static HTSimpleRegistry<hungteen.htlib.api.interfaces.raid.WaveType<?>> registry() {
        return TYPES;
    }

    record WaveType<P extends IWaveComponent>(String name, Codec<P> codec) implements hungteen.htlib.api.interfaces.raid.WaveType<P> {

        @Override
        public String getName() {
            return name();
        }

        @Override
        public String getModID() {
            return HTLibHelper.get().getModID();
        }
    }

}
