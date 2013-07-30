package srclib.huyanwei.phonelistener;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.Settings.NameValueTable;
import android.text.TextUtils;
import android.util.Log;

public class ConfigContentProvider extends ContentProvider {
	
    private static final String TAG = "srclib.huyanwei.phonelistener.DatabaseProvider";
    
	public static final String AUTHORITY = "srclib.huyanwei.phonelistener"; // AndroidManifest.xml
	
	public static final String PARAMETER_TABLE  = "config";
    public static final String PARAMETER_NOTIFY = "notify";
	
    // content://srclib.huyanwei.phonelistener/config/
    // config table
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/"+PARAMETER_TABLE);
	
    private ConfigSQLiteOpenHelper 	mConfigSQLiteOpenHelper;
    private	SQLiteDatabase 			mSQLiteDatabase;
    
    private int mDatabaseInited = 0 ;
    
    // content://srclib.huyanwei.phonelistener/config/1?notify=true
    // content://srclib.huyanwei.phonelistener/config/2?notify=false
    
    /**
     * The content:// style URL for a given row, identified by its id.
     *
     * @param id The row id.
     * @param notify True to send a notification is the content changes.
     *
     * @return The unique content URL for the specified row.
     */
    static Uri getContentUri(long id, boolean notify) {
        return Uri.parse("content://" + AUTHORITY +
                "/" + PARAMETER_TABLE + "/" + id + "?" +
                PARAMETER_NOTIFY + "=" + notify);
    }
    
	@Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = mConfigSQLiteOpenHelper.getWritableDatabase();
        int count = db.delete(args.table, args.where, args.args);
        
        if (count > 0) 
        	sendNotify(uri);

        return count;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
        SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
	}

	@Override
    public Uri insert(Uri uri, ContentValues initialValues) {
		// TODO Auto-generated method stub
        SqlArguments args = new SqlArguments(uri);

        SQLiteDatabase db = mConfigSQLiteOpenHelper.getWritableDatabase();
        final long rowId = db.insert(args.table, null, initialValues);
        
        if (rowId <= 0) 
        	return null;

        uri = ContentUris.withAppendedId(uri, rowId);
        sendNotify(uri);

        return uri;
	}

	public void config(ConfigSQLiteOpenHelper hlp,SQLiteDatabase mDatabase)
	{
		Cursor mCursor = mDatabase.rawQuery("select value from config where name=config_inited limit 1;",null);
		if(mCursor.getCount() > 0)
		{
			mCursor.moveToFirst();
			mDatabaseInited = mCursor.getInt(0); // get value.
			/*
			do
			{
				mDatabaseInited = mCursor.getInt(0); // get value. 
				break;
			}while (mCursor.moveToNext());
			*/
		}
		
		mCursor.close();
		
		if(mDatabaseInited == 0)  // not inited yet.
		{
			hlp.Init(this.getContext());
		}
	}
	
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
        // init database
		mConfigSQLiteOpenHelper = new ConfigSQLiteOpenHelper(this.getContext(),null,null,1);
		mSQLiteDatabase 		= mConfigSQLiteOpenHelper.getWritableDatabase();		
		config(mConfigSQLiteOpenHelper,mSQLiteDatabase); // ��ʼ�� xml ������. 
        return true;
	}

	@Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(args.table);

        SQLiteDatabase db = mConfigSQLiteOpenHelper.getWritableDatabase();
        Cursor result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
	}

	@Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SqlArguments args = new SqlArguments(uri, selection, selectionArgs);

        SQLiteDatabase db = mConfigSQLiteOpenHelper.getWritableDatabase();
        int count = db.update(args.table, values, args.where, args.args);
        
        if (count > 0)
        	sendNotify(uri);

        return count;
    }
	
    private void sendNotify(Uri uri) {
        String notify = uri.getQueryParameter(PARAMETER_NOTIFY);
        if (notify == null || "true".equals(notify)) 
        {
            getContext().getContentResolver().notifyChange(uri, null);
        }
    }
    
    // sql helper class 
    static class SqlArguments {
        public final String table;
        public final String where;
        public final String[] args;

        SqlArguments(Uri url, String where, String[] args) 
        {
        	Log.d(TAG,"url.getPathSegments().size()="+url.getPathSegments().size());
            if (url.getPathSegments().size() == 1)
            {
                this.table = url.getPathSegments().get(0);
                this.where = where;
                this.args = args;
            }
            else if (url.getPathSegments().size() != 2) 
            {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
            else if (!TextUtils.isEmpty(where))
            {
                throw new UnsupportedOperationException("WHERE clause not supported: " + url);
            }
            else
            {
                this.table = url.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(url);                
                this.args = null;
            }
        }

        SqlArguments(Uri url)
        {
            if (url.getPathSegments().size() == 1) 
            {
                table = url.getPathSegments().get(0);
                where = null;
                args = null;
            } 
            else
            {
                throw new IllegalArgumentException("Invalid URI: " + url);
            }
        }
    }
}
