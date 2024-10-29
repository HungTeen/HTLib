package hungteen.htlib.client;

import hungteen.htlib.client.render.entity.EmptyEffectRender;
import hungteen.htlib.client.render.entity.HTBoatRender;
import hungteen.htlib.common.entity.HTLibEntities;
import hungteen.htlib.common.impl.HTLibBoatTypes;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 23:23
 **/
public class ClientRegister {

    public static void registerRenderers() {
//        EntityRenderSuit.getRenderSuits().forEach(suit -> suit.preInitialize(event));
        EntityRendererRegistry.register(HTLibEntities.BOAT.get(), (c) -> new HTBoatRender(c, false));
        EntityRendererRegistry.register(HTLibEntities.CHEST_BOAT.get(), (c) -> new HTBoatRender(c, true));
        EntityRendererRegistry.register(HTLibEntities.SEAT.get(), EmptyEffectRender::new);

//        event.registerBlockEntityRenderer(HTBlockEntities.SIGN.get(), SignRenderer::new);
//        event.registerBlockEntityRenderer(HTBlockEntities.HANGING_SIGN.get(), HangingSignRenderer::new);
    }

    public static void registerRendererLayers() {
        HTLibBoatTypes.getBoatTypes().forEach(type -> {
            if(type != HTLibBoatTypes.DEFAULT){
                EntityModelLayerRegistry.registerModelLayer(HTModelLayers.createBoatModelName(type), BoatModel::createBodyModel);
                EntityModelLayerRegistry.registerModelLayer(HTModelLayers.createBoatModelName(type), ChestBoatModel::createBodyModel);
            }
        });
    }

//    @SubscribeEvent
//    public static void registerBakeModels(ModelEvent.RegisterAdditional event){
//        HTResourceManager.getExtraModels().forEach(model -> {
//            event.register(ClientHelper.getModelLocation(model));
//        });
//    }

}
