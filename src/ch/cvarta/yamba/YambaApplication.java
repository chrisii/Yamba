package ch.cvarta.yamba;

import winterwell.jtwitter.Twitter;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class YambaApplication extends Application implements OnSharedPreferenceChangeListener{
	
	private static final String TAG = YambaApplication.class.getSimpleName();
	private Twitter twitter;
	private SharedPreferences prefs;
	
	
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

	@Override
	public synchronized void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		//invalidate twitter objekt so it gets recreated on next access
		twitter = null;
	}
	
}
