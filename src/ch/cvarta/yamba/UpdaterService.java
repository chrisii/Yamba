package ch.cvarta.yamba;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	
	static final String TAG = "UpdateService";
	static final int DELAY = 60000;  //Interval of service request in ms
	
	volatile private boolean runFlag = false;
	
	private Updater updater;
	

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
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
		Log.d(TAG, "onDestroyed");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		
		//start the updater in separate thread
		runFlag = true;
		updater.start();
		
		Log.d(TAG, "onStartCommand");
		return START_STICKY;
	}
	class Updater extends Thread{
		public Updater(){
			super("UpdaterService-Updater");
		}

		@Override
		public void run() {
			while(UpdaterService.this.runFlag){
				Log.d(TAG, "updater running");
				try{
					//Do some work
					Thread.sleep(DELAY);
				} catch(InterruptedException ie){
					UpdaterService.this.runFlag = false;
				}
			}
		}
		
		
	}
}
