package hungteen.htlib.client.render;

import hungteen.htlib.api.HTLibAPI;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-02 12:43
 **/
@EventBusSubscriber(modid = HTLibAPI.MOD_ID, value = Dist.CLIENT)
public class LevelRenderHandler {

    @SubscribeEvent
    public static void gatherComponents(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            DummyEntityRenderManager.renderFormations(event.getCamera());
        }
    }

}
