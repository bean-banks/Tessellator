package tessellator.editor.graph.block;


import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import tessellator.editor.graph.block.category.BlockCategory;
import tessellator.editor.graph.block.theme.BlockTheme;
import tessellator.editor.graph.edge.component.EdgeComponent;
import tessellator.editor.preview.Drawing;
import tessellator.editor.preview.TileCanvasCaretaker;

/**
 * An object which represents a node within the graph of the graph builder. It groups together
 * all the visual and semantic components of a node. To create a block you must first instantiate
 * a the BlockBuilder innerclass and chain the required methods to build the block.
 * 
 * For example:
 * Block block = new Block.BlockBuilder(theme, this)
 * 		.outputConnector("V1")
 *		.outputConnector("V2")
 *		.outputConnector("V3")
 *		.textFieldAttribute("Rotation", 3)
 *		.comboBoxAttribute("Border", FXCollections.observableArrayList("Visible", "Invisible"))
 *		.build(toString(), javafx.scene.paint.Color.DARKSEAGREEN);
 */
public class Block {

    // These are the components that make up a block
    private final Node header;
    private final Node body;
    private final Node title;
    private final List<Connector> inputConnectors;
    private final List<Connector> outputConnectors;
    private final List<Attribute> attributes;
    private final Container container;

    private final BlockCategory blockCat;
    private final BlockTheme theme;
	private final boolean isRoot;
	private boolean satisfied;
	private Drawing drawing;
	private int id;
	private boolean isLabeled;

	private Block(BlockBuilder builder) {
		this.header = builder.header;
		this.body = builder.body;
		this.title = builder.title;
		this.inputConnectors = builder.inputConnectors;
		this.outputConnectors = builder.outputConnectors;
		this.attributes = builder.attributes;
		this.container = builder.container;
		this.blockCat = builder.blockCat;
		this.theme = builder.theme;
		this.isRoot = builder.isRoot;
		// If a block is a root blook then it will always be fully satisfied, otherwise
		// a block always starts of as unsatisfied.
		satisfied = isRoot;
		attributes.forEach(a -> a.setBlock(this));
		id = theme.getGraphBuilder().getAvailableBlockId();
		isLabeled = false;
	}

    public static class BlockBuilder {

		private Node header;
		private Node body;
		private Node title;
		private List<Connector> inputConnectors;
		private List<Connector> outputConnectors;
		private List<Attribute> attributes;
		private Container container;
		private BlockTheme theme;
		private BlockCategory blockCat;
		private boolean isRoot;

		public BlockBuilder(BlockTheme theme, BlockCategory blockCat) {
			container  = new Container();
			this.theme = theme;
			this.blockCat = blockCat;
			
			inputConnectors = new ArrayList<>();
			outputConnectors = new ArrayList<>();
            attributes = new ArrayList<>();
		}

		public BlockBuilder inputConnector(String label) {
            inputConnectors.add(theme.constructInputConnector(label, inputConnectors.size()+2, container));
			return this;
		}

		public BlockBuilder outputConnector(String label) {
            outputConnectors.add(theme.constructOutputConnector(label, outputConnectors.size()+2, container));
			return this;
		}

		public BlockBuilder textFieldAttribute(String attribute, String labelHelpText, String inputHelpText, int textBoxColumnCount) {
			attributes.add(theme.constructTextFieldAttribute(attribute, labelHelpText, inputHelpText, attributes.size()+2, textBoxColumnCount, container));
			return this;
		}

		public BlockBuilder comboBoxAttribute(String attribute, String labelHelpText, ObservableList<String> items) {
			attributes.add(theme.constructComboBoxAttribute(attribute, labelHelpText, attributes.size()+2, items, container));
			return this;
		}

		public Block build(String title, Color headerColor) {

			header = theme.constructHeader(headerColor, container);
			this.title = theme.constructTitle(title, container);
			// If the block doesn't have any input connectors then it is a root block.
			isRoot = (inputConnectors.size()==0) ? true : false;
			
            int height = Math.max(Math.max(inputConnectors.size(), outputConnectors.size()), attributes.size());
			body = theme.constructBody(height+1, container);
			
			theme.clean();

			container.getChildren().addAll(header, body, this.title);
			inputConnectors.forEach(e -> container.getChildren().addAll(e.label(), e.visibleRegion(), e.transparentRegion()));
			outputConnectors.forEach(e -> container.getChildren().addAll(e.label(), e.visibleRegion(), e.transparentRegion()));
			attributes.forEach(e -> container.getChildren().addAll(e.label(), e.attributeRegion()));
			
			Block block = new Block(this);
			container.setBlock(block);
			container.requestFocus();
			
			return block;
		}
    }

	public Node getHeader() {
		return header;
	}

	public Node getBody() {
		return body;
	}

	public Node getTitle() {
		return title;
	}

	public List<Connector> getInputConnectors() {
		return inputConnectors;
	}

	public List<Connector> getOutputConnectors() {
		return outputConnectors;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public Container getContainer() {
		return container;
	}
	
	public BlockCategory getBlockCategory() {
		return blockCat;
	}
	
	public BlockTheme getTheme() {
		return theme;
	}

	public boolean isRoot() {
		return isRoot;
	}

	public boolean isSatisfied() {
		return satisfied;
	}

	/**
	 * Making a block satisfied results in the block being drawn.
	 * Descendant blocks are also checked to see if they can also swap from unsatisfied to
	 * satisfied. If they become satisfied then they too are drawn.
	 */
	public void satisfied() {
		
		satisfied = true;
		// If the drawing that we are redrawing was previously labelled, make the new
		// drawing labelled as well.
		if (drawing != null) {
			if (drawing.pointLabels().size()>0) {
				blockCat.drawWithLabels(this);
			} else {
				blockCat.draw(this);
			}
		} else {
			blockCat.draw(this);
		}
		Set<Block> visitedChildren = new HashSet<>();

		for (Connector oc : outputConnectors) {
			for (EdgeComponent e : oc.edgeEnds()) {
				Block child = e.getEdge().orElseThrow().inputBlock();
				// A means to avoid pointless checking by skipping iterations that check child blocks
				// that have already been checked
				if (visitedChildren.contains(child)) {
					continue;
				}
				visitedChildren.add(child);
				boolean childIsSatisfied = false;
				// If any of the child's input connectors are unoccupied then the child can never be satisfied
				// so the search ends
				for (Connector ic : child.getInputConnectors()) {
					// If the child's input connector is occupied then check the whether the parent block
					// it connects to is satisfied
					if (ic.edgeEnds().size()==1) {
						Block parent = ic.edgeEnds().iterator().next().getEdge().orElseThrow().outputBlock();
						childIsSatisfied = parent.isSatisfied();
					} else {
						childIsSatisfied = false;
					}
					if (!childIsSatisfied) {
						break;
					}
				}

				if (childIsSatisfied) {
					child.satisfied();
				}
			}
		}
	}

	/**
	 * Making a block unsatisfied results in the block having its drawing removed.
	 * All descendant blocks also become unsatisfied, resulting in their drawings
	 * being removed as well.
	 */
	public void unsatisfied() {

		satisfied = false;
		blockCat.undraw(this);

		for (Connector oc : outputConnectors) {
			for (EdgeComponent e : oc.edgeEnds()) {
				Block child = e.getEdge().orElseThrow().inputBlock();
				child.unsatisfied();
			}
		}
	}

	/**
	 * A recursive method for creating the list of blocks to redraw.
	 */
	private void constructRedrawList(List<Block> list, Block root) {
		for (Connector oc : root.getOutputConnectors()) {
			for (EdgeComponent end: oc.edgeEnds()) {
				Block child = end.getEdge().orElseThrow().inputBlock();
				if (child.isSatisfied()) {
					if (!list.contains(child)) {
						list.add(child);
						constructRedrawList(list, child);
					}
				}
			}
		}
	}

	/**
	 * This method only to applies for changes in attribute values on not input connector values.
	 * When a block's attribute's value changes the block is redrawn and any satisfied descendants
	 * are also redrawn.
	 */
	public void valueHasChanged() {
		List<Block> redrawList = new ArrayList<>();
		redrawList.add(this);
		constructRedrawList(redrawList, this);
		for (Block block: redrawList) {
			block.getBlockCategory().draw(block);
		}
		TileCanvasCaretaker.applyDrawingsToCanvas();
	}

	/**
	 * Whenever a drawing of a block is made, make sure to update the drawing field of the block.
	 */
	public void setDrawing(Drawing drawing) {
		this.drawing = drawing;
	}

	public Drawing getDrawing() {
		return drawing;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isLabeled() {
		return isLabeled;
	}

	public void setIsLabeled(boolean labeled) {
		isLabeled = labeled;
	}
}
