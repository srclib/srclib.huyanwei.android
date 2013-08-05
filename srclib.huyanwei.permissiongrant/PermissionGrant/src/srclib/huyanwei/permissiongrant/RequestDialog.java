package srclib.huyanwei.permissiongrant;

import android.app.Activity;
import android.os.Bundle;

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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
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

public class RequestDialog extends Activity {
	
	String TAG = "srclib.huyanwei.permissiongrant.RequestDialog";

	public static RequestDialog mInstance; // global .
	
	TextView mTextView;
	CheckBox mCheckBox;
	Button mYesButton;
	Button mNoButton;
	Button mAlwaysButton;
	Intent mIntent;
	String mName;
	
	int mUid;
	int mPid;
	String mServer_addr;
	String mPackage_name ; 
	String mApplication_name ; 

	Utils mUtils;
	
	SQLiteDatabase mDatabase;
	Cursor mCursor;
	
	public static RequestDialog getInstance()
	{
		return mInstance;
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mInstance = this ;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);		
		
		setContentView(R.layout.grant_request);

		
		DatabaseHelper hlp = new DatabaseHelper(this);
		mDatabase = hlp.getWritableDatabase();
        
		mUtils = new Utils(this,mDatabase);
		
		mYesButton = (Button) findViewById(R.id.YesButton);
		mNoButton = (Button) findViewById(R.id.NoButton);		
		mCheckBox = (CheckBox)findViewById(R.id.RememberCheckBox);
		mTextView = (TextView) findViewById(R.id.ProcessName);

		mIntent = getIntent();
		mUid = mIntent.getIntExtra("uid", -1);
		mPid = mIntent.getIntExtra("pid", -1);
		mServer_addr =  mIntent.getStringExtra("socket_addr");
		mPackage_name = mUtils.process_to_package_name(mPid);
		mApplication_name = mUtils.package_name_to_application_name(mPackage_name);
		
		mYesButton.setOnClickListener(new View.OnClickListener()
		{
			/*@Override*/
			public void onClick(View v)
			{	
				// always yes.
				if(mCheckBox.isChecked())
				{
					String local_package_name = mUtils.process_to_package_name(mPid);
					String local_application_name = mUtils.package_name_to_application_name(local_package_name);
					mUtils.record_grant(mUid,mPid,local_package_name,local_application_name,1);
				}

				mUtils.notify_server_result(mServer_addr,1);

				finish();
			}
		});

		mNoButton.setOnClickListener(new View.OnClickListener()
		{
			/*@Override*/
			public void onClick(View v)
			{
				// always no
				if(mCheckBox.isChecked())
				{
					String local_package_name = mUtils.process_to_package_name(mPid);
					String local_application_name = mUtils.package_name_to_application_name(local_package_name);
					mUtils.record_grant(mUid,mPid,local_package_name,local_application_name,0);
				}
				
				mUtils.notify_server_result(mServer_addr,0); // notify su

				finish();
			}
		});       		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuItem item;
		item = menu.add(Menu.NONE, 0,0, R.string.VIEW_LIST);
		//item.setIcon(R.drawable.clear_grant_list);

		item = menu.add(Menu.NONE, 1,1, R.string.VIEW_LOG);
		//item.setIcon(R.drawable.view_grant_log);

		return true;
		//return super.onCreateOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        Toast toast;
		switch (item.getItemId())
        {
		case 0:			
		    Intent viewlist_intent = new Intent();
		    viewlist_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    viewlist_intent.setComponent(new ComponentName("srclib.huyanwei.permissiongrant", "srclib.huyanwei.permissiongrant.GrantListActivity"));
		    startActivity(viewlist_intent);
			break;
		case 1:
		    Intent viewlog_intent = new Intent();
		    viewlog_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		    viewlog_intent.setComponent(new ComponentName("srclib.huyanwei.permissiongrant", "srclib.huyanwei.permissiongrant.GrantLogActivity"));
		    startActivity(viewlog_intent);
		    break;
		default:
		    break;
        }        
		return super.onOptionsItemSelected(item);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mInstance = null ;
		mDatabase.close();		
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		// TODO Auto-generated method stub
		if((keyCode == KeyEvent.KEYCODE_BACK))
		{
			//mUtils.notify_server_result(mServer_addr,0); // notify su
			//return true;
			
			return super.onKeyDown(keyCode, event);
		}			
		else
		{
			return super.onKeyDown(keyCode, event);
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyLongPress(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyLongPress(keyCode, event);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyMultiple(int, int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyMultiple(keyCode, repeatCount, event);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		mName = "";
		String package_name = mUtils.process_to_package_name(mPid);
		String application_name = mUtils.package_name_to_application_name(package_name);
	 	mName += application_name + "\n"; //mName += mApplication_name + "\n";
	 	
		String pre_text = getString(R.string.Question);				
		//String process_info = String.format("uid=%d,pid=%d",mUid,mPid);
		//huyanwei fix ar language String.format bug.
		String uid_str = Integer.toString(mUid);
		String pid_str = Integer.toString(mPid);
		String process_info = String.format("uid=%s,pid=%s",uid_str,pid_str);

		mTextView.setText(pre_text+"\r\n"+process_info+"\r\n\r\n"+mName);		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onRestart()
	 */
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub		
		super.onStop();		
	}

}
