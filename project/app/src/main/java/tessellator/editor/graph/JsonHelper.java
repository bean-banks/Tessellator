package tessellator.editor.graph;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.shape.Shape;
import tessellator.editor.graph.block.Attribute;
import tessellator.editor.graph.block.Block;
import tessellator.editor.graph.block.Connector;
import tessellator.editor.graph.block.Container;
import tessellator.editor.graph.block.category.Category;
import tessellator.editor.graph.block.eventhandling.ConnectorDragStrategies;
import tessellator.editor.graph.block.factory.BlockFactory;
import tessellator.editor.graph.edge.CoordinatePair;
import tessellator.editor.graph.edge.Edge;
import tessellator.editor.graph.edge.component.EdgeComponent;
import tessellator.editor.graph.edge.component.end.CircleComponent;
import tessellator.editor.graph.edge.component.end.ConnectorComponent;
import tessellator.editor.graph.edge.line.LineFactory;
import tessellator.editor.preview.TileCanvasCaretaker;

/**
 * A helper object for encoding and decoding the graph builder into json.
 */
public class JsonHelper {
    
    private final GraphBuilder graphBuilder;
    
    public JsonHelper(GraphBuilder graphBuilder) {
        this.graphBuilder = graphBuilder;
    }

    /**
     * Capture content from the graph builder and produce a json string that holds
     * the content in a structured format.
     * 
     * @return A json string of the graph builder's content.
     */
    public String graphToJson() {
        // SImplify all the blocks in the graph builder
        List<SimplifiedBlock> blocks = new ArrayList<>();
        for (Node child : graphBuilder.getChildren()) {
            if (child instanceof Container) {
                Container container = (Container) child;
                blocks.add(simplifyBlock(container.getBlock()));
            }
        }
        // Simplifiy the graph builder
        SimplifiedGraphBuilder graph = new SimplifiedGraphBuilder(graphBuilder.getZoomScale(), graphBuilder.getStrokeWidthOfLines(), blocks);

        // Create an ObjectMapper instance
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // Enable pretty printing

        try {
            // Return the simplified graphbuilder as a json string
            return objectMapper.writeValueAsString(graph);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private SimplifiedBlock simplifyBlock(Block block) {
        // Construct the simplified output connectors
        List<SimplifiedOutputConnector> simplifiedOutputConnectors = new ArrayList<>();
        for (Connector connector: block.getOutputConnectors()) {
            List<SimplifiedChild> children = new ArrayList<>();
            for (Edge edge : connector.edgeEnds().stream().map(e -> e.getEdge().orElseThrow()).toList()) {
                int id = edge.inputBlock().getId();
                ConnectorComponent comp = (edge.isOutputToInput()) ? (ConnectorComponent) edge.endComponent() : (ConnectorComponent) edge.startComponent();
                int connectorIndex = edge.inputBlock().getInputConnectors().indexOf(comp.getConnector().orElseThrow());
                children.add(new SimplifiedChild(id, connectorIndex));
            }
            simplifiedOutputConnectors.add(new SimplifiedOutputConnector(children));
        }

        return new SimplifiedBlock(
            block.getId(),
            block.isRoot(),
            block.getBlockCategory().toString(),
            block.getContainer().getLayoutX(),
            block.getContainer().getLayoutY(),
            block.getAttributes().stream().map(a -> a.value()).toList(),
            simplifiedOutputConnectors
        );
    }

    /**
     * Update the graph builder with the information stored in the json string.
     * 
     * @param json The json string which represents saved contents of the graph builder.
     * @param factory The factory which creates the blocks. This allows blocks from old
     * saves to be opened with different skins/themes.
     */
    public void jsonToGraph(String json, BlockFactory factory) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Read JSON string and convert it to a simplified graph builder
            SimplifiedGraphBuilder graph = objectMapper.readValue(json, SimplifiedGraphBuilder.class);
            // Clear all the old content of the graph builder then migrate the data
            // from the simplified graph builder object to the graph builder
            graphBuilder.clearContent();

            graphBuilder.setStrokeWidthOfLines(graph.strokeWidthOfLines());
            graphBuilder.setZoomScale(graph.zoom());
            TileCanvasCaretaker.getDrawings().clear();

            // Construct all of the blocks
            List<Block> blocks = graph.blocks().stream().map(simpBlock -> factory.createBlock(Category.fromString(simpBlock.category()), 0, 0)).toList();
            // Block ids have to be freed again because when any block is created it is automatically given an new id.
            // To conteract this we make all the ids available again, then replace the ids that were automatically given
            // with the ids that were inside the json string.
            graphBuilder.freeAllBlockIds();
            // Properly set up all of the recently constructed blocks
            for (int i = 0; i < blocks.size(); i ++) {
                // Set up the general block info
                Block block = blocks.get(i);
                if (block.isRoot()) {
                    graphBuilder.setRoot(block);
                    graphBuilder.setHasRoot(true);
                }
                // Since the order isn't kept in the list of blocks, the simplified block that is related to the block has to be
                // found this way.
                SimplifiedBlock simpBlock = graph.blocks().stream().filter(b -> b.id()==block.getId()).findFirst().orElseThrow();
                block.setId(simpBlock.id());
                graphBuilder.usingBlockId(simpBlock.id());
                block.getContainer().setLayoutX(simpBlock.layoutX());
                block.getContainer().setLayoutY(simpBlock.layoutY());

                // Set up the attributes
                for (int j = 0; j < block.getAttributes().size(); j ++) {
                    Attribute attribute = block.getAttributes().get(j);
                    attribute.setBlock(block);
                    attribute.setValue(simpBlock.attributeValues().get(j));
                    Node attrRegion = attribute.attributeRegion();
                    if (attrRegion instanceof TextField) {
                        TextField attr = (TextField) attrRegion;
                        attr.setText(simpBlock.attributeValues().get(j));
                    } else if (attrRegion instanceof ComboBox) {
                        ComboBox<String> attr = (ComboBox<String>) attrRegion;
                        attr.setValue(simpBlock.attributeValues().get(j));
                    }
                }

                // Set up the edges between blocks
                for (int j = 0; j < simpBlock.outputConnectors().size(); j++) {
                    for (SimplifiedChild child : simpBlock.outputConnectors().get(j).children()) {
                        Block inputBlock = blocks.get(child.id());
                        LineFactory lineFactory = ConnectorDragStrategies.getLineFactory();

                        double outputWidth = block.getBody().minWidth(0);
                        double outputGap = (outputWidth-outputWidth*block.getContainer().getScaleX())/2;

                        double inputWidth = inputBlock.getBody().minWidth(0);
                        double inputGap = (inputWidth-inputWidth*inputBlock.getContainer().getScaleX())/2;

                        CircleComponent outputEnd = new CircleComponent();
                        Connector outputConnector = block.getOutputConnectors().get(j);
                        outputConnector.addEdgeEnd(outputEnd);
                        outputEnd.setRadius(0);
                        outputEnd.layoutXProperty().bind(outputConnector.visibleRegion().layoutXProperty().add(block.getContainer().layoutXProperty()).add(-outputGap));
                        double outputCenterHeight = block.getContainer().getBoundsInLocal().getHeight()/2;
                        double outputScaledDiff = (outputConnector.visibleRegion().layoutYProperty().get()-outputCenterHeight)*block.getContainer().getScaleY();
                        outputEnd.layoutYProperty().bind(block.getContainer().layoutYProperty().add(outputCenterHeight+outputScaledDiff));
                        outputEnd.setConnector(outputConnector);
                        graphBuilder.getChildren().add(outputEnd);

                        CircleComponent inputEnd = new CircleComponent();
                        Connector inputConnector = inputBlock.getInputConnectors().get(child.connectorIndex());
                        inputConnector.addEdgeEnd(inputEnd);
                        inputEnd.setRadius(0);
                        inputEnd.layoutXProperty().bind(inputConnector.visibleRegion().layoutXProperty().add(inputBlock.getContainer().layoutXProperty()).add(inputGap));
                        double inputCenterHeight = inputBlock.getContainer().getBoundsInLocal().getHeight()/2;
                        double inputScaledDiff = (inputConnector.visibleRegion().layoutYProperty().get()-inputCenterHeight)*inputBlock.getContainer().getScaleY();
                        inputEnd.layoutYProperty().bind(inputBlock.getContainer().layoutYProperty().add(inputCenterHeight+inputScaledDiff));
                        inputEnd.setConnector(inputConnector);
                        graphBuilder.getChildren().add(inputEnd);

                        CoordinatePair cp = new CoordinatePair(outputEnd.layoutXProperty(), outputEnd.layoutYProperty(), inputEnd.layoutXProperty(), inputEnd.layoutYProperty());
                        Shape line = (Shape) lineFactory.createLine(cp);
                        line.setStrokeWidth(graphBuilder.getStrokeWidthOfLines());
                        graphBuilder.getChildren().add(line);
                        line.toBack();
                        
                        new Edge(outputEnd, inputEnd, (EdgeComponent) line, inputBlock, block, true);
                    }
                }

            }

            // Make sure blocks that should be satisfied are satisfied
            // and draw all the satisfied blocks
            if (graphBuilder.hasRoot()) graphBuilder.getRoot().orElseThrow().satisfied();
            TileCanvasCaretaker.applyDrawingsToCanvas();

        } catch (Exception e) {
            System.out.println("The contents of the provided file are faulty.");
        }
    }

    private record SimplifiedChild(
        int id,
        int connectorIndex
    ){}

    private record SimplifiedOutputConnector(
        List<SimplifiedChild> children
    ){}

    private record SimplifiedBlock(
        int id,
        boolean isRoot,
        String category,
        double layoutX,
        double layoutY,
        List<String> attributeValues,
        List<SimplifiedOutputConnector> outputConnectors
    ) {}

    private record SimplifiedGraphBuilder(
        double zoom,
        double strokeWidthOfLines,
        List<SimplifiedBlock> blocks

    ){}
}
