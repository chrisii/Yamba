package ch.cvarta.yamba;

import java.util.List;

import winterwell.jtwitter.Twitter;
import winterwell.jtwitter.TwitterException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	
	static final String TAG = "UpdateService";
	static final int DELAY = 60000;  //Interval of service request in ms
	
	volatile private boolean runFlag = false;
	
	private Updater updater;
	private YambaApplication appcontext;
	

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
					//loop over timeline just log all messages:
					if (timeline!=null){
						for (Twitter.Status status : timeline){
							Log.d(TAG, String.format("%s: %s", status.user, status.text));
						}
					}
					Thread.sleep(DELAY);
				} catch(InterruptedException ie){
					UpdaterService.this.runFlag = false;
				}
			}
		}
		
		
	}
}
