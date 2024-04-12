package tessellator.editor.graph.block.eventhandling;

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import javafx.scene.Node;
import tessellator.editor.graph.block.Container;
import tessellator.editor.preview.Drawing;
import tessellator.editor.preview.TileCanvasCaretaker;

/**
 * This class provides methods that make blocks draggable within the graph builder.
 */
public class BlockDragStrategies {

	private static double xOffset;
	private static double yOffset;
	
	private static final Map<Node, Double> xOffsets = new HashMap<>();
	private static final Map<Node, Double> yOffsets = new HashMap<>();
	
	/**
	 * This method enables a block to be dragged within the graph builder and it also
	 * enables a labeled version of the block's drawing to be viewed in the editor's canvas
	 * by shift pressing on a block.
	 * 
	 * @param component The component within the container that the events occur on.
	 * @param container The container of the block that is having draggability added to it.
	 */
	public static void addDrag(Node component, Container container) {
		
        component.setOnMousePressed(me -> {
			if (me.isShiftDown()) {
				// Draw the block that has been pressed with labels, and remove the labels
				// from any drawing of any other block, there will at most be one other labeled block.
				// Do the above if the block wasn't labelled before the shift click. However
				// If it was already labelled then make it unlabelled (like a deselect).
				if (container.getBlock().isSatisfied()) {
					if (!container.getBlock().isLabeled()) {
						Drawing labeledDrawing = null;
						for (Drawing drawing : TileCanvasCaretaker.getDrawings()) {
							if (drawing.pointLabels().size()>0) labeledDrawing = drawing;
						}
						if (labeledDrawing != null) {
							labeledDrawing.block().getBlockCategory().draw(labeledDrawing.block());
						}
						container.getBlock().getBlockCategory().drawWithLabels(container.getBlock());
					} else {
						container.getBlock().getBlockCategory().draw(container.getBlock());
					}
					TileCanvasCaretaker.applyDrawingsToCanvas();
				}
				me.consume();
			} else {
				component.setMouseTransparent(true);
				me.setDragDetect(true);
				container.requestFocus();
	
				double width = container.getBoundsInLocal().getWidth();
				double xScaleGap = (width-width*container.getScaleX())/2;
	
				double height = container.getBoundsInLocal().getHeight();
				double yScaleGap = (height-height*container.getScaleY())/2;
	
				xOffset = (me.getX()+component.getLayoutX())*container.getScaleX()+xScaleGap+OffsetCalculator.supplyOffsetX(container.getParent());
				yOffset = (me.getY()+component.getLayoutY())*container.getScaleY()+yScaleGap+OffsetCalculator.supplyOffsetY(container.getParent());
	
				// When a component is pressed, its block must be moved to the front so all other blocks will be behind it, as it is dragged.
				// This creates the illusion that the pressed block is more important than all others.
				container.toFront();
				me.consume();
			}
		});
		
		component.setOnMouseReleased(me -> {
			component.setMouseTransparent(false);
			me.consume();
		});
		
		component.setOnMouseDragged(me -> {
			// Subtract the offsets from the position of the cursor relative to the scene to avoid the cursor locking to the wrong position.
			container.setLayoutX(me.getSceneX() - xOffset);
			container.setLayoutY(me.getSceneY() - yOffset);
			me.setDragDetect(false);
			me.consume();
		});
	}
	
	/**
	 * This method enables a group of selected blocks to be dragged together. If one of the selected
	 * blocks is dragged then the other blocks are dragged with it.
	 * 
	 * @param component The component within the container that the events occur on.
	 * @param container The container of the block that is having its group dragability added to it.
	 * @param selectedNodes The other nodes which will be dragged alongside the dragged block.
	 */
	public static void addGroupDrag(Node component, Container container, Set<Node> selectedNodes) {
		
        component.setOnMousePressed(me -> {
			//actionEvent(container, true);
			component.setMouseTransparent(true);
			me.setDragDetect(true);
			container.requestFocus();

			double width = container.getBoundsInLocal().getWidth();
			double xScaleGap = (width-width*container.getScaleX())/2;

			double height = container.getBoundsInLocal().getHeight();
			double yScaleGap = (height-height*container.getScaleY())/2;

			// Prepare the offsets for the selected containers that will be dragged
			selectedNodes.forEach(n -> {
				if (n instanceof Container) {
					xOffsets.put(n, (me.getX()+component.getLayoutX())*container.getScaleX()+xScaleGap+container.getLayoutX()-n.getLayoutX()+OffsetCalculator.supplyOffsetX(n.getParent()));
					yOffsets.put(n, (me.getY()+component.getLayoutY())*container.getScaleY()+yScaleGap+container.getLayoutY()-n.getLayoutY()+OffsetCalculator.supplyOffsetY(n.getParent()));
				}
			});
			me.consume();
		});
		
		component.setOnMouseReleased(me -> {
			component.setMouseTransparent(false);
			xOffsets.clear();
			yOffsets.clear();
			me.consume();
		});
		
		component.setOnMouseDragged(me -> {
			
			// Drag the selected containers
			selectedNodes.forEach(n -> {
				if (n instanceof Container) {
					n.setLayoutX(me.getSceneX() - xOffsets.get(n));
					n.setLayoutY(me.getSceneY() - yOffsets.get(n));
				}
			});
			me.setDragDetect(false);
			me.consume();
		});
	}
}
