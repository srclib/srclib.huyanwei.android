package srclib.huyanwei.CameraView;

import android.view.animation.Animation;

import android.view.animation.Animation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.graphics.Camera;
import android.graphics.Matrix;

public class Translate3dAnimation extends Animation {	    
	    private final float mStepX;    
	    private final float mStepY;        
	    private final float mStepZ;

	    private Camera mCamera;
	    
	    public Translate3dAnimation(
	    		float stepX,float stepY,float stepZ) {
	        mStepX = stepX;    
	        mStepY = stepY;
	        mStepZ = stepZ;
	    }

	    @Override
	    public void initialize(int width, int height, int parentWidth, int parentHeight) {
	        super.initialize(width, height, parentWidth, parentHeight);
	        mCamera = new Camera();
	    }

	    @Override
	    protected void applyTransformation(float interpolatedTime, Transformation t) {
	        final Camera camera = mCamera;
	        final Matrix matrix = t.getMatrix();
	        camera.save();
        	camera.translate(mStepX,mStepY, mStepZ);	        
	        camera.getMatrix(matrix);
	        camera.restore();	        
	        
	    }	
}
