package com.utis.patient;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

public class CommonClass {
	private static final boolean D = true;
	private static final String TAG = "CommonClass";
	public static final int clrDkRed = 0xff8B0000;
	public static final String developerPhoneNumber = "+380933877905";
	public static final String PREF_NAME = "MyPref";
	public static final String noData = "н/д";
	public static String lastErrorMsg = "";
	public static boolean keepLog;
	public static boolean newVerAvail = false;
	private static final String LAST_DICTIONARY_UPDATE_FILENAME = "last_dict_update.txt";
	private static final String LAST_REPLICATOR_TIME_FILENAME = "last_repl_time.txt";
	
	public static void showMessage(Context mContext, String msgTitle, String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
	    builder
	    .setTitle(msgTitle)
	    .setMessage(msg)
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) {       
	            //do some thing here which you need
	        	dialog.dismiss();
	        }
	    });             
	    AlertDialog alert = builder.create();
	    alert.show();
	}
	
	//Create a new file and write some data
	private static boolean writeDate2File(Context mContext, Date aDate, String fileName) {
		boolean res = false;
		try { 
			FileOutputStream mOutput = mContext.openFileOutput(fileName, Activity.MODE_PRIVATE); 
//			String data = aDate.toString(); 
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");  
			String data = df.format(aDate);
			if (D) Log.e(TAG, "writeLastUpdate = " + data);	
			mOutput.write(data.getBytes()); 
			mOutput.close();
			res = true;
		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		return res;
	}
	
	private static String readDateFromFile(Context mContext, String fileName) {
		String res = "";
		try { 
			FileInputStream mInput = mContext.openFileInput(fileName);
			byte[] data = new byte[12]; 
			mInput.read(data); 
			mInput.close(); 
			res = new String(data); 
			if (res.length() == 0)
				res = "201309010000";
		} catch (FileNotFoundException e) { 
			e.printStackTrace(); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		}
		return res;
	}

	public static boolean writeReplTime(Context mContext, Date aDate) {
		return writeDate2File(mContext, aDate, LAST_REPLICATOR_TIME_FILENAME);
	}
	
	public static String readReplTime(Context mContext) {
		return readDateFromFile(mContext, LAST_REPLICATOR_TIME_FILENAME);
	}	
	
	public static boolean writeLastUpdate(Context mContext, Date aDate) {
		return writeDate2File(mContext, aDate, LAST_DICTIONARY_UPDATE_FILENAME);
	}

	public static String readLastUpdate(Context mContext) {
		return readDateFromFile(mContext, LAST_DICTIONARY_UPDATE_FILENAME);
	}
	
    public static void Copy2Clipboard(String text, Activity activity) {
		int sdk = android.os.Build.VERSION.SDK_INT;
		if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
		    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
		    clipboard.setText(text);
		} else {
		    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE); 
		    android.content.ClipData clip = android.content.ClipData.newPlainText("", text);
		    clipboard.setPrimaryClip(clip);
		}
    }

    public static void OpenOnlineWarrants(Context mContext) {
    	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https:////85.238.112.13/contenti/Ol3"));
    	mContext.startActivity(browserIntent);
    }
	

	public static int convertDpToPixel(Context mContext, float dp) {
	       DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
	       float px = dp * (metrics.densityDpi / 160f);
	       return (int) px;
	}
	
	public static void showUndo(final View viewContainer) {
	    viewContainer.setVisibility(View.VISIBLE);
	    viewContainer.setAlpha(1);
	    viewContainer.animate().alpha(0.4f).setDuration(5000)
	        .withEndAction(new Runnable() {

	          @Override
	          public void run() {
	        	  viewContainer.setVisibility(View.GONE);
	          }
	        });
	}

	public static int getAge(Date dateOfBirth) {

	    Calendar today = Calendar.getInstance();
	    Calendar birthDate = Calendar.getInstance();

	    int age = 0;

	    birthDate.setTime(dateOfBirth);
	    if (birthDate.after(today)) {
	        throw new IllegalArgumentException("Can't be born in the future");
	    }

	    age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

	    // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year   
	    if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
	            (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
	        age--;

	     // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
	    } else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
	              (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
	        age--;
	    }

	    return age;
	}	
	
}
