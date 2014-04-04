package rocabee.com.plugin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract.Events;
import android.util.Log;
import android.widget.TextView;

public class CalendarPlugin extends CordovaPlugin {
final static String ISO8601DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
public JSONArray pub_args;
public CalendarPlugin cal;


@Override
public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {

this.pub_args=args;
this.cal = this;

if (action.equals("createEvent")) {
	
	try {
		cal.createEvent(pub_args.getString(0), pub_args.getString(1), pub_args.getString(2), pub_args.getString(3), pub_args.getString(4));
	} catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
  
	return true;

}
 
return false;


}




private void createEvent(String title, String location, String description, String startDate, String endDate){
 Context context =this.cordova.getActivity().getApplicationContext(); 
 Calendar calendarStart = CalendarPlugin.getCalendarFromISO(startDate);
 //Calendar calendarEnd = CalendarPlugin.getCalendarFromISO(endDate);
 if(this.Checkevent(context,calendarStart,title)){
     Log.e("this event already exsit:",title);
     try{
      new AlertDialog.Builder(this.cordova.getActivity()).setTitle("Uyarmak").setMessage("Bu karşılaşma takvim'de bulunuyor").setNeutralButton("Yakın", null).show();
     }catch(Exception e){
       Log.e("ERROR MESSAGE:",e.getMessage());
     }
  }else{
	  
	  new AlertDialog.Builder(this.cordova.getActivity()).setTitle("Onay").setMessage("Karşılaşmayı takvime eklemek istiyor musunuz?").setNegativeButton("Hayır", null ).setPositiveButton("Evet", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		     try { 
		    	Calendar calendarStart = CalendarPlugin.getCalendarFromISO(pub_args.getString(3));
		        Calendar calendarEnd = CalendarPlugin.getCalendarFromISO(pub_args.getString(4));
		        Intent intent = new Intent(Intent.ACTION_EDIT);
		        intent.setType("vnd.android.cursor.item/event");
		        intent.putExtra(Events.TITLE, pub_args.getString(0));
		        intent.putExtra(Events.EVENT_LOCATION, pub_args.getString(1));
		        intent.putExtra(Events.DESCRIPTION, pub_args.getString(2));
		        intent.putExtra("beginTime", calendarStart.getTimeInMillis());
		        intent.putExtra("endTime", calendarEnd.getTimeInMillis());
		        intent.putExtra("hasAlarm",0);
		        cal.cordova.getActivity().startActivity(intent);
		     }catch (JSONException e) {
		 		// TODO Auto-generated catch block
		 		e.printStackTrace();
		 	 } 
		    }
		}).show();  
  }
 }

private boolean Checkevent(Context context, Calendar startDate, String title){
	String str_date=String.valueOf(startDate.getTimeInMillis());
	Log.e("start date:",str_date);
	Log.e("title:",title);
	/*Log.e("end date:",endDate);*/
	/*check if the event already in the calendar*/
	ContentResolver cr = context.getContentResolver();
	Cursor cursor = cr.query(Uri.parse("content://com.android.calendar/events"), new String[]{ "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" }, "title='"+title+"' and dtstart='"+str_date+"'", null, null);
    int evnet_number = cursor.getCount();
    cursor.close();
	if(evnet_number>0){
	   //Log.e("this event already there!","");
	    return true;
	}else{
	   //Log.e("this event is new","");
		return false;
	}
	
	/*String add = null;
	cursor.moveToFirst();
	String[] CalNames = new String[cursor.getCount()];
	int[] CalIds = new int[cursor.getCount()];
	for (int i = 0; i < CalNames.length; i++) {
	    CalIds[i] = cursor.getInt(0);
	    CalNames[i] = "Event"+cursor.getInt(0)+": \nTitle: "+ cursor.getString(1)+"\nDescription: "+cursor.getString(2)+"\nStart Date: "+cursor.getString(3)+"\nEnd Date : "+new Date(cursor.getLong(4))+"\nLocation : "+cursor.getString(5);
	    Log.e("event:",CalNames[i]);
	    if(add == null)
	        add = CalNames[i];
	    else{
	        add += CalNames[i];
	    }           
	    //((TextView)findViewById(R.id.calendars)).setText(add);

	    cursor.moveToNext();
	}*/
   // return false;
}


public static Calendar getCalendarFromISO(String dateString) {
//dateString = dateString.trim().replaceAll(":00$", ":00"); // Changing format for SimpleDateFormat
//dateString = dateString.trim().replaceAll("T", " "); // Changing format for SimpleDateFormat
Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
SimpleDateFormat dateformat = new SimpleDateFormat(ISO8601DATEFORMAT, Locale.getDefault());

try {
Date date = dateformat.parse(dateString);
calendar.setTime(date);
} catch (ParseException e) {
e.printStackTrace();
}

return calendar;
  }

}
