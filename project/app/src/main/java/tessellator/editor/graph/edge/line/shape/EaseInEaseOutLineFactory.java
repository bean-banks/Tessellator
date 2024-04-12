package tessellator.editor.graph.edge.line.shape;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import tessellator.editor.graph.edge.CoordinatePair;
import tessellator.editor.graph.edge.component.line.CubicCurveComponent;

/**
 * A factory object with a creatline() method that can be used to create lines with
 * an 'S' shape curve.
 */
public class EaseInEaseOutLineFactory {

    private static final double BEZIER_FACTOR = 0.4;

    /**
     * Create an 'S' shaped ease in ease out line that is bound between
     * the double properties passed to the method.
     * 
     * @param boundCoords The double properties which the line will be bound between.
     * @return A curved line that starts at the start bound and ends at the end bound.
     */
    public static Node createLine(CoordinatePair boundCoords) {
       
        DoubleProperty x1, y1, x2, y2;
        x1 = boundCoords.startX();
		y1 = boundCoords.startY();
		x2 = boundCoords.endX();
		y2 = boundCoords.endY();

        CubicCurveComponent l = new CubicCurveComponent();
        initStartAndEndPoints(l, x1, y1, x2, y2);
		initControlPoints(l, x1, y1, x2, y2);
        
        return l;
    }

    private static void initStartAndEndPoints(CubicCurveComponent l, DoubleProperty x1, DoubleProperty y1, DoubleProperty x2, DoubleProperty y2) {
		
        l.startXProperty().bind(x1);
		l.startYProperty().bind(y1);
		l.endXProperty().bind(x2);
		l.endYProperty().bind(y2);
    }

    private static void initControlPoints(CubicCurveComponent l, DoubleProperty x1, DoubleProperty y1, DoubleProperty x2, DoubleProperty y2) {
		
        l.controlX1Property().bind(x1.add(x2.subtract(x1).multiply(BEZIER_FACTOR)));
		l.controlY1Property().bind(y1);
		l.controlX2Property().bind(x2.subtract(x2.subtract(x1).multiply(BEZIER_FACTOR)));
		l.controlY2Property().bind(y2);
    }
}
