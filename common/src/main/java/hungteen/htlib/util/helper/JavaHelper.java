package hungteen.htlib.util.helper;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * What does jvav mean ?
 * @author PangTeen
 * @program HTLib
 * @data 2023/2/23 16:01
 */
public interface JavaHelper {

    static <T> boolean alwaysTrue(T t){
        return true;
    }

    static <K, V> Optional<V> getOpt(Map<K, V> map, K key){
        return Optional.ofNullable(map.getOrDefault(key, null));
    }

    static <T> Predicate<T> not(Predicate<T> predicate){
        return t -> !predicate.test(t);
    }

    static <T, R> R ifNull(T object, Function<T, R> func, R defaultValue){
        return object == null ? defaultValue : func.apply(object);
    }

    static <T, R> Stream<R> castStream(Stream<T> stream, Class<R> clazz){
        return stream.filter(clazz::isInstance).map(clazz::cast);
    }

}
