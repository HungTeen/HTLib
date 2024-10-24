package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTResourceHelper;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/29 19:42
 */
public interface StructureHelper {

     HTResourceHelper<Structure> STRUCTURE_HELPER = () -> Registries.STRUCTURE;

     HTResourceHelper<StructureSet> STRUCTURE_SET_HELPER = () -> Registries.STRUCTURE_SET;

     HTResourceHelper<StructureTemplatePool> POOL_HELPER = () -> Registries.TEMPLATE_POOL;

     HTResourceHelper<StructureProcessorList> PROCESSOR_LIST_HELPER = () -> Registries.PROCESSOR_LIST;

    /* Common Methods */

    static HTResourceHelper<Structure> get(){
        return STRUCTURE_HELPER;
    }

    static HTResourceHelper<StructureSet> set(){
        return STRUCTURE_SET_HELPER;
    }

    static HTResourceHelper<StructureTemplatePool> pool(){
        return POOL_HELPER;
    }

    static HTResourceHelper<StructureProcessorList> processors(){
        return PROCESSOR_LIST_HELPER;
    }


}
