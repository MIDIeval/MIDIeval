package com.example.xycontroller;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.beta.imperius.AbstractSingleMIDIActivity;


public class MainActivity extends AbstractSingleMIDIActivity {
	
	private XYController xyControllerObj_m;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		this.xyControllerObj_m = (XYController)this.findViewById(R.id.xy_controller);
		
		xyControllerObj_m.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				int x = (int)arg1.getX();
				// TODO Auto-generated method stub
				xyControllerObj_m.onTouchEvent(arg1);
				float fractionConvert_f = xyControllerObj_m.getLayoutGridSideLength();
				fractionConvert_f = x/fractionConvert_f;
				fractionConvert_f  = fractionConvert_f*127.0f;
				
				 x = (int)fractionConvert_f;
				int sevenbBit_f = x & 0x3F8;
				int threeBit_f = x & 0x7;
				threeBit_f = threeBit_f << 4;

				midiOutputDeviceObj_m.fn_ControlChangeMessage(0, 0, 0x03, sevenbBit_f);
		    	midiOutputDeviceObj_m.fn_ControlChangeMessage(0, 0, 0x23, threeBit_f);
				midiOutputDeviceObj_m.fn_ControlChangeMessage(0, 0, 0x4A, sevenbBit_f);
    	    	//previousControlChange_f = value;
				return true;
			}
			
		});
		
		
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

}
