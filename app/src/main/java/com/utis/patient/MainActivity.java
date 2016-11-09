package com.utis.patient;

import java.util.HashMap;
import java.util.Set;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {
	private static final String SERVER_URI_SSL = "https://85.238.112.13:443/contenti";
	private static final String YDM = "YDM_dateFormat";
	private static final boolean D = true;
	private static final String TAG = "MainActivity";
	private DBSchemaHelper dbSch;
	private TextView caption;
	private CustomFamilyAdapter mDBArrayAdapter;
	private ListView mDBListView;
	private Context mContext;
    private SharedPreferences prefs;
    
    private boolean yearDateFormat;
	
	
    private static class CustomFamilyAdapter extends ArrayAdapter<FamilyRecord> {
    	private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
    	
    	public void setNewSelection(int position, boolean value) {
            mSelection.put(position, value);
            notifyDataSetChanged();
        }

        public boolean isPositionChecked(int position) {
            Boolean result = mSelection.get(position);
            return result == null ? false : result;
        }

        public Set<Integer> getCurrentCheckedPosition() {
            return mSelection.keySet();
        }

        public void removeSelection(int position) {
            mSelection.remove(position);
            notifyDataSetChanged();
        }

        public void clearSelection() {
            mSelection = new HashMap<Integer, Boolean>();
            notifyDataSetChanged();
        }
        
    	static class EmpMsgViewHolder {
    	    public TextView textPatientName;
    	    public ImageView photoEmp;
    	}
    	
    	public CustomFamilyAdapter(Context context, int layout, int resId, 
    			FamilyRecord[] items) { 
    		//Call through to ArrayAdapter implementation 
    		super(context, layout, resId, items); 
    	} 

    	public CustomFamilyAdapter(Context context, int textViewResourceId) { 
    		//Call through to ArrayAdapter implementation 
    		super(context, textViewResourceId); 
    	} 
    	
    	@Override 
    	public View getView(int position, View convertView, ViewGroup parent) { 
	    	View row = convertView; 
	    	EmpMsgViewHolder viewHolder;
	    	//Inflate a new row if one isn't recycled
	    	if(row == null) { 
	    		row = LayoutInflater.from(getContext()).inflate(R.layout.patient_row, parent, false);
	    		viewHolder = new EmpMsgViewHolder();
	    		viewHolder.textPatientName = (TextView)row.findViewById(R.id.linePatientName);
	    		viewHolder.photoEmp = (ImageView)row.findViewById(R.id.imageViewPatient);
	    		row.setTag(viewHolder);
	    	} else {
	    		 viewHolder = (EmpMsgViewHolder) row.getTag();
	    	}
	    	FamilyRecord item = getItem(position);	    	
	    	viewHolder.textPatientName.setText(item.combinedName);
	    	viewHolder.textPatientName.setTextColor((item.gender == 0) ? Color.RED : Color.BLUE);
	    	if (item.photo != null && item.photo.length > 0) {
		    	Bitmap pic;
		    	pic = BitmapFactory.decodeByteArray(item.photo, 0, item.photo.length);
		    	viewHolder.photoEmp.setImageBitmap(pic);
	    	} else {
	    		viewHolder.photoEmp.setImageResource(R.drawable.user);	    		
	    	}
	    	
/*    		if (item.hasNewImpMsgs)
    			row.setBackgroundResource(R.drawable.list_selector_new_imp_msg);
    		else if (item.hasNewMsgs)
    			row.setBackgroundResource(R.drawable.list_selector_new_msg);
    		else if (item.hasModifiedMsgs)
    			row.setBackgroundResource(R.drawable.list_selector_modified);
    		else */
    			row.setBackgroundResource(R.drawable.list_selector);
    		
    		if (mSelection.get(position) != null) {
				row.setBackgroundResource(R.drawable.list_selector_selected);    			
    		}
	    	
	    	return row; 
    	} 
    }
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		caption = (TextView)findViewById(R.id.main_caption);
		mDBArrayAdapter = new CustomFamilyAdapter(this, R.layout.patient_row);
		mDBListView = (ListView)findViewById(R.id.listViewPatients);
        mDBListView.setAdapter(mDBArrayAdapter);
        mDBListView.setOnItemClickListener(mMessagesClickListener);
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    mDBListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL); 
		    mDBListView.setMultiChoiceModeListener(new ModeCallback()); 
	    }
	    
		dbSch = new DBSchemaHelper(this);
		readPrefValues();
		if (!yearDateFormat) {
			convertDateFormat();
		}
	}

	private void readPrefValues() {
		prefs = getSharedPreferences(CommonClass.PREF_NAME, MODE_PRIVATE);
		yearDateFormat = prefs.getBoolean(YDM, false);
//		float temperature = prefs.getFloat("temperature", 50);
//		boolean authenticated = prefs.getBoolean("authenticated", false);
//		String username = prefs.getString("username", "");
	}	
	
	private void convertDateFormat() {
    	int cntr = 0;
    	cntr = dbSch.convertMeasurementDateFormat();
    	if (cntr > 0) {
    		SharedPreferences.Editor editor = prefs.edit();
    		editor.putBoolean(YDM, true);
    		editor.commit();
    	}
	}
	
    @Override
    public void onResume() {
        super.onResume();
        long cnt = dbSch.getFamilyCount();
        if (cnt <= 0) {
        	showFamilyActivity(true, 0);
        } else {
        	showPatients(dbSch, "");
        }
    }
        
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_add_patient:
        	addNewPatient();
        	return true;
        case R.id.action_measurement:
        	if (D) Log.d(TAG, "action_measurement");
//        	showMeasurementActivity();
            return true;
        case R.id.action_temperature:
        	if (D) Log.d(TAG, "action_temperature");
        	showTemperatureActivity();
        	return true;
        case R.id.action_download:
        	softDownload();
        	return true;
        }
        return false;
    }
	
    // The on-click listener for ListViews
    private OnItemClickListener mMessagesClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        	FamilyRecord user;
        	user = mDBArrayAdapter.getItem(arg2);
            if (D) Log.d(TAG, "nm=" + user.combinedName);            
            showMeasurementActivity(user.id, user.combinedName);
        }
    };  
		
    private void softDownload() {
    	Intent webIntent = new Intent(Intent.ACTION_VIEW, 
    			Uri.parse(SERVER_URI_SSL +"/patient.php"));
    	//Intent webIntent = new Intent(this, WebKitActivity.class);
    	startActivity(webIntent);        			    	
    }
	
    
	private void showTemperatureActivity() {
		Intent intent = new Intent(this, TemperatureActivity.class);
		startActivity(intent);
	}
	
	private void showMeasurementActivity(int id, String familyName) {
		Intent intent = new Intent(this, MeasurementActivity.class);
        Bundle b = new Bundle();
        b.putString("id", Integer.toString(id));
        b.putString("name", familyName);
        intent.putExtras(b);            
		startActivity(intent);
	}
	
	private void showFamilyActivity(boolean showAlert, int id) {
		Intent intent = new Intent(this, FamilyEditActivity.class);
        Bundle b = new Bundle();
        b.putBoolean("show_alert", showAlert);
        b.putString("id", Integer.toString(id));
        intent.putExtras(b);            
		startActivity(intent);
	}
	
	private void addNewPatient() {
		showFamilyActivity(false, 0);
	}
	
    private void showPatients(DBSchemaHelper sh, String sWhere) {
    	SQLiteDatabase sqdb = sh.getReadableDatabase();
    	Cursor c = null;
    	FamilyRecord user; 
    	String query;
    	    	
    	if (sWhere == "") {
    		query = "SELECT * FROM " + FamilyTable.TABLE_NAME +  
    				" ORDER BY " + FamilyTable.SURNAME + " ASC";    		
    	} else {
    		query = "SELECT * FROM " + FamilyTable.TABLE_NAME + " WHERE " + sWhere + 
    				" ORDER BY " + FamilyTable.SURNAME + " ASC";
    	}
    	int cntr = 0; 		
		try {
			c = sqdb.rawQuery(query, null);
			mDBArrayAdapter.clear();
	    	while (c.moveToNext()) {	
	    		user = new FamilyRecord(c);
	    		if (D) Log.d(TAG, "Msg = " + user.combinedName);
		    	mDBArrayAdapter.add(user);
		    	cntr++;
	    	}
		} catch (Exception e) {
			if (D) Log.e(TAG, "Exception: " + e.getMessage());
		} finally {
	    	if (D) Log.d(TAG, "userMsgs Count = " + cntr);
			caption.setText(String.format(getString(R.string.m_cntr), cntr));
	    	if (c != null) c.close();
		}
    }
	
    
    private class ModeCallback implements ListView.MultiChoiceModeListener {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.patient_rowselection, menu);
            mode.setTitle(mContext.getString(R.string.selected));
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//    		MenuItem mi = menu.findItem(R.id.action_edit_patient);
//    		if (mi != null) { 
//    			mi.setEnabled(mDBListView.getCheckedItemCount() == 1);
//    		}        	
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	SparseBooleanArray selectedItems = mDBListView.getCheckedItemPositions();
            switch (item.getItemId()) {
            case R.id.action_edit_patient:
            	if (selectedItems.size() == 1) {
            		// Item position in adapter
                    int position = selectedItems.keyAt(0);
                    FamilyRecord patient = mDBArrayAdapter.getItem(position);
                    showFamilyActivity(false, patient.id);
            	}            	
//            	showSelectedPatient();
                // the Action was executed, close the CAB
                mode.finish();
                return true;
            default:
                return false;
            }
        }

        public void onDestroyActionMode(ActionMode mode) {
        	mDBArrayAdapter.clearSelection();
        }

        public void onItemCheckedStateChanged(ActionMode mode,
                int position, long id, boolean checked) {
        	final int checkedCount = mDBListView.getCheckedItemCount();
            if (checked) 
            	mDBArrayAdapter.setNewSelection(position, checked);                    
            else 
            	mDBArrayAdapter.removeSelection(position);                 
            
            switch (checkedCount) {
                case 0:
                    mode.setSubtitle(null);
                    break;
                default:
                    mode.setSubtitle("" + checkedCount);
                    break;
            }
        }
        
        private void showSelectedPatient() {
        	for (int i = 0; i < mDBArrayAdapter.getCount(); i++) {
        		if (mDBArrayAdapter.isPositionChecked(i)) {
        	    	FamilyRecord item = mDBArrayAdapter.getItem(i);
        	    	showFamilyActivity(false, item.id);
        	    	break;
        		}
        	}	        	
        }

        
    }    
    
}
