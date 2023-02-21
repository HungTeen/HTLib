package hungteen.htlib.api;

import com.google.common.base.Suppliers;
import hungteen.htlib.HTLib;

import java.lang.reflect.Constructor;
import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2022/11/22 8:56
 */
public class HTAPI {

    /**
     * The API method copy from Patchouli, thanks Vazkii !!!
     */
    private static final Supplier<IHTAPI> LAZY_INSTANCE = Suppliers.memoize(() -> {
        try {
            Class<?> classes = Class.forName("hungteen.htlib.common.impl.HTAPIImpl");
            Constructor<?> constructor = classes.getDeclaredConstructor();
            return (IHTAPI) constructor.newInstance();
        } catch (ReflectiveOperationException e) {
            HTLib.getLogger().warn("Unable to find implemented API, using a dummy one");
            return DummyAPI.INSTANCE;
        }
    });

    /**
     * Obtain the Mod API, either a valid implementation if mod is present, else
     * a dummy instance instead if mod is absent.
     */
    public static IHTAPI get() {
        return LAZY_INSTANCE.get();
    }

    /**
     * mod has two implemented API. <br>
     * a dummy one {@link DummyAPI} and a implemented one {@link } <br>
     * all implement code are below impl package.
     */
    public interface IHTAPI {



    }
}
