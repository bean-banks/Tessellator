package tessellator.editor.shop;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import tessellator.editor.graph.block.category.Category;
import tessellator.editor.graph.block.factory.BlockFactory;

/**
 * The object which allows for blocks to be selected and spawned within the graph builder.
 */
public class BlockShop extends TreeView<Node> {
    
    private final BlockFactory factory;
    
    private BlockShop(TreeItem<Node> root, BlockFactory factory) {
        super(root);
        this.factory = factory;
    }

    public static BlockShop createBlockShop(BlockFactory factory) {

        TreeItem<Node> root = new TreeItem<>(new Label("Root"));
        
        TreeItem<Node> tiles = new TreeItem<>(ShopGroup.createShopGroup("Tiles"));
        TreeItem<Node> triangleTile = new TreeItem<>(ShopItem.createShopItem(Category.TRIANGLE_TILE, factory));
        TreeItem<Node> squareTile = new TreeItem<>(ShopItem.createShopItem(Category.SQUARE_TILE, factory));
        TreeItem<Node> hexagonTile = new TreeItem<>(ShopItem.createShopItem(Category.HEXAGON_TILE, factory));
        tiles.getChildren().addAll(triangleTile, squareTile, hexagonTile);

        TreeItem<Node> lines = new TreeItem<>(ShopGroup.createShopGroup("Lines"));
        TreeItem<Node> straightLine = new TreeItem<>(ShopItem.createShopItem(Category.STRAIGHT_LINE, factory));
        lines.getChildren().addAll(straightLine);

        root.getChildren().addAll(tiles, lines);

        BlockShop shop = new BlockShop(root, factory);
        shop.setShowRoot(false);
        shop.setCellFactory(tv -> new CustomTreeCell());

        shop.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

        return shop;
    }

    static class CustomTreeCell extends javafx.scene.control.TreeCell<Node> {
        
        @Override
        protected void updateItem(Node item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                setEffect(null);
                setBackground(Background.EMPTY); // Clear background
            } else {
                setText(null);
                setGraphic(item);
                setTextFill(Color.BLACK);
                setEffect(null);
                
                setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
            }
        }

        @Override
        public void updateSelected(boolean selected) {
            super.updateSelected(selected);
            setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
        }
    }

    public BlockFactory getFactory() {
        return factory;
    }
}
