package hungteen.htlib.client.render;

import hungteen.htlib.api.registry.SimpleEntry;
import hungteen.htlib.common.registry.suit.HTEntitySuit;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

import java.util.ArrayList;
import java.util.List;
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
    private Map<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions;
    private final List<ModelLayerLocation> layers = new ArrayList<>();

    public HTClientEntitySuit(HTEntitySuit<T> entitySuit, EntityRendererProvider<T> rendererProvider, Map<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions) {
        this.entitySuit = entitySuit;
        this.rendererProvider = rendererProvider;
        this.layerDefinitions = layerDefinitions;
    }

    public void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        if(layerDefinitions != null){
            layerDefinitions.forEach((location, supplier) -> {
                event.registerLayerDefinition(location, supplier);
                layers.add(location);
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
    public ModelLayerLocation get(int index){
        return layers.get(index);
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

    public static class EntitySuitBuilder<T extends Entity> {

        private final HTEntitySuit<T> entitySuit;
        private EntityRendererProvider<T> rendererProvider;
        private Map<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions;

        public EntitySuitBuilder(HTEntitySuit<T> entitySuit) {
            this.entitySuit = entitySuit;
        }

        public EntitySuitBuilder<T> renderer(EntityRendererProvider<T> rendererProvider){
            this.rendererProvider = rendererProvider;
            return this;
        }

        public EntitySuitBuilder<T> layer(ModelLayerLocation location, Supplier<LayerDefinition> layerDefinition){
            layerDefinitions.put(location, layerDefinition);
            return this;
        }

        public EntitySuitBuilder<T> layers(Map<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions) {
            this.layerDefinitions = layerDefinitions;
            return this;
        }

        public HTClientEntitySuit<T> build() {
            return new HTClientEntitySuit<>(entitySuit, rendererProvider, layerDefinitions);
        }
    }
}
