package hungteen.htlib.client;

import hungteen.htlib.client.render.entity.EmptyEffectRender;
import hungteen.htlib.client.render.entity.HTBoatRender;
import hungteen.htlib.client.util.ClientHelper;
import hungteen.htlib.common.HTResourceManager;
import hungteen.htlib.common.blockentity.HTLibBlockEntities;
import hungteen.htlib.common.entity.HTLibEntities;
import hungteen.htlib.common.impl.HTLibBoatTypes;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 23:23
 **/
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientRegister {

    @SubscribeEvent
    public static void clientSetUp(FMLClientSetupEvent event) {
//        event.enqueueWork(() -> {
//            BlockHelper.getWoodTypes().forEach(ClientHelper::addWoodType);
//        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(HTLibEntities.BOAT.get(), (c) -> new HTBoatRender(c, false));
        event.registerEntityRenderer(HTLibEntities.CHEST_BOAT.get(), (c) -> new HTBoatRender(c, true));
        event.registerEntityRenderer(HTLibEntities.SEAT.get(), EmptyEffectRender::new);

        event.registerBlockEntityRenderer(HTLibBlockEntities.SIGN.get(), SignRenderer::new);
        event.registerBlockEntityRenderer(HTLibBlockEntities.HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    @SubscribeEvent
    public static void registerRendererLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        HTLibBoatTypes.getBoatTypes().forEach(type -> {
            if(type != HTLibBoatTypes.DEFAULT){
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
