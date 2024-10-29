package hungteen.htlib.client.render;

import hungteen.htlib.api.HTLibAPI;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-02 12:43
 **/
@Mod.EventBusSubscriber(modid = HTLibAPI.MOD_ID, value = Dist.CLIENT)
public class LevelRenderHandler {

    @SubscribeEvent
    public static void gatherComponents(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            DummyEntityRenderManager.renderFormations(event.getCamera());
        }
    }

}
