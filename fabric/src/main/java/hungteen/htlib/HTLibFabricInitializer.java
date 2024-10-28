package hungteen.htlib;

import hungteen.htlib.common.HTLibFabricNetworkHandler;
import hungteen.htlib.common.HTLibFabricRegistryHandler;
import net.fabricmc.api.ModInitializer;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/28 8:46
 **/
public class HTLibFabricInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        HTLibFabricNetworkHandler.init();
        HTLibFabricRegistryHandler.register();
    }
}