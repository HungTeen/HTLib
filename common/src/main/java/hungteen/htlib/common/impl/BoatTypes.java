package hungteen.htlib.common.impl;

import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.item.HTBoatDispenseItemBehavior;
import hungteen.htlib.util.helper.HTLibHelper;
import hungteen.htlib.util.interfaces.BoatType;
import net.minecraft.world.level.block.DispenserBlock;

import java.util.Collection;
import java.util.Collections;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/4 17:07
 */
public class BoatTypes {

    private static final HTSimpleRegistry<BoatType> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("boat_type"));

    /**
     * Register Boat Dispense Behavior
     */
    public static void register(){
        getBoatTypes().forEach(type -> {
            DispenserBlock.registerBehavior(type.getBoatItem(), new HTBoatDispenseItemBehavior(type, false));
            DispenserBlock.registerBehavior(type.getChestBoatItem(), new HTBoatDispenseItemBehavior(type, true));
        });
    }

    /**
     * Usually other mod no need to use this method, because HTLib has done everything. <br>
     */
    public static BoatType registerBoatType(BoatType type) {
        return registry().register(type);
    }

    public static Collection<BoatType> getBoatTypes() {
        return Collections.unmodifiableCollection(registry().getValues());
    }

    public static BoatType getBoatType(String name) {
        return registry().getValue(name).orElse(BoatType.DEFAULT);
    }

    public static HTSimpleRegistry<BoatType> registry(){
        return TYPES;
    }
}
