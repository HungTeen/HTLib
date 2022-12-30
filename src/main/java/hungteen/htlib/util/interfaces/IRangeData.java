package hungteen.htlib.util.interfaces;

import hungteen.htlib.api.interfaces.ISimpleEntry;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 15:20
 *
 * define range data entityType. <br>
 * use HashMap to store range data later.
 **/
public interface IRangeData<T extends Number> extends ISimpleEntry {

    T defaultData();

    T getMaxData();

    T getMinData();

}
