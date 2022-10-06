package hungteen.htlib.interfaces;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 16:53
 **/
public interface INameEntry {

    /**
     * 获取名字。
     */
    String getName();

    /**
     * 获取MOD ID。
     */
    String getModID();

    /**
     * 获取注册名。
     */
    default String getRegistryName(){
        return getModID() + ":" + getName();
    }

}
