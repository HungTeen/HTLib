package hungteen.htlib.common.impl.wave;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.api.interfaces.raid.IWaveComponent;
import hungteen.htlib.api.interfaces.raid.IWaveType;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.util.helper.HTLibHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 16:10
 */
public class HTWaveTypes {

    private static final HTSimpleRegistry<IWaveType<?>> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("wave_type"));

    public static final IWaveType<CommonWave> COMMON = register(new WaveType<>("common", CommonWave.CODEC));

    public static <T extends IWaveComponent> IWaveType<T> register(IWaveType<T> type) {
        return registry().register(type);
    }

    public static IHTSimpleRegistry<IWaveType<?>> registry() {
        return TYPES;
    }

    record WaveType<P extends IWaveComponent>(String name, Codec<P> codec) implements IWaveType<P> {

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
