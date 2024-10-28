package hungteen.htlib.client;

import hungteen.htlib.common.HTLibFabricNetworkHandler;
import net.fabricmc.api.ClientModInitializer;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/28 8:46
 **/
public class HTLibFabricClientInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HTLibFabricNetworkHandler.initClient();
    }

}