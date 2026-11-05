package hungteen.htlib.client.gui.screen.codec;

/** Five toolbar controls remain separate at Minecraft's minimum 320-pixel GUI width. */
record EditorToolbarLayout(int typeX, int typeWidth, int modeX, int modeWidth,
                           int packX, int packWidth, int resourceX, int resourceWidth, int actionsX, int actionsWidth) {
    static EditorToolbarLayout forWidth(int screenWidth, int actionsWidth) {
        int margin = 8;
        int gap = 6;
        int modeWidth = 50;
        int available = screenWidth - margin * 2 - gap * 4 - modeWidth - actionsWidth;
        int typeWidth = Math.min(120, Math.max(60, available - 110));
        int packWidth = Math.min(80, Math.max(40, (available - typeWidth) / 2));
        int resourceWidth = available - typeWidth - packWidth;
        int modeX = margin + typeWidth + gap;
        int packX = modeX + modeWidth + gap;
        int resourceX = packX + packWidth + gap;
        return new EditorToolbarLayout(margin, typeWidth, modeX, modeWidth, packX, packWidth,
            resourceX, resourceWidth, screenWidth - margin - actionsWidth, actionsWidth);
    }
}
