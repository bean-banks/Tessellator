package tessellator.editor.eventhandling;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tessellator.editor.graph.GraphBuilder;
import tessellator.editor.graph.block.factory.BlockFactory;
import tessellator.editor.preview.TileCanvasCaretaker;

/**
 * An object that provides specific methods for handling all functionalities related
 * to reading and writing to disk within at the editor level.
 */
public class EditorFileHandler {
    
    private final GraphBuilder graphBuilder;
    private final BlockFactory blockFactory;
    // necessary for implmenting popups
    private final Stage stage;
    // If the save path is null then a save becomes a saveAs
    private File saveFile;

    public EditorFileHandler(GraphBuilder graphBuilder, BlockFactory factory, Stage stage) {
        this.graphBuilder = graphBuilder;
        blockFactory = factory;
        this.stage = stage;
    }

    /**
     * Creates a popup that allows the user to create a new blank graph within the graph builder,
     * as well as a new blank file which can be save to.
     */
    public void newGraph() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("New File");

        // Set extension filters
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Json files", "*.json"));

        // Show new file dialog
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            saveFile = file;
            graphBuilder.clearContent();
            TileCanvasCaretaker.getDrawings().clear();
            TileCanvasCaretaker.applyDrawingsToCanvas();
            writeToFile(saveFile, graphBuilder.createJson());
        }
    }

    /**
     * Creates a popup that opens a file that was a previously saved graph and display
     * it within the editor.
     */
    public void open() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");

        // Set the initial directory
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Set extension filters
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Json files", "*.json"));

        // Saves are now automatically done on this opened file
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            saveFile = file;
            graphBuilder.interpretJson(readFromFile(saveFile), blockFactory);
        }

    }

    /**
     * Save the graph within the graph builder to a file on disk. If the file hasn't already
     * been specified then a pop up for detailing the file to save to appears.
     */
    public void save() {
        if (saveFile == null) {
            saveAs();
        } else {
            writeToFile(saveFile, graphBuilder.createJson());
        }
    }

    /**
     * Creates a popup letting the user save the graph within the graph builder to a file on disk.
     */
    public void saveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");

        // Set extension filters
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Json files", "*.json"));

        // Show save file dialog
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            saveFile = file;
            writeToFile(saveFile, graphBuilder.createJson());
        }
    }

    /**
     * Creates a popup allowing the user to save the svg shown in the preview to a file on disk.
     */
    public void export() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export File");

        // Set extension filters
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("SVG files", "*.svg"));

        // Show export file dialog
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            writeToFile(file, TileCanvasCaretaker.svgDocToString());
        }
    }

    private void writeToFile(File file, String content) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile(File file) {
        try {
            // Read the entire contents of the file into a single string
            return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
}
