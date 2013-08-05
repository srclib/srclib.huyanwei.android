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

public class ResponseBroadcastReceiver extends BroadcastReceiver {

	private static final String TAG = "srclib.huyanwei.permissiongrant.ResponseBroadcastReceiver";
	
	private static final String RESPONSE_ACTION = "srclib.huyanwei.permissiongrant.response";
	
	@Override
	public void onReceive(Context context, Intent intent) {		
		if(intent.getAction().equals(RESPONSE_ACTION))
		{
			SQLiteDatabase mDatabase;
			DatabaseHelper hlp = new DatabaseHelper(context);
			mDatabase = hlp.getWritableDatabase();	
			Utils mUtils = new Utils(context,mDatabase);

			
			Log.d(TAG, "Result BroadcastReceiver onReceive " + intent);
			int uid = -1;
			int pid = -1;
			int res = -1;
			uid=intent.getIntExtra("uid",-1);	
			pid=intent.getIntExtra("pid",-1);	
			res=intent.getIntExtra("grant_result",-1);	
			String local_package_name = mUtils.process_to_package_name(pid);
			String local_application_name = mUtils.package_name_to_application_name(local_package_name);

			mUtils.record_log(uid,pid,local_package_name,local_application_name,res); // database.

			String str_res_success = context.getString(R.string.RESULT_SUCCESS);
			String str_res_fail = 	 context.getString(R.string.RESULT_FAIL);
			String str_res_timeout = context.getString(R.string.RESULT_TIMEOUT);
			
			/*
			// avoid app request always. case toast show always.
			Toast toast;
			if(res == 0)
			{
				toast = Toast.makeText(context, local_application_name+str_res_success+"\n", Toast.LENGTH_LONG);
			}
			else if(res == -1)
			{
				toast = Toast.makeText(context, local_application_name+str_res_fail+"\n", Toast.LENGTH_LONG);
			}	
			else
			{
				toast = Toast.makeText(context, local_application_name+str_res_timeout+"\n", Toast.LENGTH_LONG);
			}
			toast.show();
			*/
			
			if(RequestDialog.getInstance() != null)
			{
				RequestDialog.getInstance().finish();
			}
			
			mDatabase.close();
			//this.finish();
		}
	}
}
