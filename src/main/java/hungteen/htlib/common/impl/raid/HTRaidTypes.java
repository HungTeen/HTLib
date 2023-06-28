package hungteen.htlib.common.impl.raid;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTSimpleRegistry;
import hungteen.htlib.api.interfaces.raid.IRaidComponent;
import hungteen.htlib.api.interfaces.raid.IRaidType;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.util.helper.HTLibHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 15:40
 */
public class HTRaidTypes {

    private static final HTSimpleRegistry<IRaidType<?>> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("raid_type"));

    public static final IRaidType<CommonRaid> COMMON = register(new RaidType<>("common", CommonRaid.CODEC));

    public static <T extends IRaidComponent> IRaidType<T> register(IRaidType<T> type){
        return registry().register(type);
    }

    public static IHTSimpleRegistry<IRaidType<?>> registry(){
        return TYPES;
    }

    record RaidType<P extends IRaidComponent>(String name, Codec<P> codec) implements IRaidType<P> {

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
