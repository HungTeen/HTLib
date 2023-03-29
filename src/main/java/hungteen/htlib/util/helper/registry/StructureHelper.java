package hungteen.htlib.util.helper.registry;

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

    private static final ResourceHelper<Structure> STRUCTURE_HELPER = new ResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<Structure>> resourceKey() {
            return Registries.STRUCTURE;
        }
    };

    private static final ResourceHelper<StructureSet> STRUCTURE_SET_HELPER = new ResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<StructureSet>> resourceKey() {
            return Registries.STRUCTURE_SET;
        }
    };

    private static final ResourceHelper<StructureTemplatePool> POOL_HELPER = new ResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<StructureTemplatePool>> resourceKey() {
            return Registries.TEMPLATE_POOL;
        }
    };

    private static final ResourceHelper<StructureProcessorList> PROCESSOR_LIST_HELPER = new ResourceHelper<>(){

        @Override
        public ResourceKey<? extends Registry<StructureProcessorList>> resourceKey() {
            return Registries.PROCESSOR_LIST;
        }
    };

    /* Common Methods */

    public static ResourceHelper<Structure> get(){
        return STRUCTURE_HELPER;
    }

    public static ResourceHelper<StructureSet> set(){
        return STRUCTURE_SET_HELPER;
    }

    public static ResourceHelper<StructureTemplatePool> pool(){
        return POOL_HELPER;
    }

    public static ResourceHelper<StructureProcessorList> processors(){
        return PROCESSOR_LIST_HELPER;
    }


}
