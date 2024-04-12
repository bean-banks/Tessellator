package tessellator.editor.preview;

import java.util.Set;
import java.util.HashSet;
import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.swing.JSVGCanvas;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

import tessellator.util.DocumentHelper;

import java.awt.Color;

/**
 * The object which is responsible for the collection of drawings as well as rendering those
 * drawings onto the canvas within the tile preview.
 * 
 * The class is static because there is only one canvas, so static methods have the same effect
 * as instance methods, but with only static methods no caretaker instance needs to be passed
 * around, making everything easier.
 */
public class TileCanvasCaretaker {

    private static final Set<Drawing> drawings = new HashSet<>();
    private static final String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    private static SVGDocument document;
    private static Element tileGroup;
    private static JSVGCanvas canvas;
    private static double canvasWidth;
    private static double canvasHeight;

    public static void newDocument() {
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        document = (SVGDocument) impl.createDocument(svgNS, "svg", null);
        tileGroup = document.createElementNS(svgNS, "g");
        tileGroup.setAttributeNS(null, "id", "tile");
        document.getDocumentElement().appendChild(tileGroup);
    }

    public static void newCanvas() {
        canvas = new JSVGCanvas();
    }

    public static SVGDocument getDocument() {
        return document;
    }

    public static JSVGCanvas getCanvas() {
        return canvas;
    }

    public static void renderCanvas() {
        canvas.setSVGDocument(document);
    }

    public static Set<Drawing> getDrawings() {
        return drawings;
    }

    public static void setCanvasDimensions(double width, double height) {
        canvasWidth = width;
        canvasHeight = height;
    }

    public static double getCanvasWidth() {
        return canvasWidth;
    }

    public static double getCanvasHeight() {
        return canvasHeight;
    }

    /**
     * Once there are no longer any more changes needed to made to the collection of
     * drawings then this method should be invoked to visualise the changes that have been
     * made to the collection of drawings.
     * 
     * With every invocation a new document is created and all the drawings in the drawings
     * collection are drawn onto the document. The document is then rendered. This proved to
     * be quicker and simpler compared to editing and fixing a document that already exists.
     */
    public static void applyDrawingsToCanvas() {
        newDocument();
        drawings.forEach(drawing -> {
            switch (drawing.typeOfShape()) {
                case TRIANGLE_TILE:
                    drawTriangleTile(drawing);
                    break;
                case SQUARE_TILE:
                    drawSquareTile(drawing);
                    break;
                case HEXAGON_TILE:
                    drawHexagonTile(drawing);
                    break;
                case STRAIGHT_LINE:
                    drawStraightLine(drawing);
                    break;
            }
        });
        renderCanvas();
    }

    private static void drawTriangleTile(Drawing drawing) {

        // Create a polygon element for the triangle
        Element triangle = document.createElementNS(svgNS, "polygon");
        String points = "";
        for (int i = 0; i < drawing.xCoords().size(); i++) {
            points += drawing.xCoords().get(i) + "," + drawing.yCoords().get(i) + " ";
        }
        triangle.setAttributeNS(null, "points", points);
        triangle.setAttributeNS(null, "fill", "none");
        triangle.setAttributeNS(null, "stroke", svgColor(drawing.colorOfOutline()));
        triangle.setAttributeNS(null, "id", "triangle_boundary");

        // Add labels
        addLabels(drawing);

        // Add the triangle to the group
        tileGroup.appendChild(triangle);

        // Print out the XML content of the SVG document
        //writeSVGDocumentToFile(document, "triangle_tile.svg");
        
    }

    private static void drawSquareTile(Drawing drawing) {

        // Create a polygon element for the square
        Element square = document.createElementNS(svgNS, "polygon");
        String points = "";
        for (int i = 0; i < drawing.xCoords().size(); i++) {
            points += drawing.xCoords().get(i) + "," + drawing.yCoords().get(i) + " ";
        }
        square.setAttributeNS(null, "points", points);
        square.setAttributeNS(null, "fill", "none");
        square.setAttributeNS(null, "stroke", svgColor(drawing.colorOfOutline()));
        square.setAttributeNS(null, "id", "square_boundary");

        // Add labels
        addLabels(drawing);

        // Add the triangle to the group
        tileGroup.appendChild(square);
    }

    private static void drawHexagonTile(Drawing drawing) {

        // Create a polygon element for the hexagon
        Element hexagon = document.createElementNS(svgNS, "polygon");
        String points = "";
        for (int i = 0; i < drawing.xCoords().size(); i++) {
            points += drawing.xCoords().get(i) + "," + drawing.yCoords().get(i) + " ";
        }
        hexagon.setAttributeNS(null, "points", points);
        hexagon.setAttributeNS(null, "fill", "none");
        hexagon.setAttributeNS(null, "stroke", svgColor(drawing.colorOfOutline()));
        hexagon.setAttributeNS(null, "id", "hexagon_boundary");

        // Add labels
        addLabels(drawing);

        // Add the triangle to the group
        tileGroup.appendChild(hexagon);
    }

    private static void drawStraightLine(Drawing drawing) {
        
        // Create a polygon element for the triangle
        Element line = document.createElementNS(svgNS, "line");
        
        // Set attributes for the line
        line.setAttribute("x1", drawing.xCoords().get(0)+""); // Starting X coordinate
        line.setAttribute("y1", drawing.yCoords().get(0)+""); // Starting Y coordinate
        line.setAttribute("x2", drawing.xCoords().get(1)+""); // Ending X coordinate
        line.setAttribute("y2", drawing.yCoords().get(1)+""); // Ending Y coordinate
        line.setAttributeNS(null, "fill", "none");
        line.setAttributeNS(null, "stroke", svgColor(drawing.colorOfOutline()));
        line.setAttributeNS(null, "class", "straight_line");

        // Add labels
        addLabels(drawing);

        // Add the triangle to the group
        tileGroup.appendChild(line);
    }

    private static String svgColor(Color color) {
        // Convert RGB values to hexadecimal notation
        String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
        return hexColor;
    }

    /**
     * Helper method for adding labels to the points of a shape.
     */
    private static void addLabels(Drawing drawing) {
        if (drawing.pointLabels().size()>0) {
            for (int i = 0; i < drawing.pointLabels().size(); i++) {
                Element text = document.createElementNS(svgNS, "text");
                text.setAttributeNS(null, "x", String.valueOf(drawing.xCoords().get(i)));
                text.setAttributeNS(null, "y", String.valueOf(drawing.yCoords().get(i)));
                text.setAttributeNS(null, "fill", "green");
                text.setTextContent(drawing.pointLabels().get(i));
                tileGroup.appendChild(text);
            }
        }
    }

    /**
     * Transforms the java svg document object into an svg document embedded within a string.
     * 
     * @return The SVG document as a string.
     */
    public static String svgDocToString() {
        return DocumentHelper.docToString(document);
    }
}
