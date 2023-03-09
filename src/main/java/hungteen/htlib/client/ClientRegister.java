package hungteen.htlib.client;

import hungteen.htlib.client.render.entity.EmptyEffectRender;
import hungteen.htlib.client.render.entity.HTBoatRender;
import hungteen.htlib.common.WoodIntegrations;
import hungteen.htlib.common.entity.HTEntities;
import hungteen.htlib.util.helper.registry.BlockHelper;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
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
            BlockHelper.getWoodTypes().forEach(Sheets::addWoodType);
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(HTEntities.BOAT.get(), (c) -> new HTBoatRender(c, false));
        event.registerEntityRenderer(HTEntities.CHEST_BOAT.get(), (c) -> new HTBoatRender(c, true));
        event.registerEntityRenderer(HTEntities.SEAT.get(), EmptyEffectRender::new);
    }

    @SubscribeEvent
    public static void registerRendererLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        WoodIntegrations.getBoatTypes().forEach(type -> {
            event.registerLayerDefinition(HTModelLayers.createBoatModelName(type), () -> BoatModel.createBodyModel(false));
            event.registerLayerDefinition(HTModelLayers.createBoatModelName(type), () -> BoatModel.createBodyModel(true));
        });
    }

}
