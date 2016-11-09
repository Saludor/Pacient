package com.utis.patient;

import java.util.Calendar;
import java.util.Date;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.graphics.BitmapFactory;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class FamilyEditActivity extends FragmentActivity {

	private static final boolean D = true;
	private static final String TAG = "FamilyEditActivity";
	private static final int DATE_PICKER_ID = 1;
	private static final int TAKE_PHOTO = 2;
	private static final int SAVE_PHOTO = 3;
	private EditText name1Edit, name2Edit, surnameEdit, dateEdit;
	private Button saveBtn, cancelBtn, changeDate;
	private TextView ageTextView;
//    private EditText mEdit;
    private boolean showAlert, shownAlert = false;
    private Context mContext;
    private RadioButton maleGenderRadio, femaleGenderRadio;
    private RadioGroup GenderRadio;
    private ImageView photo;    
    private Bitmap bitmap;
    private File photo_file; 
    private DBSchemaHelper dbSch;
    private String mName1, mName2, mSurname, mBirtday;
    private Date birthDay;
    private Calendar calendar;
    private int id, year, month, day;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_family_edit);
		mContext = this;
		
		name1Edit = (EditText)findViewById(R.id.editTextName1);
		name2Edit = (EditText)findViewById(R.id.editTextName2);
		surnameEdit = (EditText)findViewById(R.id.editTextSurname);
		dateEdit = (EditText)findViewById(R.id.editTextDate);
		dateEdit.setKeyListener(null);
		GenderRadio = (RadioGroup)findViewById(R.id.radio_gender);
		maleGenderRadio = (RadioButton)findViewById(R.id.radio_male);
		femaleGenderRadio = (RadioButton)findViewById(R.id.radio_female);
		photo = (ImageView)findViewById(R.id.imageViewPhoto); 
		ageTextView = (TextView)findViewById(R.id.textViewAge);
		
		dbSch = new DBSchemaHelper(this);
		Bundle b = getIntent().getExtras();
		showAlert = b.getBoolean("show_alert");
		id = Integer.parseInt(b.getString("id"));
		calendar = Calendar.getInstance();
		photo_file = new File(Environment.getExternalStorageDirectory(), "myimage.jpg");
		
		saveBtn = (Button)findViewById(R.id.action_OK);
		saveBtn.setOnClickListener(new View.OnClickListener() {
									   @Override
									   public void onClick(View view) {
										   attemptSave();
									   }
								   }
		);
		cancelBtn = (Button)findViewById(R.id.action_Cancel);
		cancelBtn.setOnClickListener(new View.OnClickListener() {
										 @Override
										 public void onClick(View view) {
											 finish();
										 }
									 }
		);
		
		if (id > 0)
			showPatient();
	}

    @Override
    public void onResume() {
        super.onResume();
        if (showAlert && !shownAlert) {
        	shownAlert = true;
        	CommonClass.showMessage(mContext, getString(R.string.m_alert), getString(R.string.m_need_user));
        }
    }       	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.family_edit, menu);
		return true;
	}
	
	private void showPatient() {
		SQLiteDatabase sqdb = dbSch.getReadableDatabase();
    	Cursor c = null;
    	FamilyRecord user; 
    	String query;
    	    	
   		query = "SELECT * FROM " + FamilyTable.TABLE_NAME + " WHERE " + FamilyTable.ID + " = " + id; 
		try {
			c = sqdb.rawQuery(query, null);
	    	while (c.moveToNext()) {	
	    		user = new FamilyRecord(c);
	    		name1Edit.setText(user.name1);
	    		name2Edit.setText(user.name2);
	    		surnameEdit.setText(user.surname);
	    		birthDay = user.birthDate;
	    		calendar.set(birthDay.getYear()+1900, birthDay.getMonth(), birthDay.getDate());
	    		dateEdit.setText(dbSch.dateFormatDay.format(birthDay));
	    		
		    	int age = CommonClass.getAge(birthDay);
		    	ageTextView.setText(String.format("%d %s", age, getString(R.string.m_years)));
	    		
	    		if (user.gender == 0)
	    			femaleGenderRadio.setChecked(true);
	    		else
	    			maleGenderRadio.setChecked(true);
		    	if (user.photo != null && user.photo.length > 0) {
			    	bitmap = BitmapFactory.decodeByteArray(user.photo, 0, user.photo.length);
			    	photo.setImageBitmap(bitmap);
		    	} else {
		    		photo.setImageResource(R.drawable.user);	    		
		    	}	    		
	    	}
		} catch (Exception e) {
			if (D) Log.e(TAG, "Exception: " + e.getMessage());
		} finally {
	    	if (c != null) c.close();
		}		
	}
	
	private void attemptSave() {
		boolean cancel = false;
		View focusView = null;
		// Reset errors.
		name1Edit.setError(null);
		name2Edit.setError(null);
		surnameEdit.setError(null);
		dateEdit.setError(null);

		// Store values at the time of the login attempt.
		mName1 = name1Edit.getText().toString();
		mName2 = name2Edit.getText().toString();
		mSurname = surnameEdit.getText().toString();
		mBirtday = dateEdit.getText().toString();
				
		if (!cancel && TextUtils.isEmpty(mSurname)) {
			surnameEdit.setError(getString(R.string.error_field_required));
			focusView = surnameEdit;
			cancel = true;
		}
		if (!cancel && TextUtils.isEmpty(mName1)) {
			name1Edit.setError(getString(R.string.error_field_required));
			focusView = name1Edit;
			cancel = true;
		}
		if (!cancel && TextUtils.isEmpty(mBirtday)) {
			dateEdit.setError(getString(R.string.error_field_required));
			focusView = dateEdit;
			cancel = true;
		}
		
		if (cancel) {
			// There was an error; focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			int gender = maleGenderRadio.isChecked() ? 1 : 0;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			if (bitmap != null)
				bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
            byte[] b = baos.toByteArray();
			if (id > 0) {
				if (dbSch.updateFamilyMember(id, 0, mSurname, mName1, mName2, birthDay, gender, b));
					finish();				
			} else {
				if (dbSch.addFamilyMember(0, mSurname, mName1, mName2, birthDay, gender, b) > 0);
					finish();
			}
		}
	}

	public void selectDate(View view) {
		DialogFragment newFragment = new SelectDateFragment();
		newFragment.show(getSupportFragmentManager(), "DatePicker");
	}
	
	public void selectPhoto(View view) {
//		Intent intent = new Intent();
//	    intent.setType("image/*");
//	    intent.setAction(Intent.ACTION_GET_CONTENT);
//	    intent.addCategory(Intent.CATEGORY_OPENABLE);
		
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
	    startActivityForResult(intent, TAKE_PHOTO);
	}
	
	public void savePhoto(View view) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); 
		//Add extra to save full-image somewhere 
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo_file)); 
		startActivityForResult(intent, SAVE_PHOTO);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case TAKE_PHOTO: 
				//Process and display the image 
				bitmap = (Bitmap)data.getExtras().get("data"); 
				photo.setImageBitmap(bitmap);			
				break;
			case SAVE_PHOTO:
				try { 
					FileInputStream in = new FileInputStream(photo_file); 
					BitmapFactory.Options options = new BitmapFactory.Options(); 
					options.inSampleSize = 10; //Downsample by 10x 
					bitmap = BitmapFactory.decodeStream(in, null, options); 
					photo.setImageBitmap(bitmap); 
				} catch (Exception e) { 
					e.printStackTrace(); 
				}
				break;
			}
		}
	}    
	
	public void populateSetDate(int year, int month, int day) {
		Calendar today = Calendar.getInstance();
		Calendar birthDate = Calendar.getInstance();
		birthDate.set(year, month, day);		
		
	    if (birthDate.after(today)) {
	    	CommonClass.showMessage(mContext, getString(R.string.m_error), getString(R.string.m_wrong_birthdate));
	        //throw new IllegalArgumentException(getString(R.string.m_wrong_birthdate));
	    } else {		
			calendar.set(year, month, day);
			birthDay = calendar.getTime();
	    	dateEdit.setText(dbSch.dateFormatDay.format(birthDay));
	    	int age = CommonClass.getAge(birthDay);
	    	ageTextView.setText(String.format("%d %s", age, getString(R.string.m_years)));
	    }
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
			populateSetDate(yy, mm, dd);
		}

	}	
}
