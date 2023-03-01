package hungteen.htlib.util;

import net.minecraft.util.RandomSource;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用于在一堆item中根据权重进行选择。
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-24 23:08
 **/
public class WeightList<T> {

    private final List<T> itemList; // items to be selected.
    private final List<Integer> weightList; // weight of items
    private T leftItem = null; // alternative item.
    protected int totalWeight = 0;

    public WeightList() {
        this.itemList = new ArrayList<>();
        this.weightList = new ArrayList<>();
    }

    public WeightList(List<T> itemList, Function<T, Integer> func) {
        this(itemList, itemList.stream().map(func).collect(Collectors.toList()));
    }

    public WeightList(List<T> itemList, List<Integer> weightList) {
        this.itemList = new ArrayList<>(itemList);
        this.weightList = new ArrayList<>(weightList);
        this.totalWeight = this.weightList.stream().reduce(Integer::sum).orElseGet(() -> 0);
    }

    public WeightList<T> addItem(T item, int w) {
        this.itemList.add(item);
        this.weightList.add(w);
        this.totalWeight += w;
        return this;
    }

    /**
     * 设置保底。
     */
    public WeightList<T> setLeftItem(T item) {
        this.leftItem = item;
        return this;
    }

    /**
     * set total range may cause empty getting item.
     */
    public WeightList<T> setTotalWeight(int total) {
        this.totalWeight = Math.max(total, this.totalWeight);
        return this;
    }

    public boolean isEmpty() {
        return this.itemList.isEmpty();
    }

    public int getLen() {
        return this.itemList.size();
    }

    public void clear() {
        this.itemList.clear();
        this.weightList.clear();
        this.leftItem = null;
        this.totalWeight = 0;
    }

    public void addAll(WeightList<T> list) {
        for (int i = 0; i < list.getLen(); ++i) {
            this.addItem(list.getItem(i), list.getWeight(i));
        }
    }

    public List<T> getItemList() {
        return Collections.unmodifiableList(this.itemList);
    }

    public T getItem(int pos) {
        return this.itemList.get(pos);
    }

    public void removeItem(int pos) {
        this.itemList.remove(pos);
        this.weightList.remove(pos);
    }

    public void removeItem(T item) {
        this.removeItem(this.itemList.indexOf(item));
    }

    private int getWeight(int pos) {
        return this.weightList.get(pos);
    }

    /**
     * get the weight item randomly.
     */
    public Optional<T> getRandomItem(RandomSource rand) {
        if (this.totalWeight > 0) {
            final int pos = rand.nextInt(this.totalWeight);
            int now = 0;
            for (int i = 0; i < this.itemList.size(); ++i) {
                now += this.weightList.get(i);
                if (pos < now) {
                    return Optional.ofNullable(this.itemList.get(i));
                }
            }
        }
        return Optional.ofNullable(this.leftItem);
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

    /**
     * add several items with weight.
     */
    @SafeVarargs
    public static <W> WeightList<W> of(Pair<W, Integer>... pairs) {
        return of(Arrays.stream(pairs).map(Pair::cast).toArray(com.mojang.datafixers.util.Pair[]::new));
    }

    /**
     * set total while adding.
     */
    @SafeVarargs
    public static <W> WeightList<W> of(int tot, Pair<W, Integer>... pairs) {
        return of(pairs).setTotalWeight(tot);
    }

    /**
     * add several items with weight.
     */
    @SafeVarargs
    public static <W> WeightList<W> of(com.mojang.datafixers.util.Pair<W, Integer>... pairs) {
        WeightList<W> list = new WeightList<>();
        for (com.mojang.datafixers.util.Pair<W, Integer> p : pairs) {
            list.addItem(p.getFirst(), p.getSecond());
        }
        return list;
    }

    /**
     * set total while adding.
     */
    @SafeVarargs
    public static <W> WeightList<W> of(int tot, com.mojang.datafixers.util.Pair<W, Integer>... pairs) {
        return of(pairs).setTotalWeight(tot);
    }

}