package hungteen.htlib.util.helper.registry;

import hungteen.htlib.api.interfaces.IHTResourceHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/29 19:42
 */
public class StructureHelper {

    private static final IHTResourceHelper<Structure> STRUCTURE_HELPER = new IHTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<Structure>> resourceKey() {
            return Registries.STRUCTURE;
        }
    };

    private static final IHTResourceHelper<StructureSet> STRUCTURE_SET_HELPER = new IHTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<StructureSet>> resourceKey() {
            return Registries.STRUCTURE_SET;
        }
    };

    private static final IHTResourceHelper<StructureTemplatePool> POOL_HELPER = new IHTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<StructureTemplatePool>> resourceKey() {
            return Registries.TEMPLATE_POOL;
        }
    };

    private static final IHTResourceHelper<StructureProcessorList> PROCESSOR_LIST_HELPER = new IHTResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<StructureProcessorList>> resourceKey() {
            return Registries.PROCESSOR_LIST;
        }
    };

    /* Common Methods */

    public static IHTResourceHelper<Structure> get(){
        return STRUCTURE_HELPER;
    }

    public static IHTResourceHelper<StructureSet> set(){
        return STRUCTURE_SET_HELPER;
    }

    public static IHTResourceHelper<StructureTemplatePool> pool(){
        return POOL_HELPER;
    }

    public static IHTResourceHelper<StructureProcessorList> processors(){
        return PROCESSOR_LIST_HELPER;
    }


}
