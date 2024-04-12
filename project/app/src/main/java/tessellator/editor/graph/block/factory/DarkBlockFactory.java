package tessellator.editor.graph.block.factory;

import tessellator.editor.graph.GraphBuilder;
import tessellator.editor.graph.block.theme.DarkBlockTheme;

/**
 * This block factory produces dark themed blocks.
 */
public class DarkBlockFactory extends BlockFactory{
    
	public DarkBlockFactory(GraphBuilder graphBuilder) { 
		super(new DarkBlockTheme(graphBuilder));
	}
}
