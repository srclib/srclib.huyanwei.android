package srclib.huyanwei.CameraView;

import android.view.animation.Animation;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.graphics.Camera;
import android.graphics.Matrix;

/**
 * An animation that rotates the view on the Y axis between two specified angles.
 * This animation also adds a translation on the Z axis (depth) to improve the effect.
 */
public class Rotate3dAnimation extends Animation {
    private final float mFromDegrees;
    private final float mToDegrees;
    private final float mCenterX;
    private final float mCenterY;
    private final float mStepX;    
    private final float mStepY;        
    private final float mStepZ;
    private final boolean mReverse;
    private Camera mCamera;
    
    /**
     * Creates a new 3D rotation on the Y axis. The rotation is defined by its
     * start angle and its end angle. Both angles are in degrees. The rotation
     * is performed around a center point on the 2D space, definied by a pair
     * of X and Y coordinates, called centerX and centerY. When the animation
     * starts, a translation on the Z axis (depth) is performed. The length
     * of the translation can be specified, as well as whether the translation
     * should be reversed in time.
     *
     * @param fromDegrees the start angle of the 3D rotation
     * @param toDegrees the end angle of the 3D rotation
     * @param centerX the X center of the 3D rotation
     * @param centerY the Y center of the 3D rotation
     * @param reverse true if the translation should be reversed, false otherwise
     */
    public Rotate3dAnimation(float fromDegrees, float toDegrees,
    		float centerX, float centerY,
    		float stepX,float stepY,float stepZ,
    		boolean reverse) {
        mFromDegrees = fromDegrees;
        mToDegrees = toDegrees;
        
        mCenterX = centerX;
        mCenterY = centerY;
        
        mStepX = stepX;    
        mStepY = stepY;
        mStepZ = stepZ; 
        mReverse = reverse;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        final float fromDegrees = mFromDegrees;
        final float fromX = 0;
        
        float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

        final float centerX = mCenterX;
        final float centerY = mCenterY;
        
        final Camera camera = mCamera;

        /*
        final Matrix matrix = t.getMatrix();

        camera.save();

        if (mReverse) {
            camera.translate(0-interpolatedTime*200, 0.0f, mDepthZ * interpolatedTime);
        } else {
            camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
        }
        
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
        */
        
        final Matrix matrix = t.getMatrix();

        camera.save();

        if (mReverse) {
            camera.translate(mStepX, mStepY, mStepZ);        	
        } else {
            camera.translate(-mStepX, -mStepY, -mStepZ);
        }
        
        //camera.rotateY(degrees);
        camera.rotateX(degrees);
        camera.getMatrix(matrix);
        camera.restore();

        //matrix.preTranslate(-centerX, -centerY);
        //matrix.postTranslate(centerX, centerY);                
        
    }
}
