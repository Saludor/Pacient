package com.utis.patient;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import android.database.Cursor;

public class FamilyRecord {
	public int id, id_external;
	public String surname, name1, name2, combinedName;
	public int gender, modified;
	public Date birthDate;
	public byte[] photo;
	
	   /**
     * Constructor. 
     */
    public FamilyRecord() {
    	 id = 0;
    }

    public FamilyRecord(Cursor c) {
		int colid = c.getColumnIndex(FamilyTable.ID);
    	id = c.getInt(colid);
		colid = c.getColumnIndex(FamilyTable.ID_EXTERNAL);
    	id_external = c.getInt(colid);
		colid = c.getColumnIndex(FamilyTable.MODIFIED);
    	modified = c.getInt(colid);
		colid = c.getColumnIndex(FamilyTable.SURNAME);
    	surname = c.getString(colid);
		colid = c.getColumnIndex(FamilyTable.NAME1);
    	name1 = c.getString(colid);
		colid = c.getColumnIndex(FamilyTable.NAME2);
    	name2 = c.getString(colid);
    	if (name2.length() > 0)
    		combinedName = surname + " " + name1.substring(0, 1) + ". " + name2.substring(0, 1) + ".";
    	else
    		combinedName = surname + " " + name1.substring(0, 1) + ". ";
		colid = c.getColumnIndex(FamilyTable.GENDER);
    	gender = c.getInt(colid);
		colid = c.getColumnIndex(FamilyTable.PHOTO);
    	photo = c.getBlob(colid);
    	
    	colid = c.getColumnIndex(FamilyTable.BIRTH_DATE);
     	String date_str = c.getString(colid);
     	SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
     	java.util.Date date;
     	try {
     		date = formatter.parse(date_str); 
     		birthDate = new Date(date.getTime());
     	} catch (ParseException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
     	}
    	
    }
    
	public String toString() {
		return id_external + " " + surname;
	}

}
