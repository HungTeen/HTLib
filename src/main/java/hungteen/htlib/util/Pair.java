package hungteen.htlib.util;

import java.util.Map;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-25 07:35
 **/
public record Pair<T, K>(T first, K second) {

    public T getFirst() {
        return first();
    }

    public K getSecond() {
        return second();
    }

    /**
     * Vanilla usage.
     */
    public com.mojang.datafixers.util.Pair<T, K> cast(){
        return com.mojang.datafixers.util.Pair.of(getFirst(), getSecond());
    }

    public static <T, K> Pair<T, K> of(Map.Entry<T, K> entry){
        return of(entry.getKey(), entry.getValue());
    }

    public static <T, K> Pair<T, K> of(T first, K second) {
        return new Pair<>(first, second);
    }

}
