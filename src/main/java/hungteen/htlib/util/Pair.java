package hungteen.htlib.util;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-25 07:35
 **/
public record Pair<T, K>(T first, K second) {

    public T getFirst() {
        return first;
    }

    public K getSecond() {
        return second;
    }

    public static <T, K> Pair<T, K> of(T first, K second) {
        return new Pair<>(first, second);
    }

}
