package ch.cvarta.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DbHelper extends SQLiteOpenHelper {
	

	public static final String TAG = "DbHelper";
	public static final String DB_NAME = "timeline.db";
	public static final int 	DB_VERSION = 3;
	public static final String TABLE = "timeline";
	public static final String C_ID = BaseColumns._ID;
	public static final String C_CREATED_AT = "created_at";
	public static final String C_SOURCE = "source";
	public static final String C_TEXT = "txt";
	public static final String C_USER = "user";
	
	Context context;
	

	public DbHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String sql = "create table " + TABLE + " (" + C_ID + " int primary key, " + C_CREATED_AT + " int,"+ C_SOURCE + " text," + C_USER + " text," + C_TEXT + " text)";  
		
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
		db.execSQL("drop table if exists " + TABLE);
		Log.d(TAG, "onUpgrade");
		//run onCreate to get a new database
		onCreate(db);
	}

}
