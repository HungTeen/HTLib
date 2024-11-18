package hungteen.htlib.util.helper.impl;

import hungteen.htlib.api.util.helper.HTResourceHelper;
import hungteen.htlib.api.util.helper.HTVanillaRegistryHelper;
import hungteen.htlib.util.helper.MathHelper;
import hungteen.htlib.util.helper.StringHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WoodType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 08:48
 **/
public interface BlockHelper {

    List<WoodType> WOOD_TYPES = new ArrayList<>();

    HTVanillaRegistryHelper<Block> HELPER = () -> BuiltInRegistries.BLOCK;

    HTVanillaRegistryHelper<BlockEntityType<?>> ENTITY_HELPER = () -> BuiltInRegistries.BLOCK_ENTITY_TYPE;

    HTResourceHelper<BannerPattern> BANNER_HELPER = () -> Registries.BANNER_PATTERN;

    HTResourceHelper<PaintingVariant> PAINT_HELPER = () -> Registries.PAINTING_VARIANT;

    static ResourceLocation blockTexture(Block block) {
        return StringHelper.blockTexture(BlockHelper.get().getKey(block));
    }

    static ResourceLocation blockTexture(Block block, String suffix) {
        return StringHelper.blockTexture(BlockHelper.get().getKey(block), suffix);
    }

    static boolean stillValid(Player player, BlockEntity entity) {
        if (player.level().getBlockEntity(entity.getBlockPos()) != entity) {
            return false;
        } else {
            return player.distanceToSqr(MathHelper.toVec3(entity.getBlockPos())) <= 64;
        }
    }

    /**
     * 注册可燃物，不保证线程安全。
     */
    static void setFlammable(Block block, int spreadSpeed, int burnSpeed) {
        if (Blocks.FIRE instanceof FireBlock fire) {
            fire.setFlammable(block, spreadSpeed, burnSpeed);
        }
    }

    /**
     * 注册堆肥桶的肥料，不保证线程安全。
     */
    static void registerCompostable(float chance, ItemLike itemIn) {
        ComposterBlock.COMPOSTABLES.put(itemIn.asItem(), chance);
    }

    /**
     * 注册斧头的方块行为，不保证线程安全。
     */
    static void registerAxeStrip(Block oldState, Block newState) {
        AxeItem.STRIPPABLES.put(oldState, newState);
    }

    /**
     * 注册木头类型，不保证线程安全。
     */
    static void registerWoodType(WoodType woodType) {
        WoodType.register(woodType);
        BlockSetType.register(woodType.setType());
        WOOD_TYPES.add(woodType);
    }

    @Deprecated(since = "1.1.0")
    static List<WoodType> getWoodTypes() {
        return Collections.unmodifiableList(WOOD_TYPES);
    }

    /**
     * Set property only when there is a property for state.
     */
    static <T extends Comparable<T>> BlockState setProperty(BlockState state, Property<T> property, T value) {
        return state.hasProperty(property) ? state.setValue(property, value) : state;
    }

    /* Common Methods */

    static HTVanillaRegistryHelper<Block> get() {
        return HELPER;
    }

    static HTVanillaRegistryHelper<BlockEntityType<?>> entity() {
        return ENTITY_HELPER;
    }

    static HTResourceHelper<BannerPattern> banner() {
        return BANNER_HELPER;
    }

    static HTResourceHelper<PaintingVariant> paint() {
        return PAINT_HELPER;
    }

}
