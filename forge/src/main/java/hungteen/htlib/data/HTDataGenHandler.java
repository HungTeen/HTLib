package hungteen.htlib.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-11-30 10:24
 **/
public class HTDataGenHandler {

    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeServer(), new HTDatapackEntriesGen(output, provider));
    }
}
