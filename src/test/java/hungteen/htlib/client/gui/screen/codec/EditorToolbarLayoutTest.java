package hungteen.htlib.client.gui.screen.codec;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EditorToolbarLayoutTest {
    @Test
    void fiveControlsStayUsableAtEverySupportedWidth() {
        for (int width = 320; width <= 1920; width++) {
            for (int actionWidth : new int[]{40, 64}) {
                EditorToolbarLayout layout = EditorToolbarLayout.forWidth(width, actionWidth);
                assertTrue(layout.typeX() >= 0);
                assertTrue(layout.typeWidth() >= 60);
                assertTrue(layout.packWidth() >= 50);
                assertTrue(layout.resourceWidth() >= 50);
                assertTrue(layout.typeX() + layout.typeWidth() < layout.modeX());
                assertTrue(layout.modeX() + layout.modeWidth() < layout.packX());
                assertTrue(layout.packX() + layout.packWidth() < layout.resourceX());
                assertTrue(layout.resourceX() + layout.resourceWidth() < layout.actionsX());
                assertTrue(layout.actionsX() + layout.actionsWidth() < width);
            }
        }
    }
}
