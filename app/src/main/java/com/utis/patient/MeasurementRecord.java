package com.utis.patient;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.database.Cursor;

public class MeasurementRecord {
	public static final int SYSTOLIC_HIGH_MARGIN = 140;
	public static final int SYSTOLIC_LOW_MARGIN = 110;
	public static final int DIASTOLIC_HIGH_MARGIN = 100;
	public static final int DIASTOLIC_LOW_MARGIN = 70;
	public static final int HR_HIGH_MARGIN = 90;
	public static final int HR_LOW_MARGIN = 50;
	
	public int id, id_external, id_family;
	public int systolic, diastolic, heartRate, modified;
	public Date mDate;
	public String dateStr, dateStrLbl;

	
	   /**
     * Constructor. 
     */
    public MeasurementRecord() {
    	 id = 0;
    }

    public MeasurementRecord(Cursor c) {
		int colid = c.getColumnIndex(MeasurementTable.ID);
    	id = c.getInt(colid);
		colid = c.getColumnIndex(MeasurementTable.ID_EXTERNAL);
    	id_external = c.getInt(colid);
		colid = c.getColumnIndex(MeasurementTable.ID_FAMILY);
    	id_family = c.getInt(colid);
		colid = c.getColumnIndex(MeasurementTable.MODIFIED);
    	modified = c.getInt(colid);
		colid = c.getColumnIndex(MeasurementTable.SYSTOLIC);
    	systolic = c.getInt(colid);
		colid = c.getColumnIndex(MeasurementTable.DIASTOLIC);
    	diastolic = c.getInt(colid);
		colid = c.getColumnIndex(MeasurementTable.HEART_RATE);
    	heartRate = c.getInt(colid);
    	
    	colid = c.getColumnIndex(MeasurementTable.M_DATE);
     	dateStr = c.getString(colid);
     	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
     	java.util.Date date;
     	try {
     		date = formatter.parse(dateStr); 
     		mDate = new Date(date.getTime());
     		formatter = new SimpleDateFormat("dd.MM HH:mm");
     		dateStrLbl = formatter.format(mDate);
     	} catch (ParseException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
     	}
    	
    }
    
	public String toString() {
//		return id_external + " " + id_family;
		return String.format("%d / %d : %d", systolic, diastolic, heartRate);
	}


}
