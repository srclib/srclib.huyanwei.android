package srclib.huyanwei.popupwindow;

import android.os.Bundle;
import android.app.Activity;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class MainActivity extends Activity {

	private TextView tv ;
	
	//�Զ���ĵ�������
	private SelectPicPopupWindow menuWindow ;
	
	private View.OnClickListener  mOnClickListener = new View.OnClickListener()
	{
        public void onClick(View v) {
            //ʵ����SelectPicPopupWindow
            menuWindow = new SelectPicPopupWindow(MainActivity.this, itemsOnClick);
            //��ʾ����
            menuWindow.showAtLocation(MainActivity.this.findViewById(R.id.main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //����layout��PopupWindow����ʾ��λ��
        }
	};
	
	
	//Ϊ��������ʵ�ּ�����
	private OnClickListener  itemsOnClick = new OnClickListener()
	{
	    		public void onClick(View v) {
	            menuWindow.dismiss();
	            switch (v.getId()) {
	            case R.id.btn_take_photo:
	                break;
	            case R.id.btn_pick_photo:              
	                break;
	            default:
	                break;
	            }
	        }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		tv = (TextView) this.findViewById(R.id.click);
		
		//�����ֿؼ���Ӽ�������������Զ��崰��
		tv.setOnClickListener(mOnClickListener);		    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
