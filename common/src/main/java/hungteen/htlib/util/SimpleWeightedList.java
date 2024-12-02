package hungteen.htlib.util;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    public Optional<T> getItem(RandomSource rand) {
        return this.getRandomItem(rand).map(WeightedEntry.Wrapper::data);
    }

    public List<T> getItems(RandomSource rand, int count, boolean different) {
        return this.getRandomItems(rand, count, different).stream().map(WeightedEntry.Wrapper::data).toList();
    }

    public static <T> Codec<SimpleWeightedList<T>> wrappedCodecAllowingEmpty(Codec<T> codec) {
        return WeightedEntry.Wrapper.<T>codec(codec).listOf().xmap(SimpleWeightedList::new, SimpleWeightedList::unwrap);
    }

    public static <T> Codec<SimpleWeightedList<T>> wrappedCodec(Codec<T> codec) {
        return ExtraCodecs.nonEmptyList(WeightedEntry.Wrapper.<T>codec(codec).listOf()).xmap(SimpleWeightedList::new, SimpleWeightedList::unwrap);
    }

    public static <T> SimpleWeightedList.Builder<T> builder() {
        return new SimpleWeightedList.Builder<>();
    }

    public static <T> SimpleWeightedList<T> empty() {
        return new SimpleWeightedList<>(List.of());
    }

    public static <T> SimpleWeightedList<T> single(T item) {
        return list(List.of(item));
    }

    public static <T> SimpleWeightedList<T> pair(T item1, T item2) {
        return list(Arrays.asList(item1, item2));
    }

    public static <T> SimpleWeightedList<T> list(List<T> items) {
        return new SimpleWeightedList<>(items.stream().map(t -> WeightedEntry.wrap(t, 1)).toList());
    }

    public static <T> SimpleWeightedList<T> list(List<T> items, List<Integer> weights) {
        Builder<T> builder = builder();
        for (int i = 0; i < Math.min(items.size(), weights.size()); i++) {
            builder.add(items.get(i), weights.get(i));
        }
        return builder.build();
    }

    public static <T> SimpleWeightedList<T> list(T[] items, int[] weights) {
        Builder<T> builder = builder();
        for (int i = 0; i < Math.min(items.length, weights.length); i++) {
            builder.add(items[i], weights[i]);
        }
        return builder.build();
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
