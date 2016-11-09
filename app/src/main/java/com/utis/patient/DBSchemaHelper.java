package com.utis.patient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBSchemaHelper extends SQLiteOpenHelper {
	private static final boolean D = true;
	private static final String TAG = "DBHelper";
	private static final String ZERO_CONDITION = " = 0 ";
	private static final String NOT_ZERO_CONDITION = " <> 0 ";
	public static final String REC_STAT_DEL_CONDITION = " = 2 ";
	public static final String REC_STAT_NODEL_CONDITION = " < 2 ";
	private static final String DATABASE_NAME = "patient1.db";
	// TOGGLE THIS NUMBER FOR UPDATING TABLES AND DATABASE
	private static final int DATABASE_VERSION = 6;
	public final SimpleDateFormat dateFormatDayYY = new SimpleDateFormat("yyyy-MM-dd");
	public final SimpleDateFormat dateFormatDay = new SimpleDateFormat("dd.MM.yyyy");
	
	public final SimpleDateFormat dateFormatYY = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
	
	public final SimpleDateFormat dateFormatMM = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	public final SimpleDateFormat dateFormatMSYY = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	public final SimpleDateFormat dateFormatMS = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
	
	DBSchemaHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// CREATE DB TABLES
		createCommonTables(db);
		if (D) Log.d(TAG, "Tables created"); 
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (D) { Log.w(TAG, "Upgrading database FROM version "
				+ oldVersion + " to " + newVersion + ",	which will destroy all old data"); };
		// Implement how to "move" your application data
		// during an upgrade of schema versions.
		// Move or delete data as required. Your call.		
		// KILL PREVIOUS TABLES IF UPGRADED
		dropTable(db, FamilyTable.TABLE_NAME);
		dropTable(db, MeasurementTable.TABLE_NAME);
		dropTable(db, TemperatureTable.TABLE_NAME);
		// CREATE NEW INSTANCE OF SCHEMA
		onCreate(db);
	}

	public void dropTable(SQLiteDatabase db, String tableName) {
		try {
			db.execSQL("DROP TABLE IF EXISTS " + tableName);
		} catch (Exception e) {
			Log.e("dropTable: "+ tableName, e.getMessage());
		}
	}
		
	public boolean emptyTable(String tableName) {
		SQLiteDatabase sd = getWritableDatabase();
		int result = 0;
		try {
			result = sd.delete(tableName, "1", null);
		} catch (Exception e) {
			Log.e("LOG_TAG", e.getMessage());
		}
		return (result > 0);		
	}

	public void createCommonTables(SQLiteDatabase db) {
		try {
			createFamilyTable(db);
			createMeasurementTable(db);
			createTemperatureTable(db);
			createLogsTable(db);
		} catch (Exception e) {
			Log.e("createCommonTables", e.getMessage());
		}
	}
	
	public void createFamilyTable(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + FamilyTable.TABLE_NAME + 
				" (" + FamilyTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ FamilyTable.ID_EXTERNAL + " LONG,"
				+ FamilyTable.SURNAME + " TEXT,"
				+ FamilyTable.NAME1 + " TEXT,"
				+ FamilyTable.NAME2 + " TEXT,"
				+ FamilyTable.GENDER + " INTEGER,"
				+ FamilyTable.MODIFIED + " INTEGER DEFAULT 1,"
				+ FamilyTable.PHOTO + " BLOB,"
				+ FamilyTable.BIRTH_DATE + " TEXT);");
		} catch (Exception e) {
			Log.e("createFamilyTable", e.getMessage());
		}
	}	

	public void createMeasurementTable(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + MeasurementTable.TABLE_NAME + 
				" (" + MeasurementTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ MeasurementTable.ID_EXTERNAL + " LONG,"
				+ MeasurementTable.MODIFIED + " INTEGER DEFAULT 1,"
				+ MeasurementTable.SYSTOLIC + " INTEGER,"
				+ MeasurementTable.DIASTOLIC + " INTEGER,"
				+ MeasurementTable.HEART_RATE + " INTEGER,"
				+ MeasurementTable.ID_FAMILY + " LONG,"
				+ MeasurementTable.M_DATE + " TEXT);");
		} catch (Exception e) {
			Log.e("createMeasurementTable", e.getMessage());
		}
	}	
		
	public void createTemperatureTable(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + TemperatureTable.TABLE_NAME + 
				" (" + TemperatureTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ TemperatureTable.ID_EXTERNAL + " LONG,"
				+ TemperatureTable.MODIFIED + " INTEGER DEFAULT 1,"
				+ TemperatureTable.TEMP + " REAL,"
				+ TemperatureTable.ID_FAMILY + " LONG,"
				+ TemperatureTable.M_DATE + " TEXT);");
		} catch (Exception e) {
			Log.e("createTemperatureTable", e.getMessage());
		}
	}	
		
	public void createLogsTable(SQLiteDatabase db) {
		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS " + LogsTable.TABLE_NAME + 
				" (" + LogsTable.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
				+ LogsTable.LEVEL + " INTEGER,"
				+ LogsTable.MODIFIED + " INTEGER DEFAULT 0,"
				+ LogsTable.MSG_DATE + " TEXT,"
				+ LogsTable.MSG + " TEXT);");
		} catch (Exception e) {
			Log.e("createLogsTable", e.getMessage());
		}
	}	
	
	/* ************ COMMON ****************************** */
	private Boolean isRecordPresent(String query) {
		Boolean result = true;
		Cursor c = null;
		if (query.length() > 0) {
			try {
				SQLiteDatabase sd = getWritableDatabase();
		    	c = sd.rawQuery(query, null);
				result = c.moveToNext();
	    	} catch (Exception ex) {
	        	ex.printStackTrace();
	        } finally {
				if (c != null)
					c.close();
	        }					
		}
		return result;
	}

	public long getTableCount(String query) {
		long result = 0;
		Cursor c = null;
		SQLiteDatabase sd = getReadableDatabase();
    	try {
    		c = sd.rawQuery(query, null);
    		if (c != null && c.moveToFirst()) {
	    		result = c.getLong(0);
    		}
    	} catch (Exception e) {
    		result = -1;
    		if (D) Log.e(TAG, "getTableCount exception = " + e.getMessage());
		} finally {
			if (c != null)
				c.close();
		}
		return result;
	}
	
	/* ****************************************** */
	public long getLogsCount() {
		String query = "SELECT COUNT("+ LogsTable.ID +") FROM " + LogsTable.TABLE_NAME;
		return getTableCount(query);
	}
	
	public long addLogItem(int level, Date m_date, String msg) {
		java.sql.Date sqlDate = new java.sql.Date(m_date.getTime());
		long result = 0;
		try {
			ContentValues cv = new ContentValues();
			cv.put(LogsTable.LEVEL, level);
			cv.put(LogsTable.MSG_DATE, dateFormatMSYY.format(sqlDate));			
			cv.put(LogsTable.MSG, msg);
	
			SQLiteDatabase sd = getWritableDatabase();		
			result = sd.insert(LogsTable.TABLE_NAME, LogsTable.MSG, cv);
		} catch (Exception e) {
			if (D) Log.e(TAG, "addLogItem exception = " + e.getMessage());
		}
		if (result < 0) Log.d(TAG, "addLogItem - Error");
		return result;
	}
	
	public String[] getDistinctLogsDates() {
    	Cursor c;
		String[] dates = null;
		String logDate;
		String query = "SELECT DISTINCT SUBSTR("+ LogsTable.MSG_DATE + ",1,10)AS "+ LogsTable.MSG_DATE +
    			" FROM " + LogsTable.TABLE_NAME;
		SQLiteDatabase sd = getWritableDatabase();

		try {
			int cntr = 0;
	    	c = sd.rawQuery(query, null);
	    	dates = new String[c.getCount()];
	    	while (c.moveToNext()) {
	    		int colid = c.getColumnIndex(LogsTable.MSG_DATE);
	    		logDate = c.getString(colid);
	         	java.util.Date date;
	         	try {
	         		date = dateFormatDayYY.parse(logDate); 
		    		dates[cntr] = dateFormatDay.format(date);
		    		cntr++;
	         	} catch (ParseException e) {
	     			if (D) Log.e(TAG, "Exception: " + e.getMessage());
	         	}		    		    				   	    		
	    	}
	    	return dates;
		} catch(Exception e) {
			if (D) Log.e(TAG, "getDistinctLogsDates exception = " + e.getMessage());
		} 		   	
    	return dates;		
	}
	
	public void clearLogs(String logDate) {
		String sWhere;
		if (logDate != null)
			sWhere = "SUBSTR("+ LogsTable.MSG_DATE + ",1,10) = \""+ logDate + "\"";
		else
			sWhere = "1";
		SQLiteDatabase sd = getWritableDatabase();
		try {
			sd.delete(LogsTable.TABLE_NAME, sWhere, null);
		} catch (Exception e) {
			if (D) Log.e(TAG, "clearLogs exception = " + e.getMessage());
		}
	}
	
	public void clearOldLogs(String logDate) {
		String sWhere;
		if (logDate != null)
			sWhere = "SUBSTR("+ LogsTable.MSG_DATE + ",1,10) <= \""+ logDate + "\"";
		else
			sWhere = "1";
		try {
			SQLiteDatabase sd = getWritableDatabase();		
			sd.delete(LogsTable.TABLE_NAME, sWhere, null);
		} catch (Exception e) {
			if (D) Log.e(TAG, "clearOldLogs exception = " + e.getMessage());
		}
	}
	
	/* ****************************************** */
	public long getFamilyCount() {
		String query = "SELECT COUNT("+ FamilyTable.ID +") FROM " + FamilyTable.TABLE_NAME;
		return getTableCount(query);
	}
	
	private Boolean isFamilyMemberPresent(String surname, String name, String patronimic, Date birth) {
		java.sql.Date sqlDate = new java.sql.Date(birth.getTime());		
		Boolean result = true;
		String query = "SELECT "+ FamilyTable.ID +" FROM " + 
				FamilyTable.TABLE_NAME + " WHERE " + FamilyTable.SURNAME + " = \"" + surname + 
				"\" AND " + FamilyTable.NAME1 + " = \"" + name +
				"\" AND " + FamilyTable.NAME2 + " = \"" + patronimic +
				"\" AND " + FamilyTable.BIRTH_DATE + " = \"" + dateFormatDay.format(sqlDate) + "\"";
		result = isRecordPresent(query);
		return result;
	}
	
	public long addFamilyMember(long ext_id, String surname, String name, 
			String patronimic, Date birth, int gender, byte[] photo) {
		java.sql.Date sqlDate = new java.sql.Date(birth.getTime());
		long result = 0;
		if (!isFamilyMemberPresent(surname, name, patronimic, birth)) {
			try {
				ContentValues cv = new ContentValues();
				cv.put(FamilyTable.ID_EXTERNAL, ext_id);
				cv.put(FamilyTable.SURNAME, surname);
				cv.put(FamilyTable.NAME1, name);
				cv.put(FamilyTable.NAME2, patronimic);
				cv.put(FamilyTable.GENDER, gender);
				cv.put(FamilyTable.BIRTH_DATE, dateFormatDay.format(sqlDate));
				cv.put(FamilyTable.PHOTO, photo);
				SQLiteDatabase sd = getWritableDatabase();	
				result = sd.insert(FamilyTable.TABLE_NAME, FamilyTable.SURNAME, cv);
			} catch (Exception e) {
				if (D) Log.e(TAG, "addFamilyMember exception = " + e.getMessage());				
			}
		}
		if (result < 0) Log.d(TAG, "addFamilyMember - Error");
		return result;
	}
	
	public boolean updateFamilyMember(long id, long ext_id, String surname, String name, 
			String patronimic, Date birth, int gender, byte[] photo) {
		java.sql.Date sqlDate = new java.sql.Date(birth.getTime());
		Boolean result = false;
		try {
			ContentValues cv = new ContentValues();
			cv.put(FamilyTable.ID_EXTERNAL, ext_id);
			cv.put(FamilyTable.SURNAME, surname);
			cv.put(FamilyTable.NAME1, name);
			cv.put(FamilyTable.NAME2, patronimic);
			cv.put(FamilyTable.GENDER, gender);
			cv.put(FamilyTable.BIRTH_DATE, dateFormatDay.format(sqlDate));
			cv.put(FamilyTable.MODIFIED, 1);
			cv.put(FamilyTable.PHOTO, photo);
			SQLiteDatabase sd = getWritableDatabase();	
			result = sd.update(FamilyTable.TABLE_NAME, cv, FamilyTable.ID + "=" + id, null) == 1;
		} catch (Exception e) {
			if (D) Log.e(TAG, "updateFamilyMember exception = " + e.getMessage());
		}
		return result;		
	}

	public Boolean removeFamilyMember(long id) {
		Boolean result = false;
		try {
			if (id > 0) {
				SQLiteDatabase sd = getWritableDatabase();
				result = sd.delete(FamilyTable.TABLE_NAME, FamilyTable.ID + " = " + id, null) > 0;
			}
		} catch (Exception e) {
			if (D) Log.e(TAG, "removeFamilyMember exception = " + e.getMessage());
		}
		return result;
	}

	/* ****************************************** */
	private Boolean isMeasurementPresent(Date date) {
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());		
		Boolean result = true;
		String query = "SELECT "+ MeasurementTable.ID + " FROM " + MeasurementTable.TABLE_NAME + 
				" WHERE " + MeasurementTable.M_DATE + " = \"" + dateFormatYY.format(sqlDate) + "\"";
		result = isRecordPresent(query);
		return result;
	}
	
	public long addMeasurement(long ext_id, int systolic, int diastolic, int heartRate, Date m_date, long familyMember) {
		java.sql.Date sqlDate = new java.sql.Date(m_date.getTime());
		long result = 0;
		if (!isMeasurementPresent(m_date)) {
			try {
				ContentValues cv = new ContentValues();
				cv.put(MeasurementTable.ID_EXTERNAL, ext_id);
				cv.put(MeasurementTable.SYSTOLIC, systolic);
				cv.put(MeasurementTable.DIASTOLIC, diastolic);
				cv.put(MeasurementTable.HEART_RATE, heartRate);
				cv.put(MeasurementTable.ID_FAMILY, familyMember);
				cv.put(MeasurementTable.M_DATE, dateFormatYY.format(sqlDate));
				SQLiteDatabase sd = getWritableDatabase();	
				result = sd.insert(MeasurementTable.TABLE_NAME, MeasurementTable.ID_FAMILY, cv);
			} catch (Exception e) {
				if (D) Log.e(TAG, "addMeasurement exception = " + e.getMessage());				
			}
		}
		if (result < 0) Log.d(TAG, "addMeasurement - Error");
		return result;
	}
	
	public boolean updateMeasurement(long id, long ext_id, int systolic, int diastolic, int heartRate, Date m_date) {
		java.sql.Date sqlDate = new java.sql.Date(m_date.getTime());
		Boolean result = false;
		try {
			ContentValues cv = new ContentValues();
			cv.put(MeasurementTable.ID_EXTERNAL, ext_id);
			cv.put(MeasurementTable.SYSTOLIC, systolic);
			cv.put(MeasurementTable.DIASTOLIC, diastolic);
			cv.put(MeasurementTable.HEART_RATE, heartRate);
			cv.put(MeasurementTable.M_DATE, dateFormatYY.format(sqlDate));
			cv.put(MeasurementTable.MODIFIED, 1);
			SQLiteDatabase sd = getWritableDatabase();	
			result = sd.update(MeasurementTable.TABLE_NAME, cv, MeasurementTable.ID + "=" + id, null) == 1;
		} catch (Exception e) {
			if (D) Log.e(TAG, "updateMeasurement exception = " + e.getMessage());
		}
		return result;		
	}

	public int convertMeasurementDateFormat() {
		String query = "SELECT "+ MeasurementTable.M_DATE + ", " + MeasurementTable.ID + 
				" FROM " + MeasurementTable.TABLE_NAME;
    	SQLiteDatabase sqdb = getReadableDatabase();
    	Cursor c = null;
    	String dateStr;
    	int cntr = 0, id; 		
		try {
			c = sqdb.rawQuery(query, null);
	    	while (c.moveToNext()) {	
	    		int colid = c.getColumnIndex(MeasurementTable.ID);
	    		id = c.getInt(colid);
	    		colid = c.getColumnIndex(MeasurementTable.M_DATE);
	    		dateStr = c.getString(colid);
	         	java.util.Date date;
	         	try {
	         		date = dateFormat.parse(dateStr); 
	         		updateMeasurementDate(id, date);
	         		cntr++;
	         	} catch (ParseException e) {
	     			if (D) Log.e(TAG, "Exception: " + e.getMessage());
	         	}		    		    				   
	    	}
		} catch (Exception e) {
			if (D) Log.e(TAG, "Exception: " + e.getMessage());
		} finally {
	    	if (c != null) c.close();
		}
		return cntr;
	}
	
	public boolean updateMeasurementDate(long id, Date m_date) {
		java.sql.Date sqlDate = new java.sql.Date(m_date.getTime());
		Boolean result = false;
		try {
			ContentValues cv = new ContentValues();
			cv.put(MeasurementTable.M_DATE, dateFormatYY.format(sqlDate));
			cv.put(MeasurementTable.MODIFIED, 1);
			SQLiteDatabase sd = getWritableDatabase();	
			result = sd.update(MeasurementTable.TABLE_NAME, cv, MeasurementTable.ID + "=" + id, null) == 1;
		} catch (Exception e) {
			if (D) Log.e(TAG, "updateMeasurement exception = " + e.getMessage());
		}
		return result;		
	}

	public Boolean removeMeasurement(long id) {
		Boolean result = false;
		try {
			if (id > 0) {
				SQLiteDatabase sd = getWritableDatabase();
				result = sd.delete(MeasurementTable.TABLE_NAME, MeasurementTable.ID + " = " + id, null) > 0;
			}
		} catch (Exception e) {
			if (D) Log.e(TAG, "removeMeasurement exception = " + e.getMessage());
		}
		return result;
	}
	
	/* ****************************************** */
	private Boolean isTemperaturePresent(Date date) {
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());		
		Boolean result = true;
		String query = "SELECT "+ TemperatureTable.ID + " FROM " + TemperatureTable.TABLE_NAME + 
				" WHERE " + TemperatureTable.M_DATE + " = \"" + dateFormatYY.format(sqlDate) + "\"";
		result = isRecordPresent(query);
		return result;
	}
	
	public long addTemperature(long ext_id, double temperature, Date m_date, long familyMember) {
		java.sql.Date sqlDate = new java.sql.Date(m_date.getTime());
		long result = 0;
		if (!isTemperaturePresent(m_date)) {
			try {
				ContentValues cv = new ContentValues();
				cv.put(TemperatureTable.ID_EXTERNAL, ext_id);
				cv.put(TemperatureTable.TEMP, temperature);
				cv.put(TemperatureTable.ID_FAMILY, familyMember);
				cv.put(TemperatureTable.M_DATE, dateFormatYY.format(sqlDate));
				SQLiteDatabase sd = getWritableDatabase();	
				result = sd.insert(TemperatureTable.TABLE_NAME, TemperatureTable.ID_FAMILY, cv);
			} catch (Exception e) {
				if (D) Log.e(TAG, "addTemperature exception = " + e.getMessage());				
			}
		}
		if (result < 0) Log.d(TAG, "addTemperature - Error");
		return result;
	}
	
	public boolean updateTemperature(long id, long ext_id, double temperature, Date m_date) {
		java.sql.Date sqlDate = new java.sql.Date(m_date.getTime());
		Boolean result = false;
		try {
			ContentValues cv = new ContentValues();
			cv.put(TemperatureTable.ID_EXTERNAL, ext_id);
			cv.put(TemperatureTable.TEMP, temperature);
			cv.put(TemperatureTable.M_DATE, dateFormatYY.format(sqlDate));
			cv.put(TemperatureTable.MODIFIED, 1);
			SQLiteDatabase sd = getWritableDatabase();	
			result = sd.update(TemperatureTable.TABLE_NAME, cv, TemperatureTable.ID + "=" + id, null) == 1;
		} catch (Exception e) {
			if (D) Log.e(TAG, "updateTemperature exception = " + e.getMessage());
		}
		return result;		
	}

	public Boolean removeTemperature(long id) {
		Boolean result = false;
		try {
			if (id > 0) {
				SQLiteDatabase sd = getWritableDatabase();
				result = sd.delete(TemperatureTable.TABLE_NAME, TemperatureTable.ID + " = " + id, null) > 0;
			}
		} catch (Exception e) {
			if (D) Log.e(TAG, "removeTemperature exception = " + e.getMessage());
		}
		return result;
	}
	
}
