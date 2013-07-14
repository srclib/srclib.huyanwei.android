package srclib.huyanwei.opengraphlib;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class OpenGraphLibRender implements Renderer {

	private static final String TAG ="srclib.huyanwei.opengraphlib.OpenGraphLibRender";
		
	private static final int BITMAP_TEXTURE = 1;
	private static final int COLOR_TEXTURE = 2;
	
	private static final int mStyle = BITMAP_TEXTURE;
	//private static final int mStyle = COLOR_TEXTURE;
	
	//Alpha Blend .
	//private static boolean mBlend = true;
	private static boolean mBlend = false;
	
	private Context mContext;
	
	private SurfaceHolder mHolder;
	
	//四边形顶点数据
	private float cube_vertices_coods[][] = 
	{
			new float[]{//top
					-1.0f,1.0f,-1.0f,
					-1.0f,1.0f,1.0f,
					1.0f,1.0f,1.0f,
					1.0f,1.0f,-1.0f,
			},
			new float[]{//bottom
					1.0f,-1.0f,1.0f,
					1.0f,-1.0f,-1.0f,
					-1.0f,-1.0f,-1.0f,
					-1.0f,-1.0f,1.0f,
			},

			new float[]{//front
					-1.0f,1.0f,1.0f,
					-1.0f,-1.0f,1.0f,
					1.0f,-1.0f,1.0f,
					1.0f,1.0f,1.0f
			},
			new float[]{//right
					1.0f,1.0f,1.0f,
					1.0f,-1.0f,1.0f,
					1.0f,-1.0f,-1.0f,
					1.0f,1.0f,-1.0f,
			},
			new float[]{//back
					1.0f,1.0f,-1.0f,
					1.0f,-1.0f,-1.0f,
					-1.0f,-1.0f,-1.0f,
					-1.0f,1.0f,-1.0f
			},
			new float[]{//left
					-1.0f,1.0f,-1.0f,
					-1.0f,-1.0f,-1.0f,
					-1.0f,-1.0f,1.0f,
					-1.0f,1.0f,1.0f,
			},
	};
	
	//正方体的顶点颜色值(r,g,b,a)
	private float cube_vertices_color[][] = {
			 new float[]{
			 1.0f,0.0f,0.0f,1.0f,
			 1.0f,0.0f,0.0f,1.0f,
			 0.0f,1.0f,0.0f,1.0f,
			 0.0f,0.0f,1.0f,1.0f,
			 },
			 new float[]{
			 1.0f,0.0f,0.0f,1.0f,
			 1.0f,0.0f,0.0f,1.0f,
			 0.0f,1.0f,0.0f,1.0f,
			 0.0f,0.0f,1.0f,1.0f,
			 },
			 new float[]{
			 1.0f,0.0f,0.0f,1.0f,
			 1.0f,0.0f,0.0f,1.0f,
			 0.0f,1.0f,0.0f,1.0f,
			 0.0f,0.0f,1.0f,1.0f,
			 },
			 new float[]{
			 1.0f,0.0f,0.0f,1.0f,
			 1.0f,0.0f,0.0f,1.0f,
			 0.0f,1.0f,0.0f,1.0f,
			 0.0f,0.0f,1.0f,1.0f,
			 },
			 new float[]{
			 1.0f,0.0f,0.0f,1.0f,
			 1.0f,0.0f,0.0f,1.0f,
			 0.0f,1.0f,0.0f,1.0f,
			 0.0f,0.0f,1.0f,1.0f,
			 },
			 new float[]{
			 1.0f,0.0f,0.0f,1.0f,
			 1.0f,0.0f,0.0f,1.0f,
			 0.0f,1.0f,0.0f,1.0f,
			 0.0f,0.0f,1.0f,1.0f,
			 },
	};
	
	//四边形纹理顶点数据
	private float cube_texture_coods[][] = 
	{

			new float[]{ //top
					0.0f,0.0f,	
					0.0f,1.0f,
					1.0f,1.0f,
					1.0f,0.0f,
			},
			new float[]{ //right
					0.0f,0.0f,	
					0.0f,1.0f,
					1.0f,1.0f,
					1.0f,0.0f,
					},
			new float[]{ //bottom
					0.0f,0.0f,	
					0.0f,1.0f,
					1.0f,1.0f,
					1.0f,0.0f,
			},
			new float[]{ //left
					0.0f,0.0f,	
					0.0f,1.0f,
					1.0f,1.0f,
					1.0f,0.0f,
			},
			new float[]{ //front
					0.0f,0.0f,	
					0.0f,1.0f,
					1.0f,1.0f,
					1.0f,0.0f,
			},
			new float[]{ //back
					0.0f,0.0f,	
					0.0f,1.0f,
					1.0f,1.0f,
					1.0f,0.0f,
			},
	};
	
	private float cube_normal_coods[][] = {
		new float[]{//top
					0.0f,1.0f,0.0f,//对应到每个顶点的法线
					0.0f,1.0f,0.0f,
					0.0f,1.0f,0.0f,
					0.0f,1.0f,0.0f,
		},
		new float[]{//right
					1.0f,0.0f,0.0f,
					1.0f,0.0f,0.0f,
					1.0f,0.0f,0.0f,
					1.0f,0.0f,0.0f,
		},
		new float[]{//bottom
					0.0f,-1.0f,0.0f,
					0.0f,-1.0f,0.0f,
					0.0f,-1.0f,0.0f,
					0.0f,-1.0f,0.0f,
		},
		new float[]{//left
					-1.0f,0.0f,0.0f,
					-1.0f,0.0f,0.0f,
					-1.0f,0.0f,0.0f,
					-1.0f,0.0f,0.0f,
		},
		new float[]{//front
					0.0f,0.0f,1.0f,
					0.0f,0.0f,1.0f,
					0.0f,0.0f,1.0f,
					0.0f,0.0f,1.0f,
					0.0f,0.0f,1.0f,
		},
		new float[]{//back
					0.0f,0.0f,-1.0f,
					0.0f,0.0f,-1.0f,
					0.0f,0.0f,-1.0f,
					0.0f,0.0f,-1.0f,
		},
	};

	private float xrot,yrot,zrot;
	
	private float step = 0.4f;
	private float xspeed;
	private float yspeed;
	private float z = -5.0f;
	
	private float touchX = 0;
	private float touchY = 0;	
	
	private boolean key_down_state = false;
	private boolean touch_down_state = false;
	
	private int angle = 32;
	
	float eye_z = 2.0f;
	float point_z = 0.0f;
	
	private FloatBuffer vertex_buffers[]  = new FloatBuffer[cube_vertices_coods.length];
	private FloatBuffer color_buffers[]   = new FloatBuffer[cube_vertices_color.length];
	private FloatBuffer texture_buffers[] = new FloatBuffer[cube_texture_coods.length];
	private FloatBuffer normal_buffers[]  = new FloatBuffer[cube_normal_coods.length];
	
	//private int texture [] = new int[OpenGraphLibBitmap.mCount];
	private int texture [] = new int[6];
	
	public OpenGraphLibRender(Context context){
		
		mContext = context;
		
		/////vertex
		Log.d(TAG,"vertex_buffers.length="+vertex_buffers.length);
		Log.d(TAG,"cube_vertices_coods[0].length="+cube_vertices_coods[0].length);
		
		for(int i=0; i < vertex_buffers.length;i++){
			if(1 == 1)
			{	
				ByteBuffer vbb = ByteBuffer.allocateDirect(cube_vertices_coods[i].length * 4);
				vbb.order(ByteOrder.nativeOrder());
				vertex_buffers[i] = vbb.asFloatBuffer();
				vertex_buffers[i].put(cube_vertices_coods[i]);
				vertex_buffers[i].position(0);
			}
		}
		/////color
		for(int i=0; i < color_buffers.length;i++){
			if(1 == 1)
			{	
				ByteBuffer cbb = ByteBuffer.allocateDirect(cube_vertices_color[i].length * 4);
				cbb.order(ByteOrder.nativeOrder());
				color_buffers[i] = cbb.asFloatBuffer();
				color_buffers[i].put(cube_vertices_color[i]);
				color_buffers[i].position(0);
			}
		}		

		/////texture
		for(int i=0; i < texture_buffers.length;i++){
			if(1 == 1)
			{	
				ByteBuffer tbb = ByteBuffer.allocateDirect(cube_texture_coods[i].length * 4);
				tbb.order(ByteOrder.nativeOrder());
				texture_buffers[i] = tbb.asFloatBuffer();
				texture_buffers[i].put(cube_texture_coods[i]);
				texture_buffers[i].position(0);
			}
		}
	
		/////normal
		for(int i=0; i < normal_buffers.length;i++){
			if(1 == 1)
			{	
				ByteBuffer nbb = ByteBuffer.allocateDirect(cube_normal_coods[i].length * 4);
				nbb.order(ByteOrder.nativeOrder());
				normal_buffers[i] = nbb.asFloatBuffer();
				normal_buffers[i].put(cube_normal_coods[i]);
				normal_buffers[i].position(0);
			}
		}
	}	
	
	public void onDrawFrame(GL10 arg0) {
		// TODO Auto-generated method stub
		// Clears the screen and depth buffer.
		/*
		angle >>= 1 ;  
		
		if(angle < 1)
		{
			angle = 16;
		}
		*/
		angle = 32;
		
	    if(touch_down_state == false)
	    {	
		    xrot += 0.0f;
		    yrot -= (0.1f*angle);
		    zrot -= 0.0f;
	    }

	    GL10 gl = arg0;
	    
		if((mStyle == BITMAP_TEXTURE ))
		{	
			//允许2D贴图,纹理
			arg0.glEnable(GL10.GL_TEXTURE_2D);	

			//OpenGraphLibBitmap.replaceBitmap();
			
			//用之前的纹理数据.
			//for(int i = 0 ; i<OpenGraphLibBitmap.mCount ; i++)
			for(int i = 0 ; i<6 ; i++)  // 6 面
			{
				//设置要使用的纹理		   
				arg0.glBindTexture(GL10.GL_TEXTURE_2D, texture[i]);	
				//生成纹理		
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, OpenGraphLibBitmap.mBitmap[i], 0);	
		 		// 线形滤波
				arg0.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
				arg0.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);			
				
				//OpenGraphLibBitmap.mBitmap[i].recycle();
			}	
		}   
	    
 	    // 清除屏幕和深度缓存		  
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	    
		// 重置当前的模型观察矩阵
	    gl.glLoadIdentity();
	    
	    //禁止背面剪裁 . disable cut face  
	    //gl.glDisable(GL10.GL_CULL_FACE);
        //gl.glEnable(GL10.GL_CULL_FACE); 
	    
		if(mBlend)
		{
			//Turn Blending On
			gl.glEnable(GL10.GL_BLEND);		
			// Disables depth testing. 		 
			gl.glDisable(GL10.GL_DEPTH_TEST);
		}	
		else
		{
			//Turn Blending Off
			gl.glDisable(GL10.GL_BLEND);
			// Enables depth testing.			
			gl.glEnable(GL10.GL_DEPTH_TEST);		
		}
		
		/* 渲染正方体 */		
		//gl.glEnable(GL10.GL_TEXTURE_2D);	
		
		//重置当前的模型观察矩阵		  
		gl.glLoadIdentity();		

		gl.glPushMatrix();//保护变换矩阵现场
		
		// 左移 1.5 单位，并移入屏幕 6.0
		gl.glTranslatef(0.0f, 0.0f, -4.0f);

		gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);
		
		/*
		//不太可能变形的视角——小视角  
		GLU.gluLookAt
        (  
                gl,   
                0.0f,   //人眼位置的X  
                0.0f,   //人眼位置的Y  
                eye_z,   //人眼位置的Z  
                0,      //人眼球看的点X  
                0,     //人眼球看的点Y  
                point_z,      //人眼球看的点Z  
                0,      //摄像头朝向的垂直方向点x
                0,      //摄像头朝向的垂直方向点y
                -1       //摄像头朝向的垂直方向点z
        );
		*/
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		if(mStyle == BITMAP_TEXTURE)
		{
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		}
		else
		{
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
		}			

		
		for(int i=0; i<6; i++)
		{			  
			//设置和绘制正方形
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex_buffers[i]);
			
			if(mStyle == BITMAP_TEXTURE)
			{
				//设置纹理坐标
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texture_buffers[i]);
				 
				//设置法线
				gl.glNormalPointer(GL10.GL_FLOAT, 0, normal_buffers[i]);

				// 绑定纹理
				gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[i]);
				
		 		// 线形滤波
				gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			}
			else
			{
				// 设置颜色
				gl.glColorPointer(4, GL10.GL_FLOAT, 0, color_buffers[i]);
			}
			
			//绘制正方形
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0,4); // 从v0开始画4个点的面 : {v0,v1,v2},{v0,v2,v3}
			//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0,4); // 从v0开始画4个点的面:{v0,v1,v2},{v1,v2,v3}
		}
				  
		//绘制正方形结束
		gl.glFinish();
		
		gl.glRotatef(-xrot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(-yrot, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(-zrot, 0.0f, 0.0f, 1.0f);
		
		gl.glPopMatrix();//恢复变换矩阵现场
				 
		//取消顶点数组
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		if(mStyle == BITMAP_TEXTURE)
		{
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}
		else
		{
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);			
		}				
				  
		if ( key_down_state == true )
		{
		   xrot+=xspeed;
		   yrot+=yspeed;
		}		  
	}

	public void onSurfaceChanged(GL10 arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

		GL10 gl= arg0;
		int width = arg1;
		int height = arg2;
		
		int view_style = 1;
		
		if( view_style == 0)
		{	
			float ratio = (float) arg1 / arg2;  
	
	        // 设置场景尺寸
	        arg0.glViewport(0, 0, arg1, arg2);
	        
			//
			arg0.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			
			//设置投影矩阵 
			arg0.glMatrixMode(GL10.GL_PROJECTION);
			//gl.glMatrixMode(GL10.GL_PROJECTION); 指明接下来的代码将影响 projection matrix （投影矩阵），投影矩阵负责为场景增加透视度。
			//重置投影矩阵	
			arg0.glLoadIdentity();			
			//gl.glLoadIdentity(); 此方法相当于我们手机的重置功能，它将所选择的矩阵状态恢复成原始状态，调用  glLoadIdentity(); 之后为场景设置透视图。
	
			// 设置视口的大小  
			//arg0.glFrustumf(-ratio, ratio, -1, 1, 1, 10);  
			arg0.glFrustumf(-ratio, ratio, -1, 1, 1.5f, 10);
			//arg0.glFrustumf(-ratio, ratio, -1, 1, 0.1f, 8.0f);
	
			// 选择模型观察矩阵
	
			arg0.glMatrixMode(GL10.GL_MODELVIEW);  
			  
			// 重置模型观察矩阵
	
			arg0.glLoadIdentity();
		}
		else
		{	
			// 设置视口的大小
			gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
			
			gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
			gl.glLoadIdentity(); 					//Reset The Projection Matrix
	
			//Calculate The Aspect Ratio Of The Window // 透视 ,透视角为45度. 人视野角度为45.锥体
			GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
			// gluPerspective表示投射投影，锥体.
			// gluOrtho 表示正交投影 ,大小不变 .
	
			gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
			gl.glLoadIdentity(); 					//Reset The Modelview Matrix
		}
	}
	

	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// TODO Auto-generated method stub
		
		//OpenGraphLibBitmap.load(mContext.getResources());
		
		//关闭抗抖动   
		arg0.glDisable(GL10.GL_DITHER);  
		
		// Really nice perspective calculations. 
		arg0.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_NICEST);
		//GL_NICEST为使用质量最好的模式,GL_FASTEST为使用速度最快的模式.		

		// Set the background color to yellow ( rgba ).
		//arg0.glClearColor(1.0f, 1.0f, 0.0f, 0.0f);
		arg0.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Enable Smooth Shading, default not really needed.
		arg0.glShadeModel(GL10.GL_SMOOTH);

		// Depth buffer setup.
		arg0.glClearDepthf(1.0f);

		if(mBlend)
		{
			//Turn Blending On
			arg0.glEnable(GL10.GL_BLEND);
			
			// Disables depth testing. 		 
			arg0.glDisable(GL10.GL_DEPTH_TEST);
			
			//arg0.glColor4f(1.0f,1.0f,1.0f,0.85f);   // 全亮度， 50% Alpha 混合
			arg0.glColor4f(1.0f,1.0f,1.0f,0.45f);   // 全亮度， 50% Alpha 混合
			
			//arg0.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE);  // 基于源象素alpha通道值的半透明混合函数
			arg0.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);  // 基于源象素alpha通道值的半透明混合函数
			//arg0.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_DST_ALPHA);
			//arg0.glBlendFunc(GL10.GL_ONE,GL10.GL_ONE);  // 基于源象素alpha通道值的半透明混合函数
		}	
		else
		{
		 	//Turn Blending Off
			arg0.glDisable(GL10.GL_BLEND);
			
			// Enables depth testing.
			arg0.glEnable(GL10.GL_DEPTH_TEST);		
			
			//The type of depth testing to do.   
			arg0.glDepthFunc(GL10.GL_LEQUAL);
		}
		
		if(mStyle == BITMAP_TEXTURE )
		{	
			//允许2D贴图,纹理
			arg0.glEnable(GL10.GL_TEXTURE_2D);	
			
			//分配6个纹理.
			//IntBuffer intBuffer = IntBuffer.allocate(OpenGraphLibBitmap.mCount);		\
			IntBuffer intBuffer = IntBuffer.allocate(6);
			//创建6个纹理
			//arg0.glGenTextures(OpenGraphLibBitmap.mCount, intBuffer);
			arg0.glGenTextures(6, intBuffer);
			
			//for(int i = 0 ; i<OpenGraphLibBitmap.mCount ; i++)
			for(int i = 0 ; i< 6 ; i++)
			{
				//纹理.
				texture[i] = intBuffer.get(i);	
				//设置要使用的纹理		   
				arg0.glBindTexture(GL10.GL_TEXTURE_2D, texture[i]);	
				//生成纹理		
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, OpenGraphLibBitmap.mBitmap[i], 0);	
		 		// 线形滤波
				arg0.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
				arg0.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);			
				
				//OpenGraphLibBitmap.mBitmap[i].recycle();
			}	
		}        
	}
	
	public boolean onKeyUp(int keyCode, KeyEvent event)
	{
		key_down_state = false;

	 	xspeed=0.0f;

	 	yspeed=0.0f;

	 	return false;
	 	
		//return true;
	
	}
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		switch ( keyCode )
	 	{
	 	case KeyEvent.KEYCODE_DPAD_UP:
	 		key_down_state = true;
	 		xspeed=-step;
	 		break;
	 	case KeyEvent.KEYCODE_DPAD_DOWN:
	 		key_down_state = true;
	 		xspeed=step;
	 		break;
	 	case KeyEvent.KEYCODE_DPAD_LEFT:
	 		key_down_state = true;
	 		yspeed=-step;
	 		break;
	 	case KeyEvent.KEYCODE_DPAD_RIGHT:
	 		key_down_state = true;
	 		yspeed=step;
	 		break;
	 	case KeyEvent.KEYCODE_W:
	 		z -= step;
	 		break;
	 	case KeyEvent.KEYCODE_S:
	 		z += step;
	 		break;
	 	}
	 	return false;
		//return true;
	}
	
	public boolean onTouchEvent(MotionEvent event)
	{
		if(event.getAction()==MotionEvent.ACTION_DOWN)	{

		       //处理屏幕屏点下事件 手指点击屏幕时触发
			    touch_down_state = true ;
			    
			    //更新点信息
				touchX=event.getX();
				touchY=event.getY();
			}
			else if(event.getAction()==MotionEvent.ACTION_MOVE)	{

		       //处理移动事件 手指在屏幕上移动时触发
				//xrot -= (event.getY() - touchY);
				yrot += (event.getX() - touchX);	
				
				eye_z += (event.getX() - touchX)/80;

			    // 更新点信息
			    touchX = event.getX();
			    touchY = event.getY();
			}
			else if(event.getAction() == MotionEvent.ACTION_UP)
			{
				touch_down_state = false;
			}				

		return true;  //此处需要返回true,才可以正常处理move事件
		//return true;
	}
}