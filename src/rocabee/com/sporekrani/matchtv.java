/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package rocabee.com.sporekrani;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import org.apache.cordova.*;



public class matchtv extends DroidGap
{		
	
	public boolean IS_UNDER_DEVELOPMENT=true;
	public String url = "http://www.sporekrani.com/index.php/?platform=android";
	//public String url = "http://54.217.229.27/sporekrani/index.php/?platform=android&development";
	public boolean IS_COME_FROM_CALENDAR=false;
    public long lastupdated;
	public long idletime = 30*60*1000;
	
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
		int screenSize = getResources().getConfiguration().screenLayout &
		    Configuration.SCREENLAYOUT_SIZE_MASK;

		switch(screenSize) {
		    
		    case Configuration.SCREENLAYOUT_SIZE_LARGE:
		        //Toast.makeText(this, "Large screen",Toast.LENGTH_LONG).show();
		        
		        /**
		         * check if it is tablet
		         */
		        if(this.isTablet()){
		        	
		           setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		        }else{
		        	
		           setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		        }
		        break;
		    case Configuration.SCREENLAYOUT_SIZE_NORMAL:
		    	//Toast.makeText(this, "normal screen",Toast.LENGTH_LONG).show();
		        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		        break;
		    case Configuration.SCREENLAYOUT_SIZE_SMALL:
		    	//Toast.makeText(this, "small screen",Toast.LENGTH_LONG).show();
		        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		        break;
		    default:
		    	//Toast.makeText(this, "unknown screen",Toast.LENGTH_LONG).show();
		        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		

		
		
		
		this.lastupdated = System.currentTimeMillis();
		
		
	     // initialize receiver
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        BroadcastReceiver mReceiver = new ScreenReciver();
        registerReceiver(mReceiver, filter);
		
		
		try{
		 super.onCreate(savedInstanceState);
       
        //url="http://54.217.229.27/sporekrani/index.php/?platform=android";
        
        if(getIntent().getExtras() != null){
        	Bundle extras = getIntent().getExtras();
        	if(extras.getString("event_url")!=null){
        	 url=extras.getString("event_url")+"&plateform=android";
        	}
        }
        super.init(); // Calling this is necessary to make this work
        super.setIntegerProperty("loadUrlTimeoutValue", 60000);
        appView.addJavascriptInterface(this, "MainActivity");
        super.setIntegerProperty("splashscreen", R.drawable.splash_lunch);
        super.loadUrl(url,3000);
        
		}catch(Exception e){
			String err = (e.getMessage()==null)?"SD Card failed":e.getMessage();
			Log.e("sdcard-err2:",err); 
		}
	        
       
    }
	
	/*
	 * when user try to add something to the calendar the variable IS_COME_FROM_CALENDAR should set to true
	 * */
	
	public void reset_calendar_track_value(){
	    this.IS_COME_FROM_CALENDAR=true;
	}
	
	
	
	/*
	 * check if the device is tablet
	 */
	public boolean isTablet(){
        Display display = getWindowManager().getDefaultDisplay();
		int width=display.getWidth();
		int height=display.getHeight();
        //new AlertDialog.Builder(this.getActivity()).setTitle("Uyarmak").setMessage("width:"+display.getWidth()+",height:"+display.getHeight()).setNeutralButton("ok", null).show();
		
        int min= Math.min(width, height);
        if(min>=601){
  	      return true;
  	    }
		return false;
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.exit:
	            this.finish();
	            return true;
	        case R.id.go_to_home:
	            loadhomepage();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	
	public void loadhomepage(){
	   super.loadUrl(url);
	}
	
	
	public void start_activity(View view,String event_url,String event, String event_id){
    	
		Intent notificationIntent = new Intent(this, displaynotification.class);
    	notificationIntent.putExtra("event_url", event_url);
    	notificationIntent.putExtra("event_name", event);
    	notificationIntent.putExtra("event_id", event_id);
    	startActivity(notificationIntent);
    	
	}
    
    public void customFunctionCalled(String date,String hour,String min,String event,String event_url, String event_id) {
        Log.e("event", event);
        Log.e("event_url", event_url);
        Log.e("date", date);
        Log.e("hour", hour);
        Log.e("min", min);
        Log.e("event_id", event_id);
        
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);                 
        
        //---get current date and time---
        Calendar calendar = Calendar.getInstance();       

       
        String[] date_arr = date.split("-");
        int match_year = Integer.parseInt(date_arr[2]);
        int match_month = Integer.parseInt(date_arr[1]);
        int match_day = Integer.parseInt(date_arr[0]);
        int match_hour= Integer.parseInt(hour);
        int match_min= Integer.parseInt(min);
    
        Log.e("match_year",date_arr[2]);
        Log.e("match_month",date_arr[1]);
        Log.e("match_day",date_arr[0]);
        //---sets the time for the alarm to trigger---
        calendar.set(Calendar.YEAR, match_year);
        calendar.set(Calendar.MONTH, match_month-1);
        calendar.set(Calendar.DAY_OF_MONTH, match_day);                 
        calendar.set(Calendar.HOUR_OF_DAY, match_hour);
        calendar.set(Calendar.MINUTE, match_min);
        calendar.set(Calendar.SECOND, 0);
        
        Long current_time = System.currentTimeMillis();
        Long calendar_time = calendar.getTimeInMillis();
        
        Log.e("current time", current_time.toString() );
        Log.e("calendar time", calendar_time.toString() );
        
        //---PendingIntent to launch activity when the alarm triggers-
        Intent notificationIntent = new Intent(this, displaynotification.class);
    	notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	notificationIntent.putExtra("event_url", event_url);
    	notificationIntent.putExtra("event_name", event);
    	notificationIntent.putExtra("event_id", event_id);
    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //---sets the alarm to trigger---
        alarmManager.set(AlarmManager.RTC_WAKEUP, 
        calendar.getTimeInMillis(), contentIntent);
        
        
        //this.start_activity(this.appView, event_url,event,event_id);
        //this.show_nofification(this.appView, date, hour, min, event, event_url);
        
    }
    
    
    public void open_link(String url){
       Log.e("url is",url);
       this.IS_COME_FROM_CALENDAR=true;
       Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
       startActivity(browserIntent);
    
    }
    
    /*
     * when call this function, the whole app should reload the home page
     */
    public void exitApp(){
    	loadhomepage();
    }
    
    
    /*
     * when user resume the application, the application should be on home page
     */
    @Override
    public void onResume(){
    	super.onResume();
    	//Log.e("on resume","resume the application");
    	
    	// only when screen turns on
    	//Log.e("on resume",getString(ScreenReciver.wasScreenOn));
    	//Toast toast = Toast.makeText(getApplicationContext(), getString(ScreenReciver.wasScreenOn), 10000);
    	//toast.show();
    	if(ScreenReciver.wasScreenOn){
    		System.out.println("screen is turn on");
    		this.IS_COME_FROM_CALENDAR=true;
    	}
    	
    	long current_time=System.currentTimeMillis();
    	long distance= current_time - this.lastupdated;
    	if(distance<this.idletime){
    	   this.IS_COME_FROM_CALENDAR=true;
    	}
    	
    	
    	
    	if(!this.IS_COME_FROM_CALENDAR){
    	  loadhomepage();
    	}else{
    	  this.IS_COME_FROM_CALENDAR=false;
    	}
    }


	/**
	 * when application go to background, this should be excuted 
	 **/
    @Override 
     public void onPause(){
    	super.onPause();
        Log.e("application pause","application go to background!"); 
        
        /**
         * exit the application immediately 
         */
        if(!this.IS_COME_FROM_CALENDAR){
          this.finish();
        }
        
     }
    
    
    
    
	/**
	 * ovarride touch event, it will be used to track user system idle
	 **/
	
	 @Override
	    public void onUserInteraction()
	    {
	        super.onUserInteraction();
	        //Toast toast = Toast.makeText(getApplicationContext(), "user intercating", 100000);
	        //toast.show();
	        this.lastupdated=System.currentTimeMillis();
	        
	    }
        
  }

