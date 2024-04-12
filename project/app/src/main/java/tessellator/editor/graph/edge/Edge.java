package tessellator.editor.graph.edge;

import javafx.scene.Node;
import tessellator.editor.graph.block.Block;
import tessellator.editor.graph.edge.component.EdgeComponent;

/**
 * An object that represents an edge within the graph builder. Instead of inheriting
 * from the JavaFX Node class this object uses composition to group together all the
 * parts that semantically make up an edge. Once an Edge object is instantiated none
 * of its fields can be changed. Instantiate a new Edge object if you wish to change
 * any of the fields. 
 * 
 * The idea behind this implementation is that when an edge is created in the graph
 * builder that edge will be rooted between two blocks. If two new blocks need to be
 * linked then an existing edge won't be repurposed to link them, instead a new edge
 * will be created between them.
 */
public class Edge {
    
    // All of the components implement the Node, Selectable and EdgeComponent interfaces
    // The end components of the edge
    private final Node startComponent;
    private final Node endComponent;
    // The line component that is bound between the two end components
    private final Node lineComponent;

    private final Block inBlock;
    private final Block outBlock;

    // This boolean records how the line was created (the direction of the line):
    // - was it created by dragging from an input connector to an output connector
    // - was it created by dragging from an output connector to an input connector
    private final boolean isOutputToInput;

    /**
     * This constructor initialises the edge with it's components and the blocks said edge
     * is linking. It also updates the edge fields of the passed components to reference
     * this edge.
     * 
     * Each passed EdgeComponent object must also implement the Selectable and Node interfaces.
     * 
     * @param s The start component of the edge (the start component should be bound to a node within the start block).
     * @param e The end component of the edge (the end component should be bound to a node within the end block).
     * @param l The line component of the edge.
     * @param inBlock The block whose input connector the edge is connected to.
     * @param outBlock The block whose output connector the edge is connected to.
     * @param isOutputToInput Is the start component from an output block or an input block.
     */
    public Edge(EdgeComponent s, EdgeComponent e, EdgeComponent l, Block inBlock, Block outBlock, boolean isOutputToInput) {
        
        // Initialise the fields of the class
        // Components are stored as Nodes because the Node interface is used more than Selectable or EdgeComponent
        startComponent = (Node) s;
        endComponent = (Node) e;
        lineComponent = (Node) l;
        this.inBlock = inBlock;
        this.outBlock = outBlock;
        this.isOutputToInput = isOutputToInput;

        // Link the edge components to the edge they are now apart of
        s.setEdge(this);
        e.setEdge(this);
        l.setEdge(this);
    }

    public Node startComponent() {
        return startComponent;
    }

    public Node endComponent() {
        return endComponent;
    }

    public Node lineComponent() {
        return lineComponent;
    }

    public Block inputBlock() {
        return inBlock;
    }

    public Block outputBlock() {
        return outBlock;
    }

    public boolean isOutputToInput() {
        return isOutputToInput;
    }
}
