
///******************************************************************************//
///**************************请尊重tank的成果毕竟这也是花了笔者很多时间和心思*****//
/*************************  为了让大家容易懂tank特地详细的写了很多的解释*********************************************////
///**************************欢迎访问我的博客http://www.cnblogs.com/tankaixiong/********************************************//
///***************************里面文章将持续更新！***************************************************//

package com.android.tank;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery.LayoutParams;
import android.widget.ViewSwitcher.ViewFactory;

public class Main extends Activity implements OnItemSelectedListener,
		ViewFactory, OnItemClickListener {
	private Gallery gallery;

	// 图片数组
	private int[] resIds = new int[] { R.drawable.gallery_photo_1,
			R.drawable.gallery_photo_2, R.drawable.gallery_photo_3,
			R.drawable.gallery_photo_4, R.drawable.gallery_photo_5,
			R.drawable.gallery_photo_6, R.drawable.gallery_photo_7,
			R.drawable.gallery_photo_8 };
	//自定义图片的填充方式
	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;

		public ImageAdapter(Context context) {
			mContext = context;
			TypedArray typedArray = obtainStyledAttributes(R.styleable.Gallery);
			mGalleryItemBackground = typedArray.getResourceId(
					R.styleable.Gallery_android_galleryItemBackground, 0);
		}

		// 第1点改进，返回一个很大的值，例如，Integer.MAX_VALUE
		public int getCount() {
			return resIds.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// ImageView imageView = new ImageView(mContext);
			// // // 第2点改进，通过取余来循环取得resIds数组中的图像资源ID
			// Resources re = mContext.getResources();
			// InputStream is = re.openRawResource(resIds[position]);
			// BitmapDrawable mapdraw = new BitmapDrawable(is);
			// Bitmap bitmap = mapdraw.getBitmap();
			//
			// imageView.setImageBitmap(MyImgView.createReflectedImage(bitmap));
			//
			// imageView.setLayoutParams(new Gallery.LayoutParams(80, 60));
			// // imageView.setBackgroundResource(mGalleryItemBackground);
			// return imageView;
			return composeItem(position);
		}

		public View composeItem(int position) {

			ImageView iv = new ImageView(mContext);
			iv.setImageDrawable(lvalue.get(position).icon);

			iv.setImageBitmap(MyImgView.createReflectedImage(MyImgView
					.drawableToBitmap(lvalue.get(position).icon)));//加入处理过的图片
			iv.setLayoutParams(new Gallery.LayoutParams(80, 60));

			return iv;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// 选中Gallery中某个图像时
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	// ImageSwitcher组件需要这个方法来创建一个View对象（一般为ImageView对象）
	// 来显示图像
	public View makeView() {
		ImageView imageView = new ImageView(this);
		imageView.setBackgroundColor(0xFF000000);
		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		return imageView;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 实例化launcher列表
		getLauncher();

		final CoverFlow cf = new CoverFlow(this);
		cf.setAdapter(new ImageAdapter(this));
		ImageAdapter imageAdapter = new ImageAdapter(this);
		cf.setAdapter(imageAdapter);//自定义图片的填充方式
		cf.setAnimationDuration(1500);
		cf.setOnItemClickListener(this);
		cf.setOnItemLongClickListener(lonClick);
		setContentView(cf);

	}

	// 点击某项时
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		//切换标题的字
		setTitle(((LauncherItem) lvalue.get(position)).getName());

	}

	// 长按事件，长按打开软件
	public OnItemLongClickListener lonClick = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setComponent(lvalue.get(position).component);
			startActivity(intent);
			return true;
		}
	};

	//这里保存从应用程序中获取到的信息LIST（包括图片的信息），你也可以自己定一个图片集合
	List<LauncherItem> lvalue;

	// 获得app 列表信息
	public void getLauncher() {
		lvalue = new ArrayList<LauncherItem>();

		PackageManager pkgMgt = this.getPackageManager();//这个方法是关键

		// to query all launcher & load into List<>
		Intent it = new Intent(Intent.ACTION_MAIN);
		it.addCategory(Intent.CATEGORY_LAUNCHER);

		List<ResolveInfo> ra = pkgMgt.queryIntentActivities(it, 0);//查询
		//存入集合中
		for (int i = 0; i < ra.size(); i++) {
			ActivityInfo ai = ra.get(i).activityInfo;

			// String ainfo = ai.toString();
			Drawable icon = ai.loadIcon(pkgMgt);
			String label = ai.loadLabel(pkgMgt).toString();
			ComponentName c = new ComponentName(ai.applicationInfo.packageName,
					ai.name);

			LauncherItem item = new LauncherItem(icon, label, c);

			lvalue.add(item);
		}

	}

}