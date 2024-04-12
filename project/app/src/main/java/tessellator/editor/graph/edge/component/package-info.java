package tessellator.editor.graph.edge.component;

/**
 * The purpose of 'component' package is to group together all of the
 * graphical parts of an edge. By graphical I mean the JavaFX nodes which
 * the user interacts with (clicks and drags) in the graph builder.
 * 
 *                 /-------------------O
 *      O---------/    ^               ^
 *      ^              |               |
 *      |              |         end component
 *  end component      |
 *                line component
 *
 * Above is an example of how an edge looks within the graph builder. An edge
 * has 2 ends and a line connecting those ends. The ends and the line joining them
 * are the components that make up the edge.
 */