package hungteen.htlib.util.helper.registry;

import hungteen.htlib.api.interfaces.HTResourceHelper;
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
public class StructureHelper {

    private static final HTResourceHelper<Structure> STRUCTURE_HELPER = () -> Registries.STRUCTURE;

    private static final HTResourceHelper<StructureSet> STRUCTURE_SET_HELPER = () -> Registries.STRUCTURE_SET;

    private static final HTResourceHelper<StructureTemplatePool> POOL_HELPER = () -> Registries.TEMPLATE_POOL;

    private static final HTResourceHelper<StructureProcessorList> PROCESSOR_LIST_HELPER = () -> Registries.PROCESSOR_LIST;

    /* Common Methods */

    public static HTResourceHelper<Structure> get(){
        return STRUCTURE_HELPER;
    }

    public static HTResourceHelper<StructureSet> set(){
        return STRUCTURE_SET_HELPER;
    }

    public static HTResourceHelper<StructureTemplatePool> pool(){
        return POOL_HELPER;
    }

    public static HTResourceHelper<StructureProcessorList> processors(){
        return PROCESSOR_LIST_HELPER;
    }


}
