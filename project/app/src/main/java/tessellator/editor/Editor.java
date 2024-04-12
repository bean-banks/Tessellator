package tessellator.editor;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tessellator.editor.eventhandling.EditorEventHandler;
import tessellator.editor.eventhandling.EditorFileHandler;
import tessellator.editor.graph.GraphBuilder;
import tessellator.editor.graph.block.factory.BlockFactory;
import tessellator.editor.graph.block.factory.DarkBlockFactory;
import tessellator.editor.preview.TilePreview;
import tessellator.editor.shop.BlockShop;
import tessellator.tessellation.Tessellator;

/**
 * The object which groups together the graph builder, tile preview and block shop.
 */
public class Editor extends BorderPane {

    private final GraphBuilder graphBuilder;
    private final BlockShop blockShop;
    private final TilePreview tilePreview;
    private final EditorFileHandler fileHandler;
    private final Stage stage;
    private final BlockFactory blockFactory;
    private final EditorEventHandler eventHandler;
    
    private Editor(Stage stage, double tilePreviewWidth, double tilePreviewHeight) {
        this.stage = stage;
        graphBuilder = new GraphBuilder();
        blockFactory = new DarkBlockFactory(graphBuilder);
        blockShop = BlockShop.createBlockShop(blockFactory);
        tilePreview = TilePreview.createTilePreview(tilePreviewWidth, tilePreviewHeight);
        fileHandler = new EditorFileHandler(graphBuilder, blockFactory, stage);
        eventHandler = new EditorEventHandler(this, fileHandler);
        eventHandler.handleEvents();
    }

    public static Editor createEditor(Stage stage, double width, double height) {
        
        Editor editor = new Editor(stage, width*0.25, height*0.5);

        SplitPane secondarySp = new SplitPane(editor.tilePreview(), editor.blockShop());
        secondarySp.setOrientation(Orientation.VERTICAL);
        // This accounts for the width of the dividers
        secondarySp.setDividerPositions(0.5+(5/height));

        SplitPane primarySp = new SplitPane(editor.graphBuilder(), secondarySp);
        primarySp.setOrientation(Orientation.HORIZONTAL);
        // This accounts for the width of the dividers
        primarySp.setDividerPositions(0.75-(5/width));

        Color lightGrey = Color.rgb(80, 80, 80);
        Background diagonalGradientBackground = new Background(new BackgroundFill(lightGrey, null, null));

        primarySp.setBackground(diagonalGradientBackground);

        // Create a menu bar
        MenuBar menuBar = new MenuBar();
        // Create the file menu
        Menu fileMenu = createFileMenu(editor);
        // Create the tessellation menu
        Menu tessellationMenu = createTessellationMenu();
        // Create the help menu
        Menu helpMenu = createHelpMenu();
        // Add menus to the menu bar
        menuBar.getMenus().addAll(fileMenu, tessellationMenu, helpMenu);
        //final String os = System.getProperty("os.name");
        //if (os != null && os.startsWith("Mac"))
        menuBar.useSystemMenuBarProperty().set(true);

        editor.setTop(menuBar);
        editor.setCenter(primarySp);

        return editor;
    }

    private static Menu createFileMenu(Editor editor) {
        EditorFileHandler fileHandler = editor.fileHandler();

        // Create menu and menu items
        Menu fileMenu = new Menu("File");
        MenuItem newItem = new MenuItem("New");
        MenuItem openItem = new MenuItem("Open");
        MenuItem saveItem = new MenuItem("Save");
        MenuItem saveAsItem = new MenuItem("Save as");
        MenuItem exportItem = new MenuItem("Export");
        
        // Handle the events of the menu items
        newItem.setOnAction(a -> fileHandler.newGraph());
        openItem.setOnAction(a -> fileHandler.open());
        saveItem.setOnAction(a -> fileHandler.save());
        saveAsItem.setOnAction(a -> fileHandler.saveAs());
        exportItem.setOnAction(a -> fileHandler.export());

        fileMenu.getItems().addAll(newItem, openItem, saveItem, saveAsItem, exportItem);
        return fileMenu;
    }

    private static Menu createTessellationMenu() {

        // Create menus and menu items
        Menu fileMenu = new Menu("Tessellation");
        MenuItem newItem = new MenuItem("New");

        newItem.setOnAction(a -> createTessellationWindow());

        fileMenu.getItems().addAll(newItem);

        return fileMenu;
    }

    private static Menu createHelpMenu() {
        // Create menus and menu items
        Menu helpMenu = new Menu("Help");
        MenuItem guideItem = new MenuItem("Guide");

        guideItem.setOnAction(a -> createHelpGuide());
        helpMenu.getItems().addAll(guideItem);
        return helpMenu;
    }

    private static void createHelpGuide() {
        // Create a new stage for the help guide
        Stage helpStage = new Stage();

        // Create and format the "Controls" header
        Text controlsHeader = new Text("Controls");
        controlsHeader.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

        // Create and format the control details
        Text controlDetails = new Text("Zoom in: " + formatShortcut("Ctrl+PLUS, Ctrl+P") + "\n"
                + "Zoom out: " + formatShortcut("Ctrl+MINUS, Ctrl+M") + "\n"
                + "Select: " + formatShortcut("Shift+Drag") + "\n"
                + "Deselect: " + formatShortcut("Esc") + "\n"
                + "Delete: " + formatShortcut("Backspace, Del") + "\n"
                + "Toggle tile labels: " + formatShortcut("Shift+Click (on a block)"));

        // Create and format the "Notes" header
        Text notesHeader = new Text("Notes");
        notesHeader.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

        // Create and format the notes details
        Text notesDetails = new Text("If an invalid value is given to a block's text field\n"
                + "then the effects of that text field are nullified.\n\n"
                + "If a connection joining two blocks cannot be made,\n"
                + "this is because one or more of the graph constraints\n"
                + "aren't being satisfied by that connection.");


        // Create a layout for the help guide window
        VBox guideLayout = new VBox();
        guideLayout.setSpacing(10);
        guideLayout.setPadding(new Insets(10));
        guideLayout.getChildren().addAll(controlsHeader, controlDetails, notesHeader, notesDetails);

        // Create the Scene for help guide
        Scene helpScene = new Scene(guideLayout, 400, 300);

        // Set the scene to the help stage and show it
        helpStage.setScene(helpScene);
        helpStage.setTitle("Guide");
        helpStage.show();
    }

    // Method to format the keyboard shortcut part of the text (value after colon)
    private static String formatShortcut(String shortcut) {
        return shortcut.replaceAll(":(.*)", ": " + "$1");
    }

    private static void createTessellationWindow() {
        Stage newStage = new Stage();
        BorderPane tessellator = Tessellator.createTessellator(newStage, 800, 500);
        Scene newScene = new Scene(tessellator, 800, 500);
        newStage.setScene(newScene);
        newStage.setTitle("Tessellator");
        newStage.show();
    }

    public GraphBuilder graphBuilder() {
        return graphBuilder;
    }

    public BlockShop blockShop() {
        return blockShop;
    }

    public TilePreview tilePreview() {
        return tilePreview;
    }

    public EditorFileHandler fileHandler() {
        return fileHandler;
    }

    public Stage stage() {
        return stage;
    }
}
