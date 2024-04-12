package tessellator.editor.graph.block;

import java.util.Set;
import java.util.HashSet;
import javafx.scene.Node;
import tessellator.editor.graph.edge.component.EdgeComponent;
import javafx.geometry.Point2D;

/**
 * An object groups captures all the components of a connector in one location.
 */
public class Connector {

	private final Node label;
	private final Node visibleRegion;
	private Node transparentRegion;
	private boolean isOutputConnector;
    private Set<EdgeComponent> edgeEnds;
	private Point2D value;
	private Point2D secondaryValue;
	
	/**
	 * Constructs a input or output connectors.
	 * 
	 * @param label The label which identifies the point the connector represents.
	 * @param visibleRegion The larger visual region of a connector which is used for hover detection.
	 * @param transparentRegion The part of the connector that you actually see in the block.
	 * @param isOutputConnector True if its an output connector, false if its an input connector.
	 */
	public Connector(Node label, Node visibleRegion, Node transparentRegion, boolean isOutputConnector) {
		this.label = label;
		this.visibleRegion = visibleRegion;
		this.transparentRegion = transparentRegion;
		this.isOutputConnector = isOutputConnector;
        edgeEnds = new HashSet<>();
		value = new Point2D(0, 0);
		secondaryValue = new Point2D(0, 0);
	}
	
	public Node label() {
		return label;
	}
	
	public Node visibleRegion() {
		return visibleRegion;
	}
	
	public Node transparentRegion() {
		return transparentRegion;
	}
	
	public boolean isOutputConnector() {
		return isOutputConnector;
	}
	
	public void setTransparentRegion(Node region) {
		transparentRegion = region;
	}

    public void addEdgeEnd(EdgeComponent end) {
        edgeEnds.add(end);
    }
    
    public void removeEdgeEnd(EdgeComponent end) {
        edgeEnds.remove(end);
    }

    public Set<EdgeComponent> edgeEnds() {
        return edgeEnds;
    }

	public Point2D value() {
		return value;
	}

	/**
	 * Output and input connectors should both make sure to update their values.
	 * The value of a connector is the coordinate in the canvas they represent.
	 */
	public void setValue(Point2D value) {
		this.value = value;
	}

	/**
	 * Only input connectors should have a secondary value. A seconday value
	 * is the coordinate of the point that comes after the point which the 'value'
	 * represents. This is necessery for attributes that represent transformations
	 * such as slide transformations.
	 */
	public void setSecondaryValue(Point2D value) {
		this.secondaryValue = value;
	}

	public Point2D secondaryValue() {
		return secondaryValue;
	}
}
