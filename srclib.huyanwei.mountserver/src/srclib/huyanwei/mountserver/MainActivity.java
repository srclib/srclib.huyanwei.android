package srclib.huyanwei.mountserver;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageManager;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author huyanwei
 *
 */
public class MainActivity extends Activity {
	
	final private String TAG = "srclib.huyanwei.mountserver";	
	private StorageManager mStorageManager = null;	
	
	//private MountService mMountService = null ;
	//final private IMountService mMountService;
	
	private String MSG_COMMAND    = "COMMAND";
	private String MSG_MOUNT_ACT1 = "MOUNT";
	private String MSG_MOUNT_ACT2 = "UMOUNT";	
	
	private Button m_mount_button;
	private Button m_umount_button;
	private TextView m_mount_state;

	//private String STORAGE_PATH = "/mnt/sdcard" ;    // GB , ICS
	private String STORAGE_PATH = "/storage/sdcard0" ; // JB 
	
	private String system_storage_path = "";
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(Message msg)
		{
			if(msg.getData().getString(MSG_COMMAND).equals(MSG_MOUNT_ACT1))
			{								
				if(mStorageManager != null)
				{
					mStorageManager.mountVolume(STORAGE_PATH);
				}
			}
			else if(msg.getData().getString(MSG_COMMAND).equals(MSG_MOUNT_ACT2))
			{	
				if(mStorageManager != null)
				{
					mStorageManager.unmountVolume(STORAGE_PATH,true,false);
				}
			}
		}
	};
	
	View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch(arg0.getId())
			{
				case R.id.mount:
					sendMessageTohandler(1);
					//m_mount_button.setVisibility(visibility)
					break;
				case R.id.umount:
					sendMessageTohandler(0);
					break;					
			}			
		}
	};
		
	public void sendMessageTohandler(int mountable)
	{
		
        Message msg = new Message();
		msg.setTarget(mHandler);
		Bundle bundle = new Bundle();
		if(mountable == 1)
		{
			bundle.putString(MSG_COMMAND,MSG_MOUNT_ACT1);
		}
		else
		{
			bundle.putString(MSG_COMMAND,MSG_MOUNT_ACT2);
		}
		msg.setData(bundle);
		msg.sendToTarget();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		m_mount_button = (Button)findViewById(R.id.mount);			
		m_mount_button.setOnClickListener(mOnClickListener);
		
		m_umount_button = (Button)findViewById(R.id.umount);			
		m_umount_button.setOnClickListener(mOnClickListener);
		
		m_mount_state = (TextView)findViewById(R.id.mount_state);		

		mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
		if(mStorageManager == null)
		{
			Log.d(TAG,"can't talk to mStorageManager.");
		}
		
		//mMountService = IMountService.Stub.asInterface(ServiceManager.getService("mount"));
		//mMountService = (MountService)getSystemService("mount");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub		
		String storage_information = "";
		
		system_storage_path = mStorageManager.getInternalStoragePath(); // hide api
		storage_information += "getInternalStoragePath()="; 
		storage_information += system_storage_path;
		storage_information += "\n";
		
		system_storage_path = mStorageManager.getExternalStoragePath(); // hide api
		storage_information += "getExternalStoragePath()=";
		storage_information += system_storage_path;
		storage_information += "\n";
		
		system_storage_path = mStorageManager.getDefaultPath(); // hide api
		storage_information += "getDefaultPath()=";
		storage_information += system_storage_path;
		storage_information += "\n";

		system_storage_path = mStorageManager.getVolumeState("/storage/sdcard0"); // hide api
		storage_information += "getVolumeState(\"/storage/sdcard0\")=";
		storage_information += system_storage_path;
		storage_information += "\n";

		system_storage_path = mStorageManager.getVolumeState("/storage/sdcard1"); // hide api
		storage_information += "getVolumeState(\"/storage/sdcard1\")=";
		storage_information += system_storage_path;
		storage_information += "\n";
		
		m_mount_state.setText(storage_information);
		m_mount_state.setTextColor(0xffff0000);
		
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

}
