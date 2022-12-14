package hungteen.htlib.api.interfaces;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 16:53
 *
 * 很多模组自定义的注册项是需要保存或显示的，故需要名字来区分显示。
 **/
public interface ISimpleEntry {

    /**
     * 获取名字。
     */
    String getName();

    /**
     * 获取MOD ID。
     */
    String getModID();

    /**
     * 获取文本显示。
     */
    default MutableComponent getComponent(){
        return Component.empty();
    }

    /**
     * 获取注册名。
     */
    default String getRegistryName(){
        return getModID() + ":" + getName();
    }

    /**
     * 获取注册名。
     */
    default ResourceLocation getLocation(){
        return new ResourceLocation(getRegistryName());
    }

}
