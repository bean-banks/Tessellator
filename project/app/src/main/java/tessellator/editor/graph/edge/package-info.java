package tessellator.editor.graph.edge;

/**
 * The 'edge' package contains the classes that make up an edge in the graph builder
 * and the classes which support said classes.
 * 
 * The Edge object embodies the semantics of an edge by using composition to group
 * together all the components of an edge. To create an edge within the graph builder
 * you must first properly set up the components of the edge and then instantiate the
 * edge with said components.
 * 
 * Example:
 * 1. Set up start component => instantiate CircleComponent and bind it to the block
 * that represents the input block of the edge.
 * 
 * 2. Set up end component => instantiate CircleComponent.
 * 
 * 3. Set up line component => create a line with the LineFactory createLine() method,
 * passing the start and end components as parameters.
 * 
 * 4. When the end block has been decided, bind the end component to the output block.
 * 
 * 5. Create an Edge object with the start, end and line components, and the input
 * and output blocks.
 * 
 * Side note: All edge components must also be of type Selectable and of type Node.
 */