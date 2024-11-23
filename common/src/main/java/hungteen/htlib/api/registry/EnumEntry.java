package hungteen.htlib.api.registry;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

/**
 * 有些注册项需要注册名，但是不需要序列化和反序列化，枚举更方便。
 * @author PangTeen
 * @program HTLib
 * @data 2023/2/28 19:33
 */
public interface EnumEntry extends SimpleEntry, StringRepresentable {

    @Override
    default String name(){
        return toString().toLowerCase(Locale.ROOT);
    }

    @Override
    default String getSerializedName(){
        return name();
    }
}
