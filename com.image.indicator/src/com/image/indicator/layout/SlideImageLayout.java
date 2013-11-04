package com.image.indicator.layout;

import java.util.ArrayList;

import com.image.indicator.R;
import com.image.indicator.parser.NewsXmlParser;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

/**
 * 生成滑动图片区域布局
 * @Description: 生成滑动图片区域布局

 * @File: SlideImageLayout.java

 * @Package com.image.indicator.layout

 * @Author Hanyonglu

 * @Date 2012-6-18 上午09:04:14

 * @Version V1.0
 */
public class SlideImageLayout {
	// 包含图片的ArrayList
	private ArrayList<ImageView> mImageList = null;
	private Context mContext = null;
	// 圆点图片集合
	private ImageView[] mImageViews = null; 
	private ImageView mImageView = null;
	private NewsXmlParser mParser = null;
	// 表示当前滑动图片的索引
	private int pageIndex = 0;
	
	public SlideImageLayout(Context context) {
		this.mContext = context;
		mImageList = new ArrayList<ImageView>();
		mParser = new NewsXmlParser();
	}
	
	/**
	 * 生成滑动图片区域布局
	 * @param id
	 * @return
	 */
	public View getSlideImageLayout(int id){
		// 包含TextView的LinearLayout
		LinearLayout imageLinerLayout = new LinearLayout(mContext);
		LinearLayout.LayoutParams imageLinerLayoutParames = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT,
				1);
		
		ImageView iv = new ImageView(mContext);
		iv.setBackgroundResource(id);
		iv.setOnClickListener(new ImageOnClickListener());
		imageLinerLayout.addView(iv,imageLinerLayoutParames);
		mImageList.add(iv);
		
		return imageLinerLayout;
	}
	
	/**
	 * 获取LinearLayout
	 * @param view
	 * @param width
	 * @param height
	 * @return
	 */
	public View getLinearLayout(View view,int width,int height){
		LinearLayout linerLayout = new LinearLayout(mContext);
		LinearLayout.LayoutParams linerLayoutParames = new LinearLayout.LayoutParams(
				width, 
				height,
				1);
		// 这里最好也自定义设置，有兴趣的自己设置。
		linerLayout.setPadding(10, 0, 10, 0);
		linerLayout.addView(view, linerLayoutParames);
		
		return linerLayout;
	}
	
	/**
	 * 设置圆点个数
	 * @param size
	 */
	public void setCircleImageLayout(int size){
		mImageViews = new ImageView[size];
	}
	
	/**
	 * 生成圆点图片区域布局对象
	 * @param index
	 * @return
	 */
	public ImageView getCircleImageLayout(int index){
		mImageView = new ImageView(mContext);  
		mImageView.setLayoutParams(new LayoutParams(10,10));
        mImageView.setScaleType(ScaleType.FIT_XY);
        
        mImageViews[index] = mImageView;
         
        if (index == 0) {  
            //默认选中第一张图片
            mImageViews[index].setBackgroundResource(R.drawable.dot_selected);  
        } else {  
            mImageViews[index].setBackgroundResource(R.drawable.dot_none);  
        }  
         
        return mImageViews[index];
	}
	
	/**
	 * 设置当前滑动图片的索引
	 * @param index
	 */
	public void setPageIndex(int index){
		pageIndex = index;
	}
	
	// 滑动页面点击事件监听器
    private class ImageOnClickListener implements OnClickListener{
    	@Override
    	public void onClick(View v) {
    		Toast.makeText(mContext, mParser.getSlideTitles()[pageIndex], Toast.LENGTH_SHORT).show();
    		Toast.makeText(mContext, mParser.getSlideUrls()[pageIndex], Toast.LENGTH_SHORT).show();
    	}
    }
}
