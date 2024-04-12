package tessellator.editor.shop;

import javafx.scene.control.Label;

/**
 * An object that represents the title for a group of blocks within the block shop.
 * Since it doesn't represent an individual block it doesn't have a plus button.
 */
public class ShopGroup extends Label {

    private ShopGroup(String groupName) {
        super(groupName);
    }

    public static ShopGroup createShopGroup(String groupName) {
        return new ShopGroup(groupName);
    }
}
