package hungteen.htlib.util.helper.registry;

import hungteen.htlib.api.interfaces.IHTResourceHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/8/23 9:47
 */
public class DamageHelper implements IHTResourceHelper<DamageType>{

    private static final IHTResourceHelper<DamageType> HELPER = new DamageHelper();

    public static DamageSource source(Entity entity, ResourceKey<DamageType> key, Entity causingEntity){
        return new DamageSource(type(entity, key), causingEntity);
    }

    public static DamageSource source(Entity entity, ResourceKey<DamageType> key, @Nullable Entity causingEntity, @Nullable Entity directEntity){
        return new DamageSource(type(entity, key), causingEntity, directEntity);
    }

    public static DamageSource source(Entity entity, ResourceKey<DamageType> key, Vec3 pos){
        return new DamageSource(type(entity, key), pos);
    }

    public static DamageSource source(Entity entity, ResourceKey<DamageType> key){
        return new DamageSource(type(entity, key));
    }

    public static Holder<DamageType> type(Entity entity, ResourceKey<DamageType> key){
        return registry(entity).getHolderOrThrow(key);
    }

    public static Registry<DamageType> registry(Entity entity){
        return entity.damageSources().damageTypes;
    }

    public static IHTResourceHelper<DamageType> get(){
        return HELPER;
    }

    @Override
    public ResourceKey<? extends Registry<DamageType>> resourceKey() {
        return Registries.DAMAGE_TYPE;
    }
}
