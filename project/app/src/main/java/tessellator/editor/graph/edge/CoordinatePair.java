package tessellator.editor.graph.edge;

import javafx.beans.property.DoubleProperty;

/**
 * An object that holds and provides access to double properties of
 * the start and end nodes which a line is bound between. Use this object
 * to reduce the cardinality (number) of parameters required in methods
 * that operate on double properties related to lines.
 * 
 * Note that each double property must be unique and the double properties
 * startX, startY must belong to the same node, and the double properties
 * endX, endY must belong to the same node but a different node to startX, startY.
 * 
 * Side note: double properties represent the coordinates of a javafx node within its
 * parent object.
 */
public record CoordinatePair(DoubleProperty startX, DoubleProperty startY, DoubleProperty endX, DoubleProperty endY) {}
