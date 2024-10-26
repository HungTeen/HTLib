package hungteen.htlib.platform;

import net.minecraftforge.forgespi.language.IModInfo;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/26 16:34
 **/
public class HTForgeModInfo implements HTModInfo {

    private final IModInfo modInfo;

    public HTForgeModInfo(IModInfo modInfo) {
        this.modInfo = modInfo;
    }

    @Override
    public String getModId() {
        return modInfo.getModId();
    }
}
