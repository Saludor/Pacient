package com.utis.patient;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

public class MeasurementEditActivity extends FragmentActivity {
	
	private static final boolean D = true;
	private static final String TAG = "MeasurementEditActivity";
	private static final boolean NUMBER_PICKER = true;
	private static final String SYSTOLIC_VALUE = "systolicVal";
	private static final String DIASTOLIC_VALUE = "diastolicVal";
	private static final String HR_VALUE = "hrVal";
	public static final String MEASUREMENT_DATE_VALUE = "measurementDateVal";
	private SeekBar topValueSeekBar, bottomValueSeekBar, hrSeekBar;
	private TextView caption;
	private EditText topValueText, bottomValueText, hrText;
	private EditText editDate;
	private Button saveBtn, cancelBtn;
	private DBSchemaHelper dbSch;
	private NumberPicker topNumberPicker, bottomNumberPicker, hrNumberPicker;

	private int id, topSeek, bottomSeek, hrSeek, familyMember;
	private int systolic, diastolic, heartRate;
	private Date mDate;
	private Calendar calendar;
	private String familyName, filterItem;
    private SharedPreferences prefs;	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (NUMBER_PICKER) {
			setContentView(R.layout.activity_measurement_edit_numpick);
			caption = (TextView)findViewById(R.id.textViewCaption);
			topNumberPicker = (NumberPicker)findViewById(R.id.numberPickerSystolic);
			topNumberPicker.setMaxValue(300);
			topNumberPicker.setMinValue(0);
			topSeek = 120;
			topNumberPicker.setValue(topSeek);
			topNumberPicker.setWrapSelectorWheel(true);
			topNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					topSeek = newVal;
				}
			});
			bottomNumberPicker = (NumberPicker)findViewById(R.id.numberPickerDiastolic);
			bottomNumberPicker.setMaxValue(200);
			bottomNumberPicker.setMinValue(0);
			bottomSeek = 80;
			bottomNumberPicker.setValue(bottomSeek);
			bottomNumberPicker.setWrapSelectorWheel(true);
			bottomNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					bottomSeek = newVal;
				}
			});
			hrNumberPicker = (NumberPicker)findViewById(R.id.numberPickerPulse);
			hrNumberPicker.setMaxValue(250);
			hrNumberPicker.setMinValue(0);
			hrSeek = 60;
			hrNumberPicker.setValue(hrSeek);
			hrNumberPicker.setWrapSelectorWheel(true);
			hrNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					hrSeek = newVal;
				}
			});
		} else {
			setContentView(R.layout.activity_measurement_edit);
			caption = (TextView)findViewById(R.id.textViewCaption);
			topValueSeekBar = (SeekBar)findViewById(R.id.seekBarTop);
			bottomValueSeekBar = (SeekBar)findViewById(R.id.seekBarBottom);
			hrSeekBar = (SeekBar)findViewById(R.id.seekBarHR);
			topValueText = (EditText)findViewById(R.id.editTextTop);
			topValueText.setBackgroundResource(R.drawable.edit_bg);
			topValueText.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN &&
							(keyCode == KeyEvent.KEYCODE_ENTER)) {
						//			    		EditText Text = (EditText)v;
						// сохраняем текст, введенный до нажатия Enter в переменную
						String strValue = topValueText.getText().toString();
						topSeek = Integer.parseInt(strValue);
						topValueSeekBar.setProgress(topSeek);
						return true;
					}
					return false;
				}
			});
			bottomValueText = (EditText)findViewById(R.id.editTextBottom);
			bottomValueText.setBackgroundResource(R.drawable.edit_bg);
			bottomValueText.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN &&
							(keyCode == KeyEvent.KEYCODE_ENTER)) {
						//			    		EditText Text = (EditText)v;
						// сохраняем текст, введенный до нажатия Enter в переменную
						String strValue = bottomValueText.getText().toString();
						bottomSeek = Integer.parseInt(strValue);
						bottomValueSeekBar.setProgress(bottomSeek);
						return true;
					}
					return false;
				}
			});
			hrText = (EditText)findViewById(R.id.editTextHR);
			hrText.setBackgroundResource(R.drawable.edit_bg);
			hrText.setOnKeyListener(new View.OnKeyListener() {
				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (event.getAction() == KeyEvent.ACTION_DOWN &&
							(keyCode == KeyEvent.KEYCODE_ENTER)) {
						//			    		EditText Text = (EditText)v;
						// сохраняем текст, введенный до нажатия Enter в переменную
						String strValue = hrText.getText().toString();
						hrSeek = Integer.parseInt(strValue);
						hrSeekBar.setProgress(hrSeek);
						return true;
					}
					return false;
				}
			});
	
			topValueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
			bottomValueSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
			hrSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
		}
		
		editDate = (EditText)findViewById(R.id.editTextDate);
		editDate.setKeyListener(null);
		
		Bundle b = getIntent().getExtras();
		id = Integer.parseInt(b.getString("id"));
		familyMember = Integer.parseInt(b.getString("id_family_member"));
		familyName = b.getString("name"); 
		filterItem = b.getString("filterItem");
		
		dbSch = new DBSchemaHelper(this);
		prefs = getSharedPreferences(CommonClass.PREF_NAME, MODE_PRIVATE);
		
		saveBtn = (Button)findViewById(R.id.action_OK);
		saveBtn.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptSave();
					}
				}
		);
		cancelBtn = (Button)findViewById(R.id.action_Cancel);
		cancelBtn.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						finish();
					}
				}
		);
		calendar = Calendar.getInstance();
		if (id > 0) {
			showMeasurement();
		} else {			
			updateTextViews();
		}
		updateMeasurementDate();
		caption.setText(familyName + "\n" + getString(R.string.m_measurement));
	}

    @Override
    public void onResume() {
        super.onResume();
        topSeek = prefs.getInt(SYSTOLIC_VALUE, 120);
        bottomSeek = prefs.getInt(DIASTOLIC_VALUE, 80);
        hrSeek = prefs.getInt(HR_VALUE, 60);
		String dateVal = prefs.getString(MEASUREMENT_DATE_VALUE, dbSch.dateFormatMM.format(mDate));
		try {
			mDate = dbSch.dateFormat.parse(dateVal);
			calendar.set(mDate.getYear()+1900, mDate.getMonth(), mDate.getDate(), mDate.getHours(), mDate.getMinutes());
			updateMeasurementDate();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (NUMBER_PICKER) {
    		topNumberPicker.setValue(topSeek);
    		bottomNumberPicker.setValue(bottomSeek);
    		hrNumberPicker.setValue(hrSeek);
		} else {
    		topValueSeekBar.setProgress(topSeek);
    		bottomValueSeekBar.setProgress(bottomSeek);
    		hrSeekBar.setProgress(hrSeek);
    		topValueText.setText("" + topSeek);
    		bottomValueText.setText("" + bottomSeek);
    		hrText.setText("" + hrSeek);
		}		
    }	

    @Override
    public void onPause() {
        super.onPause();
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(SYSTOLIC_VALUE, topSeek);
		editor.putInt(DIASTOLIC_VALUE, bottomSeek);
		editor.putInt(HR_VALUE, hrSeek);
		editor.putString(MEASUREMENT_DATE_VALUE, dbSch.dateFormat.format(mDate));
		editor.commit();		
    }		
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.measurement_edit, menu);
		return true;
	}
	
	private SeekBar.OnSeekBarChangeListener seekBarChangeListener = 
		    new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			updateTextViews();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
		}
	};
	
	private void updateTextViews() {
		if (NUMBER_PICKER) {
			
		} else {
			topSeek = topValueSeekBar.getProgress();
			bottomSeek = bottomValueSeekBar.getProgress();
			hrSeek = hrSeekBar.getProgress();
			// показываем значения
			topValueText.setText(Integer.toString(topSeek));
			bottomValueText.setText(Integer.toString(bottomSeek));
			hrText.setText(Integer.toString(hrSeek));
		}
	}
	
	private void attemptSave() {
		if (id > 0) {
			if (dbSch.updateMeasurement(id, 0, topSeek, bottomSeek, hrSeek, mDate))
				closeForm();				
		} else {
			if (dbSch.addMeasurement(0, topSeek, bottomSeek, hrSeek, mDate, familyMember) > 0)
				closeForm();
		}
	}
	
	private void closeForm() {
        Intent returnIntent = new Intent();
//		Bundle b = new Bundle();
//		b.putString("updated", Integer.toString((updated) ? 1 : 0));
//		returnIntent.putExtras(b); 
        setResult(RESULT_OK, returnIntent);
		finish();
	}
	
	private void showMeasurement() {
		SQLiteDatabase sqdb = dbSch.getReadableDatabase();
    	Cursor c = null;
    	MeasurementRecord rec; 
    	String query;
    	    	
   		query = "SELECT * FROM " + MeasurementTable.TABLE_NAME + " WHERE " + MeasurementTable.ID + " = " + id; 
		try {
			c = sqdb.rawQuery(query, null);
	    	while (c.moveToNext()) {	
	    		rec = new MeasurementRecord(c);
	    		mDate = dbSch.dateFormat.parse(rec.dateStr);
	    		calendar.set(mDate.getYear()+1900, mDate.getMonth(), mDate.getDate(), mDate.getHours(), mDate.getMinutes());
	    		if (NUMBER_PICKER) {
		    		topSeek = rec.systolic;
		    		topNumberPicker.setValue(topSeek);
		    		bottomSeek = rec.diastolic;
		    		bottomNumberPicker.setValue(bottomSeek);
		    		hrSeek = rec.heartRate;
		    		hrNumberPicker.setValue(hrSeek);
	    		} else {
		    		topValueSeekBar.setProgress(rec.systolic);
		    		bottomValueSeekBar.setProgress(rec.diastolic);
		    		hrSeekBar.setProgress(rec.heartRate);
		    		topValueText.setText("" + topSeek);
		    		bottomValueText.setText("" + bottomSeek);
		    		hrText.setText("" + hrSeek);
	    		}
	    	}
		} catch (Exception e) {
			if (D) Log.e(TAG, "Exception: " + e.getMessage());
		} finally {
	    	if (c != null) c.close();
		}		
	}	
	
	private void updateMeasurementDate() {
		mDate = calendar.getTime();
		editDate.setText(dbSch.dateFormatMM.format(mDate));
	}
	
	public void selectTime(View view) {
		DialogFragment newFragment = new SelectTimeFragment();
		newFragment.show(getSupportFragmentManager(), "TimePicker");
	}
	
	public void selectDate(View view) {
		DialogFragment newFragment = new SelectDateFragment();
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}
	
	public void populateSetTime(int hour, int minute) {
		int yy = calendar.get(Calendar.YEAR);
		int mm = calendar.get(Calendar.MONTH);
		int dd = calendar.get(Calendar.DAY_OF_MONTH);
		calendar.set(yy, mm, dd, hour, minute);
		updateMeasurementDate();
	}
	
	public void populateSetDate(int year, int month, int day) {
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);			
		calendar.set(year, month, day, hour, minute);
		updateMeasurementDate();
	}
	
	@SuppressLint("ValidFragment")
	public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int yy = calendar.get(Calendar.YEAR);
			int mm = calendar.get(Calendar.MONTH);
			int dd = calendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, yy, mm, dd);
		}
		 
		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
			populateSetDate(yy, mm/*+1*/, dd);
		}

	}	
	
	@SuppressLint("ValidFragment")
	public class SelectTimeFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);			
			return new TimePickerDialog(getActivity(), this, hour, minute, true);
		}
		 
		@Override
		public void onTimeSet(TimePicker view, int hour, int minute) {
			populateSetTime(hour, minute);
		}

	}	
	
}
