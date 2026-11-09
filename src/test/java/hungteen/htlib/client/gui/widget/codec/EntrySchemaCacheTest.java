package hungteen.htlib.client.gui.widget.codec;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntrySchemaCacheTest {
    @AfterEach
    void clear() {
        EntrySchemaCache.clear();
    }

    @Test
    void failedSchemaRequestReleasesWaitersAndCanBeRetried() {
        List<String> requests = new ArrayList<>();
        List<String> replies = new ArrayList<>();
        EntrySchemaCache.acquire("demo:type", replies::add, requests::add);
        EntrySchemaCache.acquire("demo:type", replies::add, requests::add);
        assertEquals(List.of("demo:type"), requests);
        EntrySchemaCache.update("demo:type", "");
        assertEquals(List.of("", ""), replies);
        assertNull(EntrySchemaCache.schema("demo:type"));
        EntrySchemaCache.acquire("demo:type", replies::add, requests::add);
        assertEquals(List.of("demo:type", "demo:type"), requests);
        EntrySchemaCache.update("demo:type", "{\"type\":\"STRING\"}");
        assertEquals("{\"type\":\"STRING\"}", replies.get(2));
    }

    @Test
    void successIsCachedUntilReloadClearsIt() {
        List<String> requests = new ArrayList<>();
        List<String> replies = new ArrayList<>();
        EntrySchemaCache.update("demo:type", "{\"type\":\"INT\"}");
        EntrySchemaCache.acquire("demo:type", replies::add, requests::add);
        assertTrue(requests.isEmpty());
        assertEquals(List.of("{\"type\":\"INT\"}"), replies);
        EntrySchemaCache.clear();
        EntrySchemaCache.acquire("demo:type", replies::add, requests::add);
        assertEquals(List.of("demo:type"), requests);
    }
}
