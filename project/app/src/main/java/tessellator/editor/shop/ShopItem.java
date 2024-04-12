package tessellator.editor.shop;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import tessellator.editor.graph.GraphBuilder;
import tessellator.editor.graph.block.category.Category;
import tessellator.editor.graph.block.factory.BlockFactory;

/**
 * An object which represents a block which can be selected and spawned within the grap buidler.
 */
public class ShopItem extends HBox {
    
    private final Category productCategory;
    private final BlockFactory factory;

    private ShopItem(Category blockCat, BlockFactory factory) {
        productCategory = blockCat;
        this.factory = factory;
    }

    public static ShopItem createShopItem(Category blockCat, BlockFactory factory) {
        ShopItem item = new ShopItem(blockCat, factory);
        Node producerButton = item.blockProducerButton();
        Node productLabel = item.productLabel();
        item.getChildren().addAll(producerButton, productLabel);
        item.setPadding(new Insets(0));
        return item;
    }

    private Node blockProducerButton() {
        Button addButton = new Button("+");
        addButton.setPadding(new Insets(0,4,0,4));
        addButton.setMinSize(20, 20);
        addButton.setMaxSize(20, 20);
        addButton.setScaleX(0.8);
        addButton.setScaleY(0.8);
        HBox.setMargin(addButton, new Insets(0, 2, 0, 0));

        addButton.setOnMouseClicked(me -> {
            GraphBuilder graphBuilder = factory.getGraphBuilder();
            double spawnX = graphBuilder.getWidth()/2;
            double spawnY = graphBuilder.getHeight()/2;
            factory.createBlock(productCategory, spawnX, spawnY);
        });

        return addButton;
    }

    private Node productLabel() {
        return new Label(productCategory.toString());
    }
}
