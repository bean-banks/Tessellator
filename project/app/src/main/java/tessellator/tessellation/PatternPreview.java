package tessellator.tessellation;

import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * An object which displays the results of the tessellation generation.
 */
public class PatternPreview extends StackPane {

    //double dragStartX, dragStartY;
    private final PatternCanvasCaretaker canvasCaretaker;

    public PatternPreview(PatternCanvasCaretaker canvasCaretaker) {
        this.canvasCaretaker = canvasCaretaker;
        WebView webView = createConfiguredWebView();
        getChildren().add(webView);
    }

    private WebView createConfiguredWebView() {
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        canvasCaretaker.setCanvas(webEngine);

        // Disable context menu
        webView.setContextMenuEnabled(false);

        // Inject CSS to hide the scroll bars
        // webEngine.documentProperty().addListener((obs, oldDoc, newDoc) -> {
        //     if (newDoc != null) {
        //         String css = "body { overflow: hidden; }";
        //         ((org.w3c.dom.Document) newDoc).getDocumentElement()
        //                 .getElementsByTagName("head").item(0)
        //                 .appendChild(((org.w3c.dom.Document) newDoc).createElement("style")).setTextContent(css);
        //     }
        // });

        // Generate the scroll effect by draggin on the web view
        // webView.setOnMousePressed(event -> {
        //     dragStartX = event.getSceneX();
        //     dragStartY = event.getSceneY();
        // });

        // webView.setOnMouseDragged(event -> {
        //     double offsetX = event.getSceneX() - dragStartX;
        //     double offsetY = event.getSceneY() - dragStartY;

        //     // Update the scroll position using JavaScript
        //     webEngine.executeScript("window.scrollBy(" + (-offsetX) + ", " + (-offsetY) + ");");

        //     // Update the drag start coordinates
        //     dragStartX = event.getSceneX();
        //     dragStartY = event.getSceneY();
        // });

        return webView;
    }
}
