package srclib.huyanwei.compass;

import java.util.Locale;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Compass extends Activity {
	
    private final float MAX_ROATE_DEGREE = 1.0f;
    private SensorManager mSensorManager;
    private Sensor mOrientationSensor;
    private LocationManager mLocationManager;
    private String mLocationProvider;
    private float mDirection;
    private float mTargetDirection;
    private AccelerateInterpolator mInterpolator;
    protected final Handler mHandler = new Handler();
    private boolean mStopDrawing;
    private boolean mChinease;
    
    View mCompassView;
    CompassView mPointer;
    LinearLayout mDirectionLayout;
    LinearLayout mAngleLayout;

    private Button mBtOk;
    private Button mBtFailed;
    
    private SharedPreferences mSp; // Factory
    
    View.OnClickListener mOnClickListener = new  View.OnClickListener()
    {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// Factory
			/*
            Utils.SetPreferences(getApplicationContext(), mSp, R.string.msensor_name,
                    (v.getId() == mBtOk.getId()) ? AppDefine.FT_SUCCESS : AppDefine.FT_FAILED);
            */
            finish();
		}    	
    };
    
    protected Runnable mCompassViewUpdater = new Runnable() {
        @Override
        public void run() {
            if (mPointer != null && !mStopDrawing) {
                if (mDirection != mTargetDirection) {

                    // calculate the short routine
                    float to = mTargetDirection;
                    if (to - mDirection > 180) {
                        to -= 360;
                    } else if (to - mDirection < -180) {
                        to += 360;
                    }

                    // limit the max speed to MAX_ROTATE_DEGREE
                    float distance = to - mDirection;
                    if (Math.abs(distance) > MAX_ROATE_DEGREE) {
                        distance = distance > 0 ? MAX_ROATE_DEGREE : (-1.0f * MAX_ROATE_DEGREE);
                    }

                    // need to slow down if the distance is short
                    mDirection = normalizeDegree(mDirection
                            + ((to - mDirection) * mInterpolator.getInterpolation(Math
                                    .abs(distance) > MAX_ROATE_DEGREE ? 0.4f : 0.3f)));
                    mPointer.updateDirection(mDirection);
                }

                updateDirection();

                mHandler.postDelayed(mCompassViewUpdater, 20);
            }
        }
    };
    
    private void updateDirection() {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        mDirectionLayout.removeAllViews();
        mAngleLayout.removeAllViews();

        ImageView east = null;
        ImageView west = null;
        ImageView south = null;
        ImageView north = null;
        float direction = normalizeDegree(mTargetDirection * -1.0f);
        if (direction > 22.5f && direction < 157.5f) {
            // east
            east = new ImageView(this);
            east.setImageResource(mChinease ? R.drawable.e_cn : R.drawable.e);
            east.setLayoutParams(lp);
        } else if (direction > 202.5f && direction < 337.5f) {
            // west
            west = new ImageView(this);
            west.setImageResource(mChinease ? R.drawable.w_cn : R.drawable.w);
            west.setLayoutParams(lp);
        }

        if (direction > 112.5f && direction < 247.5f) {
            // south
            south = new ImageView(this);
            south.setImageResource(mChinease ? R.drawable.s_cn : R.drawable.s);
            south.setLayoutParams(lp);
        } else if (direction < 67.5 || direction > 292.5f) {
            // north
            north = new ImageView(this);
            north.setImageResource(mChinease ? R.drawable.n_cn : R.drawable.n);
            north.setLayoutParams(lp);
        }

        if (mChinease) {
            // east/west should be before north/south
            if (east != null) {
                mDirectionLayout.addView(east);
            }
            if (west != null) {
                mDirectionLayout.addView(west);
            }
            if (south != null) {
                mDirectionLayout.addView(south);
            }
            if (north != null) {
                mDirectionLayout.addView(north);
            }
        } else {
            // north/south should be before east/west
            if (south != null) {
                mDirectionLayout.addView(south);
            }
            if (north != null) {
                mDirectionLayout.addView(north);
            }
            if (east != null) {
                mDirectionLayout.addView(east);
            }
            if (west != null) {
                mDirectionLayout.addView(west);
            }
        }

        int direction2 = (int) direction;
        boolean show = false;
        if (direction2 >= 100) {
            mAngleLayout.addView(getNumberImage(direction2 / 100));
            direction2 %= 100;
            show = true;
        }
        if (direction2 >= 10 || show) {
            mAngleLayout.addView(getNumberImage(direction2 / 10));
            direction2 %= 10;
        }
        mAngleLayout.addView(getNumberImage(direction2));

        ImageView degreeImageView = new ImageView(this);
        degreeImageView.setImageResource(R.drawable.degree);
        degreeImageView.setLayoutParams(lp);
        mAngleLayout.addView(degreeImageView);
    }

    private ImageView getNumberImage(int number) {
        ImageView image = new ImageView(this);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        switch (number) {
            case 0:
                image.setImageResource(R.drawable.number_0);
                break;
            case 1:
                image.setImageResource(R.drawable.number_1);
                break;
            case 2:
                image.setImageResource(R.drawable.number_2);
                break;
            case 3:
                image.setImageResource(R.drawable.number_3);
                break;
            case 4:
                image.setImageResource(R.drawable.number_4);
                break;
            case 5:
                image.setImageResource(R.drawable.number_5);
                break;
            case 6:
                image.setImageResource(R.drawable.number_6);
                break;
            case 7:
                image.setImageResource(R.drawable.number_7);
                break;
            case 8:
                image.setImageResource(R.drawable.number_8);
                break;
            case 9:
                image.setImageResource(R.drawable.number_9);
                break;
        }
        image.setLayoutParams(lp);
        return image;
    }
    
    
    private SensorEventListener mOrientationSensorEventListener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent event) {
            float direction = event.values[0] * -1.0f;
            mTargetDirection = normalizeDegree(direction);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    
    private float normalizeDegree(float degree) {
        return (degree + 720) % 360;
    }
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.compass);
		
		mSp = getSharedPreferences("FactoryMode", Context.MODE_PRIVATE); // Factory
		
		// init
        mDirection = 0.0f;
        mTargetDirection = 0.0f;
        mInterpolator = new AccelerateInterpolator();
        mStopDrawing = true;
                
        mChinease = TextUtils.equals(Locale.getDefault().getLanguage(), "zh");
        
        mCompassView = findViewById(R.id.view_compass);
        mPointer = (CompassView) findViewById(R.id.compass_pointer);
        mDirectionLayout = (LinearLayout) findViewById(R.id.layout_direction);
        mAngleLayout = (LinearLayout) findViewById(R.id.layout_angle);

        mBtOk 	 	= (Button) findViewById(R.id.bt_ok);
		mBtFailed 	= (Button) findViewById(R.id.bt_fail);   
		
		mBtOk.setOnClickListener(mOnClickListener);
		mBtFailed.setOnClickListener(mOnClickListener);
		
        mPointer.setImageResource(mChinease ? R.drawable.compass_cn : R.drawable.compass);
        
        // sensor manager
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.		
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
        mStopDrawing = true;
        if (mOrientationSensor != null) {
            mSensorManager.unregisterListener(mOrientationSensorEventListener);
        }
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
        if (mOrientationSensor != null) {
            mSensorManager.registerListener(mOrientationSensorEventListener, mOrientationSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }
        mStopDrawing = false;
        mHandler.postDelayed(mCompassViewUpdater, 20);
		super.onResume();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
}
