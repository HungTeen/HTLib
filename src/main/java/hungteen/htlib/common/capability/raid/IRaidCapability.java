package hungteen.htlib.common.capability.raid;

import hungteen.htlib.util.interfaces.IRaid;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-12-01 09:47
 **/
@AutoRegisterCapability
public interface IRaidCapability {

    boolean isRaider();

    void setRaid(IRaid raid);

    IRaid getRaid();

    void setWave(int wave);

    int getWave();
}
