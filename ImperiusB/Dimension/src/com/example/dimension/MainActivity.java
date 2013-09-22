package com.example.dimension;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.R;
import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensor.sensorlibrary.SensorGate;
import com.transformer.transformerLibrary.TransformSensorToMidi;
 
public class MainActivity extends Activity implements SensorEventListener,
        OnClickListener, org.openintents.sensorsimulator.hardware.SensorEventListener, Observer {
    private SensorManagerSimulator sensorManager;
    private SensorManager sensorManager_m;
    private Button btnStart, btnStop;
    private boolean started = false;
    private ArrayList sensorData;
    private LinearLayout layout;
    private View mChart;
    private TextView[] txtView_Vector_m;
    
    private SensorGate sensorGateObj_m;
    private TransformSensorToMidi transformObj_m;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy); 
        setContentView(R.layout.activity_main);
        
        
        layout = (LinearLayout) findViewById(R.id.chart_container);
        //Android Sensor Simulator sensor manager
        sensorManager = SensorManagerSimulator.getSystemService(this, SENSOR_SERVICE);
        //Android hardware sensor manager
        sensorManager_m = (SensorManager)getSystemService(SENSOR_SERVICE);
        //Async Task instance to start a new thread for a network connection to connect to the Simulator
        new RetreiveFeedTask().execute(sensorManager);
        
        sensorData = new ArrayList();
        
        //Button Settings to start and stop actions
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        txtView_Vector_m = new TextView[2];
        txtView_Vector_m[0] = (TextView)this.findViewById(R.id.txtView_01);
        txtView_Vector_m[1] = (TextView)this.findViewById(R.id.txtView_02);
        
        sensorGateObj_m = new SensorGate(sensorManager);
        transformObj_m = new TransformSensorToMidi();
        //Register Transformer object as listener for SensorGate instance
        sensorGateObj_m.addObserver(transformObj_m);
        //Register Main activity as listener for TransformSensorToMidi instance
        transformObj_m.addObserver(this);
        
        
        
 
    }
 
    @Override
    protected void onResume() {
        super.onResume();
 
    }
 
    @Override
    protected void onPause() {
        super.onPause();
        if (started == true) {
            sensorManager.unregisterListener((org.openintents.sensorsimulator.hardware.SensorEventListener) this);
        }
    }
 
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
 
    }
 
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (started) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.currentTimeMillis();
            AccelData data = new AccelData(timestamp, x, y, z);
            sensorData.add(data);
        }
 
    }
 
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.btnStart:
        	
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            sensorData = new ArrayList();
            // save prev data if available
            started = true;
                    
            //sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            //        SensorManager.SENSOR_DELAY_FASTEST);
            sensorGateObj_m.registerListener();
            
            break;
        case R.id.btnStop:
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            started = false;
            sensorManager.unregisterListener((org.openintents.sensorsimulator.hardware.SensorEventListener) this);
            layout.removeAllViews();
            openChart();
 
            // show data in chart
            break;
        default:
            break;
        }
 
    }
 
    private void openChart() {
        if (sensorData != null || sensorData.size() > 0) {
            long t = ((AccelData)sensorData.get(0)).getTimestamp();
            XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
 
            XYSeries xSeries = new XYSeries("X");
            XYSeries ySeries = new XYSeries("Y");
            XYSeries zSeries = new XYSeries("Z");
 
            for (Object dataObj : sensorData) {
            	AccelData data = (AccelData)dataObj;
                xSeries.add(data.getTimestamp() - t, data.getX());
                ySeries.add(data.getTimestamp() - t, data.getY());
                zSeries.add(data.getTimestamp() - t, data.getZ());
            }
 
            dataset.addSeries(xSeries);
            dataset.addSeries(ySeries);
            dataset.addSeries(zSeries);
 
            XYSeriesRenderer xRenderer = new XYSeriesRenderer();
            xRenderer.setColor(Color.RED);
            xRenderer.setPointStyle(PointStyle.CIRCLE);
            xRenderer.setFillPoints(true);
            xRenderer.setLineWidth(1);
            xRenderer.setDisplayChartValues(false);
 
            XYSeriesRenderer yRenderer = new XYSeriesRenderer();
            yRenderer.setColor(Color.GREEN);
            yRenderer.setPointStyle(PointStyle.CIRCLE);
            yRenderer.setFillPoints(true);
            yRenderer.setLineWidth(1);
            yRenderer.setDisplayChartValues(false);
 
            XYSeriesRenderer zRenderer = new XYSeriesRenderer();
            zRenderer.setColor(Color.BLUE);
            zRenderer.setPointStyle(PointStyle.CIRCLE);
            zRenderer.setFillPoints(true);
            zRenderer.setLineWidth(1);
            zRenderer.setDisplayChartValues(false);
 
            XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();
            multiRenderer.setXLabels(0);
            multiRenderer.setLabelsColor(Color.RED);
            multiRenderer.setChartTitle("t vs (x,y,z)");
            multiRenderer.setXTitle("Sensor Data");
            multiRenderer.setYTitle("Values of Acceleration");
            multiRenderer.setZoomButtonsVisible(true);
            for (int i = 0; i < sensorData.size(); i++) {
 
                multiRenderer.addXTextLabel(i + 1, ""
                        + (((AccelData)sensorData.get(i)).getTimestamp() - t));
            }
            for (int i = 0; i < 12; i++) {
                multiRenderer.addYTextLabel(i + 1, ""+i);
            }
 
            multiRenderer.addSeriesRenderer(xRenderer);
            multiRenderer.addSeriesRenderer(yRenderer);
            multiRenderer.addSeriesRenderer(zRenderer);
 
            // Getting a reference to LinearLayout of the MainActivity Layout
 
            // Creating a Line Chart
            mChart = ChartFactory.getLineChartView(getBaseContext(), dataset,
                    multiRenderer);
 
            // Adding the Line Chart to the LinearLayout
            layout.addView(mChart);
 
        }
    }

	@Override
	public void onAccuracyChanged(
			org.openintents.sensorsimulator.hardware.Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSensorChanged(
			org.openintents.sensorsimulator.hardware.SensorEvent event) {
		// TODO Auto-generated method stub
		if (started) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];
            long timestamp = System.currentTimeMillis();
            AccelData data = new AccelData(timestamp, x, y, z);
            sensorData.add(data);
        }
		
	}

	@Override
	public void update(Observable arg0, Object f_MidiVector) {
		float[] f_MidiVector_f = (float[])f_MidiVector;
		if ( f_MidiVector != null ){
			String vectorString_f = transformObj_m.vectorToString(f_MidiVector_f) ;			
			txtView_Vector_m[0].setText(vectorString_f);
		}
		
	}
}


class RetreiveFeedTask extends AsyncTask<SensorManagerSimulator, Void, Integer> {

    private Exception exception;

    protected void onPostExecute() {
        // TODO: check this.exception 
        // TODO: do something with the feed
    }

	@Override
	protected Integer doInBackground(SensorManagerSimulator... params) {
		try {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

			StrictMode.setThreadPolicy(policy); 
			SensorManagerSimulator sensorManager = (SensorManagerSimulator)params[0];
        	sensorManager.connectSimulator();
        	if ( sensorManager.isConnectedSimulator())
        		return Integer.valueOf(0);
        	else
        		return Integer.valueOf(-1);
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
	}

}