package tessellator.editor.graph.edge.line.style;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class Scarlet {

    /**
     * Styles the passed line to be scarlet red.
     */
    public static void style(Shape line) {
		line.setFill(Color.TRANSPARENT);
		line.setStroke(Color.FIREBRICK);
    }
}
