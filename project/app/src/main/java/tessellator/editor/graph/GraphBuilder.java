package tessellator.editor.graph;

import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import tessellator.editor.graph.block.Block;
import tessellator.editor.graph.block.factory.BlockFactory;
import tessellator.editor.graph.eventhandling.GraphBuilderEventHandler;

/**
 * An object whicha embodies the graph builder concept.
 */
public class GraphBuilder extends BorderPane {
    
    private double strokeWidthOfLines;
    private double zoomScale;
    private final Set<Node> selected;
    private boolean hasRoot;
    private final JsonHelper jsonHelper;
    // required for traversing the graph during the json conversion
    private Block root;
    // Used as the store of available and unavailable block ids
    // The index of a boolean represents the id and the boolean is true if
    // if the id available and falso if the id is taken
    private List<Boolean> blockIds;
    private final GraphBuilderEventHandler eventHandler;

    public GraphBuilder() {
        strokeWidthOfLines = 1;
        zoomScale = 1;
        selected = new HashSet<>();
        eventHandler = new GraphBuilderEventHandler(this);
        eventHandler.handleEvents();
        jsonHelper = new JsonHelper(this);

        // Start off with 100 available ids, if all one hundred ids are taken then
        // the pool of ids is automatically increased by 100.
        blockIds = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            blockIds.add(true);
        }

    }

    public void setStrokeWidthOfLines(double strokeWidth) {
        strokeWidthOfLines = strokeWidth;
    }

    public double getStrokeWidthOfLines() {
        return strokeWidthOfLines;
    }

    public void setZoomScale(double scale) {
        zoomScale = scale;
    }

    public double getZoomScale() {
        return zoomScale;
    }

    public Set<Node> getSelected() {
        return selected;
    }

    public boolean hasRoot() {
        return hasRoot;
    }

    public void setHasRoot(boolean hasRoot) {
        this.hasRoot = hasRoot;
    }

    public void setRoot(Block root) {
        this.root = root;
    }

    // The graph may not have a root block
    public Optional<Block> getRoot() {
        return Optional.ofNullable(root);
    }

    /**
     * Create a json representation of the graph builder.
     * 
     * @return The json representation as a string.
     */
    public String createJson() {
        return jsonHelper.graphToJson();
    }

    /**
     * Apply the data in the json string to this graph builder.
     * 
     * @param json
     */
    public void interpretJson(String json, BlockFactory factory) {
        jsonHelper.jsonToGraph(json, factory);
    }

    /**
     * Get the first available id, then also mark that id as unavailable so other
     * blocks connot obtain the same id.
     */
    public int getAvailableBlockId() {
        int idCount = blockIds.size();
        // Mark the first available id as unavailable and return said id
        for (int i = 0; i < idCount; i++) {
            if (blockIds.get(i)) {
                blockIds.set(i, false);
                return i;
            }
        }
        // If all ids are unavailable then add 100 more available ids to the id pool
        // and return the first available id whilest also marking it as unavailable
        for (int i = idCount; i < idCount+100; i++) {
            blockIds.add(true);
        }
        blockIds.set(idCount, false);
        return idCount;
    }

    /**
     * Give back a block's id so that another block may be able to use said id again.
     */
    public void freeBlockId(int id) {
        blockIds.set(id, true);
    }

    /**
     * Make all ids available and reduce the pool of available ids back to 100.
     */
    public void freeAllBlockIds() {
        blockIds.clear();
        for (int i = 0; i < 100; i++) {
            blockIds.add(true);
        }
    }

    /**
     * When a block already has an id and needs to notify the graph builder
     * that its id should be made unavailable.
     * This method should only be used when absolutely sure that that the given
     * id is not already in use by some other block.
     * @param Id The id of the block.
     */
    public void usingBlockId(int id) {
        int idCount = blockIds.size();
        // If the index of the id is greater than the current maximum index in the pool of ids
        // then increase the pool of ids to the index of this new id plus 100
        if (id >= idCount) {
            for (int i = idCount; i < id+100; i++) {
                blockIds.add(true);
            }
            
        }
        blockIds.set(id, false);
    }

    /**
     * Clear all of the content that currently exists in graph builder such as blocks,
     * edges, selected blocks, root flags, etc.
     */
    public void clearContent() {
        freeAllBlockIds();
        getChildren().clear();
        selected.clear();
        hasRoot = false;
        root = null;
    }
}
