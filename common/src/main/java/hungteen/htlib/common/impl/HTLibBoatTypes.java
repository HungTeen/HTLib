package hungteen.htlib.common.impl;

import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.item.HTBoatDispenseItemBehavior;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import hungteen.htlib.util.HTBoatType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.Collection;
import java.util.Collections;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/4 17:07
 */
public interface HTLibBoatTypes {

    HTSimpleRegistry<HTBoatType> TYPES = HTRegistryManager.simple(HTLibHelper.prefix("boat_type"));

    HTBoatType DEFAULT = registerBoatType(new HTBoatType() {
        @Override
        public String name() {
            return "oak";
        }

        @Override
        public String getModID() {
            return "minecraft";
        }

        @Override
        public Block getPlanks() {
            return Blocks.OAK_PLANKS;
        }

        @Override
        public Item getBoatItem() {
            return Items.OAK_BOAT;
        }

        @Override
        public Item getChestBoatItem() {
            return Items.OAK_CHEST_BOAT;
        }
    });

    /**
     * Register Boat Dispense Behavior in different platforms.
     */
    static void registerDispenserBehaviors(){
        getBoatTypes().forEach(type -> {
            DispenserBlock.registerBehavior(type.getBoatItem(), new HTBoatDispenseItemBehavior(type, false));
            DispenserBlock.registerBehavior(type.getChestBoatItem(), new HTBoatDispenseItemBehavior(type, true));
        });
    }

    /**
     * Usually other mod no need to use this method, because HTLib has done everything. <br>
     */
    static HTBoatType registerBoatType(HTBoatType type) {
        return registry().register(type);
    }

    static Collection<HTBoatType> getBoatTypes() {
        return Collections.unmodifiableCollection(registry().getValues());
    }

    static HTBoatType getBoatType(String name) {
        return registry().getValue(name).orElse(HTLibBoatTypes.DEFAULT);
    }

    static HTSimpleRegistry<HTBoatType> registry(){
        return TYPES;
    }
}
