package rocabee.com.sporekrani;

import org.apache.cordova.DroidGap;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class displaynotification extends DroidGap {
    
	 @Override
	 public void onCreate(Bundle savedInstanceState)
	   {
	        super.onCreate(savedInstanceState);
	        Intent intent = getIntent();
	        String event_url = intent.getStringExtra("event_url");
	        String event_name = intent.getStringExtra("event_name");
	        String event_id = intent.getStringExtra("event_id");
	        
	        Log.e("event url in notification ",event_url);
	        Log.e("event name in notification",event_name);
	        Log.e("event id in notification",event_id);
	        
	        show_nofification(this.appView, event_name, event_url, event_id);
	        
	      //---destroy the activity---
	        finish();
	        
	   }
	
	
	
	public void show_nofification(View view, String event,String event_url, String event_id){
    	String ns = Context.NOTIFICATION_SERVICE;
    	NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        int icon = R.drawable.ic_launcher;        
    	CharSequence tickerText = "Matchtv"; // ticker-text
    	long when = System.currentTimeMillis();         
    	Context context = getApplicationContext();     
    	CharSequence contentTitle = "Matchtv";  
    	CharSequence contentText = event;      
    	Intent notificationIntent = new Intent(this, matchtv.class);
    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	notificationIntent.putExtra("event_url", event_url);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
    	Notification notification = new Notification(icon, tickerText, when);
    	//notification.number=1;
    	notification.flags = Notification.FLAG_INSISTENT | Notification.FLAG_AUTO_CANCEL | Notification.FLAG_ONLY_ALERT_ONCE;
    	//notification.sound = Uri.parse("android.resource://" + getPackageName() + "/"+R.raw.message);
    	notification.defaults=Notification.DEFAULT_VIBRATE;
    	notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        final int HELLO_ID = Integer.parseInt(event_id);
        //Log.e("hello_id in notification", event_id);
        
    	mNotificationManager.notify(HELLO_ID, notification);
    }

}
