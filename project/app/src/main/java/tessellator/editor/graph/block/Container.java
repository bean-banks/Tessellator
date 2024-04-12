package tessellator.editor.graph.block;

import java.util.Set;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Effect;
import tessellator.editor.graph.Selectable;
import tessellator.editor.graph.block.eventhandling.BlockDragStrategies;

public class Container extends Group implements Selectable {
	
    private Block block;
	private boolean selected;
	private Effect oldEffect;
	private boolean oldEffectSwapped;
	
	public void setBlock(Block block) {
		this.block = block;
	}
	
	public Block getBlock() {
		return block;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * When container is selected highlight it and change the drag strategies of its
	 * draggable children to that of group drag. So that when the container is dragged
	 * any other selected containers are also dragged.
	 */
	public void select(Set<Node> selectedNodes) {
		
        oldEffect = getEffect();
		oldEffectSwapped = true;
		ColorAdjust colorAdjust = new ColorAdjust();
		colorAdjust.setInput(oldEffect);
		colorAdjust.setSaturation(0.16);
		setEffect(colorAdjust);
		//setOpacity(0.5);
		

		BlockDragStrategies.addGroupDrag(block.getHeader(), this, selectedNodes);
		BlockDragStrategies.addGroupDrag(block.getTitle(), this, selectedNodes);
		BlockDragStrategies.addGroupDrag(block.getBody(), this, selectedNodes);
		
		 
		selected = true;
	}
	
	/**
	 * When a container is deselected remove its highlight and swap the drag strategies
	 * of its draggable children back to regular drag. Such that when the container is
	 * dragged only the container and no other blocks are dragged.
	 */
	public void deselect() {
		
        if (oldEffectSwapped) {
			setEffect(oldEffect);
			oldEffectSwapped = false;
			//setOpacity(1);
		}
		
		BlockDragStrategies.addDrag(block.getHeader(), this);
		BlockDragStrategies.addDrag(block.getTitle(), this);
		BlockDragStrategies.addDrag(block.getBody(), this);
		
		selected = false;
	}
}
