package hungteen.htlib.data;

import hungteen.htlib.HTLib;
import hungteen.htlib.common.WoodIntegrations;
import hungteen.htlib.util.helper.StringHelper;
import hungteen.htlib.util.helper.registry.BlockHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashSet;
import java.util.Set;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 22:57
 **/
public abstract class HTBlockStateGen extends BlockStateProvider {

    protected final Set<Block> addedBlocks = new HashSet<>();
    protected final String modId;

    public HTBlockStateGen(PackOutput output, String modId, ExistingFileHelper exFileHelper) {
        super(output, modId, exFileHelper);
        this.modId = modId;
    }

    protected static String solid(){
        return RenderType.solid().name;
    }

    protected static String cutout(){
        return RenderType.cutout().name;
    }

    protected static String cutoutMipped(){
        return RenderType.cutoutMipped().name;
    }

    protected static String translucent(){
        return RenderType.translucent().name;
    }

    protected ResourceLocation key(Block block) {
        return BlockHelper.get().getKey(block);
    }

    protected String name(Block block) {
        return key(block).getPath();
    }

    public void simpleBlock(Block block, String renderType) {
        simpleBlock(block, cubeAll(block, renderType));
    }

    public ModelFile cubeAll(Block block, String renderType) {
        return models().cubeAll(name(block), blockTexture(block)).renderType(renderType);
    }

    protected void cross(Block block) {
        cross(block, cutout());
    }

    protected void cross(Block block, String renderType) {
        simpleBlock(block, models().cross(name(block), blockTexture(block)).renderType(renderType));
    }

    protected void simpleWithNoBlockModel(Block block) {
        getVariantBuilder(block).partialState();
    }

    protected void crop(Block block, Property<Integer> property) {
        crop(block, property, cutout());
    }

    protected void crop(Block block, Property<Integer> property, String renderType) {
        getVariantBuilder(block).forAllStates(state -> {
            int i = state.getValue(property);
            return ConfiguredModel.builder()
                    .modelFile(models().crop(name(block) + "_" + i, StringHelper.res(this.modId, "block/" + name(block) + "_" + i)).renderType(renderType))
                    .build();
        });
        this.addedBlocks.add(block);
    }

    protected void log(RotatedPillarBlock block) {
        log(block, solid());
    }

    protected void log(RotatedPillarBlock block, String renderType) {
        logBlockWithRenderType(block, renderType);
        this.addedBlocks.add(block);
    }

    protected void wood(RotatedPillarBlock block) {
        wood(block, solid());
    }

    protected void wood(RotatedPillarBlock block, String renderType) {
        final ResourceLocation res = StringHelper.blockTexture(StringHelper.replace(key(block), "wood", "log"));
        axisBlockWithRenderType(block, res, res, renderType);
        this.addedBlocks.add(block);
    }

    protected void door(DoorBlock block) {
        door(block, solid());
    }

    protected void door(DoorBlock block, String renderType) {
        doorBlockWithRenderType(block, BlockHelper.blockTexture(block, "_bottom"), BlockHelper.blockTexture(block, "_top"), renderType);
        this.addedBlocks.add(block);
    }

    protected void trapdoor(TrapDoorBlock block) {
        trapdoor(block, solid());
    }

    protected void trapdoor(TrapDoorBlock block, String renderType) {
        trapdoorBlockWithRenderType(block, BlockHelper.blockTexture(block), true, renderType);
        this.addedBlocks.add(block);
    }

    protected void fence(FenceBlock block) {
        fence(block, solid());
    }

    protected void fence(FenceBlock block, String renderType) {
        fenceBlockWithRenderType(block, StringHelper.blockTexture(StringHelper.replace(key(block), "fence", "planks")), renderType);
        this.addedBlocks.add(block);
    }

    protected void fenceGate(FenceGateBlock block) {
        fenceGate(block, solid());
    }

    protected void fenceGate(FenceGateBlock block, String renderType) {
        fenceGateBlockWithRenderType(block, StringHelper.blockTexture(StringHelper.replace(key(block), "fence_gate", "planks")), renderType);
        this.addedBlocks.add(block);
    }

    protected void sign(StandingSignBlock standingSignBlock, WallSignBlock wallSignBlock) {
        sign(standingSignBlock, wallSignBlock, solid());
    }

    protected void sign(StandingSignBlock standingSignBlock, WallSignBlock wallSignBlock, String renderType) {
        sign(standingSignBlock, wallSignBlock, StringHelper.blockTexture(StringHelper.replace(key(standingSignBlock), "sign", "planks")), renderType);
    }

    public void sign(StandingSignBlock standingSignBlock, WallSignBlock wallSignBlock, ResourceLocation texture, String renderType) {
        ModelFile sign = models().sign(name(standingSignBlock), texture).renderType(renderType);
        signBlock(standingSignBlock, wallSignBlock, sign);
        this.addedBlocks.add(standingSignBlock);
        this.addedBlocks.add(wallSignBlock);
    }

    protected void stair(StairBlock block) {
        stair(block, solid());
    }

    protected void stair(StairBlock block, String renderType) {
        stairsBlockWithRenderType(block, StringHelper.blockTexture(StringHelper.replace(key(block), "stairs", "planks")), renderType);
        this.addedBlocks.add(block);
    }

    protected void button(ButtonBlock block) {
        button(block, solid());
    }

    protected void button(ButtonBlock block, String renderType) {
        button(block, StringHelper.blockTexture(StringHelper.replace(key(block), "button", "planks")), renderType);
    }

    public void button(ButtonBlock block, ResourceLocation texture, String renderType) {
        ModelFile button = models().button(name(block), texture).renderType(renderType);
        ModelFile buttonPressed = models().buttonPressed(name(block) + "_pressed", texture).renderType(renderType);
        buttonBlock(block, button, buttonPressed);
        this.addedBlocks.add(block);
    }

    protected void slab(SlabBlock block) {
        final ResourceLocation res = StringHelper.blockTexture(StringHelper.replace(key(block), "slab", "planks"));
        slab(block, res, res, solid());
    }

    public void slab(SlabBlock block, ResourceLocation doubleSlab, ResourceLocation texture, String renderType) {
        slab(block, doubleSlab, texture, texture, texture, renderType);
    }

    public void slab(SlabBlock block, ResourceLocation doubleSlab, ResourceLocation side, ResourceLocation bottom, ResourceLocation top, String renderType) {
        ModelFile slab = models().slab(name(block), side, bottom, top).renderType(renderType);
        ModelFile slabTop = models().slabTop(name(block) + "_top", side, bottom, top).renderType(renderType);
        ModelFile doubleSlabFile = models().getExistingFile(doubleSlab);
        slabBlock(block, slab, slabTop, doubleSlabFile);
        this.addedBlocks.add(block);
    }

    protected void pressPlate(PressurePlateBlock block) {
        pressPlate(block, StringHelper.blockTexture(StringHelper.replace(key(block), "pressure_plate", "planks")), solid());
    }

    public void pressPlate(PressurePlateBlock block, ResourceLocation texture, String renderType) {
        ModelFile pressurePlate = models().pressurePlate(name(block), texture).renderType(renderType);
        ModelFile pressurePlateDown = models().pressurePlateDown(name(block) + "_down", texture).renderType(renderType);
        pressurePlateBlock(block, pressurePlate, pressurePlateDown);
        this.addedBlocks.add(block);
    }

//	private void horizontalBlockState(Block block) {
//		ModelFile file = models().cubeAll(block.getRegistryName().getPath(), StringUtil.prefix("block/" + block.getRegistryName().getPath()));
//		horizontalBlock(block, file);
//	}

    /**
     * Gen wood-related at once.
     */
    protected void woodIntegration(WoodIntegrations.WoodIntegration woodIntegration) {
        /* Must gen first to avoid crash. */
        if(woodIntegration.hasWoodSuit(WoodIntegrations.WoodSuits.PLANKS)){
            woodIntegration.getBlockOpt(WoodIntegrations.WoodSuits.PLANKS).ifPresent(block -> {
                this.addedBlocks.add(block);
                this.simpleBlock(block);
            });
        } else {
            HTLib.getLogger().warn("Wood Integration {} Data Gen skipped, because wood planks was banned !", woodIntegration.getRegistryName());
            return;
        }
        woodIntegration.getWoodBlocks().forEach(pair -> {
            final Block block = pair.getValue();
            switch (pair.getKey()){
                /* RotatedPillarBlocks. */
                case LOG, STRIPPED_LOG -> {
                    if(block instanceof RotatedPillarBlock b) this.log(b);
                }
                /* Blocks with 2 textures(top & side). */
                case WOOD, STRIPPED_WOOD -> {
                    if(block instanceof RotatedPillarBlock b) this.wood(b);
                }
                case DOOR -> {
                    if(block instanceof DoorBlock b) this.door(b);
                }
                case TRAP_DOOR -> {
                    if(block instanceof TrapDoorBlock b) this.trapdoor(b);
                }
                case FENCE -> {
                    if(block instanceof FenceBlock b) this.fence(b);
                }
                case FENCE_GATE -> {
                    if(block instanceof FenceGateBlock b) this.fenceGate(b);
                }
                case STAIRS -> {
                    if(block instanceof StairBlock b) this.stair(b);
                }
                case BUTTON -> {
                    if(block instanceof ButtonBlock b) this.button(b);
                }
                case SLAB -> {
                    if(block instanceof SlabBlock b) this.slab(b);
                }
                case PRESSURE_PLATE -> {
                    if(block instanceof PressurePlateBlock b) this.pressPlate(b);
                }
                default -> {

                }
            }
        });
        /* Sign Blocks. */
        woodIntegration.getBlockOpt(WoodIntegrations.WoodSuits.STANDING_SIGN).ifPresent(block1 -> {
            woodIntegration.getBlockOpt(WoodIntegrations.WoodSuits.WALL_SIGN).ifPresent(block2 -> {
                if(block1 instanceof StandingSignBlock b1 && block2 instanceof WallSignBlock b2) sign(b1, b2);
            });
        });
    }

    @Override
    public String getName() {
        return this.modId + " blockstates";
    }
}
