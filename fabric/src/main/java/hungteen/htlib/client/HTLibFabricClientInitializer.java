package hungteen.htlib.client;

import hungteen.htlib.client.render.LevelRenderHandler;
import hungteen.htlib.common.HTLibFabricNetworkHandler;
import hungteen.htlib.common.HTLibProxy;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/28 8:46
 **/
@Environment(EnvType.CLIENT)
public class HTLibFabricClientInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HTLibFabricNetworkHandler.initClient();

        ClientRegister.registerRenderers();
        ClientRegister.registerRendererLayers();

        LevelRenderHandler.renderFormations();

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            HTLibProxy.get().clearDummyEntities();
        });
    }

}