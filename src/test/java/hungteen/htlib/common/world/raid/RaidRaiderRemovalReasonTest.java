package hungteen.htlib.common.world.raid;

import net.minecraft.world.entity.Entity;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 袭击者的引用被区块卸载带走时不能算作死亡。
 *
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/27 12:00
 */
class RaidRaiderRemovalReasonTest {

    @Test
    void chunkUnloadIsParkedInsteadOfDeath() {
        assertTrue(AbstractRaid.isChunkUnloaded(Entity.RemovalReason.UNLOADED_TO_CHUNK));
    }

    @Test
    void realRemovalsAreDeaths() {
        assertFalse(AbstractRaid.isChunkUnloaded(Entity.RemovalReason.KILLED));
        assertFalse(AbstractRaid.isChunkUnloaded(Entity.RemovalReason.DISCARDED));
        assertFalse(AbstractRaid.isChunkUnloaded(Entity.RemovalReason.CHANGED_DIMENSION));
        assertFalse(AbstractRaid.isChunkUnloaded(Entity.RemovalReason.UNLOADED_WITH_PLAYER));
        assertFalse(AbstractRaid.isChunkUnloaded(null));
    }

}
