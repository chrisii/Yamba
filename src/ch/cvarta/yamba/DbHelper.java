package ch.cvarta.yamba;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

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
	

	//Constructor
	public DbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
