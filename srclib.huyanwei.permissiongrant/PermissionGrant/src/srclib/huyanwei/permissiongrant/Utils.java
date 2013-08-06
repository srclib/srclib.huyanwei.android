package srclib.huyanwei.permissiongrant;

import android.app.Activity;
import android.os.Bundle;


import android.net.LocalSocket;
import android.net.LocalSocketAddress;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
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

public class Utils {
	
	static final String TAG = "srclib.huyanwei.permissiongrant.Utils";
	
	public static Context mContext;	
	
	public static SQLiteDatabase mDatabase;

	/*
	public Utils(Context mContent) {
		super();
		this.mContext = mContent;
	}
	*/
	
	public Utils(Context Content,SQLiteDatabase  Database) {
		super();
		this.mContext = Content;
		this.mDatabase = Database ;
	}
	
	public static String process_to_package_name(int pid)
	{
		try
		{
			String  Command = String.format(Locale.ENGLISH, "ps %d",pid); // 只取指定的pid的信息
			String package_name = null ;
			Process p = Runtime.getRuntime().exec(Command);
			InputStream reader = p.getInputStream();
			Thread.sleep(200);
/*			
			byte[] buff = new byte[10000];
			int read = reader.read(buff, 0, buff.length);
			String str = new String(buff);
*/
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String str = "" , line = "" ;
			try {
				while ((line = in.readLine()) != null) {   
					 str += line + "\n";                  
				   }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//Log.d(TAG,"string="+str);
			//String pattern = String.format("^\\w+\\s+%d\\s+(.*?)\n", pid);
			//huyanwei modify it for ar language.
			String pid_str = Integer.toString(pid) ;
			String pattern = String.format("^\\w+\\s+%s\\s+(.*?)\n",pid_str);
			Pattern regex = Pattern.compile(pattern, Pattern.MULTILINE);				
			Matcher match = regex.matcher(str);
			
			package_name = "";
			while (match.find())
			{
				String[] strings = match.group(1).split(" ");

				package_name = strings[strings.length -1];	// first one

				//Log.d(TAG,"before package_name ="+package_name);

				break ;
			}
			package_name = package_name.replace("'","");
			package_name = package_name.replace("\"","");

			//Log.d(TAG,"after package_name="+package_name);

			return package_name;
		}
		catch (Exception e)
		{
			return "";
		}		
	}
	
	public static String package_name_to_application_name(String local_package_name)
	{
		 Hashtable local_app_table = new Hashtable();		 
		 PackageManager local_packageManager ;
		 local_packageManager = mContext.getPackageManager();
		 List<PackageInfo> packageInfoList = local_packageManager.getInstalledPackages(0);		

		 for (int i = 0; i < packageInfoList.size(); i++) 
		 {
			   PackageInfo pinfo = (PackageInfo) packageInfoList.get(i);
			   String app_name =	 local_packageManager.getApplicationLabel(pinfo.applicationInfo).toString();
			   String package_name = pinfo.applicationInfo.packageName;		
			   local_app_table.put(package_name, app_name);
		 }		
		 
		if(local_app_table.get(local_package_name) == null)
		{	
			return (String)local_package_name;
		}
		else
		{
			return (String)local_app_table.get(local_package_name);
		}
	}	
	
	public static void record_grant(int uid,int pid, String package_name,String application_name,int result)
	{
		if(package_name != null)
		{
			package_name = package_name.replace("\"","");		
			package_name = package_name.replace("'","");
		}
		if(application_name != null)
		{
			application_name = application_name.replace("\"","");
			application_name = application_name.replace("'","");
		}
		
		if(mDatabase != null)
		{
			//String sql = String.format("insert into grant_list(uid,pid,package_name,application_name,result) values(%d,%d,\"%s\",\"%s\",%d);",uid,pid,package_name,application_name,result);

			//huyanwei modify it for ar language.
			
			String uid_str = Integer.toString(uid) ;
			String pid_str = Integer.toString(pid) ;
			String result_str = Integer.toString(result) ;
			String sql = String.format("insert into grant_list(uid,pid,package_name,application_name,result) values(%s,%s,\"%s\",\"%s\",%s);",uid_str,pid_str,package_name,application_name,result_str);
			mDatabase.execSQL(sql);
		}
		
	}

	public static void record_log(int uid,int pid, String package_name,String application_name,int result)
	{
		if(package_name != null)
		{
			package_name = package_name.replace("\"","");		
			package_name = package_name.replace("'","");
		}
		if(application_name != null)
		{
			application_name = application_name.replace("\"","");
			application_name = application_name.replace("'","");
		}	
		if(mDatabase != null)
		{
			//huyanwei add check record total :
			Cursor mCursor = mDatabase.rawQuery("select * from grant_log ;",null);	
			Log.d(TAG, "mCursor.getCount()="+mCursor.getCount());
			if(mCursor.getCount() >= 2000 )	// max 2000 
			{				
				int record_min_id = 0 ;
				Cursor mCursor_min = mDatabase.rawQuery("select id from grant_log order by id asc limit 1 ;",null);
				if(mCursor_min.getCount()!= 0)
				{	
					while (mCursor_min.moveToNext())
					{
						record_min_id = mCursor_min.getInt(0); 
						break;
					}

					if(record_min_id>0)
					{
						//String sql_del = String.format("delete from grant_log where id =%d ;",record_min_id);

						//huyanwei modify it for ar language.
						
						String record_min_id_str = Integer.toString(record_min_id) ;
						String sql_del = String.format("delete from grant_log where id =%s ;",record_min_id_str);
						mDatabase.execSQL(sql_del);
					}
				}
				mCursor_min.close();
			}
			else
			{
				//huyanwei Test record total :
				/*
				if(mCursor.getCount() < 900 )
				{
					for(int i = 0 ; i<100; i++)
					{
						String sql_ist = String.format("insert into grant_log(uid,pid,package_name,application_name,result) values(%d,%d,\"%s\",\"%s\",%d);",2000,2000,"srclib.huyanwei.permissiongrant","grant"+i+1,0);
						mDatabase.execSQL(sql_ist);		
					}
				}
				*/
			}
			mCursor.close();
/*
			String sql = String.format("insert into grant_log(uid,pid,package_name,application_name,result) values(%d,%d,\"%s\",\"%s\",%d);",uid,pid,package_name,application_name,result);
*/
			// huyanwei modify it for ar language
			String uid_str = Integer.toString(uid) ; 
			String pid_str = Integer.toString(pid) ;
			String result_str = Integer.toString(result) ;
			String sql = String.format("insert into grant_log(uid,pid,package_name,application_name,result) values(%s,%s,\"%s\",\"%s\",%s);",uid_str,pid_str,package_name,application_name,result_str);

			mDatabase.execSQL(sql);

		}
	}
	
	public static void notify_server_result(String server_addr,int allow)
	{
		LocalSocket socket = new LocalSocket();
		OutputStream os = null;;
		try {
		    socket.connect(new LocalSocketAddress(server_addr,LocalSocketAddress.Namespace.FILESYSTEM));
		    os = socket.getOutputStream();
		    if (allow >1)
		    {
			 os.write("ALWAYS".getBytes());
		    }
		    else if(allow == 1)
		    {
			 os.write("ALLOW".getBytes());
		    }
		    else
		    {
			os.write("DENY".getBytes());
		    }
		    os.flush();
		    os.close();
		    socket.close();
		} catch (IOException e) {
		    Log.e(TAG, "Failed to write to socket", e);
		}
	}
}
