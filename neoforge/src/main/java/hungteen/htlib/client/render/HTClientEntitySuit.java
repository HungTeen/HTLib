package hungteen.htlib.client.render;

import hungteen.htlib.api.registry.SimpleEntry;
import hungteen.htlib.client.util.ModelLayerType;
import hungteen.htlib.client.util.ModelLayerHelper;
import hungteen.htlib.common.registry.suit.HTEntitySuit;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2024/12/2 21:22
 **/
public class HTClientEntitySuit<T extends Entity> implements SimpleEntry  {

    private final HTEntitySuit<T> entitySuit;
    private EntityRendererProvider<T> rendererProvider;
    private Map<ModelLayerType, LayerDefinitionEntry> layerDefinitions;
    private final Map<ModelLayerType, ModelLayerLocation> layers = new HashMap<>();

    public HTClientEntitySuit(HTEntitySuit<T> entitySuit, EntityRendererProvider<T> rendererProvider, Map<ModelLayerType, LayerDefinitionEntry> layerDefinitions) {
        this.entitySuit = entitySuit;
        this.rendererProvider = rendererProvider;
        this.layerDefinitions = layerDefinitions;
    }

    public void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        if(layerDefinitions != null){
            layerDefinitions.forEach((layer, entry) -> {
                event.registerLayerDefinition(entry.location(), entry.layerDefinition());
                layers.put(layer, entry.location());
            });
        }
    }

    public void registerRenderer(EntityRenderersEvent.RegisterRenderers event){
        if(rendererProvider != null){
            event.registerEntityRenderer(entitySuit.get(), rendererProvider);
        }
    }

    /**
     * 获取指定位置的模型层。
     */
    public ModelLayerLocation getLayerLocation(ModelLayerType layer){
        return layers.get(layer);
    }

    public ModelPart getPart(EntityRendererProvider.Context context, ModelLayerType layer){
        return context.bakeLayer(getLayerLocation(layer));
    }

    public void clear(){
        this.rendererProvider = null;
        this.layerDefinitions = null;
    }

    @Override
    public String name() {
        return entitySuit.name();
    }

    @Override
    public String getModID() {
        return entitySuit.getModID();
    }

    public record LayerDefinitionEntry(ModelLayerLocation location, Supplier<LayerDefinition> layerDefinition) {
    }

    public static class EntitySuitBuilder<T extends Entity> {

        private final HTEntitySuit<T> entitySuit;
        private EntityRendererProvider<T> rendererProvider;
        private Map<ModelLayerType, LayerDefinitionEntry> layerDefinitions = new HashMap<>();

        public EntitySuitBuilder(HTEntitySuit<T> entitySuit) {
            this.entitySuit = entitySuit;
        }

        public EntitySuitBuilder<T> renderer(EntityRendererProvider<T> rendererProvider){
            this.rendererProvider = rendererProvider;
            return this;
        }

        public EntitySuitBuilder<T> mainLayer(Supplier<LayerDefinition> layerDefinition){
            layerDefinitions.put(ModelLayerType.MAIN, new LayerDefinitionEntry(ModelLayerHelper.create(entitySuit.getLocation()), layerDefinition));
            return this;
        }

        public EntitySuitBuilder<T> layer(ModelLayerType layer, Supplier<LayerDefinition> layerDefinition){
            layerDefinitions.put(layer, new LayerDefinitionEntry(ModelLayerHelper.create(entitySuit.getLocation(), layer), layerDefinition));
            return this;
        }

        public EntitySuitBuilder<T> layer(ModelLayerType layer, ModelLayerLocation location, Supplier<LayerDefinition> layerDefinition){
            layerDefinitions.put(layer, new LayerDefinitionEntry(location, layerDefinition));
            return this;
        }

        public EntitySuitBuilder<T> layers(Map<ModelLayerType, LayerDefinitionEntry> layerDefinitions) {
            this.layerDefinitions = layerDefinitions;
            return this;
        }

        public HTClientEntitySuit<T> build() {
            return new HTClientEntitySuit<>(entitySuit, rendererProvider, layerDefinitions);
        }
    }
}
