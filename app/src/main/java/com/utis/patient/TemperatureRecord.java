package com.utis.patient;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.database.Cursor;

public class TemperatureRecord {
	public int id;
	public int id_external;
	public int id_family;
	public int modified;
	public double temperaure;
	public Date mDate;

	
	   /**
     * Constructor. 
     */
    public TemperatureRecord() {
    	 id = 0;
    }

    public TemperatureRecord(Cursor c) {
		int colid = c.getColumnIndex(TemperatureTable.ID);
    	id = c.getInt(colid);
		colid = c.getColumnIndex(TemperatureTable.ID_EXTERNAL);
    	id_external = c.getInt(colid);
		colid = c.getColumnIndex(TemperatureTable.ID_FAMILY);
    	id_family = c.getInt(colid);
		colid = c.getColumnIndex(TemperatureTable.MODIFIED);
    	modified = c.getInt(colid);
		colid = c.getColumnIndex(TemperatureTable.TEMP);
    	temperaure = c.getDouble(colid);
    	
    	colid = c.getColumnIndex(TemperatureTable.M_DATE);
     	String date_str = c.getString(colid);
     	SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
     	java.util.Date date;
     	try {
     		date = formatter.parse(date_str); 
     		mDate = new Date(date.getTime());
     	} catch (ParseException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
     	}
    	
    }
    
	public String toString() {
		return id_external + " " + id_family;
	}


}
