package hungteen.htlib.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;

import java.util.function.Supplier;

/**
 * @program: HTLib
 * @author: PangTeen
 * @create: 2024/10/23 16:09
 **/
public interface ForgeHelper {

    static boolean isModLoaded(String modId){
        return ModList.get().isLoaded(modId);
    }

    static void runClient(Supplier<Runnable> runnable){
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, runnable);
    }

    static void runServer(Supplier<Runnable> runnable){
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, runnable);
    }

}
