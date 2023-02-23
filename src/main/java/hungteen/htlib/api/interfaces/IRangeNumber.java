package hungteen.htlib.api.interfaces;

/**
 * 玩家能力系统中常用的基本数值，需要保存的double, float, long, int, bool都可以使用。
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 15:20
 **/
public interface IRangeNumber<T extends Number> extends ISimpleEntry {

    T defaultData();

    T getMaxData();

    T getMinData();

}
