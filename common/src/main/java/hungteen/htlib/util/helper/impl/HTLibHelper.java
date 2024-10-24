package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.HTLibAPI;
import hungteen.htlib.api.util.helper.HTModIDHelper;
import net.minecraft.resources.ResourceLocation;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/13 14:56
 */
public interface HTLibHelper {

    HTModIDHelper HELPER = () -> HTLibAPI.MOD_ID;

    static HTModIDHelper get(){
        return HELPER;
    }

    static ResourceLocation prefix(String path){
        return get().prefix(path);
    }

}
