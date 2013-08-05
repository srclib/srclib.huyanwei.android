package srclib.huyanwei.permissiongrant;


import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;

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

public class GrantListActivity extends Activity {
	
	static String TAG= "srclib.huyanwei.permissiongrant.GrantListActivity";
	
	SQLiteDatabase mDatabase;
	Utils mUtils;
	
	ListView  mListView;
	ScrollView mScrollView;
	TextView mTextView;
	
	ArrayList< HashMap<String, Object>> listItem ;
	SimpleAdapter mlistAdapter;
	   
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        
		setContentView(R.layout.grant_log);
		
		DatabaseHelper hlp = new DatabaseHelper(this);
		mDatabase = hlp.getWritableDatabase();
        
		mUtils = new Utils(this,mDatabase);
		
		mListView   = (ListView)findViewById(R.id.grant_log_list);
		mTextView   = (TextView)findViewById(R.id.grant_title);		
		
		listItem = new ArrayList<HashMap<String, Object>>();

	}

	//长按菜单响应函数   
    @Override  
    public boolean onContextItemSelected(MenuItem item) {   
        //setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目");  
    	String sql ;
    	Toast toast;
    	switch(item.getItemId())
    	{
    		case 0:
    		    sql = String.format("delete from grant_list;");
    		    mDatabase.execSQL(sql);

    		    listItem.clear();		    
    		    mlistAdapter.notifyDataSetChanged();
    		    
    		    toast = Toast.makeText(this, getString(R.string.CLS_LIST), Toast.LENGTH_LONG);
    		    toast.show();
    		    
    		    this.finish();
    		    
    		    return true;    			
    		    
    		case 1:
    		    Intent viewlog_intent = new Intent();
    		    viewlog_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    		    viewlog_intent.setComponent(new ComponentName("srclib.huyanwei.permissiongrant", "srclib.huyanwei.permissiongrant.GrantLogActivity"));
    		    startActivity(viewlog_intent);
    			return true;
    			
    		case 2:
    		    sql = String.format("delete from grant_log;");
    		    mDatabase.execSQL(sql);
    		    
    		    toast = Toast.makeText(this, getString(R.string.CLS_LOG), Toast.LENGTH_LONG);
    		    toast.show();
    		    
    		    return true;    			
    		default:
    			break;
    	}    	
        return super.onContextItemSelected(item);   
    }   
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mDatabase.close();
		super.onDestroy();
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
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub		
		
		mTextView.setText(R.string.VIEW_LIST);
		
		listItem.clear();
		
	    Cursor local_cursor = mDatabase.rawQuery("select package_name,application_name,result from grant_list;",null);
	    while (local_cursor.moveToNext())
	    {	    	
	    	HashMap<String, Object> map = new HashMap<String, Object>();
	    	
			map.put("application_name", local_cursor.getString(1));
			map.put("package_name", local_cursor.getString(0));
			if(local_cursor.getInt(2) == 1)
			{
				map.put("action", getString(R.string.ACTION_ALLOW));
			}
			else
			{				
				map.put("action",getString(R.string.ACTION_DENY));
			}
			listItem.add(map);		
	    }
	    local_cursor.close();
		
		mlistAdapter =new SimpleAdapter(this, listItem, R.layout.list_item, new String[]{"application_name","package_name","action"}, new int[]{R.id.application_name,R.id.package_name,R.id.action}); 
		//设置显示ListView 
		mListView.setAdapter(mlistAdapter);

		//添加点击   
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {    
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//setTitle("点击第"+arg2+"个项目");				
			}   
        });
		
		//添加长按点击   
		mListView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {   
                menu.setHeaderTitle(R.string.app_name);      
                menu.add(0, 0, 0, R.string.CLS_LIST);
                menu.add(0, 1, 1, R.string.VIEW_LOG);
                menu.add(0, 2, 2, R.string.CLS_LOG);
            }
        });	
		
		
		super.onResume();
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
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
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
	 * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyUp(keyCode, event);
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return super.onCreateOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onOptionsItemSelected(item);
	}

}
