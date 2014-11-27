package srclib.huyanwei.screeneffection;

import java.util.ArrayList;


import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;


/*
 * @ref: view-source:http://mudcu.be/labs/JS1k/BreathingGalaxies.html
<script type="text/javascript">
window.onload=function()
{
	C=Math.cos;
	S=Math.sin;
	U=0;
	w=window;
	j=document;
	d=j.getElementById("c");
	c=d.getContext("2d");
	W=d.width=w.innerWidth;
	H=d.height=w.innerHeight;
	c.fillRect(0,0,W,H);
	c.globalCompositeOperation="lighter";
	c.lineWidth=0.2;c.lineCap="round";
	var bool=0,t=0;
	d.onmousemove=function(e)
	{
		if(window.T)
		{
			if(D==9)
			{
				D=Math.random()*15;
				f(1);
			}
			clearTimeout(T);
		}
		X=e.pageX;
		Y=e.pageY;
		a=0;
		b=0;
		A=X,B=Y;
		R=(e.pageX/W*999>>0)/999;
		r=(e.pageY/H*999>>0)/999;
		U=e.pageX/H*360>>0;
		D=9;
		g=360*Math.PI/180;
		T=setInterval(
			f=function(e)
			{
				c.save();
				c.globalCompositeOperation="source-over";
				if(e!=1)
				{
					c.fillStyle="rgba(0,0,0,0.02)";
					c.fillRect(0,0,W,H);
				}
				c.restore();
				i=25;
				while(i--)
				{
					c.beginPath();
					if(D>450||bool)
					{
						if(!bool)
						{
							bool=1;
						}
						if(D<0.1)
						{
							bool=0;
						}
						t-=g;
						D-=0.1;
					}
					if(!bool)
					{
						t+=g;
						D+=0.1;
					}
					q=(R/r-1)*t;
					x=(R-r)*C(t)+D*C(q)+(A+(X-A)*(i/25))+(r-R);
					y=(R-r)*S(t)-D*S(q)+(B+(Y-B)*(i/25));
					if(a)
					{
						c.moveTo(a,b);
						c.lineTo(x,y);
					}
					c.strokeStyle="hsla("+(U%360)+",100%,50%,0.75)";
					c.stroke();
					a=x;
					b=y;
				}
				U-=0.5;
				A=X;
				B=Y;
			},16);
		}
		j.onkeydown=function(e){
				a=b=0;
				R+=0.05
				}
		d.onmousemove({pageX:300,pageY:290})}</script>
*/


public class ScreenEffectionView extends View {

	public final String TAG = "ScreenEffectionView";
	
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float mRotate;
    private Matrix mMatrix = new Matrix();
    private Shader mShader;
    
    private int miCount = 0; 

    public ScreenEffectionView(Context context) {
        super(context);

        float x = 160;
        float y = 100;
        //mShader = new SweepGradient(x, y, new int[] { Color.GREEN,Color.RED,Color.BLUE,Color.GREEN }, null);
        //mShader = new RadialGradient(x,y,50,new int[] { Color.GREEN,Color.RED},null,Shader.TileMode.CLAMP);
        //mShader = new RadialGradient(x,y,50,new int[] { Color.GREEN,Color.RED},null,Shader.TileMode.REPEAT);        
    
		//mPaint.setShader(mShader);
		
        /*
        BlurMaskFilter filter = new BlurMaskFilter(5.0f, Blur.SOLID);
        mPaint.setColor(Color.rgb(112, 112, 112));
        mPaint.setStyle(Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1.0f);
        mPaint.setMaskFilter(filter);
        */
    }  
    
    
    
    
    private void DrawHeart(Canvas canvas,int start_x , int start_y)
    {
	   	 Paint paint = new Paint();
	     paint.setAntiAlias(true);
	     
	     //paint.setColor(Color.BLACK);
	     //canvas.drawRect(0, 0, 320, 480, paint);
	     switch (miCount % 6) {
	     case 0:
	             paint.setColor(Color.BLUE);
	             break;
	     case 1:
	             paint.setColor(Color.GREEN);
	             break;
	     case 2:
	             paint.setColor(Color.RED);
	             break;
	     case 3:
	             paint.setColor(Color.YELLOW);
	             break;
	     case 4:
	             paint.setColor(Color.argb(255, 255, 181, 216));
	             break;
	     case 5:
	             paint.setColor(Color.argb(255, 0, 255, 255));
	             break;
	     default:
	             paint.setColor(Color.WHITE);
	             break;
	     }
	     int i, j;
	     double x, y, r;
	
	     double xx = start_x;//*Math.random();
	     double yy = start_y;//*Math.random();	    		 
	     
	     
	     for (i = 0; i <= 90; i++) 
	     {
	             for (j = 0; j <= 90; j++) 
	             {
	                     r = Math.PI / 45 * i * (1 - Math.sin(Math.PI / 45 * j))*5;
	                     x = r * Math.cos(Math.PI / 45 * j) * Math.sin(Math.PI / 45 * i) + xx;
	                     y = -r * Math.sin(Math.PI / 45 * j) + yy;
	                     canvas.drawPoint((float) x, (float) y, paint);
	             }
	     }    	
	     
    }   
    
    
    int statX = 250;
    int statY = 550;
    
    @Override protected void onDraw(Canvas canvas) {
    	 
    	
    	statX = (statX+(int)(5*Math.random()))%540;
    	statY = (statY+(int)(5*Math.random()))%960;
    	
    	Log.d("huyanwei","statX="+statX);
    	Log.d("huyanwei","statY="+statY);
    	
    	DrawBeautifulGraphics(canvas,statX,statY);
    	
        this.invalidate();
        
/*    	
       if (miCount < 12) {
            miCount++;            
            this.invalidate();
       } else {
            miCount = 0;
       }
    	
    	DrawHeart(canvas,10*3,10*3);    	
    	
    	DrawHeart(canvas,40*3,70*3);

    	DrawHeart(canvas,70*3,150*3);
    	
    	DrawHeart(canvas,90*3,300*3);    
  */      
    }

    
    int W=540;
    int H=960;
    float x = 0;
    float y = 0;    
    float X = 0;
    float Y = 0;
    float a = 0 ;
    float b = 0 ;
    float A = 0;
    float B = 0;
	boolean bool = false;
	double t = 0;

	double R = 0;
	double r = 0;
	
	double U=0;
	double D=9;
	double g=360*Math.PI/180;
	double q = 0;
	
	int i = 0;
	
	Canvas mCanvas ;
	Path   mPath = new Path();
	
	void function(int e)
	{ 
		
		mCanvas.save();
		//c.globalCompositeOperation="source-over";
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
		if(e!=1)
		{
			mPaint.setARGB(100, 255, 255, 255);
			mCanvas.drawRect(new Rect(0,0,W,H), mPaint);
		}
		mCanvas.restore();	
		
		i=20;
		while(i-- >0)
		{ 
			Log.d("huyanwei","huyanwei debug D="+D);
			Log.d("huyanwei","huyanwei debug bool="+bool);
			Log.d("huyanwei","huyanwei debug g="+g);
			Log.d("huyanwei","huyanwei debug t="+t);
			Log.d("huyanwei","huyanwei debug R="+R);
			Log.d("huyanwei","huyanwei debug r="+r);

			mPath.reset();
			if(D>450||bool)
			{ 
				if(!bool)
				{
					bool=true; 
				}
				if(D<0.1)
				{ 
					bool=false; 
				}
				t-=g;
				D-=1.0; 
			}
			if(!bool)
			{ 
				t+=g;
				D+=1.0; 
			}
			q=(R/r-1)*t;
			x=(float) ((R-r)*Math.cos(t)+D*Math.cos(q)+(A+0+(X-A)*(i/25))+(r-R));
			y=(float) ((R-r)*Math.sin(t)-D*Math.sin(q)+(B+0+(Y-B)*(i/25)));
			
			Log.d("huyanwei","huyanwei debug ("+a+","+b+")->("+x+","+y+")->t="+t+",q="+q);
			
			if(a>0)
			{
				mPath.moveTo(a,b);
				mPath.lineTo(x,y);
			}
			//c.strokeStyle="hsla("+(U%360)+",100%,50%,0.75)";
			//c.stroke();
			//mPaint.setARGB(100, (U%256), 255, 255);
			//mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
			mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
	        mPaint.setStyle(Style.STROKE);
	        mPaint.setAntiAlias(true);
	        mPaint.setStrokeWidth(0.5f);
	        
	        switch ((int)(Math.random()*100) % 6) {
		     case 0:
		    	 mPaint.setColor(Color.BLUE);
		             break;
		     case 1:
		    	 mPaint.setColor(Color.GREEN);
		             break;
		     case 2:
		    	 mPaint.setColor(Color.RED);
		             break;
		     case 3:
		    	 mPaint.setColor(Color.YELLOW);
		             break;
		     case 4:
		    	 mPaint.setColor(Color.argb(255, 255, 181, 216));
		             break;
		     default:
		     case 5:
		    	 mPaint.setColor(Color.argb(255, 0, 255, 255));
		             break;
		     }	        
			//mPaint.setARGB(255, 255, (int)((U%255)), 0);
			mPaint.setStrokeCap(Cap.ROUND);
			
	        BlurMaskFilter filter = new BlurMaskFilter(1.0f, Blur.SOLID);
	        mPaint.setAntiAlias(true);
	        mPaint.setMaskFilter(filter);
	        		
			mCanvas.drawPath(mPath, mPaint);
			a=x;
			b=y; 
		}
		U-=1.5;
		A=X;
		B=Y;
	}
	
	
	static int first_times = 0;  
	
    void DrawBeautifulGraphics(Canvas canvas,int x, int y)
    {	
    	mCanvas = canvas;
    	
    	x = statX;    	    	
    	y = statY;
    	
    	if(first_times == 0)
    	{	
    	
    	W = 540;
    	H = 960;
    	
    	X = x;
    	Y = y;
	
    	//this.x = x;
    	//this.y = y;
    	
    	a = 0 ;
    	b = 0 ;
    	
    	A = X;
    	B = Y;
    	
    	R=(((double)x/W)*999)/999;
    	r=(((double)y/H)*999)/999;
    	//R=0.2;
    	//r=0.16;    	
    	
    	U=x/H*360;
    	
    	D = 9;
    	
    	g=360*Math.PI/180;
    	
		if(D==9)
		{
			D=Math.random()*15;
			function(1);
		}
		first_times =1 ;
    	}
		function(2);		
    }
}

