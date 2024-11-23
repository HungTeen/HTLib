package hungteen.htlib.common.registry;

import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.common.impl.registry.HTRegistryManager;
import hungteen.htlib.common.registry.suit.HTBlockSuit;
import hungteen.htlib.util.helper.impl.HTLibHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2024/11/23 17:02
 **/
public interface HTLibBlockSuits {

    HTCustomRegistry<HTBlockSuit<?>> BLOCK_SUITS = HTRegistryManager.custom(HTLibHelper.prefix("block_suit"));


    static HTCustomRegistry<HTBlockSuit<?>> registry() {
        return BLOCK_SUITS;
    }
}
