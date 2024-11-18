package hungteen.htlib.common.registry;

import hungteen.htlib.api.registry.HTSimpleRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.impl.registry.HTSimpleRegistryImpl;
import hungteen.htlib.common.registry.suit.HTStoneSuit;
import hungteen.htlib.util.helper.impl.HTLibHelper;

import java.util.Collection;
import java.util.Collections;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/4 15:05
 */
public interface HTLibStoneSuits {

    HTSimpleRegistryImpl<HTStoneSuit> SUITS = HTRegistryManager.simple(HTLibHelper.prefix("stone_suit"));

    /**
     * Modders should call this method in their mod constructor.
     */
    static HTStoneSuit register(HTStoneSuit type) {
        return registry().register(type);
    }

    static Collection<HTStoneSuit> getSuits() {
        return Collections.unmodifiableCollection(registry().getValues());
    }

    static HTSimpleRegistry<HTStoneSuit> registry(){
        return SUITS;
    }

}
