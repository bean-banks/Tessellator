package tessellator.editor.graph.eventhandling;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import tessellator.editor.graph.GraphBuilder;
import tessellator.editor.graph.block.Container;
import tessellator.editor.graph.block.eventhandling.OffsetCalculator;

/**
 * An object which provides methods for handling drag events within the graph builder.
 */
public class GraphBuilderDragHandler {

    private final GraphBuilder graphBuilder;

    private final Map<Node, Double> graphXOffsets = new HashMap<>();
	private final Map<Node, Double> graphYOffsets = new HashMap<>();
	private final Set<Node> nodesToMove = new HashSet<>();
    
    public GraphBuilderDragHandler(GraphBuilder graphBuilder) {
        this.graphBuilder = graphBuilder;
    }

    public void startGraphDrag(MouseEvent me) {
		nodesToMove.clear();
		graphBuilder.requestFocus();
		
		graphBuilder.getChildren().forEach(n -> {
			if (n instanceof Container) {
				nodesToMove.add(n);
				graphXOffsets.put(n, me.getX()+graphBuilder.getLayoutX()-n.getLayoutX()+OffsetCalculator.supplyOffsetX(n.getParent()));
				graphYOffsets.put(n, me.getY()+graphBuilder.getLayoutY()-n.getLayoutY()+OffsetCalculator.supplyOffsetY(n.getParent()));
			}
		});
	}

	public void dragGraph(MouseEvent me) {
		nodesToMove.forEach(n -> {
			n.setLayoutX(me.getSceneX() - graphXOffsets.get(n));
			n.setLayoutY(me.getSceneY() - graphYOffsets.get(n));
		});
	}

	public void endGraphDrag() {
		graphXOffsets.clear();
		graphYOffsets.clear();
	}

}
