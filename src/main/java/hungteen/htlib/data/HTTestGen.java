package hungteen.htlib.data;

import hungteen.htlib.common.impl.placement.HTPlaceComponents;
import hungteen.htlib.common.impl.raid.HTRaidComponents;
import hungteen.htlib.common.impl.result.HTResultComponents;
import hungteen.htlib.common.impl.spawn.HTSpawnComponents;
import hungteen.htlib.common.impl.wave.HTWaveComponents;
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
public class HTTestGen{

    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> provider = event.getLookupProvider();
        ExistingFileHelper helper = event.getExistingFileHelper();

        HTPlaceComponents.registerStuffs();
        HTSpawnComponents.registerStuffs();
        HTWaveComponents.registerStuffs();
        HTResultComponents.registerStuffs();
        HTRaidComponents.registerStuffs();

        generator.addProvider(event.includeServer(), new HTCodecGen<>(output, helper, HTRaidComponents.RAIDS));
    }
}
