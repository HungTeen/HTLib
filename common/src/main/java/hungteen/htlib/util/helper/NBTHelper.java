package hungteen.htlib.util.helper;

import com.mojang.datafixers.util.Pair;
import hungteen.htlib.util.helper.registry.EntityHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Objects;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/2 16:43
 **/
public class NBTHelper {

    public static CompoundTag attributeTags(List<Pair<Attribute, Double>> attributes) {
        CompoundTag tag = new CompoundTag();
        {
            ListTag listtag = new ListTag();
            for (Pair<Attribute, Double> pair : attributes) {
                CompoundTag compoundtag = new CompoundTag();
                compoundtag.putString("Name", Objects.requireNonNull(EntityHelper.attribute().getKey(pair.getFirst()).toString()));
                compoundtag.putDouble("Base", pair.getSecond());
                listtag.add(compoundtag);
            }
            tag.put("Attributes", listtag);
        }
        return tag;
    }

    public static CompoundTag creeperTag(boolean powered, int radius, int swell) {
        CompoundTag tag = create();
        tag.putBoolean("powered", powered);
        tag.putInt("ExplosionRadius", radius);
        tag.putInt("Fuse", swell);
        return tag;
    }

    public static CompoundTag armorTag(List<ItemStack> armors) {
        CompoundTag tag = create();
        ListTag listtag = new ListTag();

        for(ItemStack itemstack : armors) {
            CompoundTag compoundtag = new CompoundTag();
            if (!itemstack.isEmpty()) {
                itemstack.save(compoundtag);
            }

            listtag.add(compoundtag);
        }

        tag.put("ArmorItems", listtag);
        return tag;
    }

    public static CompoundTag healthTag(float health) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("Health", health);
        return tag;
    }

    public static CompoundTag onFireTag(short tick) {
        CompoundTag tag = new CompoundTag();
        tag.putShort("Fire", tick);
        return tag;
    }

    public static CompoundTag merge(CompoundTag ... tags){
        CompoundTag tag = create();
        for (CompoundTag compoundTag : tags) {
            tag.merge(compoundTag);
        }
        return tag;
    }

    public static CompoundTag create(){
        return new CompoundTag();
    }

}
