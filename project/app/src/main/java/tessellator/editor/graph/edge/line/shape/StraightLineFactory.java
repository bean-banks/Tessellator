package tessellator.editor.graph.edge.line.shape;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import tessellator.editor.graph.edge.CoordinatePair;
import tessellator.editor.graph.edge.component.line.StraightLineComponent;

/**
 * A factory object with a creatline() method that can be used to create straight lines.
 */
public class StraightLineFactory {

    /**
     * Create a straight line that is bound between the double properties
     * passed to the method.
     * 
     * @param boundCoords The double properties which the line will be bound between.
     * @return A straight line that starts at the start bound and ends at the end bound.
     */
    public static Node createLine(CoordinatePair boundCoords) {
       
        DoubleProperty x1, y1, x2, y2;
        x1 = boundCoords.startX();
		y1 = boundCoords.startY();
		x2 = boundCoords.endX();
		y2 = boundCoords.endY();

        StraightLineComponent l = new StraightLineComponent();
        initStartAndEndPoints(l, x1, y1, x2, y2);
        
        return l;
    }

    private static void initStartAndEndPoints(StraightLineComponent l, DoubleProperty x1, DoubleProperty y1, DoubleProperty x2, DoubleProperty y2) {
		
        l.startXProperty().bind(x1);
		l.startYProperty().bind(y1);
		l.endXProperty().bind(x2);
		l.endYProperty().bind(y2);
    }
}
