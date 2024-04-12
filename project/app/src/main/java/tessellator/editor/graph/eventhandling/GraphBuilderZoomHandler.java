package tessellator.editor.graph.eventhandling;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.shape.Shape;
import tessellator.editor.graph.GraphBuilder;
import tessellator.editor.graph.block.Container;
import tessellator.editor.graph.edge.Edge;
import tessellator.editor.graph.edge.component.EdgeComponent;
import tessellator.editor.graph.edge.component.end.ConnectorComponent;

/**
 * An object which provides methods for handling zoom events within the graph builder.
 */
public class GraphBuilderZoomHandler {

    private double scale;
	private double zoomFactor;
	private double zoomConstant;
	private final Set<Edge> visitedEdges = new HashSet<>();
    private final GraphBuilder graphBuilder;

	public GraphBuilderZoomHandler(GraphBuilder graphBuilder, double zoomConstant) {
		scale = 1;
		this.zoomConstant = zoomConstant;
        this.graphBuilder = graphBuilder;
	}

    /**
     * This method can't just scale the graph builder because the drag strategies of its
     * children would break. Instead the blocks, the gaps between them and their edges
     * are scaled, but the graph builder isn't scaled.
     */
    public void zoom(boolean isZoomIn) {
        // Adjust the zoom factor to represent a zoom in or a zoom out
        zoomFactor = isZoomIn ? zoomConstant : 1/zoomConstant;
        scale = graphBuilder.getZoomScale()*zoomFactor;
        graphBuilder.setZoomScale(scale);
        graphBuilder.setStrokeWidthOfLines(graphBuilder.getStrokeWidthOfLines()*zoomFactor);
        
        visitedEdges.clear();
        graphBuilder.getChildren().forEach(n -> {
            if (n instanceof EdgeComponent) {
                EdgeComponent component = (EdgeComponent) n;
                // Since each line has three components, i need this conditional to prevent
                // repeating the same process three times since we are looping through all the nodes
                // in the diagram.
                Edge edge = component.getEdge().orElseThrow();
                if (!visitedEdges.contains(edge)) {
                    scaleLine(edge);
                }
            }
            else if (n instanceof Container) {
                scaleContainer((Container) n);
            }
        });
    }

	private void scaleLine(Edge edge) {
		visitedEdges.add(edge);
        Shape line = (Shape) edge.lineComponent();
		line.setStrokeWidth(graphBuilder.getStrokeWidthOfLines());
		
        rebindLineBounds(edge);
	}

    private void rebindLineBounds(Edge edge) {

        Container inContainer = edge.inputBlock().getContainer();
        Container outContainer = edge.outputBlock().getContainer();

        double inBodyWidth = inContainer.getBlock().getBody().minWidth(0);
        double inXOffset = (inBodyWidth-inBodyWidth*scale)/2;
       
        double outBodyWidth = outContainer.getBlock().getBody().minWidth(0);
        double outXOffset = -((outBodyWidth-outBodyWidth*scale)/2);

        // The y offsets are calculated on the basis that when a container is vertically scaled
        // the Y layout of its center line is invariant because the top and bottom the container shrink towards
        // the center line. Hence we find the the height difference between the connector the line
        // is connected to and the center line, scale that difference, then add that value to the center line
        // during the binding to ensure the line ends meet their connectors at the correct heights
        // There is a slight anomoly at very large zoom outs       
        double inCenterHeight = inContainer.getBoundsInLocal().getHeight()/2;
        double outCenterHeight = outContainer.getBoundsInLocal().getHeight()/2;

        ConnectorComponent startComponent = (ConnectorComponent) edge.startComponent();
        ConnectorComponent endComponent = (ConnectorComponent) edge.endComponent();
        Node start = startComponent.getConnector().orElseThrow().transparentRegion();
        Node end = endComponent.getConnector().orElseThrow().transparentRegion();

        if (edge.isOutputToInput()) {
            double scaledOutDiff = (start.layoutYProperty().get()-outCenterHeight)*scale;
            double scaledInDiff = (end.layoutYProperty().get()-inCenterHeight)*scale;

            edge.startComponent().layoutXProperty().bind(outContainer.layoutXProperty().add(outXOffset).add(start.layoutXProperty().get()));
            edge.endComponent().layoutXProperty().bind(inContainer.layoutXProperty().add(inXOffset).add(end.layoutXProperty().get()));
            edge.startComponent().layoutYProperty().bind(outContainer.layoutYProperty().add(outCenterHeight+scaledOutDiff));
            edge.endComponent().layoutYProperty().bind(inContainer.layoutYProperty().add(inCenterHeight+scaledInDiff));
        } else {
            double scaledInDiff = (start.layoutYProperty().get()-inCenterHeight)*scale;
            double scaledOutDiff = (end.layoutYProperty().get()-outCenterHeight)*scale;

            edge.startComponent().layoutXProperty().bind(inContainer.layoutXProperty().add(inXOffset).add(start.layoutXProperty().get()));
            edge.endComponent().layoutXProperty().bind(outContainer.layoutXProperty().add(outXOffset).add(end.layoutXProperty().get()));
            edge.startComponent().layoutYProperty().bind(inContainer.layoutYProperty().add(inCenterHeight+scaledInDiff));
            edge.endComponent().layoutYProperty().bind(outContainer.layoutYProperty().add(outCenterHeight+scaledOutDiff));
        }
    }

	private void scaleContainer(Container container) {
		
		// Scale the containers
		container.setScaleX(scale);
		container.setScaleY(scale);

        double width = container.getLayoutBounds().getWidth();
        double height = container.getLayoutBounds().getHeight();
        double xOffset = (width-width*zoomFactor)/1.5;
        double yOffset = (height-height*zoomFactor)/1.5;
		
		// I don't know if the 'offset' will scale since I don't know why it is actually needed.
		// I just plugged numbers in until the zooming centered on the centre of the diagram.
		// The offset happened to align with half the width of the block so perhaps subtracting half the block's width is the correct approach.
		double oldXGap = graphBuilder.getWidth()/2-container.getLayoutX();
		double oldYGap = graphBuilder.getHeight()/2-container.getLayoutY();
		double newXGap = oldXGap*zoomFactor;
		double newYGap = oldYGap*zoomFactor;
		// Shrink/grow the distance between blocks in proportion with the scale
		container.setLayoutX(container.getLayoutX()+oldXGap-newXGap-xOffset);
		container.setLayoutY(container.getLayoutY()+oldYGap-newYGap-yOffset);
	}
}
