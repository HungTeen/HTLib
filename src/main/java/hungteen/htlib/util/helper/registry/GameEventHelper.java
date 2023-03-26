package hungteen.htlib.util.helper.registry;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * @author PangTeen
 * @program HTLib
 * @data 2023/3/9 20:19
 */
public class GameEventHelper extends RegistryHelper<GameEvent> {

    private static final GameEventHelper HELPER = new GameEventHelper();

    /* Common Methods */

    public static GameEventHelper get() {
        return HELPER;
    }

    @Override
    public Either<IForgeRegistry<GameEvent>, Registry<GameEvent>> getRegistry() {
        return Either.right(BuiltInRegistries.GAME_EVENT);
    }
}
