package ch.cvarta.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	

	private static final String TAG = "DbHelper";
	private static final String DB_NAME = "timeline.db";
	private static final int 	DB_VERSION = 1;
	private static final String TABLE = "timeline";
	private static final String C_ID = BaseColumns._ID;
	private static final String C_CREATED_AT = "created_at";
	private static final String C_SOURCE = "source";
	private static final String C_TEXT = "txt";
	private static final String C_USER = "user";
	
	Context context;
	

	public DbHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = String.format("create table %s (%s int primary key, %s int, %s text, %s text)", TABLE, C_ID, C_CREATED_AT, C_USER, C_TEXT);
		
		db.execSQL(sql);
		
		Log.d(TAG, "onCreate: SQL: " + sql);
		

	}

	/*
	 * Method is called whenever newVersion!= oldVersion
	 * (non-Javadoc)
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//drops the old database
		db.execSQL("drop table if exists" + TABLE);
		Log.d(TAG, "onUpgrade");
		//run onCreate to get a new database
		onCreate(db);
	}

}
