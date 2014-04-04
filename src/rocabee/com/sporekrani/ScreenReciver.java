package rocabee.com.sporekrani;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReciver extends BroadcastReceiver{

	   public static boolean wasScreenOn = false;
	   
	    @Override
	    public void onReceive(Context context, Intent intent) {
	        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
	            // do whatever you need to do here
	            wasScreenOn = false;
	            Log.e("screen state","screen is off");
	        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
	            // and do whatever you need to do here
	            wasScreenOn = true;
	            Log.e("screen state","screen is on");
	        }
	    }


}
