package ch.cvarta.yamba;

import android.content.Context;
import android.database.Cursor;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class TimelineAdapter extends SimpleCursorAdapter {

	//FROM - To specifies mapping from database to a row in the listview
	static final String[] FROM = { StatusData.C_CREATED_AT, StatusData.C_USER,
			StatusData.C_TEXT };
	//representing id in the view row.xml --> specifies how a row looks like
	static final int[] TO = {R.id.textCreatedAt, R.id.textUser, R.id.textText};
	
	public TimelineAdapter(Context context, Cursor c) {
		super(context, R.layout.row, c, FROM, TO);
	}
	
	 // This is where the actual binding of a cursor to view happens
	  @Override
	  public void bindView(View row, Context context, Cursor cursor) { // <5>
	    super.bindView(row, context, cursor);

	    // Manually bind created at timestamp to its view
	    long timestamp = cursor.getLong(cursor
	        .getColumnIndex(StatusData.C_CREATED_AT)); // <6>
	    TextView textCreatedAt = (TextView) row.findViewById(R.id.textCreatedAt); // <7>
	    textCreatedAt.setText(DateUtils.getRelativeTimeSpanString(timestamp)); // <8>
	  }
}
