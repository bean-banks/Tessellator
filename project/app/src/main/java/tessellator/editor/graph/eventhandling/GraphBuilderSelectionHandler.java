package tessellator.editor.graph.eventhandling;

import java.util.Set;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import tessellator.editor.graph.GraphBuilder;
import tessellator.editor.graph.Selectable;

/**
 * An object which provides methods for handling select events within the graph builder.
 */
public class GraphBuilderSelectionHandler {
    
    private final GraphBuilder graphBuilder;
    private final Set<Node> selected;
    private SVGPath selectBox;
	// The point where the cursor pressed
	private double sBoxStartX;
	private double sBoxStartY;
	// The point where the cursor has been dragged
	private double sBoxEndX;
	private double sBoxEndY;

    public GraphBuilderSelectionHandler(GraphBuilder graphBuilder) {

        this.graphBuilder = graphBuilder;
        this.selected = graphBuilder.getSelected();
    }

    public void deselectAll() {
		// since the selected are no longer the point of interest, change the focus to the diagram
		graphBuilder.requestFocus();
		selected.forEach(n -> {
			Selectable s = (Selectable) n;
			s.deselect();
		});
		selected.clear();
	}

	public void createSelectBox(MouseEvent me) {
		
		selectBox = new SVGPath();
		selectBox.setFill(Color.LIGHTSALMON);
		selectBox.setOpacity(0.1);
		sBoxStartX = me.getX();
		sBoxStartY = me.getY();
		graphBuilder.getChildren().add(selectBox);
		
		selected.forEach(n -> {
			Selectable s = (Selectable) n;
			s.deselect();
		});
		selected.clear();
	}

	public void drawSelectBox(MouseEvent me) {

        sBoxEndX = me.getX();
        sBoxEndY = me.getY();
        selectBox.setContent("M"+sBoxStartX+", "+sBoxStartY+" L"+sBoxEndX+", "+sBoxStartY+" L"+sBoxEndX+", "+sBoxEndY+" L"+sBoxStartX+", "+sBoxEndY+" Z");
	}

	public void applyAndRemoveSelectBox() {

		graphBuilder.getChildren().forEach(n -> {
			if (n instanceof SVGPath) {
				
			}
			else if (selectBox.getBoundsInParent().intersects(n.getBoundsInParent())) {
				selected.add(n);
			}
		});
		selected.forEach(n -> {
			Selectable s = (Selectable) n;
			s.select(selected);
		});
		graphBuilder.getChildren().remove(selectBox);
	}
}

