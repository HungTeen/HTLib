package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:48
 **/
public class ItemHelper {

    private static final RegistryHelper<Item> HELPER = new RegistryHelper<>(){
        @Override
        public Either<IForgeRegistry<Item>, Registry<Item>> getRegistry() {
            return Either.left(ForgeRegistries.ITEMS);
        }
    };

    private static final RegistryHelper<CreativeModeTab> TAB_HELPER = new RegistryHelper<>(){
        @Override
        public Either<IForgeRegistry<CreativeModeTab>, Registry<CreativeModeTab>> getRegistry() {
            return Either.right(BuiltInRegistries.CREATIVE_MODE_TAB);
        }
    };

    private static final RegistryHelper<Enchantment> ENCHANTMENT_HELPER = new RegistryHelper<>(){
        @Override
        public Either<IForgeRegistry<Enchantment>, Registry<Enchantment>> getRegistry() {
            return Either.left(ForgeRegistries.ENCHANTMENTS);
        }
    };

    public static ResourceLocation itemTexture(Item item) {
        return StringHelper.itemTexture(get().getKey(item));
    }

    public static ResourceLocation itemTexture(Item item, String suffix) {
        return StringHelper.itemTexture(get().getKey(item), suffix);
    }

    /* Common Methods */

    public static RegistryHelper<Item> get(){
        return HELPER;
    }

    public static RegistryHelper<CreativeModeTab> tab(){
        return TAB_HELPER;
    }

    public static RegistryHelper<Enchantment> enchant(){
        return ENCHANTMENT_HELPER;
    }



}
