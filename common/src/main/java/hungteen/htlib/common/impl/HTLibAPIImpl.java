package hungteen.htlib.common.impl;

import hungteen.htlib.api.HTLibAPI;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/22 9:02
 */
public class HTLibAPIImpl implements HTLibAPI {

    @Override
    public int apiVersion() {
        return 1;
    }

}
