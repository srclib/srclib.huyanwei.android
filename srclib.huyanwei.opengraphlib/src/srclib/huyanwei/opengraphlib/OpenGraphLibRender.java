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
	
	//�ı��ζ�������
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
	
	//������Ķ�����ɫֵ(r,g,b,a)
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
	
	//�ı�������������
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
					0.0f,1.0f,0.0f,//��Ӧ��ÿ������ķ���
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
			//����2D��ͼ,����
			arg0.glEnable(GL10.GL_TEXTURE_2D);	

			//OpenGraphLibBitmap.replaceBitmap();
			
			//��֮ǰ����������.
			//for(int i = 0 ; i<OpenGraphLibBitmap.mCount ; i++)
			for(int i = 0 ; i<6 ; i++)  // 6 ��
			{
				//����Ҫʹ�õ�����		   
				arg0.glBindTexture(GL10.GL_TEXTURE_2D, texture[i]);	
				//��������		
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, OpenGraphLibBitmap.mBitmap[i], 0);	
		 		// �����˲�
				arg0.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
				arg0.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);			
				
				//OpenGraphLibBitmap.mBitmap[i].recycle();
			}	
		}   
	    
 	    // �����Ļ����Ȼ���		  
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	    
		// ���õ�ǰ��ģ�͹۲����
	    gl.glLoadIdentity();
	    
	    //��ֹ������� . disable cut face  
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
		
		/* ��Ⱦ������ */		
		//gl.glEnable(GL10.GL_TEXTURE_2D);	
		
		//���õ�ǰ��ģ�͹۲����		  
		gl.glLoadIdentity();		

		gl.glPushMatrix();//�����任�����ֳ�
		
		// ���� 1.5 ��λ����������Ļ 6.0
		gl.glTranslatef(0.0f, 0.0f, -4.0f);

		gl.glRotatef(xrot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(yrot, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(zrot, 0.0f, 0.0f, 1.0f);
		
		/*
		//��̫���ܱ��ε��ӽǡ���С�ӽ�  
		GLU.gluLookAt
        (  
                gl,   
                0.0f,   //����λ�õ�X  
                0.0f,   //����λ�õ�Y  
                eye_z,   //����λ�õ�Z  
                0,      //�����򿴵ĵ�X  
                0,     //�����򿴵ĵ�Y  
                point_z,      //�����򿴵ĵ�Z  
                0,      //����ͷ����Ĵ�ֱ�����x
                0,      //����ͷ����Ĵ�ֱ�����y
                -1       //����ͷ����Ĵ�ֱ�����z
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
			//���úͻ���������
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertex_buffers[i]);
			
			if(mStyle == BITMAP_TEXTURE)
			{
				//������������
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texture_buffers[i]);
				 
				//���÷���
				gl.glNormalPointer(GL10.GL_FLOAT, 0, normal_buffers[i]);

				// ������
				gl.glBindTexture(GL10.GL_TEXTURE_2D, texture[i]);
				
		 		// �����˲�
				gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
				gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			}
			else
			{
				// ������ɫ
				gl.glColorPointer(4, GL10.GL_FLOAT, 0, color_buffers[i]);
			}
			
			//����������
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0,4); // ��v0��ʼ��4������� : {v0,v1,v2},{v0,v2,v3}
			//gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0,4); // ��v0��ʼ��4�������:{v0,v1,v2},{v1,v2,v3}
		}
				  
		//���������ν���
		gl.glFinish();
		
		gl.glRotatef(-xrot, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(-yrot, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(-zrot, 0.0f, 0.0f, 1.0f);
		
		gl.glPopMatrix();//�ָ��任�����ֳ�
				 
		//ȡ����������
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
	
	        // ���ó����ߴ�
	        arg0.glViewport(0, 0, arg1, arg2);
	        
			//
			arg0.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			
			//����ͶӰ���� 
			arg0.glMatrixMode(GL10.GL_PROJECTION);
			//gl.glMatrixMode(GL10.GL_PROJECTION); ָ���������Ĵ��뽫Ӱ�� projection matrix ��ͶӰ���󣩣�ͶӰ������Ϊ��������͸�Ӷȡ�
			//����ͶӰ����	
			arg0.glLoadIdentity();			
			//gl.glLoadIdentity(); �˷����൱�������ֻ������ù��ܣ�������ѡ��ľ���״̬�ָ���ԭʼ״̬������  glLoadIdentity(); ֮��Ϊ��������͸��ͼ��
	
			// �����ӿڵĴ�С  
			//arg0.glFrustumf(-ratio, ratio, -1, 1, 1, 10);  
			arg0.glFrustumf(-ratio, ratio, -1, 1, 1.5f, 10);
			//arg0.glFrustumf(-ratio, ratio, -1, 1, 0.1f, 8.0f);
	
			// ѡ��ģ�͹۲����
	
			arg0.glMatrixMode(GL10.GL_MODELVIEW);  
			  
			// ����ģ�͹۲����
	
			arg0.glLoadIdentity();
		}
		else
		{	
			// �����ӿڵĴ�С
			gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
			
			gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
			gl.glLoadIdentity(); 					//Reset The Projection Matrix
	
			//Calculate The Aspect Ratio Of The Window // ͸�� ,͸�ӽ�Ϊ45��. ����Ұ�Ƕ�Ϊ45.׶��
			GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);
			// gluPerspective��ʾͶ��ͶӰ��׶��.
			// gluOrtho ��ʾ����ͶӰ ,��С���� .
	
			gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
			gl.glLoadIdentity(); 					//Reset The Modelview Matrix
		}
	}
	

	public void onSurfaceCreated(GL10 arg0, EGLConfig arg1) {
		// TODO Auto-generated method stub
		
		//OpenGraphLibBitmap.load(mContext.getResources());
		
		//�رտ�����   
		arg0.glDisable(GL10.GL_DITHER);  
		
		// Really nice perspective calculations. 
		arg0.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_NICEST);
		//GL_NICESTΪʹ��������õ�ģʽ,GL_FASTESTΪʹ���ٶ�����ģʽ.		

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
			
			//arg0.glColor4f(1.0f,1.0f,1.0f,0.85f);   // ȫ���ȣ� 50% Alpha ���
			arg0.glColor4f(1.0f,1.0f,1.0f,0.45f);   // ȫ���ȣ� 50% Alpha ���
			
			//arg0.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE);  // ����Դ����alphaͨ��ֵ�İ�͸����Ϻ���
			arg0.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_ONE_MINUS_SRC_ALPHA);  // ����Դ����alphaͨ��ֵ�İ�͸����Ϻ���
			//arg0.glBlendFunc(GL10.GL_SRC_ALPHA,GL10.GL_DST_ALPHA);
			//arg0.glBlendFunc(GL10.GL_ONE,GL10.GL_ONE);  // ����Դ����alphaͨ��ֵ�İ�͸����Ϻ���
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
			//����2D��ͼ,����
			arg0.glEnable(GL10.GL_TEXTURE_2D);	
			
			//����6������.
			//IntBuffer intBuffer = IntBuffer.allocate(OpenGraphLibBitmap.mCount);		\
			IntBuffer intBuffer = IntBuffer.allocate(6);
			//����6������
			//arg0.glGenTextures(OpenGraphLibBitmap.mCount, intBuffer);
			arg0.glGenTextures(6, intBuffer);
			
			//for(int i = 0 ; i<OpenGraphLibBitmap.mCount ; i++)
			for(int i = 0 ; i< 6 ; i++)
			{
				//����.
				texture[i] = intBuffer.get(i);	
				//����Ҫʹ�õ�����		   
				arg0.glBindTexture(GL10.GL_TEXTURE_2D, texture[i]);	
				//��������		
				GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, OpenGraphLibBitmap.mBitmap[i], 0);	
		 		// �����˲�
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

		       //������Ļ�������¼� ��ָ�����Ļʱ����
			    touch_down_state = true ;
			    
			    //���µ���Ϣ
				touchX=event.getX();
				touchY=event.getY();
			}
			else if(event.getAction()==MotionEvent.ACTION_MOVE)	{

		       //�����ƶ��¼� ��ָ����Ļ���ƶ�ʱ����
				//xrot -= (event.getY() - touchY);
				yrot += (event.getX() - touchX);	
				
				eye_z += (event.getX() - touchX)/80;

			    // ���µ���Ϣ
			    touchX = event.getX();
			    touchY = event.getY();
			}
			else if(event.getAction() == MotionEvent.ACTION_UP)
			{
				touch_down_state = false;
			}				

		return true;  //�˴���Ҫ����true,�ſ�����������move�¼�
		//return true;
	}
}