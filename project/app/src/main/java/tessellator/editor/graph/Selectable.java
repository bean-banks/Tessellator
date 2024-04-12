package tessellator.editor.graph;

import java.util.Set;
import javafx.scene.Node;

/**
 * This interface is intended to be implemented by interactable objects within
 * the graph builder, and for those objects this interface provides 2 features.
 * 1. They can change their state/appearance upon selection and deselection.
 * 2. They can update the set of selected nodes which allows for certain commands
 * and operations to be applied to this specific subgroup of nodes.
 *
 */
public interface Selectable
{
    /**
     * A getter to find out whether the object is currently selected or deselected.
     * 
     * @return True if the object is selected, false otherwise.
     */
	boolean isSelected();
	
    /**
     * The object becomes selected if previously deselected and stays selected if
     * previously selected.
     * 
     * @param selectedNodes The set of nodes which are currently selected prior to this object's selection.
     */
	void select(Set<Node> selectedNodes);
	
    /**
     * The object becomes deselected if previously selected and stays deselected if
     * previously deselected.
     */
	void deselect();
}
