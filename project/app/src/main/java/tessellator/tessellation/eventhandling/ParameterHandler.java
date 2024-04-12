package tessellator.tessellation.eventhandling;

import tessellator.tessellation.PatternCanvasCaretaker;

/**
 * An object which handles the input events of all the parameters in the
 * parameter space.
 */
public class ParameterHandler {

    private final PatternCanvasCaretaker canvasCaretaker;

    public ParameterHandler(PatternCanvasCaretaker canvasCaretaker) {
        this.canvasCaretaker = canvasCaretaker;
    }
    
    public void xTranslate(String amount) {
		double xTranslation = 0;
        
		try {
			xTranslation = Double.parseDouble(amount);
		} catch (Exception e) {}
        xTranslation = (xTranslation<0 || xTranslation>1) ? 0 : xTranslation;
        canvasCaretaker.translate(xTranslation, 0);;
    }

    public void yTranslate(String amount) {
        double yTranslation = 0;
		try {
			yTranslation = Double.parseDouble(amount);
		} catch (Exception e) {}
        yTranslation = (yTranslation<0 || yTranslation>1) ? 0 : yTranslation;
        canvasCaretaker.translate(0, yTranslation);

    }

    public void rotate(String amount) {
        double rotation = 0;
		try {
			rotation = Double.parseDouble(amount);
		} catch (Exception e) {}
        canvasCaretaker.rotate(rotation);
    }

    public void scale(String amount) {
        double scale = 1;
		try {
			scale = Double.parseDouble(amount);
            // scale defaults to one if it is negative or zero
            scale = (scale<=0) ? 1 : scale;
		} catch (Exception e) {}
        canvasCaretaker.scale(scale);
    }
}
