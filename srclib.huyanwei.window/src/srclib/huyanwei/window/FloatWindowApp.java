package srclib.huyanwei.window;

import android.os.Bundle;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

public class FloatWindowApp extends Application 
{
    /**
     * ����ȫ�ֱ���
     * ȫ�ֱ���һ�㶼�Ƚ������ڴ���һ���������������ļ�����ʹ��static��̬����
     * 
     * ����ʹ������Application��������ݵķ���ʵ��ȫ�ֱ���
     * ע����AndroidManifest.xml�е�Application�ڵ����android:name=".MyApplication"����
     * 
     */
    private WindowManager.LayoutParams wmParams=new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getParams(){
        return wmParams;
    }
}
