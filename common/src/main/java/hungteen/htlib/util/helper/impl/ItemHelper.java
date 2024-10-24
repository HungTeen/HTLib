package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:48
 **/
public interface ItemHelper {

    HTVanillaRegistryHelper<Item> HELPER = () -> BuiltInRegistries.ITEM;

    HTVanillaRegistryHelper<CreativeModeTab> TAB_HELPER = () -> BuiltInRegistries.CREATIVE_MODE_TAB;

    static ResourceLocation itemTexture(Item item) {
        return StringHelper.itemTexture(get().getKey(item));
    }

    static ResourceLocation itemTexture(Item item, String suffix) {
        return StringHelper.itemTexture(get().getKey(item), suffix);
    }

    /**
     * TODO bonus damage.
     */
    static double getItemBonusDamage(ItemStack stack, EquipmentSlot... slots){
        double damage = 0F;
//        for (EquipmentSlot slot : slots) {
//            for (AttributeModifier modifier : stack.getAttributeModifiers(slot).get(Attributes.ATTACK_DAMAGE)) {
//                if (modifier.getId() == Item.BASE_ATTACK_DAMAGE_UUID) {
//                    damage += modifier.getAmount();
//                    break;
//                }
//            }
//        }
        return damage;
    }

    /* Common Methods */

    static HTVanillaRegistryHelper<Item> get(){
        return HELPER;
    }

    static HTVanillaRegistryHelper<CreativeModeTab> tab(){
        return TAB_HELPER;
    }

}
