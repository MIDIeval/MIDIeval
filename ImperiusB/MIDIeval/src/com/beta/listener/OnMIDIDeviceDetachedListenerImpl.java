package com.beta.listener;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.beta.MIDIUSBFunctinality.MIDIInputDevice;
import com.beta.MIDIUSBFunctinality.MIDIOutputDevice;
import com.beta.imperius.AbstractSingleMIDIActivity;

public class OnMIDIDeviceDetachedListenerImpl implements
		OnMIDIDeviceDetachedListener {
	
	protected UsbManager usbManagerObj_m;
	protected UsbDeviceConnection usbDeviceConnectionObj_m;
	public MIDIInputDevice midiInputDeviceObj_m;
	public MIDIOutputDevice midiOutputDeviceObj_m;
	protected UsbDevice usbDeviceObj_m;
	protected Handler deviceDetachedHandlerObj_m;
	private AbstractSingleMIDIActivity abstractSingleMIDIActivity_m;

	@Override
	public void onDetachedDevice(UsbDevice usbDevice) {
		if (midiInputDeviceObj_m != null )
		{
			midiInputDeviceObj_m.stop();
			midiInputDeviceObj_m = null;
		}
		if ( midiOutputDeviceObj_m != null ){
			midiOutputDeviceObj_m.stop();
			midiOutputDeviceObj_m = null;
		}
		if ( this.usbDeviceConnectionObj_m != null ){
			this.usbDeviceConnectionObj_m.close();
			this.usbDeviceConnectionObj_m = null;
		}
		this.usbDeviceObj_m = null;
		
		Log.i("OnDeviceDetachedListenerImpl", usbDevice.getDeviceName() + " has been detached");
		
		if ( deviceDetachedHandlerObj_m == null ){
			return;
		}
		
		this.abstractSingleMIDIActivity_m.midiInputDeviceObj_m = null;
		this.abstractSingleMIDIActivity_m.midiOutputDeviceObj_m = null;
		
		Message message_f = new Message();
		message_f.obj = usbDevice;
		deviceDetachedHandlerObj_m.sendMessage(message_f);
	}
	
	//Constructor
	public OnMIDIDeviceDetachedListenerImpl(
			MIDIInputDevice midiInputDeviceObj, 
			MIDIOutputDevice midiOutputDeviceObj, 
			UsbDeviceConnection usbDeviceConnection, 
			UsbDevice usbDevice, 
			Handler deviceDetachedHandler,
			AbstractSingleMIDIActivity abstractSingleActivityInstance){
		this.midiInputDeviceObj_m = midiInputDeviceObj;
		this.midiOutputDeviceObj_m = midiOutputDeviceObj;
		this.usbDeviceConnectionObj_m = usbDeviceConnection;
		this.usbDeviceObj_m = usbDevice;		
		this.deviceDetachedHandlerObj_m = deviceDetachedHandler;
		abstractSingleMIDIActivity_m = abstractSingleActivityInstance;
	}

}
