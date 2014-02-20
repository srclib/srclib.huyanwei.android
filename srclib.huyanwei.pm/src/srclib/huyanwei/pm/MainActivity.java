package srclib.huyanwei.pm;

import java.util.List;

import android.os.Bundle;
import android.os.PowerManager;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

	private int size = 0 ;
	
	private String TAG = "srclib.huyanwei.packagemanager" ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		//PowerManager pm=(PowerManager)getSystemService(Context.POWER_SERVICE);
		//pm.reboot("reason");
		
		PackageManager pm = this.getPackageManager();
		String SpecialPackageName = "com.jeejen.family.miui";
		String SpecialActivityName = "com.jeejen.family.miui.ui.launcher.HomeActivity";
		
		Intent intent=new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        
        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
        if(resolveInfoList != null)
        {
                 size = resolveInfoList.size();
                 for(int j=0;j<size;){
                          final ResolveInfo r = resolveInfoList.get(j);
                          if(!r.activityInfo.packageName.equals(SpecialPackageName)){
                                    resolveInfoList.remove(j);
                                    size -= 1;
                           }else
                           {
                                   j++;
                           }
                 }
        }
        
        ComponentName[] set = new ComponentName[size];
        ComponentName defaultLauncher=new ComponentName(SpecialPackageName, SpecialActivityName);
        int defaultMatch=0;
        for(int i=0;i<size;i++)
        {
                 final ResolveInfo resolveInfo = resolveInfoList.get(i);
                 Log.d(TAG, resolveInfo.toString());
                 set[i] = new ComponentName(resolveInfo.activityInfo.packageName,resolveInfo.activityInfo.name);
                 if(defaultLauncher.getClassName().equals(resolveInfo.activityInfo.name)){
                           defaultMatch = resolveInfo.match;
                 }
        }		
        
        Log.d(TAG,"defaultMatch="+Integer.toHexString(defaultMatch));
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        
        pm.clearPackagePreferredActivities(defaultLauncher.getPackageName());
        pm.addPreferredActivity(filter, defaultMatch, set, defaultLauncher);
     
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.jeejen.family.miui","com.jeejen.family.miui.ui.launcher.HomeActivity"));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
