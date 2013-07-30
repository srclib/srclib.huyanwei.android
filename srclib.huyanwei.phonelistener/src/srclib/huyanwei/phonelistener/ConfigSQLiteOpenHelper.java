package srclib.huyanwei.phonelistener;

import java.util.Locale;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ConfigSQLiteOpenHelper extends SQLiteOpenHelper {

	static final String DATABASE_NAME 			= "phonelistener.db";

	static final int    DATABASE_VERSION_NUMBER = 3;
	
	private SQLiteDatabase mSQLiteDatabase;
	
	public ConfigSQLiteOpenHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION_NUMBER);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		mSQLiteDatabase = db;
		
		String defaultconfig = "";
		
		// Create Table.
		String createtable = "create table if not exists config (id integer primary key autoincrement,name text, value integer);";
		mSQLiteDatabase.execSQL(createtable);
		
		// proximity sensor listen enable.
		defaultconfig ="insert into config(name,value) values(\"config_proximity_sensor_enable\",1);";
		mSQLiteDatabase.execSQL(defaultconfig);
		
		// proximity sensor listen enable.  answer/reject
		defaultconfig ="insert into config(name,value) values(\"config_action\",1);";
		mSQLiteDatabase.execSQL(defaultconfig);
		
		// speaker on/off
		defaultconfig ="insert into config(name,value) values(\"config_speaker\",1);";
		mSQLiteDatabase.execSQL(defaultconfig);
		
		// database inited.
		defaultconfig ="insert into config(name,value) values(\"config_inited\",0);";
		mSQLiteDatabase.execSQL(defaultconfig);
	}

	public void Init(Context context)
	{
		String sql = null ;
		int config_value;
		
		config_value = context.getResources().getInteger(R.integer.config_proximity_sensor_enable);
	    sql = String.format(Locale.ENGLISH, "update config set value=%d where name=config_proximity_sensor_enable",config_value);
	    mSQLiteDatabase.execSQL(sql);
	    
		config_value = context.getResources().getInteger(R.integer.config_action);
	    sql = String.format(Locale.ENGLISH, "update config set value=1 where name=config_action");
	    mSQLiteDatabase.execSQL(sql);
	    
	    config_value = context.getResources().getInteger(R.integer.config_speaker);
	    sql = String.format(Locale.ENGLISH, "update config set value=1 where name=config_speaker");
	    mSQLiteDatabase.execSQL(sql);

	    // inited 
	    sql = String.format(Locale.ENGLISH, "update config set value=1 where name=config_inited");
	    mSQLiteDatabase.execSQL(sql);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}
}
