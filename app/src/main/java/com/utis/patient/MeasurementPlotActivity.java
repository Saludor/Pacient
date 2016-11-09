package com.utis.patient;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.Arrays;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.WindowManager;

public class MeasurementPlotActivity extends Activity {
	private static final int SYSTOLIC_NORM = 120;
	private static final int DIASTOLIC_NORM = 80;
	private XYPlot plot;
	private int familyMember;
	private String familyName;
	private int[] systolicVals, diastolicVals, heartRateVals;
	private Number[] systolicNVals, diastolicNVals, heartRateNVals, systolicNormVals, diastolicNormVals;
	private String[] dateVals;
	
	class GraphXLabelFormat extends Format {

	    @Override
	    public StringBuffer format(Object arg0, StringBuffer arg1, FieldPosition arg2) {
	        int parsedInt = Math.round(Float.parseFloat(arg0.toString()));
//	        Log.d("test", parsedInt + " " + arg1 + " " + arg2);
	        String labelString = dateVals[parsedInt];
	        arg1.append(labelString);
	        return arg1;
	    }

	    @Override
	    public Object parseObject(String arg0, ParsePosition arg1) {
	        return java.util.Arrays.asList(dateVals).indexOf(arg0);
	    }

	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// fun little snippet that prevents users from taking screenshots
        // on ICS+ devices :-)
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                                 WindowManager.LayoutParams.FLAG_SECURE);

        setContentView(R.layout.activity_measurement_plot);
		Bundle b = getIntent().getExtras();
		familyMember = Integer.parseInt(b.getString("id_family_member"));
		familyName = b.getString("name"); 
		systolicVals = b.getIntArray("systolic");
		diastolicVals = b.getIntArray("diastolic");
		heartRateVals = b.getIntArray("hr");
		dateVals = b.getStringArray("dates");
		systolicNVals = new Number[systolicVals.length];
		diastolicNVals = new Number[diastolicVals.length];
		heartRateNVals = new Number[heartRateVals.length];
		systolicNormVals = new Number[heartRateVals.length]; 
		diastolicNormVals = new Number[heartRateVals.length];
		for (int i = 0; i < systolicVals.length; i++) {
			systolicNVals[i] = (Number)systolicVals[i];
			diastolicNVals[i] = (Number)diastolicVals[i];
			heartRateNVals[i] = (Number)heartRateVals[i];
			systolicNormVals[i] = (Number)SYSTOLIC_NORM;
			diastolicNormVals[i] = (Number)DIASTOLIC_NORM;
		}
 
        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.mySimpleXYPlot);
        plot.setTitle(familyName);
 
        // Create a couple arrays of y-values to plot:
        Number[] systolicNorm = {120, 120};
        Number[] series2Numbers = {4, 6, 3, 8, 2, 10};
 
        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(systolicNVals),          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                getString(R.string.lbl_plot_systolic));                             // Set the display title of the series
 
        // same as above
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(diastolicNVals), 
        		SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, getString(R.string.lbl_plot_diastolic));
        XYSeries series3 = new SimpleXYSeries(Arrays.asList(heartRateNVals), 
        		SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, getString(R.string.lbl_heart_rate));

        XYSeries seriesSysNorm = new SimpleXYSeries(Arrays.asList(systolicNormVals), 
        		SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        XYSeries seriesDiasNorm = new SimpleXYSeries(Arrays.asList(diastolicNormVals), 
        		SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);
        
        
//        XYSeries series4 = new SimpleXYSeries(Arrays.asList(systolicNorm), 
//        		SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, null);        
        
        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(getApplicationContext(), R.xml.line_point_formatter_with_plf1);
        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);
 
        // same as above:
        LineAndPointFormatter series2Format = new LineAndPointFormatter();
        series2Format.setPointLabelFormatter(new PointLabelFormatter());
        series2Format.configure(getApplicationContext(), R.xml.line_point_formatter_with_plf2);
        plot.addSeries(series2, series2Format);
        
        LineAndPointFormatter series3Format = new LineAndPointFormatter();
        series3Format.setPointLabelFormatter(new PointLabelFormatter());
        series3Format.configure(getApplicationContext(), R.xml.line_point_formatter_3);
        plot.addSeries(series3, series3Format);
 
        LineAndPointFormatter seriesSysNormFormat = new LineAndPointFormatter();
        seriesSysNormFormat.setPointLabelFormatter(new PointLabelFormatter());
        seriesSysNormFormat.configure(getApplicationContext(), R.xml.line_point_formatter_sys_norm);
        plot.addSeries(seriesSysNorm, seriesSysNormFormat);
        LineAndPointFormatter seriesDiasNormFormat = new LineAndPointFormatter();
        seriesDiasNormFormat.setPointLabelFormatter(new PointLabelFormatter());
        seriesDiasNormFormat.configure(getApplicationContext(), R.xml.line_point_formatter_dias_norm);
        plot.addSeries(seriesDiasNorm, seriesDiasNormFormat);
        
        
/*     // Create a formatter to use for drawing a series using LineAndPointRenderer:
        LineAndPointFormatter series4Format = new LineAndPointFormatter(
                Color.rgb(0, 200, 0),                   // line color
                Color.rgb(0, 100, 0),                   // point color
                null,                   // ? color
                null);              // fill color (optional) <- my app hangs if I add it for a horizontal line

        // Add series1 to the xyplot:
        plot.addSeries(series4, series4Format);
//        Float min = Collections.min(Arrays.asList(systolicNorm));
//        Float max = Collections.max(Arrays.asList(seriesNumbers)); 
//
//        pricesPlot.setRangeBoundaries(min - 0.1*min, max + 0.1*max, BoundaryMode.AUTO);// make them a bit wider out of min/max values
*/        
        
        // reduce the number of range labels
        plot.setTicksPerRangeLabel(1);  //3
        plot.getGraphWidget().setDomainLabelOrientation(-45);
        plot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 1);
        plot.getGraphWidget().setDomainValueFormat(new GraphXLabelFormat());
//        plot.setDo
//        plot.setPlotMargins(10, 10, 10, 10);
//        plot.setPlotPadding(10, 10, 10, 10);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.measurement_plot, menu);
		return true;
	}

}
