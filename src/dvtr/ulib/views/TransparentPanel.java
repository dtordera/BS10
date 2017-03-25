// TransparentPanel : clase per crear un panel transparent flotant

package dvtr.ulib.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class TransparentPanel extends LinearLayout
{
	private Paint innerPaint, borderPaint; // Estils

    //Constructor
	public TransparentPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	//Constructor
	public TransparentPanel(Context context) {
		super(context);
		init();
	}

	// Iniciem els estils
	private void init() {
		innerPaint = new Paint();
		innerPaint.setARGB(200, 50, 50, 50); //gray
		innerPaint.setAntiAlias(true);

		borderPaint = new Paint();
		borderPaint.setARGB(255, 255, 255, 255);
		borderPaint.setAntiAlias(true);
		borderPaint.setStyle(Style.STROKE);
		borderPaint.setStrokeWidth(2);
	}

	// Apliquem els estils
	public void setInnerPaint(Paint innerPaint) {
		this.innerPaint = innerPaint;
	}

	public void setBorderPaint(Paint borderPaint) {
		this.borderPaint = borderPaint;
	}

	// Override del draw
    @Override
    protected void dispatchDraw(Canvas canvas) {

    	RectF drawRect = new RectF();
    	drawRect.set(0,0, getMeasuredWidth(), getMeasuredHeight());

    	canvas.drawRoundRect(drawRect, 5, 5, innerPaint);
		canvas.drawRoundRect(drawRect, 5, 5, borderPaint);

		super.dispatchDraw(canvas);
    }
}