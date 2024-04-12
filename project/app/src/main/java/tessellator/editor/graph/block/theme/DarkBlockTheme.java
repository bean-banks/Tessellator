package tessellator.editor.graph.block.theme;

import java.util.List;
import java.util.ArrayList;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;
import tessellator.editor.graph.GraphBuilder;
import tessellator.editor.graph.block.Attribute;
import tessellator.editor.graph.block.Connector;
import tessellator.editor.graph.block.Container;
import tessellator.editor.graph.block.eventhandling.BlockDragStrategies;
import tessellator.editor.graph.block.eventhandling.ConnectorDragStrategies;
import tessellator.editor.graph.edge.component.end.CircleComponent;
import tessellator.editor.graph.edge.line.shape.LineShape;
import tessellator.editor.graph.edge.line.style.LineStyle;

/**
 * An object that provides methods for constructing all of the visible components
 * that make up a block. This particular theme creates dark grey rectangular blocks
 * with clipped edges, a colourful header and blue connectors.
 * 
 * When invoking methods ensure you perform the connector and attribute constructions
 * before the other constructions. Also make sure to invoke the clean() method after
 * all constructions for a block have been made, this resets the theme so it can be used
 * to construct another block.
 * 
 * Example:
 * BlockTheme theme = new DarkBlockTheme(graphBuilder);
 * Connector in1 = theme.constructInputConnector(<<...>>);
 * Connector in2 = theme.constructInputConnector(<<...>>);
 * Attribute atr = theme.constructTextFieldAttribute(<<...>>);
 * Connector out = theme.constructOutputConnector(<<...>>);
 * Node header = theme.constructHeader(<<...>>);
 * Node title = theme.constructTitle(<<...>>);
 * Node body = theme.constructBody(<<...>>);
 * theme.clean();
 */
public class DarkBlockTheme extends BlockTheme {

	private static final Color BACKGROUND_COLOR = new Color(0.28, 0.28, 0.28, 1);
	private static final Color TEXT_COLOR = Color.FLORALWHITE;
	private static final double ARC_CONSTANT = 10;
	private static final double TITLE_SF = 1.2;
	private static final double TEXT_SF = 1.1;
	private static final double INSET = 20;

	// These are short hands to reduce code length
	private static final double h = UNIT*HEADER_HEIGHT;
	private static final double a = ARC_CONSTANT;
	private static double w = 0;

	// These fields are required to position the x layouts of components dynamically
	private static final double PADDING_BETWEEN_COMPONENTS = 25;
	private static final double PADDING_INSIDE_COMPONENTS = 10;
	private static List<Node> inputConnectorLabels = new ArrayList<>();
	private static List<Node> outputConnectorLabels = new ArrayList<>();
	private static List<Node> outputConnectorCircles = new ArrayList<>();
	private static List<Node> attributeLabels = new ArrayList<>();
	private static List<Node> attributeInputs = new ArrayList<>();
	private static final double DEFAULT_CONNECTOR_LABEL_WIDTH = 10;
	private static final double DEFAULT_ATTRIBUTE_LABEL_WIDTH = 30;
	private static final double DEFAULT_ATTRIBUTE_INPUT_WIDTH = 30;
	private static double maxAttributeInputWidth = DEFAULT_ATTRIBUTE_INPUT_WIDTH;
	
	/**
	 * This constructor supplies the theme a graph builder which is necessary for setting up
	 * the drag strategies of connectors. This method also sets the types of lines that are used
	 * within edges to be curved and spectral.
	 * 
	 * @param graphBuilder The graph builder where blocks are placed.
	 */
	public DarkBlockTheme(GraphBuilder graphBuilder) {
		super(graphBuilder);
		ConnectorDragStrategies.setLineFactory(LineShape.EASEINEASEOUT, LineStyle.SPECTRAL);
	}

	/**
	 * Construct the top brightly coloured part of the block that contains the block's title.
	 * 
	 * Note that during the construction of the header, the X layouts of connectors and attributes
	 * are set. This process is done at this stage because their X layouts need knowledge of the
	 * total number number of connectors and attributes within the block before they be set.
	 * 
	 * The zoom scale of the block's container is also set in this method.
	 * 
	 * @param headerColor The colour which will be used in the header.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return The header as a node.
	 */
	@Override
	public Node constructHeader(Color headerColor, Container container) {
		setXLayouts();
		// The scale of the container is set to the same scale as all the other containers in the graph builder
		container.setScaleX(graphBuilder.getZoomScale());
		container.setScaleY(graphBuilder.getZoomScale());
		
		Stop[] stops = new Stop[] {new Stop(0, headerColor), new Stop(1, BACKGROUND_COLOR)};
		
		SVGPath section  = new SVGPath();
		section.setContent("M0, "+h+" L0, "+a+" Q0, 0 "+a+", 0 L"+(w-a)+", 0 Q"+w+", 0 "+w+", "+a+" L"+w+", "+h+" Z");
		section.setFill(new RadialGradient(0, 0, w/2, h/2, w, false, CycleMethod.NO_CYCLE, stops));
		
		BlockDragStrategies.addDrag(section, container);
		
		return section;
	}

	/**
	 * Construct the region of the block that sits below the header and encompasses all the interactive
	 * components of the blocks such as the connectors and attributes.
	 * 
	 * @param height The height of the block in terms of the UNIT constant.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return The body as a node.
	 */
	@Override
	public Node constructBody(int height, Container container) {
		double h2 = UNIT*height+h;
		
		SVGPath section = new SVGPath();
		section.setContent("M0, "+h+" L0, "+(h2-a)+" Q0, "+h2+" "+a+", "+h2+" L"+(w-a)+", "+h2+" Q"+w+", "+h2+" "+w+", "+(h2-a)+" L"+w+", "+h+" Z");
		Stop[] stops = new Stop[] { new Stop(1, BACKGROUND_COLOR), new Stop(0, new Color(0.2, 0.2, 0.2, 1))};
		LinearGradient lg = new LinearGradient(0, h, w, h2, false, CycleMethod.NO_CYCLE, stops);
		section.setFill(lg);
		
		BlockDragStrategies.addDrag(section, container);
		
		DropShadow dropShadow = new DropShadow();
		dropShadow.setRadius(5.0);
		dropShadow.setOffsetX(3.5);
		dropShadow.setOffsetY(3.5);
		dropShadow.setColor(Color.color(0.16, 0.16, 0.16));
		container.setEffect(dropShadow);
		
		return section;
	}

	/**
	 * Construct the title of the block that sits centered within the block's header.
	 * 
	 * @param title The text that describes the geometry a block represents.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return The title as a Node.
	 */
	@Override
	public Node constructTitle(String title, Container container) {
		Text txt = new Text(title);
		txt.setFill(TEXT_COLOR);
		txt.setSmooth(true);
		txt.setScaleX(TITLE_SF);
		txt.setScaleY(TITLE_SF);
		txt.setLayoutY(((h)-4)/2+(txt.minHeight(0))/2);
		txt.setLayoutX(((w)-txt.minWidth(0))/2);
		
		BlockDragStrategies.addDrag(txt, container);
		
		return txt;
	}

	/**
	 * Construct an input connector along the left edge of the block. The connector is light
	 * blue and circular.
	 * 
	 * @param text The label that sits next to the connection point in the block.
	 * @param height The height (in terms of the UNIT constant) that the connector is placed down the block.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return All the parts of a connector packaged into a Connector object.
	 */
    @Override
	public Connector constructInputConnector(String text, int height, Container container) {
		
		// Input connectors have their X layouts set unlike the attributes and outputs connectors
		// becuase their X layouts will always be zero since they are positioned against the left
		// sedge of the block.
        Circle bigC = new CircleComponent();
		bigC.setRadius(15);
		bigC.setLayoutX(0);
		bigC.setLayoutY(height*UNIT);
		bigC.setFill(Color.TRANSPARENT);
		
		Circle smallC = new CircleComponent();
		smallC.setRadius(5);
		smallC.setLayoutX(0);
		smallC.setLayoutY(height*UNIT);
		smallC.setFill(new Color(0.5,0.5,0.5,1));
		Stop[] stops = new Stop[] {new Stop(0, Color.AQUAMARINE), new Stop(1, Color.BLACK)};
		smallC.setFill(new RadialGradient(0, 0, 0, 0, 13, false, CycleMethod.NO_CYCLE, stops));
		
		Text label = new Text(text);
		label.setFill(TEXT_COLOR);
		label.setSmooth(true);
		label.setLayoutX(INSET);
		label.setLayoutY(height*UNIT+label.minHeight(0)/4);
		label.setScaleX(TEXT_SF);
		label.setScaleY(TEXT_SF);
			
		
		BlockDragStrategies.addDrag(label, container);

		inputConnectorLabels.add(label);
		
		Connector content = new Connector(label, smallC, bigC, false);
		ConnectorDragStrategies.addDrag(content, container, graphBuilder);

		return content;
	}

	/**
	 * Construct an output connector along the right edge of the block. The connector is light
	 * blue and circular.
	 * 
	 * @param text The label that sits next to the connection point in the block.
	 * @param height The height (in terms of the UNIT constant) that the connector is placed down the block.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return All the parts of a connector packaged into a Connector object.
	 */
    @Override
	public Connector constructOutputConnector(String text, int height, Container container) {
		
		Circle bigC = new CircleComponent();
		bigC.setRadius(15);
		bigC.setLayoutY(height*UNIT);
		bigC.setFill(Color.TRANSPARENT);
		
		Circle smallC = new CircleComponent();
		smallC.setRadius(5);
		smallC.setLayoutY(height*UNIT);
		smallC.setFill(new Color(0.5,0.5,0.5,1));
		Stop[] stops = new Stop[] {new Stop(0, Color.AQUAMARINE), new Stop(1, Color.BLACK)};
		smallC.setFill(new RadialGradient(0, 0, 0, 0, 13, false, CycleMethod.NO_CYCLE, stops));
		
		
		Text label = new Text(text);
		label.setFill(TEXT_COLOR);
		label.setSmooth(true);
		label.setLayoutY(height*UNIT+label.minHeight(0)/4);
		label.setScaleX(TEXT_SF);
		label.setScaleY(TEXT_SF);
		
		BlockDragStrategies.addDrag(label, container);

		outputConnectorCircles.add(bigC);
		outputConnectorCircles.add(smallC);
		outputConnectorLabels.add(label);
		
		Connector content = new Connector(label, smallC, bigC, true);
		ConnectorDragStrategies.addDrag(content, container, graphBuilder);

		return content;
	}


	/**
	 * A text field attribute is constructed such that the text of the attribute is positioned 
	 * horizontally to the right of the input connector with the most text. The text box of the
	 * attribute is placed to the right of the attribute's text. The text box is white, has black
	 * text and blue highlights.
	 * 
	 * @param attribute The label that states what the attribute is.
	 * @param labelHelpText The text which explains in more detail what the attribute does.
	 * @param inputHelpText the text which explains what type of data the input region can take.
	 * @param height The height (in terms of the UNIT constant) that the attribute is placed down the block.
	 * @param columnCount The width if the text box.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return All the parts of an attribute packaged into an Attribute object.
	 */
	@Override
	public Attribute constructTextFieldAttribute(String attribute, String labelHelpText, String inputHelpText, int height, int columnCount, Container container) {

		Text label = new Text(attribute);
		label.setFill(TEXT_COLOR);
		label.setSmooth(true);
		label.setLayoutY(height*UNIT+label.minHeight(0)/4);
		label.setScaleX(TEXT_SF);
		label.setScaleY(TEXT_SF);
		Tooltip.install(label, new Tooltip(labelHelpText));
		
		TextField inputRegion = new TextField() {
			@Override
			public void replaceText(int start, int end, String text) {
				super.replaceText(start, end, text);
			}
			
			@Override
			public void replaceSelection(String text) {
				super.replaceSelection(text);
			}
		};

		inputRegion.setPadding(new Insets(2, 4, 2, 4));

		inputRegion.setPrefColumnCount(columnCount);
		
		inputRegion.setLayoutY(height*UNIT-12+2);
		// If the scaling is changed then the x layouts don't look quite right
		inputRegion.setScaleX(1);
		inputRegion.setScaleY(1);

		Tooltip.install(inputRegion, new Tooltip(inputHelpText));
		
		BlockDragStrategies.addDrag(label, container);

		attributeInputs.add(inputRegion);
		attributeLabels.add(label);
		//Creating an estimate for the width of the text box
		double inputWidth = columnCount*12.5;
		if (inputWidth > maxAttributeInputWidth) maxAttributeInputWidth = inputWidth; 
		
		Attribute content = new Attribute(label, inputRegion);

		// If the enter key is used then the text field loses focus, which cause the input text to be
		// propagated upwards as a value to higher level classes by the focus property listener
		inputRegion.setOnAction(a -> {
			container.requestFocus();
		});

		// When the text field looses focus, update the value in the attribute and if the block is fully
		// satisfied then notify the block the change.
		inputRegion.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				content.setValue(inputRegion.getText());
				if (container.getBlock().isSatisfied()) {
					content.notifyBlock();
				}
			}
		});

		return content;
	}

	/**
	 * A combobox attribute is constructed such that the text of the attribute is positioned 
	 * horizontally to the right of the input connector with the most text. The combobox of the
	 * attribute is placed to the right of the attribute's text. The combobox is darkgray
	 * with white text.
	 * 
	 * @param attribute The label that states what the attribute is.
	 * @param labelHelpText The text which explains in more detail what the attribute does.
	 * @param height The height (in terms of the UNIT constant) that the attribute is placed down the block.
	 * @param items The options that will be selectable within the combobox.
	 * @param container The object that encapsulates the visible parts of the block.
	 * 
	 * @return All the parts of an attribute packaged into an Attribute object.
	 */
	@Override
	public Attribute constructComboBoxAttribute(String attribute, String labelHelpText, int height, ObservableList<String> items, Container container) {
		Text label = new Text(attribute);
		label.setFill(TEXT_COLOR);
		label.setSmooth(true);
		label.setLayoutY(height*UNIT+label.minHeight(0)/4-1);
		label.setScaleX(TEXT_SF);
		label.setScaleY(TEXT_SF);
		Tooltip.install(label, new Tooltip(labelHelpText));
		
		Background background = new Background(new BackgroundFill(new Color(0.15, 0.15, 0.15, 1), new CornerRadii(2), new Insets(1)));
		
		ComboBox<String> cmb = new ComboBox<>(items);
		// Start the combo box with the first item in tbe list
		cmb.setValue(items.getFirst());
		cmb.setLayoutY(height*UNIT-12);
		cmb.setPadding(new Insets(0));
		
		cmb.setBackground(background);
		cmb.setButtonCell(new ListCell<String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setText(item);
				setTextFill(TEXT_COLOR);
				setFont(new Font(12));
				setPadding(new Insets(5));
				setBackground(background);
			}
		});
		
		cmb.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override 
			public ListCell<String> call(ListView<String> p) {
				return new ListCell<String>() {
					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						setText(item);
						setTextFill(TEXT_COLOR);
						setFont(new Font(12));
						setPadding(new Insets(5));
						
						ListView<String> lv = getListView();
						lv.setPadding(new Insets(0));
						lv.setBackground(background);
						
						// This handler scales the listview to match the scale of its container
						// Was changed from its previous version to avoid a one time visual scale offset bug
						cmb.setOnMouseClicked(me -> {
							double scaleX = container.getScaleX();
							double scaleY = container.getScaleY();

							lv.setScaleX(scaleX);
							lv.setScaleY(scaleY);
						
							double wGap = (lv.getWidth()-lv.getWidth()*scaleX)/2;
							double hGap = (lv.getHeight()-lv.getHeight()*scaleY)/2;
						
							lv.setLayoutX(-wGap);
							lv.setLayoutY(-hGap);
						});
						
						setBackground(background);
						//When the cell is hovered over change the colour of the cell
						hoverProperty().addListener((observable, oldValue, newValue) -> {
							if (newValue==true) {
								setBackground(new Background(new BackgroundFill(new Color(0.4, 0.4, 0.4, 1), new CornerRadii(2), new Insets(1))));
							}
							else {
								setBackground(background);
							}
						});
					}
				};
			}
		});
		
		BlockDragStrategies.addDrag(label, container);
		
		attributeInputs.add(cmb);
		attributeLabels.add(label);
		double longestItem = items.stream().map(s -> s.length()).max(Integer::compareTo).get().doubleValue();
		double inputWidth = longestItem*6+40;
		if (inputWidth > maxAttributeInputWidth) maxAttributeInputWidth = inputWidth;

		Attribute content = new Attribute(label, cmb);

		// Send the initial value of the combobox to its attribute
		content.setValue(cmb.getValue());

		// When value in the combobox changes, update the value in the attribute and if the block is fully
		// satisfied then notify the block of the change.
		cmb.setOnAction(a -> {
			content.setValue(cmb.getValue());
			if (container.getBlock().isSatisfied()) {
				content.notifyBlock();
			}
		});

		return content;
	}

	/**
	 * Find the widest node from a list of nodes. This is a helper method for the setXLayouts() method.
	 * 
	 * @param nodes The list of nodes that will be searched.
	 * @param backup The backup value that will be used for the return width if the nodes list is empty.
	 * @return The widest node.
	 */
	private double findMaxWidth(List<Node> nodes, double backup) {
		return nodes.stream().map(n -> n.minWidth(0)).max(Double::compareTo).orElse((Double) backup).doubleValue();
	}

	/**
	 * Set the X layouts for the attribute nodes and output connector nodes and update the field
	 * which represents the total width of the block. The input connector nodes don't need to be
	 * set because their X layouts are all zero since they are positioned on the left edge of the
	 * block.
	 * 
	 * Of the nodes that make up a block the input connector nodes and the attribute nodes are anchored
	 * from the left edge of the block, whereas the output nodes are anchored from the right edge of the block.
	 * This means that the X layouts of the attribute input regions are dependant on the layouts on the attribute labels,
	 * labels, which are dependent on the input connector layouts. Conversely the output connector nodes are
	 * dependent on the width of the block.
	 */
	private void setXLayouts() {
		double maxInputConnectorWidth = INSET+findMaxWidth(inputConnectorLabels, DEFAULT_CONNECTOR_LABEL_WIDTH)+PADDING_BETWEEN_COMPONENTS;
		// Input nodes always return 0.0 when minWidth() is performed on them
		// Instead an estimate has to be made for their width, the maxAttributeInputWidth is the estimate
		double maxAttributeWidth = findMaxWidth(attributeLabels, DEFAULT_ATTRIBUTE_LABEL_WIDTH)+PADDING_INSIDE_COMPONENTS+maxAttributeInputWidth+PADDING_BETWEEN_COMPONENTS;
		double maxOutputConnectorWidth = findMaxWidth(outputConnectorLabels, DEFAULT_CONNECTOR_LABEL_WIDTH)+INSET;
		double containerWidth = maxInputConnectorWidth+maxAttributeWidth+maxOutputConnectorWidth;

		attributeLabels.forEach(n -> n.setLayoutX(maxInputConnectorWidth));
		for (int i = 0; i < attributeInputs.size(); i++) {
			Node label = attributeLabels.get(i);
			attributeInputs.get(i).setLayoutX(label.getLayoutX()+label.minWidth(0)+PADDING_INSIDE_COMPONENTS);
		}

		outputConnectorCircles.forEach(n -> n.setLayoutX(containerWidth));
		outputConnectorLabels.forEach(n -> n.setLayoutX(containerWidth-INSET-n.minWidth(0)));
		
		w = containerWidth;
		
	}

	/**
	 * Clean the theme in preparation for constructing the comopnents of a new block.
	 * Cleaning the theme restores it to its original state prior to constructing a block.
	 */
	@Override
	public void clean() {
		w = 0;
		inputConnectorLabels.clear();
		attributeInputs.clear();
		attributeLabels.clear();
		outputConnectorCircles.clear();
		outputConnectorLabels.clear();
		maxAttributeInputWidth = DEFAULT_ATTRIBUTE_INPUT_WIDTH;
	}
}
