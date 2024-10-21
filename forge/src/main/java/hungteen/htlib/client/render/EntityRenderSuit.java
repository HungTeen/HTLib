package hungteen.htlib.client.render;

import net.minecraft.world.entity.Entity;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/6 17:03
 */
public final class EntityRenderSuit<T extends Entity> {

//    private static final List<EntityRenderSuit<?>> SUITS = Collections.synchronizedList(new ArrayList<>());
//    private final Supplier<EntityType<T>> entityType;
//    private final HashMap<ModelLayerLocation, Supplier<LayerDefinition>> layerDefinitions = new HashMap<>();
//
//    protected EntityRendererProvider<T> rendererProvider = null;
//    protected float shadowSize = -1;
//
//    public EntityRenderSuit(Supplier<EntityType<T>> entityType) {
//        this.entityType = entityType;
//    }
//
//    public void register(EntityRenderersEvent.RegisterRenderers event){
//        if(rendererProvider != null){
//            event.registerEntityRenderer(getEntityType(), rendererProvider);
//        } else {
//            event.registerEntityRenderer(getEntityType(), EmptyEffectRender::new);
//            HTLib.getLogger().warn("{} has no spawn placement, HTLib will make one for you.", EntityHelper.get().getKey(getEntityType()).toString());
//        }
//    }
//
//    public void registerModelLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
//        for (Map.Entry<ModelLayerLocation, Supplier<LayerDefinition>> layer : this.layerDefinitions.entrySet()) {
//            event.registerLayerDefinition(layer.getKey(), layer.getValue());
//        }
//    }
//
//    public EntityRenderSuit<T> shadowSize(float shadow) {
//        this.shadowSize = shadow;
//        return this;
//    }
//
//    public EntityRenderSuit<T> defineLayer(String path, Supplier<LayerDefinition> definition) {
//        return defineLayer(path, "main", definition);
//    }
//
//    public EntityRenderSuit<T> defineLayer(String path, String layerName, Supplier<LayerDefinition> definition) {
//        this.layerDefinitions.put(layerName, Pair.of(new ModelLayerLocation(AdventOfAscension.id(path), layerName), definition));
//
//        return this;
//    }
//
//    public EntityRenderSuit<T> provider(EntityRendererProvider<T> provider) {
//        this.rendererProvider = provider;
//        return this;
//    }
//
//    public EntityType<T> getEntityType() {
//        return entityType.get();
//    }

}
