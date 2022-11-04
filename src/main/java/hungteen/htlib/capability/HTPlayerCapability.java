package hungteen.htlib.capability;

import hungteen.htlib.interfaces.IPlayerDataManager;

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