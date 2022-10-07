package hungteen.htlib.data;

import hungteen.htlib.HTLib;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-06 22:57
 **/
public abstract class HTBlockStateGen extends BlockStateProvider {

    protected final Set<Block> addedBlocks = new HashSet<>();
    protected final String modId;

    public HTBlockStateGen(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
        this.modId = modid;
    }

    protected String name(Block block) {
        return block.getRegistryName().getPath();
    }

    protected void cross(Block block) {
        simpleBlock(block, models().cross(name(block), blockTexture(block)));
    }

    protected void simpleWithNoBlockModel(Block block) {
        getVariantBuilder(block).partialState();
    }

    protected void cropBlockState(Block block, Property<Integer> p) {
        getVariantBuilder(block).forAllStates(state -> {
            int i = state.getValue(p);
            return ConfiguredModel.builder()
                    .modelFile(models().crop(block.getRegistryName().getPath() + "_" + i, HTLib.res(this.modId, "block/" + block.getRegistryName().getPath() + "_" + i)))
                    .build();
        });
        this.addedBlocks.add(block);
    }

//    protected void cornBlockState(Block block, Property<Integer> p) {
//        getVariantBuilder(block).forAllStates(state -> {
//            final int i = state.getValue(p);
//            final boolean isUpper = CornBlock.isUpperState(state);
//            return ConfiguredModel.builder()
//                    .modelFile(models().cross(block.getRegistryName().getPath() + (isUpper ? "_upper" : "_lower") + "_" + i, Util.prefix("block/" + block.getRegistryName().getPath() + (isUpper ? "_upper" : "_lower") + "_" + i)))
//                    .build();
//
//        });
//        this.addedBlocks.add(block);
//    }

    protected void log(RotatedPillarBlock b) {
        logBlock(b);
        this.addedBlocks.add(b);
    }

    protected void wood(RotatedPillarBlock b) {
        final String path = b.getRegistryName().getPath();
        String realPath = path.substring(0, path.indexOf("wood")) + "log";
        final ResourceLocation res = HTLib.res(this.modId, "block/" + realPath);
        axisBlock(b, res, res);
        this.addedBlocks.add(b);
    }

    protected void door(DoorBlock b){
        final ResourceLocation bottom = HTLib.res(this.modId, "block/" + b.getRegistryName().getPath() + "_bottom");
        final ResourceLocation top = HTLib.res(this.modId, "block/" + b.getRegistryName().getPath() + "_top");
        doorBlock(b, bottom, top);
        this.addedBlocks.add(b);
    }

    protected void trapdoor(TrapDoorBlock b){
        final ResourceLocation res = HTLib.res(this.modId, "block/" + b.getRegistryName().getPath());
        trapdoorBlock(b, res, true);
        this.addedBlocks.add(b);
    }

    protected void fence(FenceBlock b){
        final String path = b.getRegistryName().getPath();
        final String realPath = path.substring(0, path.indexOf("fence")) + "planks";
        final ResourceLocation res = HTLib.res(this.modId, "block/" + realPath);
        fenceBlock(b, res);
        this.addedBlocks.add(b);
    }

    protected void fenceGate(FenceGateBlock b){
        final String path = b.getRegistryName().getPath();
        final String realPath = path.substring(0, path.indexOf("fence_gate")) + "planks";
        final ResourceLocation res = HTLib.res(this.modId, "block/" + realPath);
        fenceGateBlock(b, res);
        this.addedBlocks.add(b);
    }

    protected void sign(StandingSignBlock b, WallSignBlock b2){
        final String path = b.getRegistryName().getPath();
        final String realPath = path.substring(0, path.indexOf("sign")) + "planks";
        final ResourceLocation res = HTLib.res(this.modId, "block/" + realPath);
        signBlock(b, b2, res);
        this.addedBlocks.add(b);
        this.addedBlocks.add(b2);
    }

    protected void stair(StairBlock b){
        final String realPath = b.getRegistryName().getPath().replace("stairs","planks");
        final ResourceLocation res = HTLib.res(this.modId, "block/" + realPath);
        stairsBlock(b, res);
        this.addedBlocks.add(b);
    }

    protected void button(ButtonBlock b){
        final String realPath = b.getRegistryName().getPath().replace("button","planks");
        final ResourceLocation res = HTLib.res(this.modId, "block/" + realPath);
        buttonBlock(b, res);
        this.addedBlocks.add(b);
    }

    protected void slab(SlabBlock b){
        final String realPath = b.getRegistryName().getPath().replace("slab","planks");
        final ResourceLocation res = HTLib.res(this.modId, "block/" + realPath);
        slabBlock(b, res, res);
        this.addedBlocks.add(b);
    }

    protected void pressPlate(PressurePlateBlock b){
        final String realPath = b.getRegistryName().getPath().replace("pressure_plate","planks");
        final ResourceLocation res = HTLib.res(this.modId, "block/" + realPath);
        pressurePlateBlock(b, res);
        this.addedBlocks.add(b);
    }

//	private void horizontalBlockState(Block block) {
//		ModelFile file = models().cubeAll(block.getRegistryName().getPath(), StringUtil.prefix("block/" + block.getRegistryName().getPath()));
//		horizontalBlock(block, file);
//	}

    @Override
    public String getName() {
        return this.modId + " blockstates";
    }
}
