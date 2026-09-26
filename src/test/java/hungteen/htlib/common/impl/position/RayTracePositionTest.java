package hungteen.htlib.common.impl.position;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author PangTeen
 * @program HTLib
 * @create 2026/9/26 12:00
 */
class RayTracePositionTest {

    private static RayTracePosition create(double radius) {
        return new RayTracePosition(new Vec3(1, 0, 2), 2, radius, 16, false, 5, 8, 60);
    }

    @Test
    void codecRoundTrip() {
        final RayTracePosition position = create(8);
        final var encoded = RayTracePosition.CODEC.encodeStart(JsonOps.INSTANCE, position).result().orElseThrow();
        final RayTracePosition decoded = RayTracePosition.CODEC.parse(JsonOps.INSTANCE, encoded).result().orElseThrow();
        assertEquals(position.getCenterOffset(), decoded.getCenterOffset());
        assertEquals(position.getExcludeRadius(), decoded.getExcludeRadius());
        assertEquals(position.getRadius(), decoded.getRadius());
        assertEquals(position.getHeightOffset(), decoded.getHeightOffset());
        assertEquals(position.isCircle(), decoded.isCircle());
        assertEquals(position.getReflectTimes(), decoded.getReflectTimes());
        assertEquals(position.getPositionQueueSize(), decoded.getPositionQueueSize());
        assertEquals(position.getRefreshInterval(), decoded.getRefreshInterval());
    }

    @Test
    void codecDefaultValues() {
        final RayTracePosition position = RayTracePosition.CODEC.parse(JsonOps.INSTANCE,
            JsonParser.parseString("{\"radius\": 4.0}")).result().orElseThrow();
        assertEquals(Vec3.ZERO, position.getCenterOffset());
        assertEquals(0D, position.getExcludeRadius());
        assertEquals(4D, position.getRadius());
        assertEquals(0D, position.getHeightOffset());
        assertTrue(position.isCircle());
        assertEquals(3, position.getReflectTimes());
        assertEquals(16, position.getPositionQueueSize());
        assertEquals(100, position.getRefreshInterval());
        // Radius is required.
        assertTrue(RayTracePosition.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("{}")).result().isEmpty());
    }

    @Test
    void isInAreaChecksRadius() {
        final Vec3 center = Vec3.ZERO;
        final RayTracePosition circle = new RayTracePosition(Vec3.ZERO, 2, 10, 0, true, 3, 16, 100);
        assertTrue(circle.isInArea(center, new Vec3(5, 100, 0)));
        assertFalse(circle.isInArea(center, new Vec3(1, 0, 0)));
        assertFalse(circle.isInArea(center, new Vec3(11, 0, 0)));
        final RayTracePosition square = new RayTracePosition(Vec3.ZERO, 0, 10, 0, false, 3, 16, 100);
        assertTrue(square.isInArea(center, new Vec3(9, 0, 9)));
        assertFalse(square.isInArea(center, new Vec3(11, 0, 1)));
    }

    @Test
    void bounceLeavesSurface() {
        final RandomSource random = RandomSource.create(10L);
        final RayTracePosition position = create(8);
        final Vec3 incident = new Vec3(0.1, -1, 0.1).normalize();
        for (Direction face : Direction.values()) {
            final Vec3 normal = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ());
            final Vec3 bounced = position.bounce(random, incident, face);
            assertEquals(1D, bounced.length(), 1.0E-6);
            assertTrue(bounced.dot(normal) > 0, "Bounced direction should leave the surface: " + face);
        }
    }

    @Test
    void spawnPositionIsOnCandidate() {
        final RayTracePosition position = create(8);
        final RandomSource random = RandomSource.create(1L);
        final BlockPos candidate = new BlockPos(4, 64, -4);
        for (int i = 0; i < 100; ++i) {
            final Vec3 spawnPosition = position.toSpawnPosition(candidate, random);
            assertEquals(64D, spawnPosition.y, 0D);
            final double dx = spawnPosition.x - 4.5D;
            final double dz = spawnPosition.z + 3.5D;
            assertTrue(Math.sqrt(dx * dx + dz * dz) <= 0.3D + 1.0E-6D);
        }
    }
}
