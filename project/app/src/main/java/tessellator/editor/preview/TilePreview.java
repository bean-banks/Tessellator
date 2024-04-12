package tessellator.editor.preview;

import javafx.embed.swing.SwingNode;
import javafx.scene.layout.BorderPane;

/**
 * The object that holds the canvas which drawings are drawn on.
 */
public class TilePreview extends BorderPane {

    private TilePreview() {
    }
    
    public static TilePreview createTilePreview(double width, double height) {
        TilePreview tilePreview = new TilePreview();
        TileCanvasCaretaker.newDocument();
        TileCanvasCaretaker.newCanvas();
        TileCanvasCaretaker.setCanvasDimensions(width, height);
        
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(TileCanvasCaretaker.getCanvas());
        tilePreview.setCenter(swingNode);
        return tilePreview;
    }
}
