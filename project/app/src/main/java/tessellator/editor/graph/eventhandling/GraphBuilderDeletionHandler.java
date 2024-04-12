package tessellator.editor.graph.eventhandling;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.Node;
import tessellator.editor.graph.GraphBuilder;
import tessellator.editor.graph.block.Container;
import tessellator.editor.graph.edge.Edge;
import tessellator.editor.graph.edge.component.EdgeComponent;
import tessellator.editor.graph.edge.component.end.ConnectorComponent;
import tessellator.editor.preview.TileCanvasCaretaker;

/**
 * An object which provides methods for handling the delete event within the graph builder.
 */
public class GraphBuilderDeletionHandler {

    private final GraphBuilder graphBuilder;
    private final Set<Node> selected;

	public GraphBuilderDeletionHandler(GraphBuilder graphBuilder) {
        this.graphBuilder = graphBuilder;
        selected = graphBuilder.getSelected();
	}

    public void deleteSelected() {
		//graphBuilder.requestFocus();
		Set<Node> removalSet = new HashSet<>();
		for (Node n: selected) {
			if (n instanceof EdgeComponent) {
				// If the selected component is any part of the line then all the other parts
				// of that line must be removed.
				// These values could potentially be null, if a value is null, don't add it to the removal set
			    EdgeComponent component = (EdgeComponent) n;
                Edge edge = component.getEdge().orElseThrow();
				edge.inputBlock().unsatisfied();

				removalSet.add(edge.lineComponent());
				removalSet.add(edge.startComponent());
				removalSet.add(edge.endComponent());
                // The end components are also removed from the edge ends of the connectors they were connected to
                ConnectorComponent sc = (ConnectorComponent) edge.startComponent();
                ConnectorComponent ec = (ConnectorComponent) edge.endComponent();
               
                sc.getConnector().orElseThrow().removeEdgeEnd((EdgeComponent) sc);
                ec.getConnector().orElseThrow().removeEdgeEnd((EdgeComponent) ec);
			}
			else if (n instanceof Container) {
				// If a block is deleted, delete all the edges that are connected to it as well.
				Container container = (Container) n;
				// Free tbe id of a deleted block
				graphBuilder.freeBlockId(container.getBlock().getId());
				List<EdgeComponent> reducedList = new ArrayList<>();
				for (Set<EdgeComponent> set : container.getBlock().getInputConnectors().stream().map(ic -> ic.edgeEnds()).toList()) {
					reducedList.addAll(set);
				}
				for (Set<EdgeComponent> set : container.getBlock().getOutputConnectors().stream().map(ic -> ic.edgeEnds()).toList()) {
					reducedList.addAll(set);
				}
				reducedList.forEach(e -> {
					Edge edge = e.getEdge().orElseThrow();
					edge.inputBlock().unsatisfied();
					removalSet.add(edge.lineComponent());
					removalSet.add(edge.startComponent());
					removalSet.add(edge.endComponent());
				});
				if (container.getBlock().isRoot()) graphBuilder.setHasRoot(false);
				container.getBlock().unsatisfied();
				removalSet.add(n);
			}
		}
		removalSet.forEach(n -> graphBuilder.getChildren().remove(n));
		TileCanvasCaretaker.applyDrawingsToCanvas();
		selected.clear();
	}
}
