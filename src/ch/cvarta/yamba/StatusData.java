package ch.cvarta.yamba;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/*
 * Abstracts access to the database
 */
public class StatusData {
	
	private static final String TAG = StatusData.class.getSimpleName();
	
	public static final int 	DB_VERSION = 4;
	public static final String 	DB_NAME = "timeline.db";
	public static final String 	TABLE = "timeline";

	public static final String 	C_ID = "_id";
	public static final String 	C_CREATED_AT = "created_at";
	public static final String 	C_SOURCE = "source";
	public static final String 	C_TEXT = "txt";
	public static final String 	C_USER = "user";
	
	//SQL Query for getting all status updates
	private static final String GET_ALL_ORDER_BY = C_CREATED_AT + " DESC";
	
	private static final String[] MAX_CREATED_AT_COLUMNS = { "max(" + StatusData.C_CREATED_AT + ")"  };
	
	private static final String[] DB_TEXT_COLUMNS = { C_TEXT };
	
	//final to ensure that this object is created only once, whichever part of the system request it first
	private final DbHelper dbHelper;
	
	//DbHelper Implementation
	class DbHelper extends SQLiteOpenHelper {
		
		

		public DbHelper(Context context){
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			String sql = "create table " + TABLE + " (" + C_ID + " int primary key, " 
						+ C_CREATED_AT + " int, "+ C_USER + " text, " + C_TEXT + " text)";
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
	
	public StatusData(Context context){
		this.dbHelper = new DbHelper(context);
		Log.i(TAG, "Initialized Data");	
	}
	
	/*
	 * Closes database
	 */
	public void close(){
		this.dbHelper.close();
	}
	
	//insert into database
	//Needs to be surrounded with a try catch block as we are using the message id
	//as the primary key --> however as we do retrieve the same msgs over and over again
	//we inevitably will get duplicates --> insertion will fail: 2 possible solutions
	//1. write logic to filter out duplicates
	//2. let sql handle the duplicates --> catch the sql exception (especially easy)
	public void insertOrIgnore(ContentValues values){
		Log.d(TAG, "insertOrIgnore " + values);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		try{
			//Conflicts will be ignored --> same msg won't be inserted twice
			db.insertWithOnConflict(TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		}catch(SQLException sqle){
			Log.d(TAG, sqle.getMessage());
		}finally{
			db.close();
		}
	}
	
	/**
	 * all the statuses in the databases, with the latest first
	 * @return Cursor where the columns are _id, created_at, user, txt
	 */
	public Cursor getStatusUpdates(){
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		return db.query(TABLE, null, null, null, null, null, GET_ALL_ORDER_BY);
	}
	
	/**
	 * SQL query sorts time stamps descending --> first one is the latest
	 * @return time stamp of the latest status we have in the database 
	 */
	public long getLatestStatusCreatedAtTime(){
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try{
			Cursor cursor = db.query(TABLE, MAX_CREATED_AT_COLUMNS, null, null, null, null, null);
			try{
				//if there is an entry --> returns create at time, otherwise just returns MIN_VALUE
				//every value should be > than MIN_VALUE
				return cursor.moveToNext() ? cursor.getLong(0) : Long.MIN_VALUE;
			}
			finally{
				cursor.close();
			}
		}
		finally{
			db.close();
		}
	}
	
	/**
	 * @param id of the status we are looking for
	 * @return Text of the status
	 */
	public String getStatusTextById(long id){
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		try{
			Cursor cursor = db.query(TABLE, DB_TEXT_COLUMNS, C_ID + "=" + id, null, null, null, null);
			try{
				return cursor.moveToNext() ? cursor.getString(0) : null;
			}
			finally{
				cursor.close();
			}
		}
		finally{
			db.close();
		}
	}
}
