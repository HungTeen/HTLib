package hungteen.htlib.platform;

import net.fabricmc.loader.api.ModContainer;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/26 16:34
 **/
public class HTFabricModInfo implements HTModInfo {

    private final ModContainer container;

    public HTFabricModInfo(ModContainer container) {
        this.container = container;
    }

    @Override
    public String getModId() {
        return container.getMetadata().getId();
    }
}
