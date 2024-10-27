package hungteen.htlib.platform;

import net.neoforged.neoforgespi.language.IModInfo;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/26 16:34
 **/
public class HTNeoModInfo implements HTModInfo {

    private final IModInfo modInfo;

    public HTNeoModInfo(IModInfo modInfo) {
        this.modInfo = modInfo;
    }

    @Override
    public String getModId() {
        return modInfo.getModId();
    }
}
