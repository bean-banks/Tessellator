package tessellator.editor.graph.block.category;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import tessellator.editor.graph.block.Block;
import tessellator.editor.graph.block.theme.BlockTheme;
import tessellator.editor.preview.Drawing;

/**
 * An block category object which creates and draws straight line blocks.
 */
public class StraightLineCategory extends BlockCategory {

	public StraightLineCategory(BlockTheme theme) {
		super(theme, "Straight Line");
	}

	@Override
	public Optional<Block> createBlock() {
		String e1TrimLabelHelpText = "Trim a percentage of the line from \npoint E1 to point E2";
		String e2TrimLabelHelpText = "Trim a percentage of the line from \npoint E2 to point E1";
		String e1SlideLabelHelpText = "Slide point E1 a percentage towards \nits next vertex";
		String e2SlideLabelHelpText = "Slide point E2 a percentage towards \nits next vertex";
		String inputHelpText = "Enter a value between 0 and 100";

		Block block = new Block.BlockBuilder(theme, this)
				.inputConnector("")
                .inputConnector("")
				.outputConnector("E1")
                .outputConnector("E2")
				.textFieldAttribute("E1Trim", e1TrimLabelHelpText, inputHelpText, 2)
                .textFieldAttribute("E2Trim", e2TrimLabelHelpText, inputHelpText, 2)
				.textFieldAttribute("E1Slide", e1SlideLabelHelpText, inputHelpText, 2)
				.textFieldAttribute("E2Slide", e2SlideLabelHelpText, inputHelpText, 2)
				.build(toString(), Color.FIREBRICK);
		return Optional.of(block);
	}

	@Override
	public void draw(Block block) {
		
		// Initialise the points
		double e1TrimFactor = interpretString(block.getAttributes().get(0).value());
		double e1SlideFactor = interpretString(block.getAttributes().get(2).value());

		double e2TrimFactor = interpretString(block.getAttributes().get(1).value());
		double e2SlideFactor = interpretString(block.getAttributes().get(3).value());

		Point2D e1Value = block.getInputConnectors().get(0).value();
		Point2D e1SecondValue = block.getInputConnectors().get(0).secondaryValue();

		Point2D e2Value = block.getInputConnectors().get(1).value();
		Point2D e2SecondValue = block.getInputConnectors().get(1).secondaryValue();

		// slide the points
		Point2D interpolatedE1 = interpolate(e1Value, e1SecondValue, e1SlideFactor);
		Point2D interpolatedE2 = interpolate(e2Value, e2SecondValue, e2SlideFactor);

		// trim the points
		Point2D midpoint = interpolatedE1.midpoint(interpolatedE2);
		Point2D trimmedE1 = trimPoint(interpolatedE1, interpolatedE2, e1TrimFactor);
		Point2D trimmedE2 = trimPoint(interpolatedE2, interpolatedE1, e2TrimFactor);
		// If either trim overlaps the other trim then set the points returned by both
		// trims to the midpoint between them which results on no visible line between
		// the points.
		if (distance(trimmedE2, interpolatedE1)<distance(trimmedE1, interpolatedE1) ||
			distance(trimmedE1, interpolatedE2)<distance(trimmedE2, interpolatedE2)) {
			trimmedE1 = midpoint;
			trimmedE2 = midpoint;
		}

		ArrayList<Point2D> ends = new ArrayList<>();
		ends.add(trimmedE1);
		ends.add(trimmedE2);

		ArrayList<Double> xcoords = new ArrayList<>();
		ArrayList<Double> ycoords = new ArrayList<>();

		// Set up the x and y coordinate lists
		populateCoordinates(ends, xcoords, ycoords);

		// Colour the line black
		java.awt.Color color = java.awt.Color.BLACK;

		// Create the labels if required
		List<String> labels = new ArrayList<>();
		if (includeLabels()) {
			labels.add("E1");
			labels.add("E2");
		}

		// Create the drawing that will be intepreted and drawn
		Drawing drawing = new Drawing(xcoords, ycoords, labels, color, Category.STRAIGHT_LINE, block);

		// Update the various linked objects with the newly calculated output coordinates
		applyChanges(block, drawing, ends);
	}

    public double distance(Point2D p1, Point2D p2) {
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

	/**
	 * If input is not a double return zero, if the value is negative return zero,
	 * if the input is above 100 return zero, convert the percentage into a factor.
	 * 
	 * @param input The string that will be interpreted into a factor.
	 * @return The factor the string represents, default to 0 of the input can't be interpreted.
	 */
	private double interpretString(String input) {
		double value = 0;
		try {
			value = Double.parseDouble(input);
			value  = (value<0 || value>100) ? 0 : value;
		} catch (Exception e) {}
		return value/100;
	}

	/**
	 * Slide point p1 towards point p2 by a certain amount determined by the position.
	 * 
	 * @param p1 The point that will be slid.
	 * @param p2 The point which the other point is slid towards.
	 * @param position The percentage in decimal form that represents how much to slide
	 * - 0.0 => no slide, point p1 hasn't changed
	 * - 1.0 => maximum slide, point p1 as now the same as point p2
	 * @return The point after it has been slid.
	 */
    private Point2D interpolate(Point2D p1, Point2D p2, double position) {
        double newX = (1 - position) * p1.getX() + position * p2.getX();
        double newY = (1 - position) * p1.getY() + position * p2.getY();
        return new Point2D(newX, newY);
    }

	/**
	 * Move a point towards its midpoint by a certain amount determined by the trim.
	 * 
	 * @param p The point is being moved.
	 * @param midpoint The point which the other point is moved towards.
	 * @param trim The percentage in decimal form that represents how much to move
	 * - 0.0 => no movement, point p1 hasn't changed
	 * - 1.0 => maximum movement, point p1 as now the same as point p2
	 * @return The point after it has been moved.
	 */
	private Point2D trimPoint(Point2D p, Point2D midpoint, double trim) {
        double newX1 = p.getX() + (midpoint.getX() - p.getX()) * trim;
        double newY1 = p.getY() + (midpoint.getY() - p.getY()) * trim;
        return new Point2D(newX1, newY1);
	}
}
