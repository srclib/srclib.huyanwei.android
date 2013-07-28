package srclib.huyanwei.phonelistener;

import android.telephony.TelephonyManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method; 
import android.util.Log; 

//import com.android.internal.telephony.ITelephony;

public class PhoneUtils {
	/**
     * ��TelephonyManager��ʵ����ITelephony��������
     */ 
	 static public com.android.internal.telephony.ITelephony getITelephony(TelephonyManager telMgr) throws Exception { 

        Method getITelephonyMethod = telMgr.getClass().getDeclaredMethod("getITelephony"); 

        getITelephonyMethod.setAccessible(true);//˽�л�����Ҳ��ʹ�� 

        return (com.android.internal.telephony.ITelephony)getITelephonyMethod.invoke(telMgr);
    } 
}
