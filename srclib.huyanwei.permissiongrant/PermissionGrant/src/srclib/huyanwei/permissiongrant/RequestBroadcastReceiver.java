package srclib.huyanwei.permissiongrant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.preference.PreferenceManager;
import android.util.Log;


import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.IntentFilter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.view.View.OnClickListener;

import android.widget.Toast;

import android.view.MenuItem;
import android.view.Menu;

import android.provider.Settings;


public class RequestBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "srclib.huyanwei.permissiongrant.RequestBroadcastReceiver";
	
	private static final String REQUEST_ACTION = "srclib.huyanwei.permissiongrant.request";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(intent.getAction().equals(REQUEST_ACTION))
		{
			Log.d(TAG, "Request BroadcastReceiver onReceive " + intent);
			
			SQLiteDatabase mDatabase;
			DatabaseHelper hlp = new DatabaseHelper(context);
			mDatabase = hlp.getWritableDatabase();			
			Utils mUtils = new Utils(context,mDatabase);		
			
			int uid = -1;
			int pid = -1;
			String server_addr;
			
			uid			= intent.getIntExtra("uid",-1);	
			pid			= intent.getIntExtra("pid",-1);	
			server_addr = intent.getStringExtra("socket_addr");
			
			String local_package_name = 	mUtils.process_to_package_name(pid);
			String local_application_name = mUtils.package_name_to_application_name(local_package_name);
			
			if (uid == -1 || pid == -1)
			{
				Toast toast ;			
				toast = Toast.makeText(context, "This intent requires two int parameters: uid pid", Toast.LENGTH_LONG);
				toast.show();				
				return;
			}
			
			int record_result = -1;
			// first whitelist
			Cursor mCursor = mDatabase.rawQuery("select package_name,result from grant_white_list where package_name=?;" ,new String[]{local_package_name});
			if(mCursor.getCount() != 0)
			{
				mCursor.moveToFirst();				
				do
				{
					//Log.d(TAG,"package_name="+mCursor.getString(0)+" ,result="+mCursor.getInt(1)+"\n");
					record_result = mCursor.getInt(1); 
					//break;
				}while (mCursor.moveToNext());
				
				if(record_result == 1)
				{
					mUtils.notify_server_result(server_addr,1); // notify su
				}
				else
				{
					mUtils.notify_server_result(server_addr,0); // notify su
				}
				
				mCursor.close();
				mDatabase.close();
				return ; 
			}
			mCursor.close();

			//huyanwei add system grant setting {
			//boolean grantSettingEnabled = Settings.System.getInt(context.getContentResolver(),Settings.System.GRANT_SETTING,0) == 1;
			//if(grantSettingEnabled == false)
			//{
			//	mUtils.notify_server_result(server_addr,0); // notify su, reject.
			//	mDatabase.close();
			//	return ;
			//}		
			//huyanwei add system grant setting } 

			// user list . select recordset			
			mCursor = mDatabase.rawQuery("select package_name,result from grant_list where package_name=?;" ,new String[]{local_package_name});
			if(mCursor.getCount() != 0)
			{
				mCursor.moveToFirst();
				do
				{
					//Log.d(TAG,"package_name="+mCursor.getString(0)+" ,result="+mCursor.getInt(1)+"\n");
					record_result = mCursor.getInt(1); 
					//break;
				}while (mCursor.moveToNext());
				
				if(record_result == 1)
				{
					mUtils.notify_server_result(server_addr,1); // notify su
				}
				else
				{
					mUtils.notify_server_result(server_addr,0); // notify su
				}
				
				mCursor.close();
				mDatabase.close();
				return ; 
			}
			mCursor.close();
			mDatabase.close();
			
			//huyanwei add system grant setting {
			boolean grantSettingEnabled = Settings.System.getInt(context.getContentResolver(),Settings.System.GRANT_SETTING,0) == 1;
			if(grantSettingEnabled == false)
			{
				mUtils.notify_server_result(server_addr,0); // notify su, reject.
				return ;
			}		
			//huyanwei add system grant setting } 

		    // request:
		    Intent request_intent = new Intent();
		    request_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    request_intent.putExtra("uid", uid);
		    request_intent.putExtra("pid", pid);
		    request_intent.putExtra("socket_addr", server_addr);
		    request_intent.setComponent(new ComponentName("srclib.huyanwei.permissiongrant", "srclib.huyanwei.permissiongrant.RequestDialog"));
		    context.startActivity(request_intent);
		}
	}
}
