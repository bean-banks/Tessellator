package tessellator.editor.graph.block.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import tessellator.editor.graph.block.Block;
import tessellator.editor.graph.block.theme.BlockTheme;
import tessellator.editor.preview.Drawing;
import tessellator.editor.preview.TileCanvasCaretaker;

/**
 * An block category object which creates and draws square tile blocks.
 * Note that a hexagon tile is a root block, so when it is created it is
 * added straight to the editor's canvas.
 */
public class SquareTileCategory extends BlockCategory{
    
    public SquareTileCategory(BlockTheme theme) {
		super(theme, "Square Tile");
	}

	@Override
	public Optional<Block> createBlock() {
		if (graphAlreadyHasRoot()) return Optional.empty();
		String rotationLabelHelpText = "Rotate the tile clockwise about \nits center in degrees";
		String rotationInputHelpText = "Enter a numeric value";
		String borderTypeLabelHelpText = "Choose whether the tile's border\n is displayed or not";

		Block block = new Block.BlockBuilder(theme, this)
				.outputConnector("V1")
                .outputConnector("V2")
                .outputConnector("V3")
                .outputConnector("V4")
				.textFieldAttribute("Rotation", rotationLabelHelpText, rotationInputHelpText, 3)
				.comboBoxAttribute("Border", borderTypeLabelHelpText, FXCollections.observableArrayList("Visible", "Invisible"))
				.build(toString(), Color.DARKSEAGREEN);
		theme.getGraphBuilder().setHasRoot(true);
		theme.getGraphBuilder().setRoot(block);
		draw(block);
		TileCanvasCaretaker.applyDrawingsToCanvas();
		return Optional.of(block);
	}

	@Override
	public void draw(Block block) {

		// Interpret attribute inputs
		double rotationAngleDegrees = 0;
		try {
			rotationAngleDegrees = Double.parseDouble(block.getAttributes().get(0).value());
		} catch (Exception e) {}
		double rotationAngleRadians = Math.toRadians(rotationAngleDegrees);
		String border = block.getAttributes().get(1).value().toLowerCase();

		// Canvas size
        double canvasWidth = TileCanvasCaretaker.getCanvasWidth();
		double canvasHeight = TileCanvasCaretaker.getCanvasHeight();
		
        // Define the center coordinates of the square
        Point2D center = new Point2D(canvasWidth/2, canvasHeight/2);

        // Set the radius length from the center to a vertex for the square
        double radius = (canvasWidth-30)/2;

        // Starting angle to position one vertex above the center
        double startAngleRad = Math.toRadians(135);

		// Calculate the coordinates of the vertices
		List<Point2D> vertices = new ArrayList<>();
		for (int i = 0; i < 4; i++) {
            double angleRad = startAngleRad + Math.toRadians(90 * i); // Angle in radians
            double xcoord = center.getX() + radius * Math.cos(angleRad);
            double ycoord = center.getY() + radius * Math.sin(angleRad);
			vertices.add(new Point2D(xcoord, ycoord));
        }

		// Rotate the vertices
		List<Point2D> rotatedVertices = vertices.stream().map(p -> rotatePoint(p, center, rotationAngleRadians)).toList();

		ArrayList<Double> xcoords = new ArrayList<>();
		ArrayList<Double> ycoords = new ArrayList<>();

		// Populate the x and y coordinate lists
		populateCoordinates(rotatedVertices, xcoords, ycoords);

		// Colour the border black if it is visible or red if it will be invisible
		java.awt.Color color = (border.equals("visible")) ? java.awt.Color.BLACK : java.awt.Color.RED;

		List<String> labels = new ArrayList<>();
		if (includeLabels()) {
			labels.add("V1");
			labels.add("V2");
			labels.add("V3");
			labels.add("V4");
		}

		// Create the drawing that will be intepreted and drawn
		Drawing drawing = new Drawing(xcoords, ycoords, labels, color, Category.SQUARE_TILE, block);

		// Update the various linked objects with the newly calculated output coordinates
		applyChanges(block, drawing, rotatedVertices);
	}
}
