package tessellator.editor.eventhandling;

import javafx.scene.input.KeyCode;
import tessellator.editor.Editor;

/**
 * An object for handling all key and mouse events at the editor level.
 */
public class EditorEventHandler {

    private final Editor editor;
    private final EditorFileHandler fileHandler;

    public EditorEventHandler(Editor editor, EditorFileHandler fileHandler) {
        this.editor = editor;
        this.fileHandler = fileHandler;
    }

    public void handleEvents() {
        handleKeyEvents();
    }

    private void handleKeyEvents() {
        editor.setOnKeyPressed(ke -> {
            if (ke.getCode().equals(KeyCode.S) && ke.isControlDown()) {
                fileHandler.save();
                ke.consume();
			} 
        });
    }
}
