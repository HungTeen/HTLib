package hungteen.htlib.common.registry;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 11:10
 */
public class HTCodecRegistry<V> extends HTRegistry<V> {

    private final Supplier<Codec<V>> codecSup;
    private final Supplier<Codec<V>> syncSup;

    HTCodecRegistry(ResourceLocation registryName, Supplier<Codec<V>> codecSup, Supplier<Codec<V>> syncSup) {
        super(registryName);
        this.codecSup = codecSup;
        this.syncSup = syncSup;
    }

    HTCodecRegistry(ResourceLocation registryName, Supplier<RegistryBuilder<V>> sup, Supplier<Codec<V>> codecSup, Supplier<Codec<V>> syncSup) {
        super(registryName, sup);
        this.codecSup = codecSup;
        this.syncSup = syncSup;
    }

    @Override
    public <I extends V> I register(ResourceLocation name, @NotNull I type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void register(IEventBus modBus){
        modBus.addListener(this::addRegistry);
    }

    private void addRegistry(DataPackRegistryEvent.NewRegistry event){
        event.dataPackRegistry(this.getRegistryKey(), this.codecSup.get(), this.syncSup.get());
    }

//    public Codec<IRaidComponent> getCodec(){
//        return .byNameCodec().dispatch(IRaidComponent::getType, IRaidComponentType::codec);
//    }

}
