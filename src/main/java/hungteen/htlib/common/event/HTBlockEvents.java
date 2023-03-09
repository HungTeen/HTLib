package hungteen.htlib.common.event;

import hungteen.htlib.HTLib;
import hungteen.htlib.util.helper.registry.BlockHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:59
 **/
@Mod.EventBusSubscriber(modid = HTLib.MOD_ID)
public class HTBlockEvents {

    @SubscribeEvent
    public static void onToolModifyBlock(BlockEvent.BlockToolModificationEvent event){
        if(event.getToolAction() == ToolActions.AXE_STRIP){
            if(! event.isSimulated() && BlockHelper.canBeStripped(event.getFinalState().getBlock())){
                final Block newBlock = BlockHelper.getStrippedBlock(event.getFinalState().getBlock());
                final BlockState newState = newBlock.defaultBlockState().setValue(RotatedPillarBlock.AXIS, event.getFinalState().getValue(RotatedPillarBlock.AXIS));
                event.setFinalState(newState);
            }
        }
    }

}
