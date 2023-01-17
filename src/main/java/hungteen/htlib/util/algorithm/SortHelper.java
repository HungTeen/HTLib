package hungteen.htlib.util.algorithm;

import hungteen.htlib.util.Pair;
import net.minecraft.world.entity.Entity;

import java.util.Comparator;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2023-01-17 15:58
 **/
public class SortHelper {

    public static class EntitySorter implements Comparator<Entity> {
        private final Entity entity;

        public EntitySorter(Entity entityIn) {
            this.entity = entityIn;
        }

        public int compare(Entity a, Entity b) {
            double d0 = this.entity.distanceToSqr(a);
            double d1 = this.entity.distanceToSqr(b);
            if (d0 < d1) {
                return -1;
            } else {
                return d0 > d1 ? 1 : 0;
            }
        }
    }

    public static class PairSorter<T> implements Comparator<Pair<T, Integer>> {

        @Override
        public int compare(Pair<T, Integer> pair1, Pair<T, Integer> pair2) {
            return pair2.getSecond().compareTo(pair1.getSecond());
        }
    }

}
