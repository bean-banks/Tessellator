package tessellator.editor.graph.edge.line.style;

import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class Avocado {

    /**
     * Styles the passed line avocado green.
     */
    public static void style(Shape line) {
		line.setFill(Color.TRANSPARENT);
		line.setStroke(Color.rgb(86, 130, 3));
    }
}
