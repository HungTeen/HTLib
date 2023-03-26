package hungteen.htlib.client;

import hungteen.htlib.common.WoodIntegrations;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 23:15
 **/
public class HTModelLayers {

    public static ModelLayerLocation createBoatModelName(WoodIntegrations.IBoatType type) {
        return createLocation(type.getModID(), "boat/" + type.getName(), "main");
    }

    public static ModelLayerLocation createChestBoatModelName(WoodIntegrations.IBoatType type) {
        return createLocation(type.getModID(), "chest_boat/" + type.getName(), "main");
    }

    public static ModelLayerLocation createLocation(String modId, String name, String part) {
        return new ModelLayerLocation(new ResourceLocation(modId, name), part);
    }

}
