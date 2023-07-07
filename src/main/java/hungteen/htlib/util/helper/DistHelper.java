package hungteen.htlib.util.helper;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import java.util.function.Supplier;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/7/7 10:36
 */
public class DistHelper {

    public static void runClient(Supplier<Runnable> runnable){
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, runnable);
    }

    public static void runServer(Supplier<Runnable> runnable){
        DistExecutor.unsafeRunWhenOn(Dist.DEDICATED_SERVER, runnable);
    }
}
