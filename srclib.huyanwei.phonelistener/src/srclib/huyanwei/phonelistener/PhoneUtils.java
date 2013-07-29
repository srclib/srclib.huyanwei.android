package srclib.huyanwei.phonelistener;

import android.telephony.TelephonyManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method; 
import android.util.Log; 

//import com.android.internal.telephony.ITelephony;

public class PhoneUtils {
	
	private static String TAG = "srclib.huyanwei.phonelistener.PhoneUtils";
	
	/**
     * ��TelephonyManager��ʵ����ITelephony��������
     */ 
	 static public com.android.internal.telephony.ITelephony getITelephony(TelephonyManager telMgr) throws Exception { 

        Method getITelephonyMethod = telMgr.getClass().getDeclaredMethod("getITelephony"); 

        getITelephonyMethod.setAccessible(true);//˽�л�����Ҳ��ʹ�� 

        return (com.android.internal.telephony.ITelephony)getITelephonyMethod.invoke(telMgr);
    } 

	static public void printAllInform(Class clsShow)
	{
		try
		{
			// ȡ�����з���
			Method[] hideMethod = clsShow.getDeclaredMethods();
			int i = 0;
			for(i=0;i<hideMethod.length;i++) 
			{
				Log.e(TAG,"Method name="+hideMethod[i].getName());
			}
			
			//ȡ�����г���
			Field[] allFields = clsShow.getFields();
			for (i = 0; i < allFields.length; i++)
			{
				Log.e(TAG,"Field name="+allFields[i].getName());
			}
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
