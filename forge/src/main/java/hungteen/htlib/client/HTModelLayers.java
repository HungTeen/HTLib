package hungteen.htlib.client;

import hungteen.htlib.util.interfaces.BoatType;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 23:15
 **/
public class HTModelLayers {

    public static ModelLayerLocation createBoatModelName(BoatType type) {
        return createLocation(type.getModID(), "boat/" + type.getName(), "main");
    }

    public static ModelLayerLocation createChestBoatModelName(BoatType type) {
        return createLocation(type.getModID(), "chest_boat/" + type.getName(), "main");
    }

    public static ModelLayerLocation createLocation(String modId, String name, String part) {
        return new ModelLayerLocation(new ResourceLocation(modId, name), part);
    }

}
