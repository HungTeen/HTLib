package hungteen.htlib.util;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 22:44
 **/
public record Triple<T, K, U>(T left, K mid, U right) {

    public T getLeft() { return left; }

    public K getMid() { return mid; }

    public U getRight() { return right; }

    public static <T, K, U> Triple<T, K, U> of(T left, K mid, U right) {
        return new Triple<>(left, mid, right);
    }
}
