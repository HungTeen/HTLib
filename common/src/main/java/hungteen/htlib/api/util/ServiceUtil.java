package hungteen.htlib.api.util;

import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/21 15:46
 **/
public class ServiceUtil {

    public static <T> T findService(Class<T> clazz) {
        return findService(clazz, null);
    }

    /**
     * 基于 SPI 机制查找服务。
     */
    public static <T> T findService(Class<T> clazz, @Nullable Supplier<T> defaultSupplier) {
        ServiceLoader<T> loader = ServiceLoader.load(clazz);
        if (loader.findFirst().isEmpty()) {
            if (defaultSupplier != null) {
                return defaultSupplier.get();
            }
            throw new IllegalStateException("No service implementation found for " + clazz);
        } else {
            Iterator<T> iterator = loader.iterator();
            T service = iterator.next();
            if (iterator.hasNext()) {
                throw new IllegalStateException("Multiple service implementations found for " + clazz);
            }
            return service;
        }
    }
}
