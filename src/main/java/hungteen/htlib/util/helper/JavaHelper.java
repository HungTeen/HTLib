package hungteen.htlib.util.helper;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * What does jvav mean ?
 * @author PangTeen
 * @program HTLib
 * @data 2023/2/23 16:01
 */
public class JavaHelper {

    public static <T> boolean alwaysTrue(T t){
        return true;
    }

    public static <K, V> Optional<V> getOpt(Map<K, V> map, K key){
        return Optional.ofNullable(map.getOrDefault(key, null));
    }

    public static <T> Predicate<T> not(Predicate<T> predicate){
        return t -> !predicate.test(t);
    }

}
