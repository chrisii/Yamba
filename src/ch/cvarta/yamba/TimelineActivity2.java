package ch.cvarta.yamba;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimelineActivity2 extends Activity {
	
	private static final String TAG = TimelineActivity2.class.getSimpleName();

	private YambaApplication appContext;
	private StatusData sdata;
	private Cursor cursor;
	private ListView listTimeLine;
	private SimpleCursorAdapter adapter;
	
	//FROM - To specifies mapping from database to a row in the listview
	static final String[] FROM = { StatusData.C_CREATED_AT, StatusData.C_USER,
		StatusData.C_TEXT };
	//representing id in the view row.xml --> specifies how a row looks like
	static final int[] TO = {R.id.textCreatedAt, R.id.textUser, R.id.textText};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.timeline);
		
		//Find the view
		listTimeLine = (ListView)findViewById(R.id.listTimeline);
		
		//access to DB via StatusData
		appContext = (YambaApplication)this.getApplication();
		sdata = appContext.getStatusData();
		this.onResume();
		
	}

	/**
	 * to make the data as fresh as possible we query the database every time
	 * this activity is brought up front
	 */
	@Override
	protected void onResume() {
		super.onResume();
		cursor = sdata.getStatusUpdates();
		//startManagingCursor = convenience method that manages lifecycle of a cursor
		startManagingCursor(cursor);
		
		/*//Print out all the updates
		String user, text, output;
		while (cursor.moveToNext()){
			user = cursor.getString(cursor.getColumnIndex(StatusData.C_USER));
			text = cursor.getString(cursor.getColumnIndex(StatusData.C_TEXT));
			output = String.format("%s: %s\n", user, text);
			Log.d(TAG, "Printing " + output);
		}*/
		
		//Set up the adapter
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		listTimeLine.setAdapter(adapter);
	}
}
