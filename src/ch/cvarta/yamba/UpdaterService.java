package ch.cvarta.yamba;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class UpdaterService extends Service {
	
	static final String TAG = "UpdateService";

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreated");
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.d(TAG, "onDestroyed");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		super.onStartCommand(intent, flags, startId);
		Log.d(TAG, "onStartCommand");
		return START_STICKY;
	}

}
