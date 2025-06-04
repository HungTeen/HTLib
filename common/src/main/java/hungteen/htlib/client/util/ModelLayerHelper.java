package hungteen.htlib.client.util;

import hungteen.htlib.util.HTBoatType;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-13 23:15
 **/
public interface ModelLayerHelper {

    static ModelLayerLocation createBoatModelName(HTBoatType type) {
        return create(type.getModID(), "boat/" + type.name(), "main");
    }

    static ModelLayerLocation createChestBoatModelName(HTBoatType type) {
        return create(type.getModID(), "chest_boat/" + type.name(), "main");
    }

    static ModelLayerLocation create(ResourceLocation name) {
        return create(name, ModelLayerType.MAIN);
    }

    static ModelLayerLocation createInnerArmor(ResourceLocation name) {
        return create(name, ModelLayerType.INNER_ARMOR);
    }

    static ModelLayerLocation createOuterArmor(ResourceLocation name) {
        return create(name, ModelLayerType.OUTER_ARMOR);
    }

    static ModelLayerLocation create(ResourceLocation name, String type) {
        return new ModelLayerLocation(name, type);
    }

    static ModelLayerLocation create(ResourceLocation name, ModelLayerType type) {
        return create(name, type.getType());
    }

    static ModelLayerLocation create(String modId, String name, String type) {
        return create(ResourceLocation.fromNamespaceAndPath(modId, name), type);
    }

}
