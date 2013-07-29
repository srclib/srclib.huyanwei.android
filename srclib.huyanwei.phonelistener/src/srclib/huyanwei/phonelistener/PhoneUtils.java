package srclib.huyanwei.phonelistener;

import android.telephony.TelephonyManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method; 
import android.util.Log; 

//import com.android.internal.telephony.ITelephony;

public class PhoneUtils {
	
	private static String TAG = "srclib.huyanwei.phonelistener.PhoneUtils";
	
	/**
     * 从TelephonyManager中实例化ITelephony，并返回
     */ 
	 static public com.android.internal.telephony.ITelephony getITelephony(TelephonyManager telMgr) throws Exception { 

        Method getITelephonyMethod = telMgr.getClass().getDeclaredMethod("getITelephony"); 

        getITelephonyMethod.setAccessible(true);//私有化函数也能使用 

        return (com.android.internal.telephony.ITelephony)getITelephonyMethod.invoke(telMgr);
    } 

	static public void printAllInform(Class clsShow)
	{
		try
		{
			// 取得所有方法
			Method[] hideMethod = clsShow.getDeclaredMethods();
			int i = 0;
			for(i=0;i<hideMethod.length;i++) 
			{
				Log.e(TAG,"Method name="+hideMethod[i].getName());
			}
			
			//取得所有常量
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
