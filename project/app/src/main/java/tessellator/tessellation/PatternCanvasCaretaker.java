package tessellator.tessellation;


import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javafx.scene.web.WebEngine;
import tessellator.tessellation.tiling.SeedTilingStrategy;
import tessellator.tessellation.tiling.Tile;
import tessellator.tessellation.tiling.TileContent;
import tessellator.tessellation.tiling.TilingStrategy;
import tessellator.util.DocumentHelper;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;

/**
 * An object which is responsible for handling canvas within the preview of a 
 * tessellation window.
 */
public class PatternCanvasCaretaker {
    
    private Document document;
    private WebEngine canvas;
    private TilingStrategy tiler;
    private static final String svgNS = "http://www.w3.org/2000/svg";
    private Element pattern;

    private double canvasWidth;
    private double canvasHeight;

    public PatternCanvasCaretaker() {
        tiler = new SeedTilingStrategy();
    }

    // Make sure to set the canvas before creating a new document
    public boolean newDocument(File file) {
        // Assume an svg file as one and only one svg element
        try {
            // Create an instance of the SVG document factory
            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);

            // Parse the SVG file and create an Document object
            Document newDocument = factory.createDocument(file.toURI().toString());
            NodeList elements = filterNodes(newDocument.getDocumentElement().getChildNodes());

            // If the svg element of the file has more than one element then the file can't be loaded
            if (elements.getLength()!=1) return false;

            Element svgRoot = newDocument.getDocumentElement();
            String width = svgRoot.getAttribute("width");
            String height = svgRoot.getAttribute("height");
            // If the svg element doesn't contain a width and height attribute the the file can't be loaded
            if (width.equals("") || height.equals("")) return false;

            document = newDocument;

            canvasWidth = extractNumericValue(width);
            canvasHeight = extractNumericValue(height);

            //Modify initial part of the document
            Element clipPath = document.createElementNS(svgNS, "clipPath");
            clipPath.setAttribute("id", "clip");
            svgRoot.appendChild(clipPath);
            Element border = (Element) elements.item(0);
            Element borderClone = (Element) border.cloneNode(false);
            // This is done to prevent the duplication of a potential id that the given border shape may have
            borderClone.removeAttribute("id");
            clipPath.appendChild(borderClone);

            pattern = document.createElementNS(svgNS, "g");
            pattern.setAttribute("id", "pattern");
            pattern.setAttribute("clip-path", "url(#clip)");
            svgRoot.appendChild(pattern);

            tiler.setCanvasDimensions(canvasWidth, canvasHeight);
            List<Tile> tessellation = tiler.tessellate();
            applyTessellationToDoc(tessellation);
            applyDocToCanvas();

            //System.out.println(DocumentHelper.docToString(document));

            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            //System.out.println("The file was not an acceptable svg document.");
            return false;
        }
    }

    // Method to filter out text nodes and comments from the NodeList
    private NodeList filterNodes(NodeList nodeList) {
        Document document = nodeList.item(0).getOwnerDocument(); // Get the owner document of the NodeList
        DocumentFragment fragment = document.createDocumentFragment();
        
        // Iterate through the NodeList and add only elements to the new list
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Node clonedNode = node.cloneNode(true); // Clone the node to avoid modifying the original
                fragment.appendChild(clonedNode);
            }
        }
        
        return fragment.getChildNodes(); // Return the NodeList containing only elements
    }

    //Method to extract numeric value from a string
    private int extractNumericValue(String str) {
        // Regular expression to match numeric values
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        } else {
            // Handle if no numeric value found (default to 0)
            return 0;
        }
    }

    public void setCanvas(WebEngine previewCanvas) {
        canvas = previewCanvas;
    }

    public void applyDocToCanvas() {
        canvas.loadContent(DocumentHelper.docToString(document));
    }

    public void translate(double x, double y) {
        tiler.setXTranslation(x);
        tiler.setYTranslation(y);
        List<Tile> tessellation = tiler.tessellate();
        applyTessellationToDoc(tessellation);
        applyDocToCanvas();
    }

    public void rotate(double angle) {
        tiler.setRotation(angle);
        List<Tile> tessellation = tiler.tessellate();
        applyTessellationToDoc(tessellation);
        applyDocToCanvas();
    }

    public void scale(double scale) {
        tiler.setScale(scale);
        if (document == null) return;
        List<Tile> tessellation = tiler.tessellate();
        applyTessellationToDoc(tessellation);
        applyDocToCanvas();
    }

    public void applyTessellationToDoc(List<Tile> tessellation) {
        // Remove all the child nodes of the pattern element
        // Get the list of child nodes
        NodeList childNodes = pattern.getChildNodes();
        // Iterate over the child nodes and remove each one
        for (int i = childNodes.getLength() - 1; i >= 0; i--) {
            Node childNode = childNodes.item(i);
            pattern.removeChild(childNode);
        }

        // If the tessellation is empty then return
        if (tessellation.size()==0) return;

        boolean isBorderVisible = tessellation.get(0).isBorderVisible();
        // Convert the tessellation of tiles into new child elements of the pattern group element
        for (Tile tile : tessellation) {
            // Add the tile's border to the doc if the border is visible
            // Since the only tile border shapes are triangles, squares and hexagons they are all polygons.
            if (isBorderVisible) {
                Element polygon = document.createElementNS(svgNS, "polygon");
                String pointsValue = "";
                StringBuilder pointsBuilder = new StringBuilder();
                for (int i = 0; i < tile.xBorderCoords().size(); i++) {
                    pointsBuilder.append(tile.xBorderCoords().get(i)).append(",").append(tile.yBorderCoords().get(i)).append(" ");
                }
                pointsValue = pointsBuilder.toString().trim(); // Remove trailing space
                polygon.setAttribute("points", pointsValue);
                polygon.setAttribute("fill", "none");
                polygon.setAttribute("stroke", "black");
                pattern.appendChild(polygon);
            }

            // Add the contents of the tile the doc
            // Currently the contents are only straight lines but to future proof a switch
            // statment is used to allow custom Element translations for all shapes.
            for (TileContent shape : tile.contents()) {
                switch (shape.category()) {
                    case STRAIGHT_LINE:
                        Element line = document.createElementNS(svgNS, "line");
                        line.setAttribute("x1", shape.xCoords().get(0)+"");
                        line.setAttribute("y1", shape.yCoords().get(0)+"");
                        line.setAttribute("x2", shape.xCoords().get(1)+"");
                        line.setAttribute("y2", shape.yCoords().get(1)+"");
                        line.setAttribute("stroke", "black");
                        pattern.appendChild(line);
                        break;

                    default:
                        break;
                }
            }
        }
    }

    public String getDocString() {
        return DocumentHelper.docToString(document);
    }
}