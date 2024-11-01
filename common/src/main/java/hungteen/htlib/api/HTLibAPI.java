package hungteen.htlib.api;

import com.mojang.logging.LogUtils;
import hungteen.htlib.api.codec.*;
import hungteen.htlib.api.registry.HTCodecRegistry;
import hungteen.htlib.api.registry.HTCustomRegistry;
import hungteen.htlib.api.util.helper.ServiceHelper;
import org.slf4j.Logger;

import java.util.Optional;

/**
 * HTLib 对外提供的 API 接口，不依赖于平台，只有一个有效实现。
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/22 8:56
 */
public interface HTLibAPI {

    String MOD_ID = "htlib";

    Logger LOGGER = LogUtils.getLogger();

    HTLibAPI INSTANCE = ServiceHelper.findService(HTLibAPI.class, () -> new HTLibAPI() {});

    /**
     * Obtain the Mod API, either a valid implementation if mod is present, else
     * a dummy instance instead if mod is absent.
     */
    static HTLibAPI get() {
        return INSTANCE;
    }

    static String id() {
        return MOD_ID;
    }

    /**
     * @return the log instance for the mod.
     */
    static Logger logger() {
        return LOGGER;
    }

    /**
     * @return A unique version number for this version of the API.
     */
    default int apiVersion() {
        return 0;
    }

    /**
     * @return Codec registry for entity predicate.
     */
    default Optional<HTCodecRegistry<HTEntityPredicate>> entityPredicateRegistry(){
        return Optional.empty();
    }

    /**
     * @return Custom registry for entity predicate block.
     */
    default Optional<HTCustomRegistry<HTEntityPredicateType<?>>> entityPredicateTypeRegistry(){
        return Optional.empty();
    }

    /**
     * @return Codec registry for item predicate.
     */
    default Optional<HTCodecRegistry<HTItemPredicate>> itemPredicateRegistry(){
        return Optional.empty();
    }

    /**
     * @return Custom registry for item predicate block.
     */
    default Optional<HTCustomRegistry<HTItemPredicateType<?>>> itemPredicateTypeRegistry(){
        return Optional.empty();
    }

    /**
     * @return Codec registry for block predicate.
     */
    default Optional<HTCodecRegistry<HTBlockPredicate>> blockPredicateRegistry(){
        return Optional.empty();
    }

    /**
     * @return Custom registry for block predicate block.
     */
    default Optional<HTCustomRegistry<HTBlockPredicateType<?>>> blockPredicateTypeRegistry(){
        return Optional.empty();
    }

}
