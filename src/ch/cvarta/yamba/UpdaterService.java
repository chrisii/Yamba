package ch.cvarta.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	
	static final String TAG = "UpdateService";
	static final int DELAY = 60000;  //Interval of service request in ms
	
	volatile private boolean runFlag = false;
	
	private Updater updater;
	private YambaApplication appcontext;
	
	private DbHelper dbhelper;
	private SQLiteDatabase db;
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		appcontext = (YambaApplication)getApplication();
		updater = new Updater();
		
		dbhelper = new DbHelper(this);
		
		Log.d(TAG, "onCreated");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//Stopping the Thread;
		runFlag = false;
		this.updater.interrupt();
		updater = null;
		appcontext.setServiceRunning(false);
		Log.d(TAG, "onDestroyed");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		//start the updater in separate thread
		runFlag = true;
		updater.start();
		appcontext.setServiceRunning(true);
		
		Log.d(TAG, "onStartCommand");
		return START_STICKY;
	}
	class Updater extends Thread{
		
		private List<Twitter.Status> timeline;
		
		public Updater(){
			super("UpdaterService-Updater");
		}

		@Override
		public void run() {
			while(UpdaterService.this.runFlag){
				Log.d(TAG, "updater running");
				try{
					//get timeline
					try{
						this.timeline = appcontext.getTwitter().getFriendsTimeline();
					} catch(NullPointerException twe){
						Log.d(TAG, "Failed to connect to twitter service");
					}
					//open the database for writing
					db = dbhelper.getWritableDatabase();
					ContentValues values = new ContentValues();
					//loop over timeline just log all messages:
					if (timeline!=null){
						for (Twitter.Status status : timeline){
							//Insert into database
							values.clear();
							values.put(DbHelper.C_ID, status.id);
							values.put(DbHelper.C_CREATED_AT, status.createdAt.getTime());
							values.put(DbHelper.C_SOURCE, status.source);
							values.put(DbHelper.C_TEXT, status.text);
							values.put(DbHelper.C_USER, status.user.name);
							
							//insert into database
							//Needs to be surrounded with a try catch block as we are using the message id
							//as the primary key --> however as we do retrieve the same msgs over and over again
							//we inevitably will get duplicates --> insertion will fail: 2 possible solutions
							//1. write logic to filter out duplicates
							//2. let sql handle the duplicates --> catch the sql exception (especially easy)
							try {
								db.insertOrThrow(DbHelper.TABLE, null, values);
							} catch (SQLException e) {
								//just ignore
							}
							Log.d(TAG, String.format("%s: %s", status.user, status.text));
						}
					}
					//close db
					db.close();
					Thread.sleep(DELAY);
				} catch(InterruptedException ie){
					UpdaterService.this.runFlag = false;
				}
			}
		}
		
		
	}
}
