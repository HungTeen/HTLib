package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTModIDHelper;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/13 15:07
 */
public interface ForgeHelper {

     HTModIDHelper HELPER = () -> HTModIDHelper.FORGE;

    static HTModIDHelper get(){
        return HELPER;
    }

}
