package com.beta.listener;

import java.util.List;
import java.util.Set;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.beta.MIDIUSBFunctinality.MIDIInputDevice;
import com.beta.MIDIUSBFunctinality.MIDIOutputDevice;
import com.beta.imperius.AbstractSingleMIDIActivity;
import com.beta.usb.util.DeviceFilter;
import com.beta.util.UsbMIDIDeviceUtil;

public class OnMIDIDeviceAttachedListenerImpl implements
		OnMIDIDeviceAttachedListener {
	protected UsbManager usbManagerObj_m;
	protected UsbDeviceConnection usbDeviceConnectionObj_m;
	protected List<DeviceFilter> deviceFilterList_m;
	protected AbstractSingleMIDIActivity abstractActivityInstance_m;
	public MIDIInputDevice midiInputDeviceObj_m;
	public MIDIOutputDevice midiOutputDeviceObj_m;
	@Override
	public void onDeviceAttached(UsbDevice usbDevice) {
		
		if ( usbDevice != null ){
			//Already one device has been connected
			//return;
		}
		if ( this.usbManagerObj_m == null ){
			return;
		}
		this.usbDeviceConnectionObj_m = this.usbManagerObj_m.openDevice(usbDevice);
		if ( this.usbDeviceConnectionObj_m == null )
			return;
		if ( this.deviceFilterList_m == null )
			return;
		
		Set<MIDIInputDevice> foundInputDevices = UsbMIDIDeviceUtil.findAllMIDIInputDevices(usbDevice, usbDeviceConnectionObj_m, deviceFilterList_m, abstractActivityInstance_m);
		if ( foundInputDevices.size() > 0 )
		{
			midiInputDeviceObj_m = (MIDIInputDevice)foundInputDevices.toArray()[0];
		}
		
		Set<MIDIOutputDevice> foundOutputDevices = UsbMIDIDeviceUtil.findAllMIDIOutputDevices(usbDevice, usbDeviceConnectionObj_m, deviceFilterList_m);
		if ( foundOutputDevices.size() > 0)
		{
			midiOutputDeviceObj_m = (MIDIOutputDevice)foundOutputDevices.toArray()[0];
		}
		
		Log.d("OnMIDIDeviceAttachedListenerImpl", "Device " + usbDevice.getDeviceName() + " has been attached");
		
		abstractActivityInstance_m.midiInputDeviceObj_m = midiInputDeviceObj_m;
		abstractActivityInstance_m.midiOutputDeviceObj_m = midiOutputDeviceObj_m;
		
		abstractActivityInstance_m.onDeviceAttached(usbDevice);
		

	}
	
	//Parameteric Constructor
	public OnMIDIDeviceAttachedListenerImpl(
			UsbManager usbManagerObj, 
			List<DeviceFilter> deviceFilterList, 
			AbstractSingleMIDIActivity abstractActivityInstance){
		this.usbManagerObj_m = usbManagerObj;	
		this.deviceFilterList_m = deviceFilterList;
		this.abstractActivityInstance_m = abstractActivityInstance;
		
	}

}
