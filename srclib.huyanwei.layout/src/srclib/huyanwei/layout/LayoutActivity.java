package srclib.huyanwei.layout;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LayoutActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        ViewGroup view_main = (ViewGroup)findViewById(R.id.main);
        
        /*
        final LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog, view_main, true);
        */

        final LayoutInflater inflater = LayoutInflater.from(this);
        //View view = inflater.inflate(R.layout.dialog, view_main, true);
        View view = inflater.inflate(R.layout.dialog, null);
        view_main.addView(view);
        
        ImageView img = (ImageView)findViewById(R.id.imageView1);      
        
        Resources resource= getResources();
        Drawable bg_drawable = resource.getDrawable(R.drawable.ic_launcher_gallery);
        
        Bitmap   bg_bitmap = drawableToBitmap(bg_drawable);
        
        Drawable fg_drawable = new BitmapDrawable(createReflectionImageWithOrigin(bg_bitmap));
        
        img.setBackgroundDrawable((Drawable)fg_drawable);       
        
    }
    
  //放大缩小图片   
    public static Bitmap zoomBitmap(Bitmap bitmap,int w,int h){   
        int width = bitmap.getWidth();   
        int height = bitmap.getHeight();   
        Matrix matrix = new Matrix();   
        float scaleWidht = ((float)w / width);   
        float scaleHeight = ((float)h / height);   
        matrix.postScale(scaleWidht, scaleHeight);   
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);   
        return newbmp;   
    }   
    //将Drawable转化为Bitmap   
     public static Bitmap drawableToBitmap(Drawable drawable){   
            int width = drawable.getIntrinsicWidth();   
            int height = drawable.getIntrinsicHeight();   
            Bitmap bitmap = Bitmap.createBitmap(width, height,   
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888   
                            : Bitmap.Config.RGB_565);   
            Canvas canvas = new Canvas(bitmap);   
            drawable.setBounds(0,0,width,height);   
            drawable.draw(canvas);   
            return bitmap;   
               
        }   
        
     //获得圆角图片的方法   
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){   
           
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap   
                .getHeight(), Config.ARGB_8888);   
        Canvas canvas = new Canvas(output);   
    
        final int color = 0xff424242;   
        final Paint paint = new Paint();   
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());   
        final RectF rectF = new RectF(rect);   
    
        paint.setAntiAlias(true);   
        canvas.drawARGB(0, 0, 0, 0);   
        paint.setColor(color);   
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);   
    
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));   
        canvas.drawBitmap(bitmap, rect, rect, paint);   
    
        return output;   
    }   
    //获得带倒影的图片方法   
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap){   
        final int reflectionGap = 4;   
        int width = bitmap.getWidth();   
        int height = bitmap.getHeight();   
           
        Matrix matrix = new Matrix();   
        matrix.preScale(1, -1);   
           
        Bitmap reflectionImage = Bitmap.createBitmap(bitmap,    
                0, height/2, width, height/2, matrix, false);   
           
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Config.ARGB_8888);   
           
        Canvas canvas = new Canvas(bitmapWithReflection);   
        canvas.drawBitmap(bitmap, 0, 0, null);   
        Paint deafalutPaint = new Paint();   
        canvas.drawRect(0, height,width,height + reflectionGap,   
                deafalutPaint);   
           
        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);   
           
        Paint paint = new Paint();   
        LinearGradient shader = new LinearGradient(0,   
                bitmap.getHeight(), 0, bitmapWithReflection.getHeight()   
                + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);   
        paint.setShader(shader);   
        // Set the Transfer mode to be porter duff and destination in   
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));   
        // Draw a rectangle using the paint with our linear gradient   
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()   
                + reflectionGap, paint);   
    
        return bitmapWithReflection;   
    }  
    
    
    
}