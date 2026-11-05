package hungteen.htlib.common.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class SchemaPacketTest {
    private static final int CLIENTBOUND_PAYLOAD_BYTES = 1048576;
    // Use a five-byte discriminator to include the largest SimpleChannel header.
    private static final int DISCRIMINATOR = Integer.MAX_VALUE;
    private static final String REGISTRY = "中".repeat(EditorPacketLimits.MAX_ID_LENGTH);

    @ParameterizedTest
    @ValueSource(strings = {"x", "中", "\uD83D\uDE80"})
    void schemaAtUtf8BudgetFitsClientboundPayloadAndRoundTrips(String unit) {
        String schema = schemaAtBudget(unit);
        assertEquals(EditorPacketLimits.MAX_SCHEMA_BYTES, schema.getBytes(StandardCharsets.UTF_8).length);
        assertTrue(EditorPacketLimits.fitsSchema(schema));
        assertRoundTrip(new EntrySchemaResponsePacket(REGISTRY, schema), schema, "");
    }

    @ParameterizedTest
    @ValueSource(strings = {"x", "中", "\uD83D\uDE80"})
    void senderRejectsOneByteBeyondBudgetIncludingMultibyteText(String unit) {
        String schema = schemaAtBudget(unit) + " ";
        assertEquals(EditorPacketLimits.MAX_SCHEMA_BYTES + 1, schema.getBytes(StandardCharsets.UTF_8).length);
        assertFalse(EditorPacketLimits.fitsSchema(schema));
        assertThrows(RuntimeException.class, () -> new EntrySchemaResponsePacket(REGISTRY, schema));
    }

    @ParameterizedTest
    @ValueSource(strings = {"x", "中", "\uD83D\uDE80"})
    void decoderEnforcesBudgetEvenWhenSenderChecksAreBypassed(String unit) {
        String schema = schemaAtBudget(unit) + " ";
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        try {
            buffer.writeUtf(REGISTRY, EditorPacketLimits.MAX_ID_LENGTH);
            // A raw peer can send this within Minecraft's payload limit. Unicode also
            // passes readUtf's character limit, so the explicit byte budget must reject it.
            buffer.writeUtf(schema, EditorPacketLimits.MAX_SCHEMA_LENGTH + 1);
            buffer.writeUtf("", EditorPacketLimits.MAX_RESULT_LENGTH);
            assertTrue(buffer.writerIndex() < CLIENTBOUND_PAYLOAD_BYTES);
            assertThrows(RuntimeException.class, () -> new EntrySchemaResponsePacket(buffer));
        } finally {
            buffer.release();
        }
    }

    @Test
    void oversizedSchemaCanBeReportedByBoundedFailurePacket() {
        String detail = "Schema exceeds the network budget: " + "错误".repeat(EditorPacketLimits.MAX_RESULT_LENGTH);
        String expectedError = EditorPacketLimits.resultMessage(detail);
        assertEquals(EditorPacketLimits.MAX_RESULT_LENGTH, expectedError.length());
        assertRoundTrip(EntrySchemaResponsePacket.failure(REGISTRY, detail), "", expectedError);
    }

    private static String schemaAtBudget(String unit) {
        String prefix = "{\"value\":\"";
        String suffix = "\"}";
        int remaining = EditorPacketLimits.MAX_SCHEMA_BYTES - prefix.length() - suffix.length();
        int unitBytes = unit.getBytes(StandardCharsets.UTF_8).length;
        return prefix + unit.repeat(remaining / unitBytes) + "x".repeat(remaining % unitBytes) + suffix;
    }

    private static void assertRoundTrip(EntrySchemaResponsePacket packet, String schema, String error) {
        FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
        FriendlyByteBuf reencoded = new FriendlyByteBuf(Unpooled.buffer());
        try {
            buffer.writeVarInt(DISCRIMINATOR);
            packet.encode(buffer);
            assertTrue(buffer.writerIndex() < CLIENTBOUND_PAYLOAD_BYTES);
            assertDoesNotThrow(() -> new ClientboundCustomPayloadPacket(
                    new ResourceLocation("htlib", "networking"), buffer));
            assertEquals(DISCRIMINATOR, buffer.readVarInt());
            EntrySchemaResponsePacket decoded = new EntrySchemaResponsePacket(buffer);
            assertEquals(0, buffer.readableBytes());
            decoded.encode(reencoded);
            assertEquals(REGISTRY, reencoded.readUtf(EditorPacketLimits.MAX_ID_LENGTH));
            assertEquals(schema, reencoded.readUtf(EditorPacketLimits.MAX_SCHEMA_LENGTH));
            assertEquals(error, reencoded.readUtf(EditorPacketLimits.MAX_RESULT_LENGTH));
            assertEquals(0, reencoded.readableBytes());
        } finally {
            reencoded.release();
            buffer.release();
        }
    }
}
