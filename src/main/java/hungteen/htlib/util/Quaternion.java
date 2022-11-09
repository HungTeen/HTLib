package hungteen.htlib.util;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-04 09:59
 **/
public record Quaternion<T>(Pair<T, T> first, Pair<T, T> second) {

    public Quaternion(Pair<T, T> first, Pair<T, T> second){
        this.first = first;
        this.second = second;
    }

    public Quaternion(T a, T b, T c, T d){
        this(Pair.of(a, b), Pair.of(c, d));
    }

    public T getFirst() {
        return first.getFirst();
    }

    public T getSecond() {
        return first.getSecond();
    }

    public T getThird() {
        return second.getFirst();
    }

    public T getFourth() {
        return second.getSecond();
    }
}
