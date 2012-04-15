package ch.cvarta.yamba;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class BaseActivity extends Activity {
	YambaApplication yamba;
	
	@Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    yamba = (YambaApplication) getApplication(); // <3>
	  }
	
	//Called first time user clicks the menu button
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
	
	//Called when an options item is clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch (item.getItemId()) {
		case R.id.itemPrefs:
			startActivity(new Intent(this, PrefsActivity.class)
			.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		case R.id.itemToggleService:
			if (yamba.isServiceRunning()){
				stopService(new Intent(this, UpdaterService.class));
			} else{
				startService(new Intent(this, UpdaterService.class));
			}break;
		case R.id.itemTimeline:
			startActivity(new Intent(this, TimelineActivity.class));
			break;
		case R.id.itemStatus:
			startActivity(new Intent(this,StatusActivity.class)
			.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
			break;
		default:
			break;
		}
		//to consume the event return true!
		return true;
	}
	// Called every time menu is opened
	  @Override
	  public boolean onMenuOpened(int featureId, Menu menu) { // <6>
	    MenuItem toggleItem = menu.findItem(R.id.itemToggleService); // <7>
	    if (yamba.isServiceRunning()) { // <8>
	      toggleItem.setTitle(R.string.titleServiceStop);
	      toggleItem.setIcon(android.R.drawable.ic_media_pause);
	    } else { // <9>
	      toggleItem.setTitle(R.string.titleServiceStart);
	      toggleItem.setIcon(android.R.drawable.ic_media_play);
	    }
	    return true;
	  }
}
