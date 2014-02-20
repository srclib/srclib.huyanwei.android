package srclib.huyanwei.flipper;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;


public class MainActivity extends Activity implements OnGestureListener{

    // ViewFlipperʵ��
    ViewFlipper flipper;
    // �������Ƽ����ʵ��
    GestureDetector detector;
    //����һ���������飬����ΪViewFlipperָ���л�����Ч��
    Animation[] animations = new Animation[4];
    //�������ƶ�������֮�����С����
    final int FLIP_DISTANCE = 50;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//�������Ƽ����
		         detector = new GestureDetector(this);

		      // ���ViewFlipperʵ��

		         flipper = (ViewFlipper)findViewById(R.id.flipper);

		      // ΪViewFlipper���5��ImageView���
		         flipper.addView(addImageView(R.drawable.ic_1));
		         flipper.addView(addImageView(R.drawable.ic_2));
		         flipper.addView(addImageView(R.drawable.ic_3));
		         flipper.addView(addImageView(R.drawable.ic_4));
		         flipper.addView(addImageView(R.drawable.ic_2));
		         //��ʼ��Animation����
		         animations[0] = AnimationUtils.loadAnimation(this,R.anim.left_in);
		         animations[1] = AnimationUtils.loadAnimation(this,R.anim.left_out);
		         animations[2] = AnimationUtils.loadAnimation(this,R.anim.right_in);
		         animations[3] = AnimationUtils.loadAnimation(this,R.anim.right_in);
	}

    // �������ImageView�Ĺ��߷���
    private View addImageView(int resId){
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(resId);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent event1, MotionEvent event2, float velocityX,
			float velocityY) {
		// TODO Auto-generated method stub
		/*
		          * �����һ�������¼���X������ڵڶ��������¼���X���곬��FLIP_DISTANCE
		          * Ҳ�������ƴ������󻬡�
		 */
		         if (event1.getX() - event2.getX() > FLIP_DISTANCE)
		         {
		             // Ϊflipper�����л��ĵĶ���Ч��
		             flipper.setInAnimation(animations[0]);
		             flipper.setOutAnimation(animations[1]);
		             flipper.showPrevious();
		             return true;
		         }
		         /*

		          * ����ڶ��������¼���X������ڵ�һ�������¼���X���곬��FLIP_DISTANCE
		          * Ҳ�������ƴ������󻬡�
		 */

		         else if (event2.getX() - event1.getX() > FLIP_DISTANCE)
		         {
		             // Ϊflipper�����л��ĵĶ���Ч��
		             flipper.setInAnimation(animations[2]);
		             flipper.setOutAnimation(animations[3]);
		             flipper.showNext();
		             return true;
		         }
		         return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	     public boolean onTouchEvent(MotionEvent event) {
	         //����Activity�ϵĴ����¼�����GestureDetector����
	         return detector.onTouchEvent(event);
	     }
}
