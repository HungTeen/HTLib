package hungteen.htlib.data;

import com.mojang.serialization.JsonOps;
import hungteen.htlib.common.registry.HTCodecRegistry;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.JsonCodecProvider;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-25 12:44
 **/
public class HTCodecGen<T> extends JsonCodecProvider<T> {

    private final HTCodecRegistry<T> registry;
    public HTCodecGen(PackOutput output, ExistingFileHelper existingFileHelper, HTCodecRegistry<T> codecRegistry) {
        super(output, existingFileHelper, codecRegistry.getDefaultNamespace(), JsonOps.INSTANCE, PackType.SERVER_DATA, codecRegistry.getRegistryName(), codecRegistry.getCodec(), codecRegistry.getInnerMap());
        this.registry = codecRegistry;
    }

    @Override
    public String getName() {
        return this.registry.getDefaultNamespace() + " " + this.registry.getRegistryName().replaceAll("/", " ");
    }
}
