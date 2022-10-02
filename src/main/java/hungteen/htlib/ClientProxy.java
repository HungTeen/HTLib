package hungteen.htlib;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

/**
 * @program: HTLib
 * @author: HungTeen
 * @create: 2022-09-29 22:29
 **/
public class ClientProxy extends CommonProxy{

    public static final Minecraft MC = Minecraft.getInstance();

    @Override
    public Player getPlayer() {
        return MC.player;
    }
}
