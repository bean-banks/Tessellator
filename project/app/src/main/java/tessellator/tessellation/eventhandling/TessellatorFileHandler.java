package tessellator.tessellation.eventhandling;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tessellator.tessellation.PatternCanvasCaretaker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * An object that handles toolbar level events or the tessellation window.
 */
public class TessellatorFileHandler {

    // necessary for implmenting popups
    private final Stage stage;
    private final PatternCanvasCaretaker canvasCaretaker;

    public TessellatorFileHandler(Stage stage, PatternCanvasCaretaker canvasCaretaker) {
        this.stage = stage;
        this.canvasCaretaker = canvasCaretaker;
    }

    /**
     * Creates a popup that opens and displays an svg file within the pattern preview. If
     * the loading of the file fails an error popup is displayed.
     */
    public void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");

        // Set the initial directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Set extension filters
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SVG files", "*.svg"));

        // Display the svg in the pattern preview
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            boolean isSuccess = canvasCaretaker.newDocument(file);
            if (!isSuccess) {
                // Create a new alert dialog
                Alert alert = new Alert(AlertType.ERROR);
                // Set the title and content text of the alert
                alert.setTitle("File Error");
                alert.setHeaderText("Cannot load file");
                String helpText = """
                        The chosen svg file is not in an acceptable format.
                        The svg element within the file must have one and only
                        one child element which represents the boundary for the
                        tessellation.
                        """;
                alert.setContentText(helpText);
                
                // Show the alert dialog
                alert.showAndWait();
            }
        }
    }

    /**
     * Creates a popup that exports the svg in the preview to a file. If
     * the loading of the file fails an error popup is displayed.
     */
    public void export() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export File");

        // Set extension filters
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SVG files", "*.svg"));

        // Show export file dialog
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            writeToFile(file, canvasCaretaker.getDocString());
        }
    }

    private void writeToFile(File file, String content) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
