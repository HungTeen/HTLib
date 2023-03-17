package hungteen.htlib.util;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;

import java.util.List;

/**
 * Copy from {@link SimpleWeightedRandomList}. <br>
 * Item no need to extend {@link WeightedEntry} <br>
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/16 15:05
 */
public class SimpleWeightedList<T> extends WeightedList<WeightedEntry.Wrapper<T>> {


    SimpleWeightedList(List<WeightedEntry.Wrapper<T>> items) {
        super(items);
    }

    SimpleWeightedList(List<WeightedEntry.Wrapper<T>> items, int totalWeight) {
        super(items, totalWeight);
    }

    public static <T> Codec<SimpleWeightedList<T>> wrappedCodecAllowingEmpty(Codec<T> codec) {
        return WeightedEntry.Wrapper.<T>codec(codec).listOf().xmap(SimpleWeightedList::new, SimpleWeightedList::unwrap);
    }

    public static <T> Codec<SimpleWeightedList<T>> wrappedCodec(Codec<T> codec) {
        return ExtraCodecs.nonEmptyList(WeightedEntry.Wrapper.<T>codec(codec).listOf()).xmap(SimpleWeightedList::new, SimpleWeightedList::unwrap);
    }

    public static class Builder<T> {
        private final ImmutableList.Builder<WeightedEntry.Wrapper<T>> result = ImmutableList.builder();
        private int totalWeight = -1;

        public SimpleWeightedList.Builder<T> add(T item, int weight) {
            this.result.add(WeightedEntry.wrap(item, weight));
            return this;
        }

        public SimpleWeightedList.Builder<T> weight(int weight) {
            this.totalWeight = weight;
            return this;
        }

        public SimpleWeightedList<T> build() {
            return this.totalWeight == -1 ? new SimpleWeightedList<>(this.result.build()) : new SimpleWeightedList<>(this.result.build(), this.totalWeight);
        }
    }
}
