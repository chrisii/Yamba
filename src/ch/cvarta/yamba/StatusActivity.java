package ch.cvarta.yamba;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class StatusActivity extends BaseActivity implements OnClickListener, TextWatcher{
	
	private static final String TAG = "StatusActivity";
	EditText editText;
	Button updateButton;
	//TextCount is also an View
	TextView textCount;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);
        
        //small hack:
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
          }
        
        //Find views
        editText = (EditText)findViewById(R.id.editText);
        updateButton = (Button)findViewById(R.id.buttonUpdate);
        updateButton.setOnClickListener(this);
        
        textCount = (TextView)findViewById(R.id.textCount);
        textCount.setText(R.string.charCountLimit);
        textCount.setTextColor(Color.GREEN);
        //register myself as TextWatchListener
        editText.addTextChangedListener(this);
        
    }
        
        //Asynch Posts to twitter
        class PostToTwitter extends AsyncTask<String, Integer, String>{
        	//Called to initiatite the background activity
			@Override
			protected String doInBackground(String... statuses) {
				try{
					Twitter.Status status = ((YambaApplication)getApplication()).getTwitter().updateStatus(statuses[0]);
					return status.text;
				}catch(TwitterException e){
					Log.e(TAG, e.toString());
					e.printStackTrace();
					return "Failed to post";
				}
			}
			
			//called when there's a status to be updated
			@Override
			protected void onProgressUpdate(Integer... values){
				super.onProgressUpdate(values);
				//Not used in this case
			}
			
			
			//Called once the background activity has completed
			@Override
			protected void onPostExecute(String result){
				Toast.makeText(StatusActivity.this, result, Toast.LENGTH_LONG).show();
				editText.setHint(R.string.hintText);
			}
        }
			
			//Called when button is clicked
			public void onClick(View v){
			String status = editText.getText().toString();
			new PostToTwitter().execute(status);
			Log.d(TAG, "onClicked");
			}

			@Override
			public void afterTextChanged(Editable s) {
				int charLimit = Integer.parseInt(this.getString(R.string.charCountLimit));
				int newCount = charLimit - s.length();
				textCount.setText(Integer.toString(newCount));
				textCount.setTextColor(Color.GREEN);
				if (newCount < 10){
					textCount.setTextColor(Color.YELLOW);
				}
				if (newCount < 0){
					textCount.setTextColor(Color.RED);
				}
				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				//NOT USED
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				//NOT USED
			}
    }
