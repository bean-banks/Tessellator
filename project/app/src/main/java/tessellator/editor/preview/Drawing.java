package tessellator.editor.preview;

import java.util.List;

import tessellator.editor.graph.block.Block;
import tessellator.editor.graph.block.category.Category;

import java.awt.Color;

/**
 * An object which represents the meta information of a block's drawing. This object
 * can be interpreted to produce an actual drawing within a canvas.
 */
public record Drawing(
    List<Double> xCoords,
    List<Double> yCoords,
    List<String> pointLabels,
    Color colorOfOutline,
    Category typeOfShape,
    Block block) {}
