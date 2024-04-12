package tessellator.editor.graph.block.eventhandling;

import javafx.scene.Node;

/**
 * This class provides static methods for calculating offsets within a javafx scene
 * by recursively traversing the scene's node graph.
 */
public class OffsetCalculator {
	/**
	 * Get the layout x offset of the node relative to its parents.
	 */
	public static double supplyOffsetX(Node parent) {
		
        double offset = 0;
		if (parent==null) {
			return 0;
		}
		else {
			offset = parent.getLayoutX() + supplyOffsetX(parent.getParent());
			return offset;
		}
	}
	
	/**
	 * Get the layout y offset of the node relative to its parents.
	 */
	public static double supplyOffsetY(Node parent) {
		
        double offset = 0;
		if (parent==null) {
			return 0;
		}
		else {
			offset = parent.getLayoutY() + supplyOffsetY(parent.getParent());
			return offset;
		}
	}
}
