package hungteen.htlib.impl.spawn;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.world.raid.PlaceComponent;
import hungteen.htlib.common.world.raid.SpawnComponent;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.interfaces.IRaid;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-27 18:14
 **/
public abstract class BaseSpawn extends SpawnComponent {

    private final EntityType<?> entityType;
    private final CompoundTag entityNBT;
    private PlaceComponent spawnPlacement;

    public BaseSpawn(EntityType<?> entityType, CompoundTag entityNBT, PlaceComponent spawnPlacement) {
        this.entityType = entityType;
        this.entityNBT = entityNBT;
        this.spawnPlacement = spawnPlacement;
    }

    /**
     * Copy from {@link net.minecraft.server.commands.SummonCommand}
     */
    public Optional<Entity> spawnEntity(ServerLevel level, IRaid raid){
        final Vec3 spawnPosition = raid.getPlaceComponent().apply(this).getPlacePosition(level, raid.getPosition());
        if (Level.isInSpawnableBounds(MathHelper.toBlockPos(spawnPosition))) {
            CompoundTag compoundtag = this.getEntityNBT().copy();
            compoundtag.putString("id", this.getEntityType().toString());
            Entity entity = EntityType.loadEntityRecursive(compoundtag, level, (e) -> {
                e.moveTo(spawnPosition.x, spawnPosition.y, spawnPosition.z, e.getYRot(), e.getXRot());
                return e;
            });
            if (entity == null) {
                HTLib.getLogger().error("Fail to create entity {}", this.getEntityType().toString());
            } else {
                // No need ?
//                if (p_138825_ && entity instanceof Mob) {
//                    ((Mob)entity).finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.EVENT, (SpawnGroupData)null, (CompoundTag)null);
//                }

                if (!level.tryAddFreshEntityWithPassengers(entity)) {
                    HTLib.getLogger().warn("Error duplicate UUID");
                } else {
                    return Optional.of(entity);
                }
            }
        } else{
            HTLib.getLogger().warn("Can not spawn entity at position {}", spawnPosition);
        }
        return Optional.empty();
    }

    @Override
    public @Nullable PlaceComponent getSpawnPlacement() {
        return this.spawnPlacement;
    }

    public EntityType<?> getEntityType() {
        return entityType;
    }

    public CompoundTag getEntityNBT(){
        return entityNBT;
    }

}
