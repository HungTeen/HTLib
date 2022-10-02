package hungteen.htlib;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-24 23:04
 **/
@Mod(HTLib.MOD_ID)
public class HTLib {

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Mod ID.
    public static final String MOD_ID = "htlib";
    // Proxy of Server and Client.
    public static CommonProxy PROXY = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

    /**
     * get resource with mod prefix.
     */
    public static ResourceLocation prefix(String name) {
        return new ResourceLocation(MOD_ID, name);
    }

}
