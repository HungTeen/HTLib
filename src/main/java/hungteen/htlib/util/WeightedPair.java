package hungteen.htlib.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/16 15:05
 */
public class WeightedPair<T> implements WeightedEntry {

    private final Pair<T, Weight> pair;

    public WeightedPair(Pair<T, Weight> pair) {
        this.pair = pair;
    }

    public static <T> WeightedPair<T> create(Pair<T, Weight> pair) {
        return new WeightedPair<>(pair);
    }

    public Pair<T, Weight> unwrap() {
        return this.pair;
    }

    public T getItem(){
        return pair.getFirst();
    }

    @Override
    public Weight getWeight() {
        return pair.getSecond();
    }

    public static <T> Codec<WeightedPair<T>> codec(Codec<T> codec) {
        return Codec.pair(codec, Weight.CODEC).xmap(WeightedPair::create, WeightedPair::unwrap);
    }
}
