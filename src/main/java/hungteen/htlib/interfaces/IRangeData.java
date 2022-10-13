package hungteen.htlib.interfaces;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 15:20
 *
 * define range data type. <br>
 * use HashMap to store range data later.
 **/
public interface IRangeData<T extends Number> extends IComponentEntry {

    T defaultData();

    T getMaxData();

    T getMinData();

}
