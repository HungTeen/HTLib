package hungteen.htlib.common.impl.result;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.api.interfaces.raid.IResultType;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-04 09:47
 **/
public class HTResultComponents {

//    public static final HTCodecRegistry<IResultComponent> RESULTS = HTRegistryManager.create(IResultComponent.class, "custom_raid/results", HTResultComponents::getCodec, true);

//    public static final HTRegistryHolder<IResultComponent> TEST = RESULTS.innerRegister(
//            HTLibHelper.prefix("test"), new ItemStackResult(
//                    true, false,
//                    Arrays.asList(new ItemStack(Items.ACACIA_BOAT, 3, new CompoundTag()))
//            )
//    );


    public static Codec<IResultComponent> getCodec(){
        return HTResultTypes.registry().byNameCodec().dispatch(IResultComponent::getType, IResultType::codec);
    }

}
