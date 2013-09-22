package com.beta.activities;

import java.util.HashMap;

import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

import com.beta.ControlMapper.MapperPrototype;
import com.beta.Controllability.IController;
import com.beta.UIControls.XYController;
import com.beta.UIControls.XYSubController;
import com.beta.activities.SelectorDialog.ISelectorDialogListener;
import com.beta.imperius.AbstractSingleMIDIActivity;
import com.beta.xmlUtility.Mapper;


public class MainActivity extends AbstractSingleMIDIActivity implements ISelectorDialogListener {
	
	private String TestVariable;
	private IController genericPointer_m;
	private XYController xyControllerObj_m;
	private Switch switchX;
	private Switch switchY;
	private Bundle bundleForDialogObj_m = new Bundle();
	private XYSubController e_XYSubController_m;
	private MapperPrototype mapperObj_m;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.xyControllerObj_m = (XYController)this.findViewById(R.id.xy_controller);
		this.switchX = (Switch)this.findViewById(R.id.switch1);
		this.switchY = (Switch)this.findViewById(R.id.Switch02);
		genericPointer_m = this.xyControllerObj_m;
		Mapper.setContext(MainActivity.this);
		//Create a new Mapper object specific for the activity
		mapperObj_m = new MapperPrototype();
		HashMap<Integer, Integer> subControllerMapObj_f = new HashMap<Integer, Integer>();
		
//		new Asyncparser().execute(continuousControllerVector_f);
//		try {
//			Thread.currentThread().join(1000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			Log.e("Join Thread", e.toString());
//		}
		for ( Integer keyValue: this.xyControllerObj_m.getSubControllerMap().keySet()){
			subControllerMapObj_f.put(keyValue, Integer.valueOf(-1));
		}
		mapperObj_m.getControllerMapObj().put(xyControllerObj_m, subControllerMapObj_f);
		
		this.switchX.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			final String[] continuousControllerVector_f = Mapper.getContinuos();
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				e_XYSubController_m = XYSubController.X_RANGE_CHANGE;
				// TODO Auto-generated method stub
				if ( isChecked ){
					bundleForDialogObj_m.putString(getString(R.string.SELECTOR_DIALOG_BUNDLE_NAME)+"_string", "Please enter x value");
					bundleForDialogObj_m.putStringArray(getString(R.string.SELECTOR_DIALOG_BUNDLE_NAME)+"_stringarray", continuousControllerVector_f );
					SelectorDialog selectorDialog = new SelectorDialog();					
					selectorDialog.setArguments(bundleForDialogObj_m);
					selectorDialog.selectorDialogListener_m = MainActivity.this;
					selectorDialog.show(getFragmentManager(), DISPLAY_SERVICE);
					
				}
				
			}
			
		});
		
		this.switchY.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			final String[] continuousControllerVector_f = Mapper.getContinuos();
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				e_XYSubController_m = XYSubController.Y_RANGE_CHANGE;
				// TODO Auto-generated method stub
				if(isChecked)
				{
					bundleForDialogObj_m.putString(getString(R.string.SELECTOR_DIALOG_BUNDLE_NAME), "Please enter y value");
					bundleForDialogObj_m.putStringArray(getString(R.string.SELECTOR_DIALOG_BUNDLE_NAME), continuousControllerVector_f );
					SelectorDialog selectorDialog = new SelectorDialog();					
					selectorDialog.setArguments(bundleForDialogObj_m);
					selectorDialog.selectorDialogListener_m = MainActivity.this;
					selectorDialog.show(getFragmentManager(), DISPLAY_SERVICE);
				}
				
			}
			
		});
		
//		xyControllerObj_m.setOnTouchListener(new OnTouchListener(){
//
//			@Override
//			public boolean onTouch(View arg0, MotionEvent arg1) {
//				int x = (int)arg1.getX();
//				int y = (int)arg1.getY();
//				// TODO Auto-generated method stub
//				//xyControllerObj_m.onTouchEvent(arg1);
//				float fractionConvert_f = xyControllerObj_m.getLayoutGridSideLength();
//				fractionConvert_f = x/fractionConvert_f;
//				fractionConvert_f  = fractionConvert_f*127.0f;
//				
//				 x = (int)fractionConvert_f;
////				int sevenbBit_f = x & 0x3F8;
////				int threeBit_f = x & 0x7;
////				threeBit_f = threeBit_f << 4;
//				int functionValue_x = mapperObj_m.getFunctionValue(xyControllerObj_m, 1);
//				
//				//midiOutputDeviceObj_m.fn_ControlChangeMessage(0, 0, functionValue_x, x);
////		    	midiOutputDeviceObj_m.fn_ControlChangeMessage(0, 0, 0x23, threeBit_f);
////				midiOutputDeviceObj_m.fn_ControlChangeMessage(0, 0, 0x4A, sevenbBit_f);
//    	    	//previousControlChange_f = value;
//				int functionValue_y = mapperObj_m.getFunctionValue(xyControllerObj_m, 2);
//				fractionConvert_f = xyControllerObj_m.getLayoutGridSideLength();
//				fractionConvert_f = y/fractionConvert_f;
//				fractionConvert_f  = fractionConvert_f*127.0f;
//				Log.i("MAIN_ACTIVITY", "X," + arg1.getX() + ",Y," + arg1.getY() + ",Cx," + functionValue_x + ",Cy," + functionValue_y );
//				//midiOutputDeviceObj_m.fn_ControlChangeMessage(0, 0, functionValue_y, y);
//				return true;
//			}
//			
//		});
//		
		
	}
	
	
	public class Asyncparser extends AsyncTask<String[], Integer, Void>
	{
		@Override
		protected Void doInBackground(String[]... params) 
		{
			// TODO Auto-generated method stub
			String[] xmlValues =  Mapper.getContinuos();
			params[0] = xmlValues;
			return null;
		}
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	@Override 
    public void onDeviceAttached(final UsbDevice usbDevice){
    	super.onDeviceAttached(usbDevice);
    	Toast.makeText(this, "Usb MIDI Device " + this.usbMIDIDeviceDetails_m.getManufacturer() +":" +
    											this.usbMIDIDeviceDetails_m.getProduct()+ " has been attached", Toast.LENGTH_LONG).show();
    	
    	
    }
    @Override
    public void onDetachedDevice(final UsbDevice usbDevice){
    	super.onDetachedDevice(usbDevice);
    	Toast.makeText(this, "Usb MIDI Device " + this.usbMIDIDeviceDetails_m.getManufacturer() +":" +
				this.usbMIDIDeviceDetails_m.getProduct()+ " has been detached", Toast.LENGTH_LONG).show();
    	this.usbMIDIDeviceDetails_m = null;
    	
    }

	@Override
	public void onSelectionMade(String selectedObject) {
		// TODO Auto-generated method stub
		//This is where the User Mapping needs to happen
		
		int functionValue = Mapper.getContinuosFunctionValue(selectedObject); //Fetch the functionValue from the XML
		switch ( e_XYSubController_m ){
		case X_RANGE_CHANGE:
			this.mapperObj_m.setSubControllerValue(genericPointer_m, this.e_XYSubController_m.getValue(), functionValue);
			break;
		case Y_RANGE_CHANGE:
			this.mapperObj_m.setSubControllerValue(genericPointer_m, this.e_XYSubController_m.getValue(), functionValue);
			break;
		case DOUBLE_TAP:
			this.mapperObj_m.setSubControllerValue(genericPointer_m, this.e_XYSubController_m.getValue(), functionValue);
			break;
		default:
			break;
		}
		
	}

}
