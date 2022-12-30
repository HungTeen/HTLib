package hungteen.htlib.impl.result;

import com.mojang.serialization.Codec;
import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTCodecRegistry;
import hungteen.htlib.common.registry.HTRegistryHolder;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.api.interfaces.raid.IResultComponent;
import hungteen.htlib.api.interfaces.raid.IResultComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-04 09:47
 **/
public class HTResultComponents {

    public static final HTSimpleRegistry<IResultComponentType<?>> RESULT_TYPE = HTRegistryManager.create(HTLib.prefix("result_type"));
    public static final HTCodecRegistry<IResultComponent> RESULTS = HTRegistryManager.create(IResultComponent.class, "custom_raid/results", HTResultComponents::getCodec);

    /* Result types */

    public static final IResultComponentType<ItemStackReward> ITEM_STACK_TYPE = new DefaultResultType<>("item_stack", ItemStackReward.CODEC);

    /* Result */

    public static final HTRegistryHolder<IResultComponent> TEST = RESULTS.innerRegister(
            HTLib.prefix("test"), new ItemStackReward(
                    true, false,
                    Arrays.asList(new ItemStack(Items.ACACIA_BOAT, 3, new CompoundTag()))
            )
    );

    /**
     * {@link HTLib#HTLib()}
     */
    public static void registerStuffs(){
        List.of(ITEM_STACK_TYPE).forEach(HTResultComponents::registerWaveType);
    }

    public static void registerWaveType(IResultComponentType<?> type){
        RESULT_TYPE.register(type);
    }

    public static Codec<IResultComponent> getCodec(){
        return RESULT_TYPE.byNameCodec().dispatch(IResultComponent::getType, IResultComponentType::codec);
    }

    protected record DefaultResultType<P extends IResultComponent>(String name, Codec<P> codec) implements IResultComponentType<P> {

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getModID() {
            return HTLib.MOD_ID;
        }
    }
}
