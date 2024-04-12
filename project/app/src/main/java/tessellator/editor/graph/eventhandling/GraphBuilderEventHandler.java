package tessellator.editor.graph.eventhandling;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import tessellator.editor.graph.GraphBuilder;

/**
 * An object which handles all events that occur at the graph builder level.
 * It achieves comprehensive event handling by delegating the handling of specific
 * events to the methods of a more specific event handler.
 * 
 * This event organisation object is required because writing 2 event handlers
 * for the same event on the same graph builder results in the more recent
 * event handler overwritting the older event handler rather than extending the
 * older handler. So instead, a sequence of if, else if statements are required
 * for each handler to properly handle its event.
 */
public class GraphBuilderEventHandler {

    private final GraphBuilder graphBuilder;
    private final GraphBuilderDeletionHandler deletionHandler;
    private final GraphBuilderDragHandler dragHandler;
    private final GraphBuilderSelectionHandler selectionHandler;
    private final GraphBuilderZoomHandler zoomHandler;
    
    public GraphBuilderEventHandler(GraphBuilder graphBuilder) {
        this.graphBuilder = graphBuilder;
        this.deletionHandler = new GraphBuilderDeletionHandler(graphBuilder);
        this.dragHandler = new GraphBuilderDragHandler(graphBuilder);
        this.selectionHandler = new GraphBuilderSelectionHandler(graphBuilder);
        this.zoomHandler = new GraphBuilderZoomHandler(graphBuilder, 1.2);
    }

    public void handleEvents() {

        handleKeyEvents();
        handleMouseEvents();
    }

    private void handleKeyEvents() {

        graphBuilder.setOnKeyPressed(ke -> {
			if ((ke.getCode().equals(KeyCode.PLUS) && ke.isControlDown()) ||
                (ke.getCode().equals(KeyCode.P) && ke.isControlDown())) {
                zoomHandler.zoom(true);
                ke.consume();
			} 
            else if ((ke.getCode().equals(KeyCode.MINUS) && ke.isControlDown()) ||
                     (ke.getCode().equals(KeyCode.M) && ke.isControlDown())) {
                zoomHandler.zoom(false);
                ke.consume();
            }
        });

        graphBuilder.setOnKeyReleased(ke -> {
			if (ke.getCode().equals(KeyCode.BACK_SPACE) || ke.getCode().equals(KeyCode.DELETE)) {
				// To avoid graph deletions when back space is used within a text field
				if (!(ke.getTarget() instanceof TextField)) {
					deletionHandler.deleteSelected();
					ke.consume();
				}
			} else if (ke.getCode().equals(KeyCode.ESCAPE)) {
                    selectionHandler.deselectAll();
                    ke.consume();
            }
        });
    }

    private void handleMouseEvents() {

		graphBuilder.setOnMousePressed(me -> {
			if (me.getButton() == MouseButton.PRIMARY && me.isShiftDown()) {
                graphBuilder.setMouseTransparent(true);
			    me.setDragDetect(true);
				selectionHandler.createSelectBox(me);
                me.consume();
            } else if (me.getButton() == MouseButton.PRIMARY) {
                graphBuilder.setMouseTransparent(true);
			    me.setDragDetect(true);
				dragHandler.startGraphDrag(me);
                me.consume();
			}
		});

		graphBuilder.setOnMouseDragged(me -> {
			
			if (me.getButton() == MouseButton.PRIMARY && me.isShiftDown()) {
				selectionHandler.drawSelectBox(me);
                me.setDragDetect(false);
			    me.consume();
			} else if (me.getButton() == MouseButton.PRIMARY) {
				dragHandler.dragGraph(me);
                me.setDragDetect(false);
			    me.consume();
			}
		});

		graphBuilder.setOnMouseReleased(me -> {

			if (me.getButton() == MouseButton.PRIMARY && me.isShiftDown()) {
                graphBuilder.setMouseTransparent(false);
				selectionHandler.applyAndRemoveSelectBox();
                me.consume();
			} else if (me.getButton() == MouseButton.PRIMARY) {
                graphBuilder.setMouseTransparent(false);
			    dragHandler.endGraphDrag();
                me.consume();
			}
		});
    }

}
