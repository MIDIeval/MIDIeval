package com.beta.activities;

import java.util.HashMap;

import android.hardware.usb.UsbDevice;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.beta.Controllability.IController;
import com.beta.UIControls.WindowedSeekBar;
import com.beta.UIControls.XYController;
import com.beta.UIControls.XYSubController;
import com.beta.activities.SelectorDialog.ISelectorDialogListener;
import com.beta.imperius.AbstractSingleMIDIActivity;
import com.beta.xmlUtility.Mapper;


public class MainActivity extends AbstractSingleMIDIActivity{
	
	
//	public WindowedSeekBar seekBarX;
//	public WindowedSeekBar seekBarY;
	
	TextView text_X;
	TextView text_Y;
	TextView text_DoubleTap;
	 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
	
		
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
	public void fn_InvokeSelectorDialog(
			SelectorDialog selectorDialog) {
		// TODO Auto-generated method stub
		selectorDialog.show(getFragmentManager(), DISPLAY_SERVICE);
		//Invoke the selector dialog using the fragment manager
		
	}

}
