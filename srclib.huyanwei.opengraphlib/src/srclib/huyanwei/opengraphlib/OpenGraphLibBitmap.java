package srclib.huyanwei.opengraphlib;

import java.io.InputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class OpenGraphLibBitmap {
	public static final int mCount = 6;
	//public static final int mCount = 18;
	public static final int mStyle = 1;
	public static Bitmap mBitmap[] = new Bitmap[mCount];
	
	// width and height must be 2^N
	public static int pow2(int size)
	{
		// log = log10 
	    int small = (int)(Math.log((double)size)/Math.log(2.0f)) ;
	    if ( (1 << small) >= size)
	        return 1 << small;
	    else
	        return 1 << (small + 1);
	}
	
    public static Bitmap convertDrawableToBitmap(Drawable drawable) 
    {
              int width = drawable.getIntrinsicWidth();   
              int height = drawable.getIntrinsicHeight();
              Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;
              Bitmap bitmap = Bitmap.createBitmap(width, height, config);  
              Canvas canvas = new Canvas(bitmap);      
              drawable.setBounds(0, 0, width, height);
              drawable.draw(canvas);    
              return bitmap;
    }

     public static Drawable zoomDrawable(Drawable drawable, int w, int h)
     {
               int width = drawable.getIntrinsicWidth();
               int height= drawable.getIntrinsicHeight();
               Bitmap oldbmp = convertDrawableToBitmap(drawable); 
               Matrix matrix = new Matrix();              
               float scaleWidth = ((float)w / width);    
               float scaleHeight = ((float)h / height);
               matrix.postScale(scaleWidth, scaleHeight); 
               Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);   
               return new BitmapDrawable(newbmp);  
     }

    public static Drawable zoomDrawableWithBackground(Drawable drawable,Drawable bg_drawable,int w, int h)
    {
              int bg_width = bg_drawable.getIntrinsicWidth();
              int bg_height = bg_drawable.getIntrinsicHeight();
              Bitmap.Config config = bg_drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888:Bitmap.Config.RGB_565;  
              Bitmap new_bg_bmp = Bitmap.createBitmap(bg_width, bg_height, config);  
              Canvas canvas = new Canvas(new_bg_bmp);      

              Bitmap bg_bmp = convertDrawableToBitmap(bg_drawable);
              canvas.drawBitmap(bg_bmp,0,0,null);

              int width = drawable.getIntrinsicWidth();
              int height= drawable.getIntrinsicHeight();
              Bitmap oldbmp = convertDrawableToBitmap(drawable); 
              Matrix matrix = new Matrix();              
              float scaleWidth = ((float)w / width);    
              float scaleHeight = ((float)h / height);
              matrix.postScale(scaleWidth, scaleHeight); 
              Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);   
              canvas.drawBitmap(newbmp,(bg_width-width)>>1,(bg_height-height)>>1,null);
              return new BitmapDrawable(new_bg_bmp);  
    }
	
	public static void load(Resources resources) {
		int i = 0 ;
		if( mStyle  == 1)
		{	
			for(i = 0 ; i< mCount ; i++ )
			{
				mBitmap[i] = BitmapFactory.decodeResource(resources, R.drawable.mm1+i);
			}
			/*
			mBitmap[0] = BitmapFactory.decodeResource(resources, R.drawable.mm1);
			mBitmap[1] = BitmapFactory.decodeResource(resources, R.drawable.mm2);
			mBitmap[2] = BitmapFactory.decodeResource(resources, R.drawable.mm3);
			mBitmap[3] = BitmapFactory.decodeResource(resources, R.drawable.mm4);
			mBitmap[4] = BitmapFactory.decodeResource(resources, R.drawable.mm5);
			mBitmap[5] = BitmapFactory.decodeResource(resources, R.drawable.mm6);
			*/
		}
		else if(mStyle == 2)
		{
			for(i = 0 ; i< mCount ; i++ )
			{
				mBitmap[i] = convertDrawableToBitmap(resources.getDrawable(R.drawable.mm1+i));
			}
			/*
			mBitmap[0] = convertDrawableToBitmap(resources.getDrawable(R.drawable.mm1));
			mBitmap[1] = convertDrawableToBitmap(resources.getDrawable(R.drawable.mm2));
			mBitmap[2] = convertDrawableToBitmap(resources.getDrawable(R.drawable.mm3));
			mBitmap[3] = convertDrawableToBitmap(resources.getDrawable(R.drawable.mm4));
			mBitmap[4] = convertDrawableToBitmap(resources.getDrawable(R.drawable.mm5));
			mBitmap[5] = convertDrawableToBitmap(resources.getDrawable(R.drawable.mm6));
			*/			
		}
		else if(mStyle == 3)
		{
			for(i = 0 ; i< mCount ; i++ )
			{
					int img = R.drawable.mm1+i ;
					mBitmap[i] = convertDrawableToBitmap(zoomDrawable(resources.getDrawable(img),pow2(resources.getDrawable(img).getIntrinsicWidth()),pow2(resources.getDrawable(img).getIntrinsicHeight())));
			}
			/*
			mBitmap[0] = convertDrawableToBitmap(zoomDrawable(resources.getDrawable(R.drawable.mm1),pow2(resources.getDrawable(R.drawable.mm1).getIntrinsicWidth()),pow2(resources.getDrawable(R.drawable.mm1).getIntrinsicHeight())));
			mBitmap[1] = convertDrawableToBitmap(zoomDrawable(resources.getDrawable(R.drawable.mm2),pow2(resources.getDrawable(R.drawable.mm2).getIntrinsicWidth()),pow2(resources.getDrawable(R.drawable.mm2).getIntrinsicHeight())));
			mBitmap[2] = convertDrawableToBitmap(zoomDrawable(resources.getDrawable(R.drawable.mm3),pow2(resources.getDrawable(R.drawable.mm3).getIntrinsicWidth()),pow2(resources.getDrawable(R.drawable.mm3).getIntrinsicHeight())));
			mBitmap[3] = convertDrawableToBitmap(zoomDrawable(resources.getDrawable(R.drawable.mm4),pow2(resources.getDrawable(R.drawable.mm4).getIntrinsicWidth()),pow2(resources.getDrawable(R.drawable.mm4).getIntrinsicHeight())));
			mBitmap[4] = convertDrawableToBitmap(zoomDrawable(resources.getDrawable(R.drawable.mm5),pow2(resources.getDrawable(R.drawable.mm5).getIntrinsicWidth()),pow2(resources.getDrawable(R.drawable.mm5).getIntrinsicHeight())));
			mBitmap[5] = convertDrawableToBitmap(zoomDrawable(resources.getDrawable(R.drawable.mm6),pow2(resources.getDrawable(R.drawable.mm6).getIntrinsicWidth()),pow2(resources.getDrawable(R.drawable.mm6).getIntrinsicHeight())));			
			*/
		}
		else if(mStyle == 4)
		{
			for(i = 0 ; i< mCount ; i++ )
			{
					mBitmap[i] = BitmapFactory.decodeStream(resources.openRawResource(R.drawable.mm1+i));
			}
			/*
			mBitmap[0] = BitmapFactory.decodeStream(resources.openRawResource(R.drawable.mm1));
			mBitmap[1] = BitmapFactory.decodeStream(resources.openRawResource(R.drawable.mm2));
			mBitmap[2] = BitmapFactory.decodeStream(resources.openRawResource(R.drawable.mm3));
			mBitmap[3] = BitmapFactory.decodeStream(resources.openRawResource(R.drawable.mm4));
			mBitmap[4] = BitmapFactory.decodeStream(resources.openRawResource(R.drawable.mm5));
			mBitmap[5] = BitmapFactory.decodeStream(resources.openRawResource(R.drawable.mm6));
			*/
		}
	}	
	
	public static void replaceBitmap()
	{	
		/*
		int i = 0 ;
		Bitmap bmp = mBitmap[0];		
		for(i= 0 ; i< mCount-1; i++)
		{
			mBitmap[i] = mBitmap[i+1];
		}				
		mBitmap[mCount-1] = bmp ;
		*/
	}
}
