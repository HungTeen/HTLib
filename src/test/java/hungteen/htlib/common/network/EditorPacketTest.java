package hungteen.htlib.common.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EditorPacketTest {
    @Test
    void saveWithMaximumUtf8StringsFitsServerboundPayloadAndRoundTrips() {
        UUID request = UUID.randomUUID();
        String registry = "中".repeat(EditorPacketLimits.MAX_ID_LENGTH);
        String pack = "包".repeat(EditorPacketLimits.MAX_ID_LENGTH);
        String resource = "条".repeat(EditorPacketLimits.MAX_ID_LENGTH);
        String json = "\"" + "中".repeat((EditorPacketLimits.MAX_JSON_BYTES - 2) / 3) + "\"";
        assertTrue(EditorPacketLimits.fitsJson(json));
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            buffer.writeVarInt(4);
            new SaveDataPacket(request, registry, pack, resource, json).encode(buffer);
            assertTrue(buffer.readableBytes() <= 32767);
            assertEquals(4, buffer.readVarInt());
            SaveDataPacket decoded = new SaveDataPacket(buffer);
            assertEquals(0, buffer.readableBytes());
            buffer.clear();
            decoded.encode(buffer);
            assertEquals(request, buffer.readUUID());
            assertEquals(registry, buffer.readUtf(EditorPacketLimits.MAX_ID_LENGTH));
            assertEquals(pack, buffer.readUtf(EditorPacketLimits.MAX_ID_LENGTH));
            assertEquals(resource, buffer.readUtf(EditorPacketLimits.MAX_ID_LENGTH));
            assertEquals(json, buffer.readUtf(EditorPacketLimits.MAX_JSON_LENGTH));
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
    }

    @Test
    void validateUsesSameUtf8BudgetAndPreservesItsRequestId() {
        UUID request = UUID.randomUUID();
        String json = "\"" + "中".repeat(9999) + "\"";
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            new ValidateDataPacket(request, "minecraft:damage_type", json).encode(buffer);
            assertTrue(buffer.readableBytes() < 32767);
            ValidateDataPacket decoded = new ValidateDataPacket(buffer);
            assertEquals(0, buffer.readableBytes());
            buffer.clear();
            decoded.encode(buffer);
            assertEquals(request, buffer.readUUID());
            assertEquals("minecraft:damage_type", buffer.readUtf(EditorPacketLimits.MAX_ID_LENGTH));
            assertEquals(json, buffer.readUtf(EditorPacketLimits.MAX_JSON_LENGTH));
            assertEquals(0, buffer.readableBytes());
        } finally {
            buffer.release();
        }
        assertFalse(EditorPacketLimits.fitsJson("中".repeat(10001)));
        assertFalse(EditorPacketLimits.fitsJson("a".repeat(30001)));
    }
}
