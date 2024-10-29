package hungteen.htlib;

import hungteen.htlib.common.HTLibFabricDummyEntityHandler;
import hungteen.htlib.common.HTLibFabricNetworkHandler;
import hungteen.htlib.common.HTLibFabricRegistryHandler;
import hungteen.htlib.common.HTResourceManager;
import hungteen.htlib.common.command.HTLibCommand;
import hungteen.htlib.common.impl.HTLibBoatTypes;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

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
        HTResourceManager.init();

        HTLibBoatTypes.register();
        HTLibFabricDummyEntityHandler.registerDummyEntityEvents();

        CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) -> {
            HTLibCommand.register(dispatcher, context);
        });
    }
}