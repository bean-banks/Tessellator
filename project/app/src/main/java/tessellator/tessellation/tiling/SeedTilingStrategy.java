package tessellator.tessellation.tiling;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;

import java.awt.Color;

import tessellator.editor.graph.block.category.Category;
import tessellator.editor.preview.Drawing;
import tessellator.editor.preview.TileCanvasCaretaker;

/**
 * An object which generates a tessellation from a seed tile.
 */
public class SeedTilingStrategy implements TilingStrategy {

    private double canvasWidth;
    private double canvasHeight;

    private double xTranslation;
    private double yTranslation;
    private double rotation;
    private double scale;
    // The seed and it's fields never changes after class initialisation, for transformations
    // a copy of the seed it made and that copy is transformed.
    private Tile seed;
    private Tile transformedSeed;
    private List<Tile> tiles;
    private double tileSideLength;

    public SeedTilingStrategy() {

        xTranslation = 0;
        yTranslation = 0;
        rotation = 0;
        scale = 1;
        /**
         * If the graph doesn't have a root then don't bother creating the seed, set it as null.
         * The tessellate method will then not do anything if the seed is null.
         * Otherwise convert the collection of drawings which represent the contents of the seed and
         * the border of the seed into the seed tile.
         */
        Drawing seedDrawing = null;
        List<TileContent> seedContents = new ArrayList<>();
        for (Drawing drawing: TileCanvasCaretaker.getDrawings()) {
            if (drawing.typeOfShape()==Category.TRIANGLE_TILE ||
                drawing.typeOfShape()==Category.SQUARE_TILE ||
                drawing.typeOfShape()==Category.HEXAGON_TILE) {
                seedDrawing = drawing;
                continue;
            }
            List<Double> xCoordsCopy = new ArrayList<>(drawing.xCoords());
            List<Double> yCoordsCopy = new ArrayList<>(drawing.yCoords());
            TileContent content = new TileContent(xCoordsCopy, yCoordsCopy, drawing.typeOfShape());
            seedContents.add(content);
        }
        
        if (seedDrawing == null) {
            seed = null;
            return;
        }
        List<Double> seedXCoordsCopy = new ArrayList<>(seedDrawing.xCoords());
        List<Double> seedYCoordsCopy = new ArrayList<>(seedDrawing.yCoords());
        boolean isBorderVisible = (seedDrawing.colorOfOutline()==Color.RED) ? false : true;
        seed = new Tile(seedXCoordsCopy, seedYCoordsCopy, seedContents, isBorderVisible, seedDrawing.typeOfShape());
    }

    @Override
    public void setCanvasDimensions(double width, double height) {
        canvasWidth = width;
        canvasHeight = height;
        if (seed==null) return;
        normaliseSeed();
    }
    
    /**
     * Transform the seed to have a standard size and a position in the center of the canvas.
     */
    private void normaliseSeed() {
        // Calculate the current center of the seed
        double[] center = tileCenter(seed.xBorderCoords(), seed.yBorderCoords());
        double centerX = center[0];
        double centerY = center[1];

        // Calculate the translation amounts
        double xTrans = canvasWidth/2 - centerX;
        double yTrans = canvasHeight/2 - centerY;

        // Calculate the scale amount, a side length of a border shape should be 20x smaller
        // than the smallest dimension of the canvas
        double x1 = seed.xBorderCoords().get(0);
        double x2 = seed.xBorderCoords().get(1);
        double y1 = seed.yBorderCoords().get(0);
        double y2 = seed.yBorderCoords().get(1);
        double deltaX = x1 - x2;
        double deltaY = y1 - y2;
        double distanceBetweenVertices = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        double minCanvasDimension = Math.min(canvasWidth, canvasHeight);
        scale = 0.05 * (minCanvasDimension / distanceBetweenVertices);
        
        scale(seed);
        translate(seed, xTrans, yTrans);

        // restore transformation fields
        scale = 1;
    }

    private double[] tileCenter(List<Double> xCoords, List<Double> yCoords) {
        double currentCenterX = 0.0;
        double currentCenterY = 0.0;
        for (double x : xCoords) {
            currentCenterX += x;
        }
        for (double y : yCoords) {
            currentCenterY += y;
        }
        currentCenterX /= seed.xBorderCoords().size();
        currentCenterY /= seed.yBorderCoords().size();
        
        return new double[] {currentCenterX, currentCenterY};
    }

    @Override
    public List<Tile> tessellate() {
        tiles = new ArrayList<>();
        if (seed==null) return tiles; 
        transformedSeed = transformSeed();
        tiles.add(transformedSeed);
        growSeed();
        return tiles;
    }

    private void growSeed() {
        switch (seed.category()) {
            case TRIANGLE_TILE:
                growSeedByRotation(transformedSeed.xBorderCoords(), transformedSeed.yBorderCoords());
                break;
            default:
                growSeedByTranslation();
                break;
        }
    }

    // This tessellation currently only works for triangles but if modified a little could work
    // for squares but never hexagons.
    private void growSeedByRotation(List<Double> xPivots, List<Double> yPivots) {
        List<Double> nextXPivots = new ArrayList<>();
        List<Double> nextYPivots = new ArrayList<>();
        
        List<List<Tile>> transformedTilesCopies = new ArrayList<>();

        double angle = 60;

        /**
         * For each pivot create a copy of the tiles collection and rotate the copy
         * about the pivot. Once all the copies of the tiles are rotated, extend the
         * original tiles collection with the contents of it's copies. Then update the
         * pivots to the vertices of the new larger boundary of the shape. Repeat until
         * distance between 2 pivots from the previous stage of recursion is bigger
         * than than both canvas dimension.
         */
        for (int i = 0; i < xPivots.size(); i++) {
            List<Tile> tilesCopy = deepCopyTiles();
            for (int j = 0; j < tilesCopy.size(); j++) {
                rotate(tilesCopy.get(j), xPivots.get(i), yPivots.get(i), angle);
            }
            transformedTilesCopies.add(tilesCopy);

            double xBehindPivot = xPivots.get(Math.floorMod(i-1, 3));
            double yBehindPivot = yPivots.get(Math.floorMod(i-1, 3));
            double[] pivot = rotateCoordinate(xBehindPivot, yBehindPivot, xPivots.get(i), yPivots.get(i), angle);

            nextXPivots.add(pivot[0]);
            nextYPivots.add(pivot[1]);
        }

        for (List<Tile> tilesCopy : transformedTilesCopies) {
            tiles.addAll(tilesCopy);
        }

        // The exit condition for when the growing stops
        boolean exit = false;
        for (int i = 0; i < xPivots.size(); i++) {
            if (xPivots.get(i) >= canvasWidth ||
                xPivots.get(i) <= 0 ||
                yPivots.get(i) >= canvasHeight ||
                yPivots.get(i) <= 0) {
                    exit = true;
            } else {
                exit = false;
            }
        }
        if (exit) return;
        growSeedByRotation(nextXPivots, nextYPivots);
    }

    // This works for squares and hexagons but doesn't work for triangles with some modifications
    // that add rotations.
    private void growSeedByTranslation() {

        List<Point2D> layerVertices = new ArrayList<>();
        List<Point2D> centerTranslations = new ArrayList<>();
        
        // calculate the vertices of layer 0
        List<Double> xCoords = transformedSeed.xBorderCoords();
        List<Double> yCoords = transformedSeed.yBorderCoords();

        double[] center = tileCenter(xCoords, yCoords);
        double xCenter = center[0];
        double yCenter = center[1];

        List<Double> originalXDifs = new ArrayList<>();
        List<Double> originalYDifs = new ArrayList<>();
       
        for (int i = 0; i < xCoords.size(); i++) {
            Point2D p1 = new Point2D(xCoords.get(i), yCoords.get(i));
            int nextVertexIndex = Math.floorMod(i+1, xCoords.size());
            Point2D p2 = new Point2D(xCoords.get(nextVertexIndex), yCoords.get(nextVertexIndex));
            Point2D midPoint = calculateMidpoint(p1, p2);
            double xdif = midPoint.getX() - xCenter;
            originalXDifs.add(xdif*2);
            double ydif = midPoint.getY() - yCenter;
            originalYDifs.add(ydif*2);
            Point2D vertex = new Point2D(xCenter+2*xdif, yCenter+2*ydif);
            layerVertices.add(vertex);
            centerTranslations.add(vertex);
        } 
        
        double diameter = calculateDistance(new Point2D(xCenter, yCenter), new Point2D(xCoords.get(1), yCoords.get(1)))*2;
        boolean exit = false;
        int layer = 1;
        // start loop at layer 1
        while (!exit) {
            // Set the vertices for the larger layer
            for (int i = 0; i < layerVertices.size(); i++) {
                Point2D p = layerVertices.get(i);
                double xdif = p.getX() - xCenter + originalXDifs.get(i);
                double ydif = p.getY() - yCenter + originalYDifs.get(i);
                Point2D vertex = new Point2D(xCenter+xdif, yCenter+ydif);
                layerVertices.set(i, vertex);
                centerTranslations.add(vertex);
            }
            for (int i = 0; i < layerVertices.size(); i++) {
                int nextVertexIndex = Math.floorMod(i+1, layerVertices.size());
                List<Point2D> midPoints = calculateDivisionPoints(layerVertices.get(i), layerVertices.get(nextVertexIndex), layer);
                centerTranslations.addAll(midPoints);
            }
            layer++;
            if ((diameter*((layer+1)*2+1)/2)>Math.max(canvasWidth, canvasHeight)) {
                exit = true;
            }
        }
        
        // Create a new tile for each center translation, aligning the new tile's center with the point
        for (int i = 0; i < centerTranslations.size(); i++) {
            double[] translation = new double[] {centerTranslations.get(i).getX()-xCenter, centerTranslations.get(i).getY()-yCenter};
            Tile copy = transformedSeed.deepCopy();
            translate(copy, translation[0], translation[1]);
            tiles.add(copy);
        }
        
    }

    // Method to calculate the distance between two points
    private double calculateDistance(Point2D p1, Point2D p2) {
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    // Method to calculate the mid point between two give points
    private Point2D calculateMidpoint(Point2D p1, Point2D p2) {
        double midX = (p1.getX() + p2.getX()) / 2.0;
        double midY = (p1.getY() + p2.getY()) / 2.0;
        return new Point2D(midX, midY);
    }

    // Method to calculate division points between two given points
    private List<Point2D> calculateDivisionPoints(Point2D p1, Point2D p2, int divisions) {
        List<Point2D> divisionPoints = new ArrayList<>();

        // Calculate the step size for interpolation
        double stepX = (p2.getX() - p1.getX()) / (divisions + 1);
        double stepY = (p2.getY() - p1.getY()) / (divisions + 1);

        // Calculate division points
        for (int i = 1; i <= divisions; i++) {
            double x = p1.getX() + i * stepX;
            double y = p1.getY() + i * stepY;
            divisionPoints.add(new Point2D(x, y));
        }

        return divisionPoints;
    }

    private List<Tile> deepCopyTiles() {
        List<Tile> deepCopyList = new ArrayList<>();
        for (Tile tile : tiles) {
            Tile tileCopy = tile.deepCopy();
            deepCopyList.add(tileCopy);
        }
        return deepCopyList;
    }

    public double[] rotateCoordinate(double x, double y, double pivotX, double pivotY, double angleInDegrees) {
        double angleInRadians = Math.toRadians(angleInDegrees);

        double newX = pivotX + (x - pivotX) * Math.cos(angleInRadians) - (y - pivotY) * Math.sin(angleInRadians);
        double newY = pivotY + (x - pivotX) * Math.sin(angleInRadians) + (y - pivotY) * Math.cos(angleInRadians);

        return new double[] {newX, newY};
    }

    private Tile transformSeed() {
        // copy seed
        Tile seedCopy = seed.deepCopy();
        double[] center = tileCenter(seedCopy.xBorderCoords(), seedCopy.yBorderCoords());
        double seedCenterX = center[0];
        double seedCenterY = center[1];

        // apply transformations to the seed
        rotate(seedCopy, seedCenterX, seedCenterY);
        scale(seedCopy);
        translate(seedCopy);

        return seedCopy;
    }

    private void translate(Tile tile) {
        translate(tile, xTranslation*tileSideLength, yTranslation*tileSideLength);
    }

    private void translate(Tile tile, double xAmount, double yAmount) {
        // Translate the border of the tile
        List<Double> xBorderCoords = tile.xBorderCoords();
        List<Double> yBorderCoords = tile.yBorderCoords();

        for (int i = 0; i < xBorderCoords.size(); i++) {
            double newX = xBorderCoords.get(i) + xAmount;
            double newY = yBorderCoords.get(i) + yAmount;
            xBorderCoords.set(i, newX);
            yBorderCoords.set(i, newY);
        }

        // Translate the contents of the tile
        for (int i = 0; i < tile.contents().size(); i++) {
            TileContent content = tile.contents().get(i);
            List<Double> xCoords = content.xCoords();
            List<Double> yCoords = content.yCoords();

            for (int j = 0; j < xCoords.size(); j++) {
                double newX = xCoords.get(j) + xAmount;
                double newY = yCoords.get(j) + yAmount;
                xCoords.set(j, newX);
                yCoords.set(j, newY);
            }
        }
    }

    private void rotate(Tile tile, double xPivot, double yPivot, double angleDegrees) {
        
        double angleRadians = Math.toRadians(angleDegrees);

        // Rotate the border of the tile
        List<Double> xBorderCoords = tile.xBorderCoords();
        List<Double> yBorderCoords = tile.yBorderCoords();

        for (int i = 0; i < xBorderCoords.size(); i++) {
            double x = xBorderCoords.get(i);
            double y = yBorderCoords.get(i);

            double rotatedX = xPivot + (x - xPivot) * Math.cos(angleRadians) - (y - yPivot) * Math.sin(angleRadians);
            double rotatedY = yPivot + (x - xPivot) * Math.sin(angleRadians) + (y - yPivot) * Math.cos(angleRadians);

            xBorderCoords.set(i, rotatedX);
            yBorderCoords.set(i, rotatedY);
        }

        // Rotate the contents of the tile
        // Iterate through each shape within the tile
        for (int i = 0; i < tile.contents().size(); i++) {
            TileContent content = tile.contents().get(i);
            List<Double> xCoords = content.xCoords();
            List<Double> yCoords = content.yCoords();

            // Iterate through each point within a shape
            for (int j = 0; j < xCoords.size(); j++) {
                
                double x = xCoords.get(j);
                double y = yCoords.get(j);

                double rotatedX = xPivot + (x - xPivot) * Math.cos(angleRadians) - (y - yPivot) * Math.sin(angleRadians);
                double rotatedY = yPivot + (x - xPivot) * Math.sin(angleRadians) + (y - yPivot) * Math.cos(angleRadians);

                xCoords.set(j, rotatedX);
                yCoords.set(j, rotatedY);
            }
        }

    }

    private void rotate(Tile tile, double xPivot, double yPivot) {
        rotate(tile, xPivot, yPivot, rotation);
    }

    private void scale(Tile tile) {
        double[] centroid = tileCenter(tile.xBorderCoords(), tile.yBorderCoords());
        double centroidX = centroid[0];
        double centroidY = centroid[1];

        tileSideLength = calculateDistance(new Point2D(tile.xBorderCoords().get(0), tile.yBorderCoords().get(0)),
                         new Point2D(tile.xBorderCoords().get(1), tile.yBorderCoords().get(1)))*scale;

        // Scale the border of the tile
        List<Double> xBorderCoords = tile.xBorderCoords();
        List<Double> yBorderCoords = tile.yBorderCoords();

        for (int i = 0; i < xBorderCoords.size(); i++) {
            double x = xBorderCoords.get(i);
            double y = yBorderCoords.get(i);
            // Calculate vector from centroid to vertex
            double dx = x - centroidX;
            double dy = y - centroidY;
            // Scale down the length of the vector
            dx *= scale;
            dy *= scale;
            // Update coordinates of the vertex
            xBorderCoords.set(i, centroidX + dx);
            yBorderCoords.set(i, centroidY + dy);
        }

        // Scale the contents of the tile
        for (int i = 0; i < tile.contents().size(); i++) {
            TileContent content = tile.contents().get(i);
            List<Double> xCoords = content.xCoords();
            List<Double> yCoords = content.yCoords();

            for (int j = 0; j < xCoords.size(); j++) {
                double x = xCoords.get(j);
                double y = yCoords.get(j);
                // Calculate vector from centroid to vertex
                double dx = x - centroidX;
                double dy = y - centroidY;
                // Scale down the length of the vector
                dx *= scale;
                dy *= scale;
                // Update coordinates of the vertex
                xCoords.set(j, centroidX + dx);
                yCoords.set(j, centroidY + dy);
            }
        }
    }

    @Override
    public void setXTranslation(double amount) {
        xTranslation = amount;
    }

    @Override
    public void setYTranslation(double amount) {
        yTranslation = amount;
    }

    @Override
    public void setRotation(double amount) {
        rotation = amount;
    }

    @Override
    public void setScale(double amount) {
        scale = amount;
    }
}
