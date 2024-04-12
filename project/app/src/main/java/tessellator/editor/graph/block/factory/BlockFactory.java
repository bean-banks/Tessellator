package tessellator.editor.graph.block.factory;


import java.util.Optional;

import tessellator.editor.graph.GraphBuilder;
import tessellator.editor.graph.block.Block;
import tessellator.editor.graph.block.Container;
import tessellator.editor.graph.block.category.BlockCategory;
import tessellator.editor.graph.block.category.Category;
import tessellator.editor.graph.block.category.HexagonTileCategory;
import tessellator.editor.graph.block.category.SquareTileCategory;
import tessellator.editor.graph.block.category.StraightLineCategory;
import tessellator.editor.graph.block.category.TriangleTileCategory;
import tessellator.editor.graph.block.theme.BlockTheme;

/**
 * A factory object for creating blocks of any category, and spawning those blocks
 * within the graph builder.
 */
public class BlockFactory {

    private final BlockTheme theme;

    private final BlockCategory triangleTile;
    private final BlockCategory squareTile;
    private final BlockCategory hexagonTile;
    private final BlockCategory straightLine;

	public BlockFactory(BlockTheme theme) {

        this.theme = theme;

        triangleTile = new TriangleTileCategory(theme);
        squareTile = new SquareTileCategory(theme);
        hexagonTile = new HexagonTileCategory(theme);
        straightLine = new StraightLineCategory(theme);
	}

    /**
     * Spawns a block at the specified coordinates. The part of the block that is spawned
     * at the coordinates is its center. Usually the x and y coordinates passed for the
     * spawn are the center of the graph builder.
     * 
     * @param block The block that will be spawned.
     * @param spawnX The x coordinate of the spawn point.
     * @param spawnY The y coordinate of the spawn point.
     */
    private void spawnBlock(Block block, double spawnX, double spawnY) {

        // Since nodes have their layouts set based on their top left corner
        // we have to make an adjustment to give the illusion that the container's center
        // is having its layouts set
        Container container = block.getContainer();
        container.setLayoutX(spawnX-container.getBoundsInLocal().getWidth()/2);
        container.setLayoutY(spawnY-container.getBoundsInLocal().getHeight()/2);
        theme.getGraphBuilder().getChildren().add(container);
    }

    /**
     * Create a block of a specified category and spawn its center at the passed coordinates
     * within the graph builder.
     * 
     * @param cat The category of block that will be created.
     * 
     * @return The block that's been created. Note that the block may be null. This return
     * value shouldn't be used unless you are sure the block passes all constraints and will be created.
     */
    public Block createBlock(Category cat, double spawnX, double spawnY) {

        Optional<Block> block = null;
       
        switch (cat) {
            case TRIANGLE_TILE:
                block = triangleTile.createBlock();
                break;
            case SQUARE_TILE:
                block = squareTile.createBlock();
                break;
            case HEXAGON_TILE:
                block = hexagonTile.createBlock();
                break;
            case STRAIGHT_LINE:
                block = straightLine.createBlock();
                break;
        }
        // Line below added to remove yellow highlight
        if (block == null) return null;
        if (block.isPresent()) {
            spawnBlock(block.orElseThrow(), spawnX, spawnY);
        }
        return block.orElseThrow();
    }

    public GraphBuilder getGraphBuilder() {
        return theme.getGraphBuilder();
    }
}