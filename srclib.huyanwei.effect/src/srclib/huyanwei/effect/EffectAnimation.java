package srclib.huyanwei.effect;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class EffectAnimation extends Animation{
	
	private Camera mCamera;
	private Matrix mMatrix;
	
	public EffectAnimation()
	{
		
	}
	
	@Override
	public void initialize(int width, int height, int parentWidth,
			int parentHeight) {
		// TODO Auto-generated method stub
		super.initialize(width, height, parentWidth, parentHeight);
		
		mCamera = new Camera();		
		mMatrix = new Matrix();
		
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		// TODO Auto-generated method stub
		//super.applyTransformation(interpolatedTime, t);
		
		t.setAlpha((interpolatedTime));
		
		mMatrix = t.getMatrix();		
		mCamera.save();		
		mCamera.translate(0, 0, 300*(1-interpolatedTime));
		mCamera.rotateY(360*interpolatedTime);
		mCamera.getMatrix(mMatrix);
		mCamera.restore();
		//mMatrix.preRotate(360*interpolatedTime);
		mMatrix.preTranslate(-540/2, -888/2);
		mMatrix.postTranslate(540/2, 888/2);		
		
	}
}
