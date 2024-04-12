package tessellator.editor.graph.edge.line;

import javafx.scene.Node;
import javafx.scene.shape.Shape;
import tessellator.editor.graph.edge.CoordinatePair;
import tessellator.editor.graph.edge.line.shape.EaseInEaseOutLineFactory;
import tessellator.editor.graph.edge.line.shape.LineShape;
import tessellator.editor.graph.edge.line.shape.StraightLineFactory;
import tessellator.editor.graph.edge.line.style.Avocado;
import tessellator.editor.graph.edge.line.style.LineStyle;
import tessellator.editor.graph.edge.line.style.Scarlet;
import tessellator.editor.graph.edge.line.style.Spectral;

/**
 * A factory object that's used to make one type of line. Customise the factory
 * by passing the shape and style of line you would like it to produce and it will
 * produce only that type of line.
 */
public class LineFactory {

    private final LineShape shape;
    private final LineStyle style;
    
    /**
     * Customise the factory with line shape and style of your choosing.
     * 
     * @param shape The shape of the line.
     * @param style The apperance of the line.
     */
    public LineFactory(LineShape shape, LineStyle style) {
        this.shape = shape;
        this.style = style;
    }

    /**
     * Produce a line bounded between the coordinates provided that conforms to the shape
     * and style specified by the factory.
     * 
     * @param boundCoords The double property coordinates that the line will be bound between.
     * @return A bounded line that matches the style and shape of this factory.
     */
    public Node createLine(CoordinatePair boundCoords) {
        Node line = null;
        switch (shape) {
            case STRAIGHT:
                line = StraightLineFactory.createLine(boundCoords);
                break;
            case EASEINEASEOUT:
                line = EaseInEaseOutLineFactory.createLine(boundCoords);
                break;
        }

		switch (style) {
		case AVOCADO:
			Avocado.style((Shape) line);
			break;
		case SCARLET:
			Scarlet.style((Shape) line);
			break;
		case SPECTRAL:
			Spectral.style((Shape) line);
			break;
		}
        return line;
    }
}
