package com.utis.patient;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

public class MeasurementActivity extends Activity implements OnScrollListener {

	private static final boolean D = true;
	private static final String TAG = "MeasurementActivity";
	private static final int EDIT = 1;
	private static final String FILTER_ITEM = "sFilterItem";
	private DBSchemaHelper dbSch;
	private TextView caption;
	private CustomMeasurementAdapter mDBArrayAdapter;
//	private SwipeListView mDBSwipeListView;
	private ListView mDBListView;
	private Context mContext;
	private int idFamily;
	private String familyName;
	private View viewContainer;
	private int[] systolicVals, diastolicVals, heartRateVals;
	private String[] dateVals;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mPeriodTitles;
    private int selectedFilterItem = 0; 
    private SharedPreferences prefs;	

    private static class CustomMeasurementAdapter extends ArrayAdapter<MeasurementRecord> {
    	private HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
    	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    	
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
        
    	static class MeasurementViewHolder {
    	    public TextView textValue;
    	    public TextView textDate;
    	    public TextView textTopValue;
    	    public TextView textBottomValue;
    	    public TextView textHRValue;
    	}
    	
    	public CustomMeasurementAdapter(Context context, int layout, int resId, 
    			MeasurementRecord[] items) { 
    		//Call through to ArrayAdapter implementation 
    		super(context, layout, resId, items); 
    	} 

    	public CustomMeasurementAdapter(Context context, int textViewResourceId) { 
    		//Call through to ArrayAdapter implementation 
    		super(context, textViewResourceId); 
    	} 
    	
    	@Override 
    	public View getView(int position, View convertView, ViewGroup parent) { 
	    	View row = convertView; 
	    	MeasurementViewHolder viewHolder;
	    	//Inflate a new row if one isn't recycled
	    	if(row == null) { 
	    		row = LayoutInflater.from(getContext()).inflate(R.layout.measurement_row, parent, false);
	    		viewHolder = new MeasurementViewHolder();
	    		viewHolder.textValue = (TextView)row.findViewById(R.id.lineValue);
	    		viewHolder.textDate = (TextView)row.findViewById(R.id.lineDate);
	    		viewHolder.textTopValue = (TextView)row.findViewById(R.id.lineTopValue);
	    		viewHolder.textBottomValue = (TextView)row.findViewById(R.id.lineBottomValue);
	    		viewHolder.textHRValue = (TextView)row.findViewById(R.id.lineHR);
	    		row.setTag(viewHolder);
	    	} else {
	    		 viewHolder = (MeasurementViewHolder) row.getTag();
	    	}
	    	MeasurementRecord item = getItem(position);
	    	viewHolder.textTopValue.setText("" + item.systolic);
	    	viewHolder.textBottomValue.setText("" + item.diastolic);
	    	viewHolder.textHRValue.setText("" + item.heartRate);
	    	if (item.systolic > MeasurementRecord.SYSTOLIC_HIGH_MARGIN)
	    		viewHolder.textTopValue.setTextColor(Color.RED);
	    	else if (item.systolic < MeasurementRecord.SYSTOLIC_LOW_MARGIN)
	    		viewHolder.textTopValue.setTextColor(Color.BLUE);
	    	else
	    		viewHolder.textTopValue.setTextColor(Color.BLACK);
	    	if (item.diastolic > MeasurementRecord.DIASTOLIC_HIGH_MARGIN)
	    		viewHolder.textBottomValue.setTextColor(Color.RED);
	    	else if (item.diastolic < MeasurementRecord.DIASTOLIC_LOW_MARGIN)
	    		viewHolder.textBottomValue.setTextColor(Color.BLUE);
	    	else
	    		viewHolder.textBottomValue.setTextColor(Color.BLACK);
	    	if (item.heartRate > MeasurementRecord.HR_HIGH_MARGIN)
	    		viewHolder.textHRValue.setTextColor(Color.RED);
	    	else if (item.heartRate < MeasurementRecord.HR_LOW_MARGIN)
	    		viewHolder.textHRValue.setTextColor(Color.BLUE);
	    	else 
	    		viewHolder.textHRValue.setTextColor(Color.BLACK);
	    	viewHolder.textDate.setText(dateFormat.format(item.mDate));
	    	viewHolder.textValue.setText(item.toString());
//	    	viewHolder.textPatientName.setTextColor((item.gender == 0) ? Color.RED : Color.BLUE);
	    	
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
		setContentView(R.layout.activity_measurement);
		mContext = this;
		dbSch = new DBSchemaHelper(this);
		prefs = getSharedPreferences(CommonClass.PREF_NAME, MODE_PRIVATE);
		caption = (TextView)findViewById(R.id.caption);
		mDBArrayAdapter = new CustomMeasurementAdapter(this, R.layout.patient_row);
//		mDBSwipeListView = (SwipeListView)findViewById(R.id.listViewPatients);
//        mDBSwipeListView.setAdapter(mDBArrayAdapter);
		mDBListView = (ListView)findViewById(R.id.listViewPatients);
        mDBListView.setAdapter(mDBArrayAdapter);        
        mDBListView.setOnItemClickListener(mMeasurmentsClickListener);
        
        viewContainer = findViewById(R.id.undobar);
//        mDBSwipeListView.setOnScrollListener(this);
        
		Bundle b = getIntent().getExtras();
		idFamily = Integer.parseInt(b.getString("id"));
		familyName = b.getString("name");
		
		// +++++
        mTitle = mDrawerTitle = getTitle();
        mPeriodTitles = getResources().getStringArray(R.array.period_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mPeriodTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }		
		// ++++++++++
		
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
		    mDBListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL); 
		    mDBListView.setMultiChoiceModeListener(new ModeCallback()); 
	    }
/*	    
        mDBSwipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            	Log.d("swipe", String.format("onOpened %d ", position));
            }
    
            @Override
            public void onClosed(int position, boolean fromRight) {
            	Log.d("swipe", String.format("onClosed %d ", position));
            }
    
            @Override
            public void onListChanged() {
            }
    
            @Override
            public void onMove(int position, float x) {
            }
    
            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }
    
            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }
    
            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
                showMeasurementEditActivity(position);
//                mDBSwipeListView.openAnimate(position); //when you touch front view it will open    
            }
    
            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));    
                mDBSwipeListView.closeAnimate(position);//when you touch back view it will close
            }
    
            @Override
            public void onDismiss(int[] reverseSortedPositions) {
            	Log.d("swipe", String.format("onDismiss %d", reverseSortedPositions.length));
            	for (int i = 0; i < reverseSortedPositions.length; i++) {
            		deleteItem(reverseSortedPositions[i]);            		
            	}
//            	CommonClass.showUndo(viewContainer);
            }
    
        });        
*/        
//        mDBSwipeListView.setSwipeMode(mDBSwipeListView.SWIPE_MODE_LEFT); // there are five swiping modes
//        mDBSwipeListView.setSwipeActionLeft(mDBSwipeListView.SWIPE_ACTION_DISMISS); //there are four swipe actions
//        mDBSwipeListView.setSwipeActionRight(mDBSwipeListView.SWIPE_ACTION_DISMISS);
//        mDBSwipeListView.setOffsetLeft(CommonClass.convertDpToPixel(mContext, /*26*/0f)); // left side offset
//        mDBSwipeListView.setOffsetRight(CommonClass.convertDpToPixel(mContext, 0f)); // right side offset
//        mDBSwipeListView.setAnimationTime(50); // animation time
//        mDBSwipeListView.setSwipeOpenOnLongPress(false); // enable or disable SwipeOpenOnLongPress        
		
	}
	
    @Override
    public void onResume() {
        super.onResume();
		int pos = prefs.getInt(FILTER_ITEM, 0);
        selectItem(pos);        
    }	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.measurement, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem mi = menu.findItem(R.id.action_draw_plot);
		if (mi != null) 
			mi.setVisible(mDBArrayAdapter.getCount() > 0);
		return true;				
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
        case R.id.action_settings:

        	return true;
        case R.id.action_add_measure:
        	if (D) Log.d(TAG, "action_measurement");
    		SharedPreferences.Editor editor = prefs.edit();
    		editor.putString(MeasurementEditActivity.MEASUREMENT_DATE_VALUE, 
    				dbSch.dateFormat.format(Calendar.getInstance().getTime()));
    		editor.commit();		
        	showMeasurementEditActivity(0);
            return true;
        case R.id.action_draw_plot:
        	if (D) Log.d(TAG, "action_draw_plot");
        	showPlotActivity();
            return true;
        case R.id.action_add_temperature:
        	if (D) Log.d(TAG, "action_temperature");
//        	showTemperatureActivity();
        	return true;
        }
        return false;
    }
	
    // The on-click listener for ListViews
    private OnItemClickListener mMeasurmentsClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        	MeasurementRecord rec;
        	rec = mDBArrayAdapter.getItem(arg2);
            if (D) Log.d(TAG, "nm=" + rec.id); 
            showMeasurementEditActivity(rec.id);
        }
    };  
    
    private void deleteItem(int itemPosition) {
    	MeasurementRecord item = mDBArrayAdapter.getItem(itemPosition);
		if (dbSch.removeMeasurement(item.id)) {
			mDBArrayAdapter.remove(item);
		}
    }    
    
    private void showPlotActivity() {
    	reverseArrays();
    	Intent intent = new Intent(this, MeasurementPlotActivity.class);
        Bundle b = new Bundle();
        b.putString("id_family_member", Integer.toString(idFamily));
        b.putString("name", familyName);
        b.putIntArray("systolic", systolicVals);
        b.putIntArray("diastolic", diastolicVals);
        b.putIntArray("hr", heartRateVals);
        b.putStringArray("dates", dateVals);
        intent.putExtras(b);            
    	startActivity(intent);
    }
    
    private void reverseArrays() {
    	int cnt, j = 0;
    	MeasurementRecord rec; 
    	cnt = mDBArrayAdapter.getCount();
		systolicVals = new int[cnt];
    	diastolicVals = new int[cnt];
    	heartRateVals = new int[cnt];
    	dateVals = new String[cnt];
    	for (int i = cnt-1; i >= 0; i--) {
    		rec = mDBArrayAdapter.getItem(i);
		    systolicVals[j] = rec.systolic;
		    diastolicVals[j] = rec.diastolic;
		    heartRateVals[j] = rec.heartRate;
		    dateVals[j] = rec.dateStrLbl;
    		j++;
    	}
    }
			
	private void showMeasurementEditActivity(int id) {
		Intent intent = new Intent(this, MeasurementEditActivity.class);
        Bundle b = new Bundle();
        b.putString("id", Integer.toString(id));
        b.putString("id_family_member", Integer.toString(idFamily));
        b.putString("name", familyName);
        b.putString("filterItem", Integer.toString(selectedFilterItem));
        intent.putExtras(b);            
//        startActivity(intent);
		startActivityForResult(intent, EDIT);
	}
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (resultCode == RESULT_OK) {
    		switch (requestCode) {
    		case EDIT:
        		int pos = prefs.getInt(FILTER_ITEM, 0);
                selectItem(pos);
//                if (data.hasExtra("filterItem")) {
//                	int filterItem = Integer.parseInt(data.getExtras().getString("filterItem"));
//                	selectItem(filterItem);
//            		prefs = getSharedPreferences(CommonClass.prefName, MODE_PRIVATE);
//                }
    			break;
    		}
    	}
    }
	
	private void showMeasurement(String sWhere) {
		SQLiteDatabase sqdb = dbSch.getWritableDatabase();
    	Cursor c = null;
    	MeasurementRecord rec; 
    	String query;
    	    	
    	if (sWhere == "") {
    		query = "SELECT * FROM " + MeasurementTable.TABLE_NAME + " WHERE " + 
    				MeasurementTable.ID_FAMILY + " = " + idFamily +
    				" ORDER BY " + MeasurementTable.ID + " DESC";    		
    	} else {
    		query = "SELECT * FROM " + MeasurementTable.TABLE_NAME + " WHERE " + 
    				MeasurementTable.ID_FAMILY + " = " + idFamily + " AND " + sWhere +
    				" ORDER BY " + MeasurementTable.ID + " DESC";    		
    	}
    	int cntr = 0;    	
		try {
			c = sqdb.rawQuery(query, null);
			mDBArrayAdapter.clear();
	    	while (c.moveToNext()) {	
	    		rec = new MeasurementRecord(c);
		    	if (D) Log.d(TAG, "Msg = " + rec.diastolic);
			    mDBArrayAdapter.add(rec);
			    cntr++;
	    	}
		} catch (Exception e) {
			if (D) Log.e(TAG, "Exception: " + e.getMessage());
		} finally {
	    	caption.setText(String.format("%s; %s%d", familyName, getString(R.string.measurements), cntr));
	    	if (c != null) c.close();
	    	invalidateOptionsMenu();
		}		
	}
	
    private class ModeCallback implements ListView.MultiChoiceModeListener {

        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.measurement_rowselection, menu);
            mode.setTitle(mContext.getString(R.string.selected));
            return true;
        }

        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//    		MenuItem mi = menu.findItem(R.id.action_del_measurement);
//    		if (mi != null) { 
//    			mi.setEnabled(mDBSwipeListView.getCheckedItemCount() == 1);
//    		}        	
            return true;
        }

        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        	SparseBooleanArray selectedItems = mDBListView.getCheckedItemPositions();
            switch (item.getItemId()) {
            case R.id.action_del_measurement:
//            	if (selectedItems.size() == 1) {
//            		// Item position in adapter
//                    int position = selectedItems.keyAt(0);
//                    MeasurementRecord rec = mDBArrayAdapter.getItem(position);
//            	}            	
            	deleteSelected();
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
        
        private void deleteSelected() {
        	for (int i = mDBArrayAdapter.getCount() - 1; i >= 0; i--) {
        		if (mDBArrayAdapter.isPositionChecked(i)) {
        	    	MeasurementRecord item = mDBArrayAdapter.getItem(i);
        	    	dbSch.removeMeasurement(item.id);
        	    	mDBArrayAdapter.remove(item);
        		}
        	}
        	mDBArrayAdapter.clearSelection();
        	mDBArrayAdapter.notifyDataSetChanged();
        	invalidateOptionsMenu();
        	caption.setText(String.format("%s; %s%d", familyName,
					getString(R.string.measurements), mDBArrayAdapter.getCount()));
        }
        
    }

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}    

	
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
//        Fragment fragment = new PlanetFragment();
//        Bundle args = new Bundle();
//        args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//        fragment.setArguments(args);
//
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
//
    	selectedFilterItem = position;
    	Calendar cal = Calendar.getInstance();
    	int day_count = 0;
    	switch (position) {
    	case 0:
    		day_count = 7;
    		break;
    	case 1:
    		day_count = 14;
    		break;
    	case 2:
    		day_count = 21;
    		break;
    	case 3:
    		day_count = 30;
    		break;
    	case 4:
    		day_count = 60;
    		break;
    	case 5:
    		day_count = 90;
    		break;
    	case 6:
    		day_count = 180;
    		break;
    	case 7:
    		day_count = 365;
    		break;
    	}
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(FILTER_ITEM, position);
		editor.commit();
    	
    	cal.add(Calendar.DATE, -day_count);
    	String where = "SUBSTR("+ MeasurementTable.M_DATE + ",1,10) >= \"" +
    			dbSch.dateFormatDayYY.format(cal.getTime()) + "\"";
    	showMeasurement(where);
        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mPeriodTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
	
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
}
