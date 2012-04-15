package ch.cvarta.yamba;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.SimpleCursorAdapter.ViewBinder;
import android.widget.TextView;

public class TimelineActivity extends BaseActivity {
	
	private static final String TAG = TimelineActivity.class.getSimpleName();

	private YambaApplication appContext;
	private StatusData sdata;
	private Cursor cursor;
	private ListView listTimeLine;
	//private TimelineAdapter adapter;
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
		
	    // Check if preferences have been set
	    if (yamba.getPrefs().getString("username", null) == null) { // <2>
	      startActivity(new Intent(this, PrefsActivity.class));
	      Toast.makeText(this, R.string.msgSetupPrefs, Toast.LENGTH_LONG).show();
	    }
		
		//Find the view
		listTimeLine = (ListView)findViewById(R.id.listTimeline);
		
		//access to DB via StatusData
		appContext = (YambaApplication)this.getApplication();
		sdata = appContext.getStatusData();
		
	}

	/**
	 * to make the data as fresh as possible we query the database every time
	 * this activity is brought up front
	 */
	@Override
	protected void onResume() {
		super.onResume();
		this.setupList();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	private void setupList(){
		cursor = sdata.getStatusUpdates();
		//startManagingCursor = convenience method that manages lifecycle of a cursor
		startManagingCursor(cursor);
				
		//Set up the adapter
		//adapter = new TimelineAdapter(this, cursor);
		adapter = new SimpleCursorAdapter(this, R.layout.row, cursor, FROM, TO);
		adapter.setViewBinder(VIEW_BINDER);
		listTimeLine.setAdapter(adapter);
	}
	
	//View binder constant to inject business logic that converts a tiestamp to
	//relative time
	//When binding is handled by this ViewBinder, this method must return true. 
	//If this method returns false, SimpleCursorAdapter will attempts to handle 
	//the binding on its own.
	static final ViewBinder VIEW_BINDER = new ViewBinder(){
		
		public boolean setViewValue(View view, Cursor cursor, int columnIndex){
			
			if (view.getId() != R.id.textCreatedAt){
				//we only modify the timestamp of textCreatedAT
				return false;
			}
			//Update the created at text to relative time
			// Manually bind created at timestamp to its view
		    long timestamp = cursor.getLong(cursor
		        .getColumnIndex(StatusData.C_CREATED_AT)); // <6>
		    CharSequence relTime = DateUtils.getRelativeTimeSpanString(timestamp);
		    ((TextView) view).setText(relTime);
		    
		    return true;
		}
	};
}
