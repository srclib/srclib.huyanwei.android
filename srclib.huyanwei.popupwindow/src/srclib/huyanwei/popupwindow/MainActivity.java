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
	
	//自定义的弹出框类
	private SelectPicPopupWindow menuWindow ;
	
	private View.OnClickListener  mOnClickListener = new View.OnClickListener()
	{
        public void onClick(View v) {
            //实例化SelectPicPopupWindow
            menuWindow = new SelectPicPopupWindow(MainActivity.this, itemsOnClick);
            //显示窗口
            menuWindow.showAtLocation(MainActivity.this.findViewById(R.id.main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
        }
	};
	
	
	//为弹出窗口实现监听类
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
		
		//把文字控件添加监听，点击弹出自定义窗口
		tv.setOnClickListener(mOnClickListener);		    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
