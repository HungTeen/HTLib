package hungteen.htlib.common.impl.wave;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.raid.IWaveComponent;
import hungteen.htlib.api.interfaces.raid.WaveType;
import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.util.helper.HTLibHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 16:10
 */
public interface HTWaveTypes {

    HTSimpleRegistry<WaveType<?>> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("wave_type"));

    WaveType<CommonWave> COMMON = register(new WaveTypeImpl<>("common", CommonWave.CODEC));

    static <T extends IWaveComponent> WaveType<T> register(WaveType<T> type) {
        return registry().register(type);
    }

    static HTSimpleRegistry<WaveType<?>> registry() {
        return TYPES;
    }

    record WaveTypeImpl<P extends IWaveComponent>(String name, Codec<P> codec) implements WaveType<P> {

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
