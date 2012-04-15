package ch.cvarta.yamba;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class TimelineActivity1 extends Activity {
	
	private static final String TAG = TimelineActivity1.class.getSimpleName();

	private YambaApplication appContext;
	private StatusData sdata;
	private Cursor cursor;
	private TextView textTimeline;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.timeline_basic);
		
		//Find the view
		textTimeline = (TextView)findViewById(R.id.textTimeline);
		
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
		
		//Print out all the updates
		String user, text, output;
		while (cursor.moveToNext()){
			user = cursor.getString(cursor.getColumnIndex(StatusData.C_USER));
			text = cursor.getString(cursor.getColumnIndex(StatusData.C_TEXT));
			output = String.format("%s: %s\n", user, text);
			Log.d(TAG, "Printing " + output);
			textTimeline.append(output);
		}
		
	}
	
	

}
