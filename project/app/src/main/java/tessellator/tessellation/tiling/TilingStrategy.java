package tessellator.tessellation.tiling;

import java.util.List;

/**
 * An interface that provides a template for tile strategies to follow.
 */
public interface TilingStrategy {

    public void setCanvasDimensions(double width, double height);

    public List<Tile> tessellate();

    public void setXTranslation(double amount);

    public void setYTranslation(double amount);

    public void setRotation(double amount);

    public void setScale(double amount);
}
