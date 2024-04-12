package tessellator.editor.graph.edge.line.style;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;

public class Spectral {

    /**
     * Styles the passed line to have a white glow.
     */
    public static void style(Shape line) {
		line.setFill(Color.TRANSPARENT);
		line.setStroke(Color.WHITESMOKE);
		
		line.setSmooth(true);
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(2);
		dropShadow.setOffsetX(2);
		dropShadow.setOffsetY(2);
		dropShadow.setColor(Color.BLACK);
		
		line.setEffect(dropShadow);
    }
}
