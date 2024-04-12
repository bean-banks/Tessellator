package tessellator.editor.graph.block.eventhandling;

import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import tessellator.editor.graph.GraphBuilder;
import tessellator.editor.graph.Selectable;
import tessellator.editor.graph.block.Block;
import tessellator.editor.graph.block.Connector;
import tessellator.editor.graph.block.Container;
import tessellator.editor.graph.edge.CoordinatePair;
import tessellator.editor.graph.edge.Edge;
import tessellator.editor.graph.edge.component.EdgeComponent;
import tessellator.editor.graph.edge.component.end.CircleComponent;
import tessellator.editor.graph.edge.component.end.ConnectorComponent;
import tessellator.editor.graph.edge.line.LineFactory;
import tessellator.editor.graph.edge.line.shape.LineShape;
import tessellator.editor.graph.edge.line.style.LineStyle;
import tessellator.editor.preview.TileCanvasCaretaker;

/**
 * ConnectorDragStrategies currently supplies one drag strategy that can be used within the
 * dark block theme. This drag strategy enables edges that join blocks to be created and
 * a relationship between those blocks can be established.
 *
 */
public class ConnectorDragStrategies {

	private static Circle originPoint;
	private static Node endPoint;
	private static Shape line;
	
	private static Connector originConnector;
	private static Connector endConnector;
	private static Block originBlock;
	private static Block endBlock;
	private static boolean connectionFound;
	// If the setLineFactory method isn't invoked then the object defaults to having a 
	// line factory that produces curved spectral lines.
    private static LineFactory factory = new LineFactory(LineShape.EASEINEASEOUT, LineStyle.SPECTRAL);
	
	private static double xOffset;
	private static double yOffset;
	
	/**
	 * Sets the look of the edge that is created during the drag process.
	 */
	public static void setLineFactory(LineShape shape, LineStyle style) {
		factory = new LineFactory(shape, style);
	}

	public static LineFactory getLineFactory() {
		return factory;
	}
	
	/**
	 * Adds the edge generation via draggable connectors.
	 * 
	 * @param connector A connector of a block.
	 * @param container A container of the same block as the connector.
	 * @param graphBuilder The graph builder where the edges are created.
	 */
	public static void addDrag(Connector connector, Container container, GraphBuilder graphBuilder) {

		/*
		 * To fully implement the drag of connectors we have to consider when a connector is
		 * the source => being pressed on. As well as wehn the connector is the target =>
		 * being released on after a drag has occurred.
		 */
		addDragWhenSource(connector, container, graphBuilder);
        addDragWhenTarget(connector, container, graphBuilder);

    }

    private static void addDragWhenSource(Connector connector, Container container, GraphBuilder graphBuilder) {

        Circle source = (Circle) connector.transparentRegion();
		Circle template = (Circle) connector.visibleRegion();
		boolean isOutput = connector.isOutputConnector();

		source.setOnMousePressed(me -> {
			// The source is the node that is dragged and the node that is the end point of the line
			endPoint = source;
			originConnector = connector;
			originBlock = container.getBlock();
			connectionFound = false;
			
			// The gap is difference between the scaled and unscaled container width
			// The y inset is the radius of the source circle
			double width = container.getBlock().getBody().minWidth(0);
			double gap = (width-width*container.getScaleX())/2;
			
			// Replace the source circle with a copy.
			// This has to be done because the source circle is the node that's moved alongside the drag.
			Circle sourceReplacement = new CircleComponent();
			sourceReplacement.setRadius(source.getRadius());
			sourceReplacement.setFill(source.getFill());
			sourceReplacement.setEffect(source.getEffect());
			sourceReplacement.setLayoutX(source.getLayoutX());
			sourceReplacement.setLayoutY(source.getLayoutY());
		
			// Add the copy to the container
			container.getChildren().add(sourceReplacement);
			// Swap the transparent region references in the connector with the copy
			connector.setTransparentRegion(sourceReplacement);
			
			// Set up drag for the copy
			ConnectorDragStrategies.addDrag(connector, container, graphBuilder);
			
			// An anchor for the line is created with the same position as the template.
			originPoint = new CircleComponent();
			originPoint.setRadius(0);
			//The last additions to both bindings account for the offsets incurred from scaling.
			if (isOutput) {
				originPoint.layoutXProperty().bind(template.layoutXProperty().add(container.layoutXProperty()).add(-gap));
			}
			else {
				originPoint.layoutXProperty().bind(template.layoutXProperty().add(container.layoutXProperty()).add(gap));
			}
			double centerHeight = container.getBoundsInLocal().getHeight()/2;
			double scaledDiff = (template.layoutYProperty().get()-centerHeight)*container.getScaleY();
			originPoint.layoutYProperty().bind(container.layoutYProperty().add(centerHeight+scaledDiff));
			graphBuilder.getChildren().add(originPoint);
			
			// The source is removed from its container, transformed into a copy of the template
			// then added to the graph builder so it can be manipulated.
			container.getChildren().remove(source);
			source.setRadius(template.getRadius());
			source.setFill(template.getFill());
			source.setEffect(template.getEffect());
			source.setLayoutX(source.getLayoutX()+container.getLayoutX());
			source.setLayoutY(source.getLayoutY()+container.getLayoutY());
			source.setScaleX(container.getScaleX());
			source.setScaleY(container.getScaleX());
			graphBuilder.getChildren().add(source);
			
			CoordinatePair cp = new CoordinatePair(originPoint.layoutXProperty(), originPoint.layoutYProperty(), endPoint.layoutXProperty(), endPoint.layoutYProperty());
			line = (Shape) factory.createLine(cp);
			// Set the stroke width to match the value specified by the diagram
			line.setStrokeWidth(graphBuilder.getStrokeWidthOfLines());
			graphBuilder.getChildren().add(line);
			line.toBack();
			
			source.setMouseTransparent(true);
			me.setDragDetect(true);
	
			xOffset = me.getX()*container.getScaleX()+OffsetCalculator.supplyOffsetX(container.getParent());
			yOffset = me.getY()*container.getScaleX()+OffsetCalculator.supplyOffsetY(container.getParent());
			
			// Need to apply the offsets at the press stage otherwise the source appears at the wrong position when scaled.
			source.setLayoutX(me.getSceneX() - xOffset);
			source.setLayoutY(me.getSceneY() - yOffset);
			
			me.consume();
		});

		source.setOnMouseDragged(me -> {
			source.setLayoutX(me.getSceneX() - xOffset);
			source.setLayoutY(me.getSceneY() - yOffset);
			me.setDragDetect(false);
			me.consume();
		});

		source.setOnMouseReleased(me -> {
			//graphBuilder.setEventInAction(false);
			source.setMouseTransparent(false);
			// The line is destroyed if a connection isn't found.
			if (!connectionFound) {
				graphBuilder.getChildren().removeAll(originPoint, endPoint, line);
			} else {
				// Otherwise we officially create the edge with the line components
				Block inputBlock;
				Block outputBlock;
				if (originConnector.isOutputConnector()) {
					outputBlock = originBlock;
					inputBlock = endBlock;
					// update the input connector value to be the same as the output connector value
					endConnector.setValue(originConnector.value());
					// The secondary value is the coordinate that is next to the value coordinate
					int outputConnectorIndex = originBlock.getOutputConnectors().indexOf(originConnector);
					int numOfOutputConnectors = originBlock.getOutputConnectors().size();
					endConnector.setSecondaryValue(originBlock.getOutputConnectors().get((outputConnectorIndex+1)%numOfOutputConnectors).value());
				} else {
					inputBlock = originBlock;
					outputBlock = endBlock;
					// update the input connector value to be the same as the output connector value
					originConnector.setValue(endConnector.value());
					int outputConnectorIndex = endBlock.getOutputConnectors().indexOf(endConnector);
					int numOfOutputConnectors = endBlock.getOutputConnectors().size();
					originConnector.setSecondaryValue(endBlock.getOutputConnectors().get((outputConnectorIndex+1)%numOfOutputConnectors).value());
				}

				ConnectorComponent op = (ConnectorComponent) originPoint;
				ConnectorComponent ep = (ConnectorComponent) endPoint;
				op.setConnector(originConnector);
				ep.setConnector(endConnector);
				Edge edge = new Edge((EdgeComponent) originPoint, (EdgeComponent) endPoint, (EdgeComponent) line, inputBlock, outputBlock, originConnector.isOutputConnector());
				originConnector.addEdgeEnd((EdgeComponent) originPoint);
				endConnector.addEdgeEnd((EdgeComponent) endPoint);

				

				if (inputBlockCompletelyLinked(edge)) {
					edge.inputBlock().satisfied();
					TileCanvasCaretaker.applyDrawingsToCanvas();
				}
				// If either of the blocks are selected then make the line joining them selected
				if (edge.inputBlock().getContainer().isSelected() || edge.outputBlock().getContainer().isSelected()) {
					Selectable sLine = (Selectable) edge.lineComponent();
					sLine.select(null);
				}
				
			}
			me.consume();
		});
    }

    private static void addDragWhenTarget(Connector connector, Container container, GraphBuilder graphBuilder) {
		
		Circle target = (Circle) connector.transparentRegion();
		Circle template = (Circle) connector.visibleRegion();
		

		target.setOnDragDetected(me -> {
			target.startFullDrag();
			me.consume();
		});
		
		target.setOnMouseDragEntered(me -> {
			template.setOpacity(0.2);
		});
		
		target.setOnMouseDragOver(me -> {
			
		});

		target.setOnMouseDragReleased(me -> {
			endConnector = connector;
			endBlock = container.getBlock();
		
			Circle source = (Circle) me.getGestureSource();
			double width = container.getBlock().getBody().minWidth(0);
			double gap = (width-width*container.getScaleX())/2;
			
			if (edgeChecksSucceed()) {
				connectionFound = true;
				if (endConnector.isOutputConnector()) {
					source.layoutXProperty().bind(container.layoutXProperty().add(template.layoutXProperty()).add(-gap));
				} else {
					source.layoutXProperty().bind(container.layoutXProperty().add(template.layoutXProperty()).add(gap));
				}
				double centerHeight = container.getBoundsInLocal().getHeight()/2;
				double scaledDiff = (template.layoutYProperty().get()-centerHeight)*container.getScaleY();
				source.layoutYProperty().bind(container.layoutYProperty().add(centerHeight+scaledDiff));
				source.toBack();
				source.setRadius(0);
			} else {
				connectionFound = false;
			}
		});
		
		target.setOnMouseDragExited(me -> {
			template.setOpacity(1);
		});
    }

	/**
	 * Applies a series of checks so that the edge satisfies the constraints of the graph,
	 * returning true only if all the checks pass.
	 */
	private static boolean edgeChecksSucceed() {
		return sameTypeOfConnectorCheck() && sameBlockCheck() && oneEdgePerInputConnectorCheck() && satisfiedParentCheck();
	}

	/**
	 * Checks the ends of the edge aren't both inputs or both outputs
	 */
	private static boolean sameTypeOfConnectorCheck() {
		return originConnector.isOutputConnector() != endConnector.isOutputConnector();
	}

	/**
	 * Checks the edge doesn't start and end on the same block
	 */
	private static boolean sameBlockCheck() {
		return originBlock != endBlock;
	}

	/**
	 * Checks to make sure the input connector which the edge is joining from/to doesn't
	 * already have any edges attached to it.
	 */
	private static boolean oneEdgePerInputConnectorCheck() {
		if (originConnector.isOutputConnector()) {
			return (endConnector.edgeEnds().size()==0) ? true : false;
		} else {
			return (originConnector.edgeEnds().size()==0) ? true : false;
		}
	}

	/**
	 * If the block the line is being drawn from is the output block then check whether it is satisfied.
	 * If the block the line is being drawn from is the input block then check whether the other block.
	 * (the output block) is satisfied
	 */
	private static boolean satisfiedParentCheck() {
		if (originConnector.isOutputConnector()) {
			return originBlock.isSatisfied();
		} else {
			return endBlock.isSatisfied();
		}
	}

	/**
	 * An input block is completely linked if all of its input connectors have an edge attached to them.
	 */
	private static boolean inputBlockCompletelyLinked(Edge edge) {
		return edge.inputBlock().getInputConnectors().stream().map(c -> c.edgeEnds().iterator().hasNext()).reduce(true, (acc, value) -> acc && value);
	}
}
