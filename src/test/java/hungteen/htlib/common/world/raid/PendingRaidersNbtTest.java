package hungteen.htlib.common.world.raid;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/26 23:20
 */
class PendingRaidersNbtTest {

    private static Map<UUID, ChunkPos> sample() {
        final Map<UUID, ChunkPos> raiders = new HashMap<>();
        raiders.put(UUID.fromString("5f8f3a4e-9b7d-4c2a-8e31-0b6a1c9d2e47"), new ChunkPos(-22, 10));
        raiders.put(UUID.fromString("0a1b2c3d-4e5f-6071-8293-a4b5c6d7e8f9"), new ChunkPos(118, -64));
        return raiders;
    }

    @Test
    void codecRoundTrip() {
        final Map<UUID, ChunkPos> raiders = sample();
        final ListTag list = AbstractRaid.encodePendingRaiders(raiders);
        assertEquals(2, list.size());
        final Map<UUID, ChunkPos> decoded = AbstractRaid.decodePendingRaiders(list);
        assertEquals(raiders, decoded);
    }

    @Test
    void emptyRaiders() {
        final ListTag list = AbstractRaid.encodePendingRaiders(Map.of());
        assertEquals(0, list.size());
        assertTrue(AbstractRaid.decodePendingRaiders(list).isEmpty());
    }

    @Test
    void decodeIgnoresInvalidEntries() {
        final ListTag list = AbstractRaid.encodePendingRaiders(sample());
        list.add(new CompoundTag()); // Entry without uuid.
        final Map<UUID, ChunkPos> decoded = AbstractRaid.decodePendingRaiders(list);
        assertEquals(2, decoded.size());
        assertEquals(new ChunkPos(-22, 10), decoded.get(UUID.fromString("5f8f3a4e-9b7d-4c2a-8e31-0b6a1c9d2e47")));
    }

    @Test
    void raidersAreTrackedInNbt() {
        final CompoundTag tag = new CompoundTag();
        tag.put(AbstractRaid.RAIDER_IDS, AbstractRaid.encodePendingRaiders(sample()));
        assertTrue(tag.contains(AbstractRaid.RAIDER_IDS, Tag.TAG_LIST));
        final Map<UUID, ChunkPos> decoded =
            AbstractRaid.decodePendingRaiders(tag.getList(AbstractRaid.RAIDER_IDS, CompoundTag.TAG_COMPOUND));
        assertEquals(2, decoded.size());
    }
}
