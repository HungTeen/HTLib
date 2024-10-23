package hungteen.htlib.client;

import hungteen.htlib.client.render.entity.EmptyEffectRender;
import hungteen.htlib.client.render.entity.HTBoatRender;
import hungteen.htlib.client.util.ClientHelper;
import hungteen.htlib.common.HTResourceManager;
import hungteen.htlib.common.entity.HTLibEntities;
import hungteen.htlib.common.impl.BoatTypes;
import hungteen.htlib.util.helper.registry.BlockHelper;
import hungteen.htlib.util.interfaces.BoatType;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 23:23
 **/
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegister {

    @SubscribeEvent
    public static void clientSetUp(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            BlockHelper.getWoodTypes().forEach(ClientHelper::addWoodType);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
//        EntityRenderSuit.getRenderSuits().forEach(suit -> suit.initialize(event));
        event.registerEntityRenderer(HTLibEntities.BOAT.get(), (c) -> new HTBoatRender(c, false));
        event.registerEntityRenderer(HTLibEntities.CHEST_BOAT.get(), (c) -> new HTBoatRender(c, true));
        event.registerEntityRenderer(HTLibEntities.SEAT.get(), EmptyEffectRender::new);

//        event.registerBlockEntityRenderer(HTBlockEntities.SIGN.get(), SignRenderer::new);
//        event.registerBlockEntityRenderer(HTBlockEntities.HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    @SubscribeEvent
    public static void registerRendererLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        BoatTypes.getBoatTypes().forEach(type -> {
            if(type != BoatType.DEFAULT){
                event.registerLayerDefinition(HTModelLayers.createBoatModelName(type), BoatModel::createBodyModel);
                event.registerLayerDefinition(HTModelLayers.createBoatModelName(type), ChestBoatModel::createBodyModel);
            }
        });
    }

    @SubscribeEvent
    public static void registerBakeModels(ModelEvent.RegisterAdditional event){
        HTResourceManager.getExtraModels().forEach(model -> {
            event.register(ClientHelper.getModelLocation(model));
        });
    }

}
