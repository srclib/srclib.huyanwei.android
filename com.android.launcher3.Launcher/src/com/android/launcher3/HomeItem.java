
package com.android.launcher3;

import android.content.Context;  
import android.util.AttributeSet;  
import android.view.LayoutInflater;  
import android.widget.ImageView;  
import android.widget.LinearLayout;  
import android.widget.TextView;  
import android.widget.TextView;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
  
public class HomeItem extends LinearLayout {  
  
    private ImageView iv;  
    private TextView  tv;  
  
    public HomeItem(Context context) {  
        this(context, null);  
    }  
  
    public HomeItem(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        // 导入布局  
        LayoutInflater.from(context).inflate(R.layout.homeitem, this, true);  
        iv = (ImageView) findViewById(R.id.IV);  
        tv = (TextView) findViewById(R.id.TV);  
  
    }  
  
    /** 
     * 设置图片资源 
     */  
    public void setImageResource(int resId) {  
        iv.setImageResource(resId);  
    }  
  
    public void setImageDrawable(Drawable drawable) {  
        iv.setImageDrawable(drawable);  
    }  
    
    /** 
     * 设置显示的文字 
     */  
    public void setText(String text) {  
        tv.setText(text);  
    }  
    
    public void setText(CharSequence text) {  
        tv.setText(text);  
    }

	/* (non-Javadoc)
	 * @see android.view.View#onFocusChanged(boolean, int, android.graphics.Rect)
	 */
	@Override
	protected void onFocusChanged(boolean gainFocus, int direction,
			Rect previouslyFocusedRect) {
		// TODO Auto-generated method stub
		if (gainFocus) 
		{
			iv.requestFocus();
		}	
		//super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);		
	}

	/* (non-Javadoc)
	 * @see android.view.View#onFocusLost()
	 */
	@Override
	protected void onFocusLost() {
		// TODO Auto-generated method stub
		super.onFocusLost();
	}       
}  