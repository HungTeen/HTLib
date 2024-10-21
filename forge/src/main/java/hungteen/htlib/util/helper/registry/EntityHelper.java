package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import hungteen.htlib.util.helper.MathHelper;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.core.Registry;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:53
 **/
public class EntityHelper {

    private static final RegistryHelper<EntityType<?>> ENTITY_HELPER = new RegistryHelper<>(){

        @Override
        public Either<IForgeRegistry<EntityType<?>>, Registry<EntityType<?>>> getRegistry() {
            return Either.left(ForgeRegistries.ENTITY_TYPES);
        }

    };

    private static final RegistryHelper<EntityDataSerializer<?>> SERIALIZER_HELPER = new RegistryHelper<>(){

        @Override
        public Either<IForgeRegistry<EntityDataSerializer<?>>, Registry<EntityDataSerializer<?>>> getRegistry() {
            return Either.left(ForgeRegistries.ENTITY_DATA_SERIALIZERS.get());
        }

    };

    private static final RegistryHelper<Attribute> ATTRIBUTE_HELPER = new RegistryHelper<>(){

        @Override
        public Either<IForgeRegistry<Attribute>, Registry<Attribute>> getRegistry() {
            return Either.left(ForgeRegistries.ATTRIBUTES);
        }

    };

    /**
     * 右手持有某个物品、
     */
    public static boolean isMainHolding(LivingEntity entity, Predicate<ItemStack> predicate) {
        return predicate.test(entity.getMainHandItem());
    }

    /**
     * 左手持有某个物品
     */
    public static boolean isOffHolding(LivingEntity entity, Predicate<ItemStack> predicate) {
        return predicate.test(entity.getOffhandItem());
    }

    /**
     * 实体是否存活。
     */
    public static boolean isEntityValid(Entity entity) {
        return entity != null && entity.isAlive();
    }

    /**
     * used for bullets hit check.
     * get predicate entity between start to end.
     */
    public static EntityHitResult rayTraceEntities(Level world, Entity entity, Vec3 startVec, Vec3 endVec, Predicate<Entity> predicate) {
        return ProjectileUtil.getEntityHitResult(world, entity, startVec, endVec,
                entity.getBoundingBox().expandTowards(entity.getDeltaMovement()).inflate(0.5D), predicate);
    }

    /**
     * used for item ray trace.
     * get predicate entity with dis and vec.
     */
    public static EntityHitResult rayTraceEntities(Level world, Entity entity, Vec3 lookVec, double distance, Predicate<Entity> predicate) {
        final Vec3 startVec = entity.getEyePosition();
        Vec3 endVec = startVec.add(lookVec.normalize().scale(distance));
        ClipContext ray = new ClipContext(startVec, endVec, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity);
        BlockHitResult result = world.clip(ray);
        if (result.getType() != HitResult.Type.MISS) {// hit something
            endVec = result.getLocation();
        }
        return ProjectileUtil.getEntityHitResult(world, entity, startVec, endVec,
                entity.getBoundingBox().inflate(distance), predicate);
    }

    /**
     * get AABB by entity's width and height.
     */
    public static AABB getEntityAABB(Entity entity, double radius, double height){
        return MathHelper.getAABB(entity.position(), radius, height);
    }

    /**
     * base predicate target method.
     */
    public static <T extends Entity> List<T> getPredicateEntities(@Nonnull Entity attacker, AABB aabb, Class<T> tClass, Predicate<T> predicate) {
        final IntOpenHashSet set = new IntOpenHashSet();
        final List<T> entities = new ArrayList<>();
        getPredicateEntityWithParts(attacker, aabb, tClass, predicate).forEach(e -> {
            if (!set.contains(e.getId())) {
                set.addAll(getOwnerAndPartsID(e));
                entities.add(e);
            }
        });
        return entities;
    }

    /**
     * base predicate target method.
     */
    public static <T extends Entity> List<T> getPredicateEntityWithParts(@Nonnull Entity attacker, AABB aabb, Class<T> tClass, Predicate<T> predicate) {
        return attacker.level().getEntitiesOfClass(tClass, aabb).stream().filter(target -> {
            return !attacker.equals(target) && predicate.test(target);
        }).collect(Collectors.toList());
    }

    /**
     * get all parts of an entity, so that you can only hit them once.
     */
    public static List<Integer> getOwnerAndPartsID(Entity entity) {
        List<Integer> list = new ArrayList<>();
        if (entity instanceof PartEntity<?>) {
            Entity owner = ((PartEntity<?>) entity).getParent();
            list.add(owner.getId());
            for (PartEntity<?> part : owner.getParts()) {
                list.add(part.getId());
            }
        } else {
            list.add(entity.getId());
        }
        return list;
    }

    /* Common Methods */

    public static boolean isServer(Entity entity) {
        return !entity.level().isClientSide();
    }

    public static boolean inDimension(Entity entity, ResourceKey<Level> dimension){
        return entity.level().dimension().equals(dimension);
    }

    public static RegistryHelper<EntityType<?>> get(){
        return ENTITY_HELPER;
    }

    public static RegistryHelper<EntityDataSerializer<?>> serializer(){
        return SERIALIZER_HELPER;
    }

    public static RegistryHelper<Attribute> attribute(){
        return ATTRIBUTE_HELPER;
    }

}
