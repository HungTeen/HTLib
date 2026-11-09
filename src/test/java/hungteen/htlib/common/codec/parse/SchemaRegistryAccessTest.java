package hungteen.htlib.common.codec.parse;

import net.minecraft.core.RegistryAccess;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class SchemaRegistryAccessTest {

    @AfterEach
    void clearContext() {
        SchemaRegistryAccess.set(null);
    }

    @Test
    void restoresOuterRegistryAfterNestedLookup() {
        RegistryAccess outer = new RegistryAccess.ImmutableRegistryAccess(List.of());
        RegistryAccess inner = new RegistryAccess.ImmutableRegistryAccess(List.of());
        SchemaRegistryAccess.set(outer);

        String result = SchemaRegistryAccess.withAccess(inner, () -> {
            assertSame(inner, SchemaRegistryAccess.get());
            return "resolved";
        });

        assertEquals("resolved", result);
        assertSame(outer, SchemaRegistryAccess.get());
    }

    @Test
    void failedParseDoesNotRetainItsRegistry() {
        assertNull(SchemaRegistryAccess.get());
        assertThrows(IllegalStateException.class, () ->
            SchemaRegistryAccess.withAccess(RegistryAccess.EMPTY, () -> {
                throw new IllegalStateException("unrecognized codec");
            }));
        assertNull(SchemaRegistryAccess.get());
    }

    @Test
    void registryContextsDoNotLeakAcrossThreads() throws InterruptedException {
        SchemaRegistryAccess.set(RegistryAccess.EMPTY);
        AtomicReference<RegistryAccess> other = new AtomicReference<>(RegistryAccess.EMPTY);
        Thread thread = new Thread(() -> other.set(SchemaRegistryAccess.get()));
        thread.start();
        thread.join();
        assertNull(other.get());
        assertSame(RegistryAccess.EMPTY, SchemaRegistryAccess.get());
    }
}
