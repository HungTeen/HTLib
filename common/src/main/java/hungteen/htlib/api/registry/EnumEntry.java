package hungteen.htlib.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

/**
 * 有些注册项需要注册名，但是不需要序列化和反序列化，枚举更方便。
 * @author PangTeen
 * @program HTLib
 * @data 2023/2/28 19:33
 */
public interface EnumEntry extends SimpleEntry, StringRepresentable {

    @Override
    default String getName(){
        return name().toLowerCase();
    }

    @Override
    default ResourceLocation getLocation(){
        return ResourceLocation.fromNamespaceAndPath(getModID(), getName());
    }

    @Override
    default String getSerializedName(){
        return getName();
    }
}
