package hungteen.htlib.common.impl;

import hungteen.htlib.api.interfaces.HTSimpleRegistry;
import hungteen.htlib.common.item.HTBoatDispenseItemBehavior;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;
import hungteen.htlib.common.registry.suit.TreeSuits;
import hungteen.htlib.util.helper.HTLibHelper;
import hungteen.htlib.util.interfaces.IBoatType;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.registries.RegisterEvent;

import java.util.Collection;
import java.util.Collections;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/4 17:07
 */
public class BoatTypes {

    private static final HTSimpleRegistry<IBoatType> TYPES = HTRegistryManager.createSimple(HTLibHelper.prefix("boat_type"));

    public static void register(){
        /* Register Boat Dispense Behavior */
        getBoatTypes().forEach(type -> {
            DispenserBlock.registerBehavior(type.getBoatItem(), new HTBoatDispenseItemBehavior(type, false));
            DispenserBlock.registerBehavior(type.getChestBoatItem(), new HTBoatDispenseItemBehavior(type, true));
        });
    }

    /**
     * Usually other mod no need to use this method, because HTLib has done everything. <br>
     * {@link TreeSuits.TreeSuit#register(RegisterEvent)}.
     */
    public static IBoatType registerBoatType(IBoatType type) {
        return registry().register(type);
    }

    public static Collection<IBoatType> getBoatTypes() {
        return Collections.unmodifiableCollection(registry().getValues());
    }

    public static IBoatType getBoatType(String name) {
        return registry().getValue(name).orElse(IBoatType.DEFAULT);
    }

    public static HTSimpleRegistry<IBoatType> registry(){
        return TYPES;
    }
}
