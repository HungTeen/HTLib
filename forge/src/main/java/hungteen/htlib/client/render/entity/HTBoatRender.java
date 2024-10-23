package hungteen.htlib.client.render.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import hungteen.htlib.client.HTModelLayers;
import hungteen.htlib.common.entity.HasHTBoatType;
import hungteen.htlib.common.impl.BoatTypes;
import hungteen.htlib.util.interfaces.BoatType;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.ListModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.vehicle.Boat;

import java.util.Map;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 23:09
 **/
public class HTBoatRender extends BoatRenderer {

    private final Map<BoatType, Pair<ResourceLocation, ListModel<Boat>>> boatResources;

    public HTBoatRender(EntityRendererProvider.Context context, boolean hasChest) {
        super(context, hasChest);
        this.boatResources = BoatTypes.getBoatTypes().stream().collect(ImmutableMap.toImmutableMap((type) -> {
            return type;
        }, (type) -> {
            return Pair.of(ResourceLocation.fromNamespaceAndPath(type.getModID(), getTextureLocation(type, hasChest)), this.createBoatModel(context, type, hasChest));
        }));
    }

    private static String getTextureLocation(BoatType type, boolean hasChest) {
        return "textures/entity/" + (hasChest ? "chest_" : "") + "boat/" + type.getName() + ".png";
    }

    private ListModel<Boat> createBoatModel(EntityRendererProvider.Context context, BoatType boatType, boolean hasChest) {
        final ModelLayerLocation modellayerlocation = hasChest ? HTModelLayers.createChestBoatModelName(boatType) : HTModelLayers.createBoatModelName(boatType);
        final ModelPart modelpart = context.bakeLayer(modellayerlocation);
        return (ListModel<Boat>)(hasChest ? new ChestBoatModel(modelpart) : new BoatModel(modelpart));
    }

    @Deprecated // forge: override getModelWithLocation to change the texture / model
    @Override
    public ResourceLocation getTextureLocation(Boat boat) {
        return getModelWithLocation(boat).getFirst();
    }

    @Override
    public Pair<ResourceLocation, ListModel<Boat>> getModelWithLocation(Boat boat) {
        if(boat instanceof HasHTBoatType htBoat) {
            return this.boatResources.get(htBoat.getHTBoatType());
        }
        return super.getModelWithLocation(boat);
    }
}
