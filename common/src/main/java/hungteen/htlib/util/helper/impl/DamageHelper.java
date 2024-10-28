package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTResourceHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/8/23 9:47
 */
public interface DamageHelper extends HTResourceHelper<DamageType> {

    HTResourceHelper<DamageType> HELPER = () -> Registries.DAMAGE_TYPE;

    static DamageSource source(Entity entity, ResourceKey<DamageType> key, Entity causingEntity){
        return new DamageSource(type(entity, key), causingEntity);
    }

    static DamageSource source(Entity entity, ResourceKey<DamageType> key, @Nullable Entity causingEntity, @Nullable Entity directEntity){
        return new DamageSource(type(entity, key), causingEntity, directEntity);
    }

    static DamageSource source(Entity entity, ResourceKey<DamageType> key, Vec3 pos){
        return new DamageSource(type(entity, key), pos);
    }

    static DamageSource source(Entity entity, ResourceKey<DamageType> key){
        return new DamageSource(type(entity, key));
    }

    static Holder<DamageType> type(Entity entity, ResourceKey<DamageType> key){
        return registry(entity).getOrThrow(key);
    }

    static HolderLookup<DamageType> registry(Entity entity){
        return entity.registryAccess().lookupOrThrow(get().resourceKey());
    }

    static HTResourceHelper<DamageType> get(){
        return HELPER;
    }

}
