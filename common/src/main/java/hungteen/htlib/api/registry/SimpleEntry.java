package hungteen.htlib.api.registry;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * 很多模组自定义的注册项是需要保存或显示的，故需要名字来区分显示。<br>
 * 使用该接口可以利用 {@link HTRegistry} 实现注册。<br>
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 16:53
 **/
public interface SimpleEntry {

    /**
     * 获取注册项的名称。
     * @return real name of the registry.
     */
    String getName();

    /**
     * 获取注册项的命名空间。
     * @return namespace of the registry.
     */
    String getModID();

    /**
     * 获取文本显示。
     * @return Text component.
     */
    default MutableComponent getComponent(){
        return Component.empty();
    }

    /**
     * 获取注册名。
     * @return String object.
     */
    default String getRegistryName(){
        return getModID() + ":" + getName();
    }

    default ResourceLocation getLocation(){
        return ResourceLocation.parse(getRegistryName());
    }

}
