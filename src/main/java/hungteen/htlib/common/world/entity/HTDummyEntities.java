package hungteen.htlib.common.world.entity;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.registry.HTRegistryManager;
import hungteen.htlib.common.registry.HTSimpleRegistry;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 23:09
 **/
public class HTDummyEntities {

    public static final HTSimpleRegistry<DummyEntityType<?>> DUMMY_ENTITY_TYPES = HTRegistryManager.create(HTLib.prefix("dummy_entities"));

    public static void registerStuffs(){

    }

    public static void register(DummyEntityType<?> entityType){
        DUMMY_ENTITY_TYPES.register(entityType);
    }

}
