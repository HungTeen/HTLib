package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:48
 **/
public class BlockHelper {

    /**
     * Axe Strip when {@link hungteen.htlib.common.event.HTBlockEvents#onToolModifyBlock(BlockEvent.BlockToolModificationEvent)}
     */
    private static final Map<Block, Block> STRIPPABLES = new HashMap<>();
    private static final List<WoodType> WOOD_TYPES = Collections.synchronizedList(List.of());

    private static final RegistryHelper<Block> HELPER = new RegistryHelper<>(){
        @Override
        public Either<IForgeRegistry<Block>, Registry<Block>> getRegistry() {
            return Either.left(ForgeRegistries.BLOCKS);
        }
    };

    private static final RegistryHelper<BlockEntityType<?>> ENTITY_HELPER = new RegistryHelper<>(){

        @Override
        public Either<IForgeRegistry<BlockEntityType<?>>, Registry<BlockEntityType<?>>> getRegistry () {
            return Either.left(ForgeRegistries.BLOCK_ENTITY_TYPES);
        }
    };

    private static final RegistryHelper<BannerPattern> BANNER_HELPER = new RegistryHelper<>(){
        @Override
        public Either<IForgeRegistry<BannerPattern>, Registry<BannerPattern>> getRegistry() {
            return Either.right(BuiltInRegistries.BANNER_PATTERN);
        }
    };

    private static final RegistryHelper<PaintingVariant> PAINT_HELPER = new RegistryHelper<>(){
        @Override
        public Either<IForgeRegistry<PaintingVariant>, Registry<PaintingVariant>> getRegistry() {
            return Either.left(ForgeRegistries.PAINTING_VARIANTS);
        }
    };

    public static ResourceLocation blockTexture(Block block){
        return StringHelper.blockTexture(BlockHelper.get().getKey(block));
    }

    public static ResourceLocation blockTexture(Block block, String suffix){
        return StringHelper.blockTexture(BlockHelper.get().getKey(block), suffix);
    }

    public static boolean stillValid(Player player, BlockEntity entity) {
        if (player.level.getBlockEntity(entity.getBlockPos()) != entity) {
            return false;
        } else {
            return player.distanceToSqr(MathHelper.toVec3(entity.getBlockPos())) <= 64;
        }
    }

    /**
     * 不保证线程安全，请使用{@link ParallelDispatchEvent#enqueueWork(Runnable)}. <br>
     * remember to subscribe {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}.
     */
    public static void registerCompostable(float chance, ItemLike itemIn) {
        ComposterBlock.COMPOSTABLES.put(itemIn.asItem(), chance);
    }

    /**
     * 不保证线程安全，请使用{@link ParallelDispatchEvent#enqueueWork(Runnable)}. <br>
     * remember to subscribe {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}.
     */
    public static void registerAxeStrip(Block oldState, Block newState) {
        BlockHelper.STRIPPABLES.put(oldState, newState);
    }

    /**
     * register wood entityType.
     */
    public static void registerWoodType(WoodType woodType){
        WoodType.register(woodType);
        WOOD_TYPES.add(woodType);
    }

    /**
     * {@link hungteen.htlib.client.ClientRegister#clientSetUp(FMLClientSetupEvent)}
     */
    public static List<WoodType> getWoodTypes(){
        return Collections.unmodifiableList(WOOD_TYPES);
    }

    /**
     * Can block be stripped by Axe.
     */
    public static boolean canBeStripped(Block block){
        return STRIPPABLES.containsKey(block);
    }

    /**
     * Get the stripped result block.
     */
    public static Block getStrippedBlock(Block block){
        return STRIPPABLES.getOrDefault(block, block);
    }

    /**
     * Set property only when there is a property for state.
     */
    public static <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T> property, T value) {
        return state.hasProperty(property) ? state.setValue(property, value) : state;
    }

    /* Common Methods */

    public static RegistryHelper<Block> get(){
        return HELPER;
    }

    public static RegistryHelper<BlockEntityType<?>> entity(){
        return ENTITY_HELPER;
    }

    public static RegistryHelper<BannerPattern> banner() {
        return BANNER_HELPER;
    }

    public static RegistryHelper<PaintingVariant> paint() {
        return PAINT_HELPER;
    }

}
