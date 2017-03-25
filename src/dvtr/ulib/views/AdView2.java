package dvtr.ulib.views;

import android.app.Activity;
import android.graphics.Canvas;

import com.google.ads.AdSize;
import com.google.ads.AdView;

public class AdView2 extends AdView {

	public AdView2(Activity activity, AdSize adSize, String adUnitId) {
		super(activity, adSize, adUnitId);

	}

	@Override
	protected void onDraw(Canvas canvas) {
     canvas.save();
     canvas.rotate(45,25,25);
     super.onDraw(canvas);
     canvas.restore();
	}

}
