package tessellator.editor.graph.edge.component.end;

import java.util.Set;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import tessellator.editor.graph.Selectable;
import tessellator.editor.graph.block.Connector;
import tessellator.editor.graph.edge.Edge;
import tessellator.editor.graph.edge.component.EdgeComponent;

/**
 * An object that can be used for the end components of an edge in the graph builder.
 * This particular object represents a circle.
 */
public class CircleComponent extends Circle implements Selectable, EdgeComponent, ConnectorComponent {
    
    private boolean selected;
    private Edge edge;
    private Connector connector;

    @Override
	public boolean isSelected() {
		return selected;
	}
    
    /**
     * The selected flag becomes true.
     */
    @Override
	public void select(Set<Node> selectedNodes) {
		selected = true;
	}

    /**
     * The selected flag becomes false.
     */
    @Override
	public void deselect() {
		selected = false;
	}

    @Override
    public void setEdge(Edge e) {
        edge = e;
    }

    @Override
    public Optional<Edge> getEdge() {
        return Optional.ofNullable(edge);
    }

    @Override
    public void setConnector(Connector connector) {
        this.connector = connector;
    }

    @Override
    public Optional<Connector> getConnector() {
        return Optional.ofNullable(connector);
    }
}
