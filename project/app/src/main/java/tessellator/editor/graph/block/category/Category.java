package tessellator.editor.graph.block.category;

/**
 * An enum which captures all the different categories of blocks that exist
 * within the applications. Can be used to identify block categories and
 * the blocks they produce.
 */
public enum Category {
    
    TRIANGLE_TILE("Triangle Tile"),
    SQUARE_TILE("Square Tile"), 
    HEXAGON_TILE("Hexagon Tile"), 
    STRAIGHT_LINE("Straight Line");

    private final String stringValue;

    // Constructor to initialize the string value
    Category(String stringValue) {
        this.stringValue = stringValue;
    }

    // Getter method to retrieve the string value
    @Override
    public String toString() {
        return stringValue;
    }

    // Static method to convert string to enum constant
    public static Category fromString(String value) {
        for (Category category : Category.values()) {
            if (category.stringValue.equalsIgnoreCase(value)) {
                return category;
            }
        }
        throw new IllegalArgumentException("No constant with value " + value + " found");
    }
}
