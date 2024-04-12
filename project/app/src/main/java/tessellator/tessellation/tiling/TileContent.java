package tessellator.tessellation.tiling;

import java.util.ArrayList;
import java.util.List;

import tessellator.editor.graph.block.category.Category;

/**
 * An object represents a shape/geomtry within a tile.
 */
public class TileContent {
    
    private List<Double> xCoordinates;
    private List<Double> yCoordinates;
    private Category shapeOfContent;

    public TileContent(List<Double> xCoords, List<Double> yCoords, Category contentShape) {
        xCoordinates = xCoords;
        yCoordinates = yCoords;
        shapeOfContent = contentShape;
    }
    
    public List<Double> xCoords() {
        return xCoordinates;
    }

    public List<Double> yCoords() {
        return yCoordinates;
    }

    public Category category() {
        return shapeOfContent;
    }

    public TileContent deepCopy() {
        List<Double> newXCoords = new ArrayList<>(xCoordinates); // Create a copy of xCoordinates
        List<Double> newYCoords = new ArrayList<>(yCoordinates); // Create a copy of yCoordinates

        // Return a new TileContent object with copied fields
        return new TileContent(newXCoords, newYCoords, shapeOfContent);
    }
}
