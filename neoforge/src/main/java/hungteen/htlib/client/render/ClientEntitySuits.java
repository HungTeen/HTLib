package hungteen.htlib.client.render;

import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTSimpleRegistryImpl;
import hungteen.htlib.util.helper.impl.HTLibHelper;
import net.minecraft.world.entity.Entity;

import java.util.Collection;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2024/12/2 22:01
 **/
public class ClientEntitySuits {

    private static final HTSimpleRegistryImpl<HTClientEntitySuit<?>> SUITS = HTRegistryManager.simple(HTLibHelper.prefix("entity_suit"));

    public static HTSimpleRegistry<HTClientEntitySuit<?>> registry() {
        return SUITS;
    }

    public static <T extends Entity> HTClientEntitySuit<T> register(HTClientEntitySuit<T> suit) {
        return registry().register(suit);
    }

    public static Collection<HTClientEntitySuit<?>> getSuits() {
        return registry().getValues();
    }
}
