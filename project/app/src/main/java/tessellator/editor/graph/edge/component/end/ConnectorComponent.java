package tessellator.editor.graph.edge.component.end;

import java.util.Optional;

import tessellator.editor.graph.block.Connector;

/**
 * An interface which enables visual components of a connector to communicate
 * with their conncetor.
 */
public interface ConnectorComponent {
    
    public void setConnector(Connector connector);

    public Optional<Connector> getConnector();
}
