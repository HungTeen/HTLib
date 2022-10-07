package hungteen.htlib.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-10-07 12:25
 **/
public abstract class HTBlockModelGen extends BlockModelProvider {

    public HTBlockModelGen(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @NotNull
    @Override
    public String getName() {
        return this.modid + " item models";
    }
}
