package tessellator.editor.graph.block.theme;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import tessellator.editor.graph.GraphBuilder;
import tessellator.editor.graph.block.Attribute;
import tessellator.editor.graph.block.Connector;
import tessellator.editor.graph.block.Container;

/**
 * An object that provides methods for constructing the visible components of a block.
 * When invoking methods ensure you perform the connector and attribute constructions
 * before the other constructions. Also make sure to invoke the clean() method after
 * all constructions for a block have been made, this resets the theme so it can be used
 * to construct another block.
 * 
 * Example:
 * BlockTheme theme = new DarkBlockTheme(graphBuilder);
 * Connector in1 = theme.constructInputConnector(<<...>>);
 * Connector in2 = theme.constructInputConnector(<<...>>);
 * Attribute atr = theme.constructTextFieldAttribute(<<...>>);
 * Connector out = theme.constructOutputConnector(<<...>>);
 * Node header = theme.constructHeader(<<...>>);
 * Node title = theme.constructTitle(<<...>>);
 * Node body = theme.constructBody(<<...>>);
 * theme.clean();
 */
public abstract class BlockTheme
{
	// If the UNIT size is changed then the size of the block and spacing of its elements are also changed.
	// All subclasses should use these four fields
	protected static final int UNIT = 30;
	protected static final int HEADER_HEIGHT = 1;
	protected final GraphBuilder graphBuilder;
	
	/**
	 * This constructor supplies the theme a graph builder which is required for construction
	 * of certain components of a block.
	 * 
	 * @param graphBuilder The graph builder where blocks are placed.
	 */
	public BlockTheme(GraphBuilder graphBuilder)
	{
		this.graphBuilder = graphBuilder;
	}
	
	/**
	 * Construct the coloured region of the block that contains the block's title.
	 * 
	 * @param headerColor The colour which will be used in the header.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return The header as a node.
	 */
	public abstract Node constructHeader(Color headerColor, Container container);
	
	/**
	 * Construct the region of the block that encompasses all of the block's interactable components.
	 * 
	 * @param height The height of the block in terms of the UNIT constant.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return The body as a node.
	 */
	public abstract Node constructBody(int height, Container container);
	
	/**
	 * Construct the title of the block.
	 * 
	 * @param title The text that describes the geometry a block represents.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return The title as a Node.
	 */
	public abstract Node constructTitle(String title, Container container);

	/**
	 * Construct an input connector.
	 * 
	 * @param text The label that describes the point the connector represents.
	 * @param height The height (in terms of the UNIT constant) that the connector is placed down the block.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return All the parts of a connector packaged into a Connector object.
	 */
	public abstract Connector constructInputConnector(String label, int height, Container container);
	
	/**
	 * Construct an output connector.
	 * 
	 * @param text The label that describes the point the connector represents.
	 * @param height The height (in terms of the UNIT constant) that the connector is placed down the block.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return All the parts of a connector packaged into a Connector object.
	 */
	public abstract Connector constructOutputConnector(String label, int height, Container container);
	
	/**
	 * Construct a text field attribute.
	 * 
	 * @param attribute The label that states what the attribute is.
	 * @param labelHelpText The text which explains in more detail what the attribute does.
	 * @param inputHelpText the text which explains what type of data the input region can take.
	 * @param height The height (in terms of the UNIT constant) that the attribute is placed down the block.
	 * @param columnCount The width if the text box.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return All the parts of an attribute packaged into an Attribute object.
	 */
	public abstract Attribute constructTextFieldAttribute(String attribute, String labelHelpText, String inputHelpText, int height, int columnCount, Container container);
	
	/**
	 * Construct a combobox attribute.
	 * 
	 * @param attribute The label that states what the attribute is.
	 * @param labelHelpText The text which explains in more detail what the attribute does.
	 * @param height The height (in terms of the UNIT constant) that the attribute is placed down the block.
	 * @param items The options that will be selectable within the combobox.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return All the parts of an attribute packaged into an Attribute object.
	 */
	public abstract Attribute constructComboBoxAttribute(String attribute, String labelHelpText, int height, ObservableList<String> items, Container container);

	/**
	 * Clean the theme in preparation for constructing the comopnents of a new block.
	 * Cleaning the theme restores it to its original state prior to constructing a block.
	 */
	public abstract void clean();
	
	public GraphBuilder getGraphBuilder()
	{
		return graphBuilder;
	}
}
