package hungteen.htlib.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-02 12:43
 **/
public class LevelRenderHandler {

    public static void renderFormations() {
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register((context) -> {
            DummyEntityRenderManager.renderFormations(context.camera());
        });
    }


}
