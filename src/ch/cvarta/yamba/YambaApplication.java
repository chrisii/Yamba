package ch.cvarta.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.Twitter.Status;
import android.app.Application;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.SQLException;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class YambaApplication extends Application implements OnSharedPreferenceChangeListener{
	
	private static final String TAG = YambaApplication.class.getSimpleName();
	private Twitter twitter;
	private SharedPreferences prefs;
	private boolean serviceRunning;
	private StatusData statusData;
	
	/*
	 * onCreate() is called when the application is first created. The application is first created
	 * whenever any of its parts, such as an activity or a service, is first needed
	 * (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();
        //Setup Preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        Log.i(TAG, "onCreate");
		
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
		Log.i(TAG, "onTerminated");
	}
	/*
	 * Synchronized keyword is important -- all Threads have access to the Application Object
	 * The Application Object exists as long as our application is running may it be an activity, service
	 * or broadcast-receiver.
	 * 
	 * lazy initialization of the twitter object with shared preferences
	 */
    public synchronized Twitter getTwitter(){
    	if (twitter == null){
    		String username, password, apiroot;
    		username = prefs.getString("username", "");
    		password = prefs.getString("password", "");
    		apiroot = prefs.getString("apiRoot", "http://yamba.markana.com/api");
    		
    		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)
    			&& !TextUtils.isEmpty(apiroot)){
	        		//connect to twitter
	        		twitter = new Twitter(username,password);
	        		twitter.setAPIRootUrl(apiroot);
    			}
    	}
    	return twitter;
    }
    
    public StatusData getStatusData(){
    	return statusData;
    }
    
    public synchronized int fetchStatusUpdates(){
    	Log.d(TAG, "Fetching status updates");
    	Twitter twitter = this.getTwitter();
    	if (twitter == null){
    		Log.d(TAG, "Twitter connection info not initialized");
    		return 0;
    	}
    	try{
    		List<Status> statusUpdates = twitter.getFriendsTimeline();
    		long latestStatusCreatedAtTime = this.getStatusData().getLatestStatusCreatedAtTime();
    		int count = 0;
			//Insert into database StatusData takes care of duplicates
			ContentValues values = new ContentValues();
			for (Status status : statusUpdates){
				values.put(StatusData.C_ID, status.id);
				long createdAt = status.getCreatedAt().getTime();
				values.put(StatusData.C_CREATED_AT, status.createdAt.getTime());
				values.put(StatusData.C_TEXT, status.text);
				values.put(StatusData.C_USER, status.user.name);
				Log.d(TAG, "Got update with id: "+ status.getId() +  ". Saving");
				this.getStatusData().insertOrIgnore(values);
				if (latestStatusCreatedAtTime < createdAt){
					//if createtAt time is higher than the latest found in the database
					//means that there are new updates on the time line
					count++;
				}
			}
			Log.d(TAG, count > 0 ? "Got "+ count + ": Status updates" : "no new status updates");
			return count;
    	}catch(RuntimeException re){
			Log.d(TAG, "Failed to fetch status updates");
			return 0;
		}
    }

	@Override
	public synchronized void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		//invalidate twitter object so it gets recreated on next access
		twitter = null;
	}
	
	public boolean isServiceRunning(){
		return this.serviceRunning;
	}
	
	public void setServiceRunning(boolean serviceRunning){
		this.serviceRunning = serviceRunning;
	}
	
}
