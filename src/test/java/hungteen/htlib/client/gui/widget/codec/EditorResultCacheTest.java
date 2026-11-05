package hungteen.htlib.client.gui.widget.codec;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EditorResultCacheTest {
    @AfterEach
    void clear() {
        EditorResultCache.release("save");
        EditorResultCache.release("validate");
    }

    @Test
    void olderResponseCannotConsumeNewRequestsCallback() {
        List<String> messages = new ArrayList<>();
        UUID first = EditorResultCache.await("save", (success, message) -> messages.add("old:" + message));
        UUID second = EditorResultCache.await("save", (success, message) -> messages.add(message));
        EditorResultCache.complete("save", first, true, "stale");
        assertTrue(messages.isEmpty());
        EditorResultCache.complete("save", second, true, "current");
        EditorResultCache.complete("save", second, true, "duplicate");
        assertEquals(List.of("current"), messages);
    }

    @Test
    void saveAndValidateResponsesMayArriveInEitherOrder() {
        List<String> messages = new ArrayList<>();
        UUID save = EditorResultCache.await("save", (success, message) -> messages.add(message));
        UUID validate = EditorResultCache.await("validate", (success, message) -> messages.add(message));
        EditorResultCache.complete("validate", save, true, "wrong action");
        EditorResultCache.complete("validate", validate, true, "validated");
        EditorResultCache.complete("save", save, true, "saved");
        assertEquals(List.of("validated", "saved"), messages);
    }

    @Test
    void closingOrSwitchingDocumentIgnoresLateResponse() {
        List<String> messages = new ArrayList<>();
        UUID request = EditorResultCache.await("save", (success, message) -> messages.add(message));
        EditorResultCache.release("save");
        EditorResultCache.complete("save", request, true, "late");
        assertTrue(messages.isEmpty());
    }
}
