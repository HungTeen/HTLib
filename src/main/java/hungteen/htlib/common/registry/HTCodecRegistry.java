package hungteen.htlib.common.registry;

import com.mojang.serialization.Codec;
import hungteen.htlib.api.interfaces.IHTCodecRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DataPackRegistryEvent;

import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/6/28 11:10
 */
public class HTCodecRegistry<V> extends HTRegistry<V> implements IHTCodecRegistry<V> {

    private final Supplier<Codec<V>> codecSup;
    private final Supplier<Codec<V>> syncSup;

    HTCodecRegistry(ResourceLocation registryName, Supplier<Codec<V>> codecSup, Supplier<Codec<V>> syncSup) {
        super(registryName);
        this.codecSup = codecSup;
        this.syncSup = syncSup;
    }

    @Override
    public void register(IEventBus modBus){
        modBus.addListener(this::addRegistry);
    }

    private void addRegistry(DataPackRegistryEvent.NewRegistry event){
        event.dataPackRegistry(this.getRegistryKey(), this.codecSup.get(), this.syncSup.get());
    }

}
