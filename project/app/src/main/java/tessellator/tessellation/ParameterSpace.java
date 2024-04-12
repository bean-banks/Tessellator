package tessellator.tessellation;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tessellator.tessellation.eventhandling.ParameterHandler;

/**
 * An object which holds and displays all of the transformation parameters
 * of the tessellation window.
 */
public class ParameterSpace extends BorderPane {

    private static final int INPUT_LABEL_SPACING = 4;
    private static final int PARAM_OPTION_SPACING = 6;
    private static final int PARAM_SPACING = 30;
    private static final Insets INPUT_PADDING = new Insets(2, 4, 2, 4);

    private HBox container;
    private final ParameterHandler paramHandler;

    public ParameterSpace(PatternCanvasCaretaker canvasCaretaker) {
        // Set up the parameter handler
        paramHandler = new ParameterHandler(canvasCaretaker);

        // Create the main horizontal list container
        container = new HBox();
        container.setSpacing(PARAM_SPACING);
        container.setPadding(new Insets(6));
        setTop(container);

        constructTranslationParams();
        constructRotationParam();
        constructScaleParam();
        

    }

    private void constructTranslationParams() {

        VBox translationParams = new VBox();
        translationParams.setSpacing(PARAM_OPTION_SPACING);
        container.getChildren().add(translationParams);

        HBox xTranslation = new HBox();
        xTranslation.setSpacing(INPUT_LABEL_SPACING);
        translationParams.getChildren().add(xTranslation);
        Label xTranslateLabel = new Label("X Translation");
        TextField xTranslateInput = new TextField();
        Tooltip.install(xTranslateLabel, new Tooltip("Translate the pattern horizontally \nby up to 1 tile"));
        Tooltip.install(xTranslateInput, new Tooltip("Enter a value between 0 and 1"));

		xTranslateInput.setOnAction(a -> {
			container.requestFocus();
		});
		xTranslateInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				paramHandler.xTranslate(xTranslateInput.getText());
			}
		});

        xTranslateInput.setPrefColumnCount(4);
        xTranslateInput.setPadding(INPUT_PADDING);
        xTranslation.getChildren().addAll(xTranslateLabel, xTranslateInput);

        HBox yTranslation = new HBox();
        yTranslation.setSpacing(INPUT_LABEL_SPACING);
        translationParams.getChildren().add(yTranslation);
        Label yTranslateLabel = new Label("Y Translation");
        TextField yTranslateInput = new TextField();
        Tooltip.install(yTranslateLabel, new Tooltip("Translate the pattern vertically \n by up to 1 tile"));
        Tooltip.install(yTranslateInput, new Tooltip("Enter a value between 0 and 1"));

		yTranslateInput.setOnAction(a -> {
			container.requestFocus();
		});
		yTranslateInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				paramHandler.yTranslate(yTranslateInput.getText());
			}
		});

        yTranslateInput.setPrefColumnCount(4);
        yTranslateInput.setPadding(INPUT_PADDING);
        yTranslation.getChildren().addAll(yTranslateLabel, yTranslateInput);
    }

    private void constructRotationParam() {

        VBox rotationParams = new VBox();
        rotationParams.setSpacing(PARAM_OPTION_SPACING);
        container.getChildren().add(rotationParams);

        HBox rotation = new HBox();
        rotation.setSpacing(INPUT_LABEL_SPACING);
        rotationParams.getChildren().add(rotation);
        Label rotateLabel = new Label("Rotation");
        TextField rotateInput = new TextField();
        Tooltip.install(rotateLabel, new Tooltip("Rotate the pattern clockwise in degrees"));
        Tooltip.install(rotateInput, new Tooltip("Enter a numeric value"));

		rotateInput.setOnAction(a -> {
			container.requestFocus();
		});
		rotateInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				paramHandler.rotate(rotateInput.getText());
			}
		});

        rotateInput.setPrefColumnCount(3);
        rotateInput.setPadding(INPUT_PADDING);
        rotation.getChildren().addAll(rotateLabel, rotateInput);
    }

    private void constructScaleParam() {

        VBox scaleParams = new VBox();
        scaleParams.setSpacing(PARAM_OPTION_SPACING);
        container.getChildren().add(scaleParams);

        HBox scale = new HBox();
        scale.setSpacing(INPUT_LABEL_SPACING);
        scaleParams.getChildren().add(scale);
        Label scaleLabel = new Label("Scale");
        TextField scaleInput = new TextField();
        Tooltip.install(scaleLabel, new Tooltip("Scale the tiles in the pattern"));
        Tooltip.install(scaleInput, new Tooltip("Enter a positive numeric value"));

		scaleInput.setOnAction(a -> {
			container.requestFocus();
		});
		scaleInput.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				paramHandler.scale(scaleInput.getText());
			}
		});

        scaleInput.setPrefColumnCount(2);
        scaleInput.setPadding(INPUT_PADDING);
        scale.getChildren().addAll(scaleLabel, scaleInput);
    }
}
