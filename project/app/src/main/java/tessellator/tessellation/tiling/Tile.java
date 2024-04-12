package tessellator.tessellation.tiling;

import java.util.ArrayList;
import java.util.List;

import tessellator.editor.graph.block.category.Category;

/**
 * An object which represents the core parts of a tile.
 */
public class Tile {

    private List<Double> borderXCoords;
    private List<Double> borderYCoords;
    private List<TileContent> contents;
    private boolean isBorderVisible;
    private Category shapeOfTile;

    public Tile(List<Double> xCoords, List<Double> yCoords, List<TileContent> contents,
                boolean visibleBorder, Category tileShape) {
        borderXCoords = xCoords;
        borderYCoords = yCoords;
        this.contents = contents;
        isBorderVisible = visibleBorder;
        shapeOfTile = tileShape;
    }

    public List<Double> xBorderCoords() {
        return borderXCoords;
    }

    public List<Double> yBorderCoords() {
        return borderYCoords;
    }

    public List<TileContent> contents() {
        return contents;
    }

    public boolean isBorderVisible() {
        return isBorderVisible;
    }

    public Category category() {
        return shapeOfTile;
    }

    public Tile deepCopy() {
        List<Double> borderXCoordsCopy = new ArrayList<>(borderXCoords); // Create a copy of borderXCoords
        List<Double> borderYCoordsCopy = new ArrayList<>(borderYCoords); // Create a copy of borderYCoords
        List<TileContent> contentsCopy = contents.stream().map(c -> c.deepCopy()).toList();

        return new Tile(borderXCoordsCopy, borderYCoordsCopy, contentsCopy, isBorderVisible, shapeOfTile);
    }
    
}
