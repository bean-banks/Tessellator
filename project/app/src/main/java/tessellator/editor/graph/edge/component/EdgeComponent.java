package tessellator.editor.graph.edge.component;

import java.util.Optional;

import tessellator.editor.graph.edge.Edge;

/**
 * An interface to provide components of an edge a means of accessing the Edge
 * object which they belong to. Only classes that extend the javafx Node class
 * should implement this interface.
 */
public interface EdgeComponent {
    
    /**
     * Updates the edge field of the component to the passed edge.
     * 
     * @param edge The edge that the object is a component of.
     */
    public void setEdge(Edge edge);
    
    /**
     * Retrieves the edge that owns the component, wrapped in an optional.
     * There is a chance this method could return null, for instance null
     * is returned if no edge is set before this method is called.
     * 
     * @return An optional containing the edge the component belongs to.
     */
    public Optional<Edge> getEdge();
}
