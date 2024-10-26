package hungteen.htlib.common.capability.player;

import hungteen.htlib.platform.IPlayerDataManager;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 15:24
 **/
public abstract class HTPlayerCapability<T extends IPlayerDataManager> implements IHTPlayerCapability<T> {

    protected T dataManager = null;

    @Override
    public T get() {
        return dataManager;
    }
}
