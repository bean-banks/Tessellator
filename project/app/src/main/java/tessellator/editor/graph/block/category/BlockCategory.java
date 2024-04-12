package tessellator.editor.graph.block.category;

import java.util.List;
import java.util.Optional;

import javafx.geometry.Point2D;
import tessellator.editor.graph.block.Block;
import tessellator.editor.graph.block.Connector;
import tessellator.editor.graph.block.theme.BlockTheme;
import tessellator.editor.graph.edge.Edge;
import tessellator.editor.graph.edge.component.EdgeComponent;
import tessellator.editor.graph.edge.component.end.ConnectorComponent;
import tessellator.editor.preview.Drawing;
import tessellator.editor.preview.TileCanvasCaretaker;

/**
 * An object which offers an interface for creating and drawing blocks of its category. BlockCategory
 * also provides objects which extend it a selection of helper methods to make implementing
 * its draw method easier.
 * 
 * By having the block creation and block drawing capabilities in the same class, its easy of 
 * writers of subclasses to know how to interpret the information about their block's inputs while 
 * implementing their draw method becuase the definition of the block is defined by the builder method
 * chain within their createBlock() implementation.
 */
public abstract class BlockCategory {

	protected final BlockTheme theme;
    private final String blockCategory;
	private boolean includeLabels;
	
	/**
	 * Construct a block category.
	 * 
	 * @param theme The theme which dictates the look of blocks that this category creates.
	 * @param category The string that identifies the block which this category creates.
	 */
	public BlockCategory(BlockTheme theme, String category) {
		this.theme = theme;
        blockCategory = category;
		includeLabels = false;
	}
	
	/**
	 * Creating a block returns an optional because if certain constraints faile then the 
	 * requested block should not be created. For instance if there is already
	 * a root block in the graph builder then no other root block can be created until
	 * the current root block is deleted/removed from the graph builder.
	 * 
	 * @return An optional containing the block that has been built.
	 */
	public abstract Optional<Block> createBlock();
	
	/**
	 * Draw the shape the block represents onto the editor's canvas.
	 * 
	 * This method transforms input connector values/coordinates of its block using the tranformation
	 * values of its block. These transformed coordinates are packaged alongside other meta information
	 * within a drawing object. Any object that relies on these transformed coordinates or its drawing
	 * are updated accordingly.
	 * 
	 * To ensure all relevant objects are updated, subclasses implementing this method must always
	 * invoke the applyChanges() helper method within their implementation.
	 * 
	 * @param block The block to be drawn.
	 */
	public abstract void draw(Block block);

	/**
	 * Draw the shape the block represents and label its points with the same labels as its connectors.
	 * 
	 * @param block The block that is being drawn with labels
	 */
	public void drawWithLabels(Block block) {
		includeLabels = true;
		draw(block);
	}

	/**
	 * Undraw the shape the block represents from the editor's canvas.
	 * 
	 * @param block The block that is having its drawing removed.
	 */
	public void undraw(Block block) {
		block.setIsLabeled(false);
		TileCanvasCaretaker.getDrawings().remove(block.getDrawing());
	}

	@Override
	public String toString() {
		return blockCategory;
	}

	protected boolean graphAlreadyHasRoot() {
		return theme.getGraphBuilder().hasRoot();
	}

	/**
	 * A helper method for subclasses to use when implementing the draw method. It provides a quick
	 * way split a list of x,y coordinates into a list of x coordinates and a list of y coordinates.
	 * 
	 * @param vertices The list of points that will be split.
	 * @param xcoords An empty list to be populated with y coordinates.
	 * @param ycoords An empty list to be populated with x coordinates.
	 */
	protected void populateCoordinates(List<Point2D> vertices, List<Double> xcoords, List<Double> ycoords) {
		for (Point2D vertex: vertices) {
			// xcoords.add((int) Math.round(vertex.getX()));
			// ycoords.add((int) Math.round(vertex.getY()));
			xcoords.add(vertex.getX());
			ycoords.add(vertex.getY());
		}
	}

	/**
	 * Subclasses must invoke this method within their draw() implementations. This method notfies
	 * and updates any objects that are affected by the changes produced bu the draw method.
	 * These updates include:
	 * - replacing the old drawing of the block in the TileCanvasCaretaker's drawings collection
	 * with the new drawing (just adds it if there wasn't previously a drawing).
	 * - updating the block's drawing to be the newly made drawing.
	 * - updating the block's output connector values to the transformed coordinates.
	 * - propogating the new output connector values to the input connector values
	 * of connected child blocks.
	 * - ensuring that the next draw is not does not produce a labelled drawing unless specified
	 * otherwise.
	 * 
	 * @param block The block which the drawing is of.
	 * @param drawing The drawing that is produced within the draw method.
	 * @param vertices The transform input coordinates of the block.
	 */
	protected void applyChanges(Block block, Drawing drawing, List<Point2D> vertices) {
		
		// Replace the old drawing of this block with this new drawing, in the drawings collection
		TileCanvasCaretaker.getDrawings().remove(block.getDrawing());
		TileCanvasCaretaker.getDrawings().add(drawing);
		
		// Update the drawing in the block to this one
		block.setDrawing(drawing);

		// Update the values of the output connectors
		for (int i = 0; i <block.getOutputConnectors().size(); i++) {
			Connector outputConnector = block.getOutputConnectors().get(i);
			outputConnector.setValue(vertices.get(i));
		}
		// Propogate these values to the input connectors of the block's children
		for (int i = 0; i <block.getOutputConnectors().size(); i++) {
			Connector outputConnector = block.getOutputConnectors().get(i);
			for (EdgeComponent end : outputConnector.edgeEnds()) {
				Edge edge = end.getEdge().orElseThrow();
				ConnectorComponent concomp = null;
				if (edge.isOutputToInput()) {
					concomp = (ConnectorComponent) edge.endComponent();
				} else {
					concomp = (ConnectorComponent) edge.startComponent();
				}
				Connector inputConnector = concomp.getConnector().orElseThrow();
				inputConnector.setValue(outputConnector.value());
				int indexOfNextOutputConnector = (i+1)%block.getOutputConnectors().size();
				inputConnector.setSecondaryValue(block.getOutputConnectors().get(indexOfNextOutputConnector).value());
			}
		}
		
		block.setIsLabeled((includeLabels) ? true : false);


		// Don't include the labels for a standard draw
		includeLabels = false;
	}

	/**
	 * A setter for the boolean that determines whether a labelled drawing is produced
	 * or a standard drawing without labels
	 */
	protected void setIncludeLabels(boolean includeLabels) {
		this.includeLabels = includeLabels;
	}

	/** 
	 * A boolean getter to see whether to include labels in the drawing or not.
	 */
	protected boolean includeLabels() {
		return includeLabels;
	}

    /**
	 * A helper method that may be used be subclasses within their implementation of the draw method.
	 * It rotates a point about a center point by an angle.
	 * 
	 * @param point The point that will be rotated.
	 * @param center The center point which the rotation will rotate from.
	 * @param angle The angle in radians which the point will be rotated.
	 * @return The rotated point.
	 */
    protected Point2D rotatePoint(Point2D point, Point2D center, double angle) {
        double rotatedX = center.getX() + (point.getX() - center.getX()) * Math.cos(angle) - (point.getY() - center.getY()) * Math.sin(angle);
        double rotatedY = center.getY() + (point.getX() - center.getX()) * Math.sin(angle) + (point.getY() - center.getY()) * Math.cos(angle);
        return new Point2D(rotatedX, rotatedY);
    }
}
