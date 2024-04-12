package tessellator.editor.graph.edge.component.line;


import java.util.Set;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Effect;
import javafx.scene.shape.Line;
import tessellator.editor.graph.Selectable;
import tessellator.editor.graph.edge.Edge;
import tessellator.editor.graph.edge.component.EdgeComponent;

/**
 * An object that can be used as the line component of an edge in the graph builder.
 * This particular object represents a straight line.
 */
public class StraightLineComponent extends Line implements Selectable, EdgeComponent {
	
    private boolean selected;
	private Effect oldEffect;
	private boolean oldEffectSwapped;

    private Edge edge;

    @Override
	public boolean isSelected() {
		return selected;
	}
    
    /**
     * The selected flag becomes true, the style of the line changes to appear as
     * if its been highlighted.
     */
    @Override
	public void select(Set<Node> selectedNodes) {
		oldEffect = getEffect();
		oldEffectSwapped = true;
		Bloom bloom = new Bloom();
		bloom.setThreshold(0.1);
		bloom.setInput(oldEffect);
		setEffect(bloom);
		selected = true;
	}
	
    /**
     * The selected flag becomes false, the style of the line is reverted back
     * to its standard non selected appearance.
     */
    @Override
	public void deselect() {
		if (oldEffectSwapped) {
			setEffect(oldEffect);
			oldEffectSwapped = false;
		}
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
}
