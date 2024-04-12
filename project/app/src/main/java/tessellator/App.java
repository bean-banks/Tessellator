/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package tessellator;

import javafx.application.Application;
import javafx.stage.Stage;
import tessellator.editor.Editor;
import javafx.scene.Scene;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Scene scene = new Scene(Editor.createEditor(primaryStage, 1000, 500), 1000, 500);
        primaryStage.setScene(scene);
		primaryStage.setTitle("Editor");
        // if (System.getProperty("os.name").startsWith("Mac")) {
        //     primaryStage.setFullScreenExitHint(""); // Avoids a fullscreen message
        //     primaryStage.setMaximized(true); // Maximizes the window
        // }
		primaryStage.show();
    }
}

