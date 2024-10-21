package hungteen.htlib.api.interfaces;

import hungteen.htlib.api.registry.SimpleEntry;

/**
 * 玩家能力系统中常用的基本数值，需要保存的double, float, long, int, bool都可以使用。
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 15:20
 **/
public interface RangeNumber<T extends Number> extends SimpleEntry {

    T defaultData();

    T getMaxData();

    T getMinData();

}
