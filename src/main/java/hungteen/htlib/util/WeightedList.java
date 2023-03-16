package hungteen.htlib.util;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.util.random.WeightedRandomList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Copy from {@link WeightedRandomList}. <br>
 * 用于在一堆item中根据权重进行选择，麻将提供的不能选多个，也不能轮空。 <br>
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-24 23:08
 **/
public class WeightedList<T extends WeightedEntry> {

    private final int totalWeight;
    private final ImmutableList<T> items;

    public WeightedList(List<T> items) {
        this(items, 0);
    }

    public WeightedList(List<T> items, int totalWeight) {
        this.items = ImmutableList.copyOf(items);
        this.totalWeight = Math.max(WeightedRandom.getTotalWeight(items), totalWeight);
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public int getItemCount() {
        return this.items.size();
    }

    /**
     * get the weight item randomly.
     */
    public Optional<T> getRandomItem(RandomSource rand) {
        return WeightedRandom.getRandomItem(rand, this.items, this.totalWeight);
    }

    /**
     * get the weight item randomly.
     */
    public List<T> getRandomItems(RandomSource rand, int itemCount, boolean different) {
        final List<T> resultList = new ArrayList<>();
        for(int i = 0; i < itemCount; ++ i){
            getRandomItem(rand).ifPresent(item -> {
                if(! different || ! resultList.contains(item)){
                    resultList.add(item);
                }
            });
        }
        return resultList;
    }

    public static <E extends WeightedEntry> WeightedList<E> create() {
        return new WeightedList<>(ImmutableList.of());
    }

    @SafeVarargs
    public static <E extends WeightedEntry> WeightedList<E> create(E... items) {
        return new WeightedList<>(ImmutableList.copyOf(items));
    }

    public static <E extends WeightedEntry> WeightedList<E> create(List<E> items) {
        return new WeightedList<>(items);
    }

    public List<T> unwrap() {
        return this.items;
    }

    public static <E extends WeightedEntry> Codec<WeightedList<E>> codec(Codec<E> codec) {
        return codec.listOf().xmap(WeightedList::create, WeightedList::unwrap);
    }

    public static <E> Codec<WeightedList<WeightedPair<E>>> pair(Codec<E> codec) {
        return codec(WeightedPair.codec(codec));
    }

}