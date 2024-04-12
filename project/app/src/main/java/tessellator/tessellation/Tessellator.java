package tessellator.tessellation;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tessellator.tessellation.eventhandling.TessellatorFileHandler;

/**
 * An object which represents the body of a tessellation window. It holds and
 * oranises the parameter space and the pattern preview.
 */
public class Tessellator extends BorderPane {

    private final Stage stage;
    private final PatternPreview preview;
    private final ParameterSpace paramSpace;
    private final TessellatorFileHandler fileHandler;

    private Tessellator(Stage stage){
        this.stage = stage;
        PatternCanvasCaretaker canvasCaretaker = new PatternCanvasCaretaker();
        preview = new PatternPreview(canvasCaretaker);
        paramSpace = new ParameterSpace(canvasCaretaker);
        fileHandler = new TessellatorFileHandler(stage, canvasCaretaker);
    }
    
    public static Tessellator createTessellator(Stage stage, int width, int height) {
        
        Tessellator tessellator = new Tessellator(stage);

        SplitPane sp = new SplitPane(tessellator.patternPreview(), tessellator.parameterSpace());
        sp.setOrientation(Orientation.VERTICAL);
        // This accounts for the width of the divider
        sp.setDividerPositions(0.75+(5/height));

        // Create a menu bar
        MenuBar menuBar = new MenuBar();
        // Create the file menu
        Menu fileMenu = createFileMenu(tessellator);
        // Create the help menu
        Menu helpMenu = createHelpMenu();
        // Add menus to the menu bar
        menuBar.getMenus().addAll(fileMenu, helpMenu);
        menuBar.useSystemMenuBarProperty().set(true);

        tessellator.setTop(menuBar);
        tessellator.setCenter(sp);

        sp.setOnMousePressed(me -> tessellator.requestFocus());

        return tessellator;
    }

    private static Menu createFileMenu(Tessellator tessellator) {
        TessellatorFileHandler fileHandler = tessellator.fileHandler();

        // Create menu and menu items
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open");
        MenuItem exportItem = new MenuItem("Export");

        openItem.setOnAction(a -> fileHandler.open());
        exportItem.setOnAction(a -> fileHandler.export());

        fileMenu.getItems().addAll(openItem, exportItem);
        return fileMenu;
    }

    private static Menu createHelpMenu() {

        // Create menu and menu items
        Menu helpMenu = new Menu("Help");
        MenuItem guideItem = new MenuItem("Guide");

        guideItem.setOnAction(a -> createHelpGuide());
        helpMenu.getItems().addAll(guideItem);
        return helpMenu;
    }

    private static void createHelpGuide() {
        // Create a new stage for the help guide
        Stage helpStage = new Stage();

        // Create and format the "Notes" header
        Text notesHeader = new Text("Notes");
        notesHeader.setFont(Font.font("Verdana", FontWeight.BOLD, 16));

        // Create and format the notes details
        Text notesDetails = new Text("If an invalid value is given to a parameter's text field\n"
                + "then the effects of that text field are nullified.");
                
        // Create a layout for the help guide window
        VBox guideLayout = new VBox();
        guideLayout.setSpacing(10);
        guideLayout.setPadding(new Insets(10));
        guideLayout.getChildren().addAll(notesHeader, notesDetails);

        // Create the Scene for help guide
        Scene helpScene = new Scene(guideLayout, 300, 100);

        // Set the scene to the help stage and show it
        helpStage.setScene(helpScene);
        helpStage.setTitle("Guide");
        helpStage.show();
    }

    public PatternPreview patternPreview() {
        return preview;
    }

    public ParameterSpace parameterSpace() {
        return paramSpace;
    }

    public TessellatorFileHandler fileHandler() {
        return fileHandler;
    }

    public Stage stage() {
        return stage;
    }
}
