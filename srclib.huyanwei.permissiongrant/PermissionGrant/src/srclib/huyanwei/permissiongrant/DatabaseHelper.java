package srclib.huyanwei.permissiongrant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DatabaseHelper extends SQLiteOpenHelper
{
	static final int DATABASE_VERSION_NUMBER = 3; 
	
	public DatabaseHelper(Context context)
	{
		super(context, "permissiongrant.sqlite", null, DATABASE_VERSION_NUMBER);
	}

	public void InitData(SQLiteDatabase db)
	{
		String WhitelistData1="insert into grant_white_list(uid,package_name,application_name,result) values(1000,\"srclib.huyanwei.bubble\",\"srclib.huyanwei.bubble\",1);";
		db.execSQL(WhitelistData1); // gsensor

		String WhitelistData2="insert into grant_white_list(uid,package_name,application_name,result) values(1000,\"com.speedsoftware.rootexplorer\",\"com.speedsoftware.rootexplorer\",0);";
		db.execSQL(WhitelistData2);// RootExplorer

		String WhitelistData3="insert into grant_white_list(uid,package_name,application_name,result) values(1000,\"com.noshufou.android.su\",\"com.noshufou.android.su\",0);";
		db.execSQL(WhitelistData3);// Superuser

		String WhitelistData4="insert into grant_white_list(uid,package_name,application_name,result) values(1000,\"com.qihoo.appstore\",\"com.qihoo.appstore\",0);";
		db.execSQL(WhitelistData4);// 360 

		String WhitelistData5="insert into grant_white_list(uid,package_name,application_name,result) values(1000,\"com.tencent.qqpimsecure\",\"com.tencent.qqpimsecure\",0);";
		db.execSQL(WhitelistData5);// TencentMobileManager
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		/*
		String ddlWhitelist = "create table grant_list (_id integer primary key, name text, count integer);";
		db.execSQL(ddlWhitelist);
		*/
		String ddllist = "create table if not exists grant_list (id integer primary key autoincrement, uid integer,pid integer,package_name text,application_name text, result integer);";
		db.execSQL(ddllist);

		String ddlWhitelist = "create table if not exists grant_white_list (id integer primary key autoincrement, uid integer,pid integer,package_name text,application_name text, result integer);";
		db.execSQL(ddlWhitelist);

		// Log Table
		String ddlLog = "create table if not exists grant_log (id integer primary key autoincrement, uid integer,pid integer,package_name text,application_name text, result integer);";
		db.execSQL(ddlLog);

		InitData(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		String drop_list="drop table if exists grant_list;";
		db.execSQL(drop_list);

		String drop_white_list="drop table if exists grant_white_list;";
		db.execSQL(drop_list);
		
		String drop_log="drop table if exists grant_log;";
		db.execSQL(drop_log);
		
		onCreate(db);
	}
}
