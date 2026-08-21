package hungteen.htlib.api.interfaces;

import hungteen.htlib.common.registry.HTRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

/**
 * "简单条目"标记接口，用于 HTLib 特殊注册系统的"先于原版注册"分支。 <br>
 *
 * <p>很多模组自定义的注册项是需要保存或显示的，故需要名字来区分显示。<br>
 * 实现本接口的对象自带 {@link #getName()} 与 {@link #getModID()}（进而得到
 * 注册名 {@link #getRegistryName()} 与 {@link #getLocation()}），
 * 因此可以直接通过 {@link hungteen.htlib.api.interfaces.IHTSimpleRegistry#register(ISimpleEntry)}
 * 注册进 {@link hungteen.htlib.common.registry.HTSimpleRegistry}（底层是
 * {@link HTRegistry}，实现原版注册之前的注册），以及用于聊天/界面/物品名字显示。</p>
 *
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 16:53
 **/
public interface ISimpleEntry {

    /**
     * 获取注册项的名字。
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

    /**
     * 获取注册名。
     * @return ResourceLocation object.
     */
    default ResourceLocation getLocation(){
        return new ResourceLocation(getRegistryName());
    }

}
