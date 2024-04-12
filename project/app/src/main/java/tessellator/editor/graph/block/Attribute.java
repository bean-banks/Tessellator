package tessellator.editor.graph.block;

import javafx.scene.Node;
/**
 * An object which groups together the visual components of and attribute as well
 * the value it holds. Make sure to assign a block to the attribute before using
 * the notifyBlock() method.
 */
public class Attribute {
	
	private final Node label;
	private final Node attributeRegion;
	private Block block;
	private String value;

	/**
	 * An initiliased attribute has its value set to the empty string.
	 * 
	 * @param label The label which identifies the attribute.
	 * @param attributeRegion The input node which captures user input.
	 */
	public Attribute(Node label, Node attributeRegion) {

		this.label = label;
		this.attributeRegion = attributeRegion;
		value = "";
	}

	public Node label() {
		return label;
	}

	public Node attributeRegion() {
		return attributeRegion;
	}

	public String value() {
		return value;
	}

	public void setBlock(Block block) {
		this.block = block;
	}

	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * Notify the block the attribute belongs to that the value it's value has changed.
	 */
	public void notifyBlock() {
		try {
			block.valueHasChanged();
		} catch (Exception e) {
			System.out.println("An attribute's block must be set before it can notify its block.");
		}
	}
}
