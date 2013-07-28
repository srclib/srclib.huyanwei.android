package srclib.huyanwei.phonelistener;

import android.telephony.TelephonyManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method; 
import android.util.Log; 

//import com.android.internal.telephony.ITelephony;

public class PhoneUtils {
	/**
     * 从TelephonyManager中实例化ITelephony，并返回
     */ 
	 static public com.android.internal.telephony.ITelephony getITelephony(TelephonyManager telMgr) throws Exception { 

        Method getITelephonyMethod = telMgr.getClass().getDeclaredMethod("getITelephony"); 

        getITelephonyMethod.setAccessible(true);//私有化函数也能使用 

        return (com.android.internal.telephony.ITelephony)getITelephonyMethod.invoke(telMgr);
    } 
}
