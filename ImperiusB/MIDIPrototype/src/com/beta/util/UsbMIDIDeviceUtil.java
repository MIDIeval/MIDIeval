package com.beta.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import com.beta.MIDIUSBFunctinality.MIDIInputDevice;
import com.beta.MIDIUSBFunctinality.MIDIOutputDevice;
import com.beta.listener.OnMidiInputEventListener;
import com.beta.usb.util.DeviceFilter;


/*Author: Hrishik Mishra
 *Class: Poll the USB device list to find if a device has been attached/Detached
 *Functionality: associate event handlers to the USB attach/detach event and poll the USB list
 */
//<variable>_m is a member variable
//<variable>_f is a functional local variable
public class UsbMIDIDeviceUtil {
	
	public static Set<UsbInterface> findMIDIInterfaces( final UsbDevice usbDevice, int direction, List<DeviceFilter> deviceFilters){
		Set<UsbInterface> usbInterfacesList_f = new HashSet<UsbInterface>();
		int interfaceCount_f = usbDevice.getInterfaceCount();
		for ( int iCount = 0; iCount < interfaceCount_f; iCount++){
			UsbInterface usbInterface_f = usbDevice.getInterface(iCount);
			if( findMIDIEndpoint(usbDevice, usbInterface_f, direction, deviceFilters) != null ){
				usbInterfacesList_f.add(usbInterface_f);
			}
		}
		return usbInterfacesList_f;
	}
	
	public static Set<UsbInterface> findAllMIDIInterfaces(final UsbDevice usbDevice, List<DeviceFilter> deviceFilters){
		Set<UsbInterface> usbInterfacesList_f = new HashSet<UsbInterface>();		
		int interfaceCount_f = usbDevice.getInterfaceCount();
		
		for( int iCount = 0; iCount < interfaceCount_f; iCount++) {
			UsbInterface usbInterface_f = usbDevice.getInterface(iCount);
			
			if ( findMIDIEndpoint(usbDevice, usbInterface_f, UsbConstants.USB_DIR_IN, deviceFilters ) != null )
				usbInterfacesList_f.add(usbInterface_f);
			if( findMIDIEndpoint(usbDevice, usbInterface_f, UsbConstants.USB_DIR_OUT, deviceFilters) != null )
				usbInterfacesList_f.add(usbInterface_f);		
			
		}
		return usbInterfacesList_f;		
	}
	
	public static Set<MIDIInputDevice> findAllMIDIInputDevices(UsbDevice usbDevice, UsbDeviceConnection usbDeviceConnection, List<DeviceFilter> deviceFilters, OnMidiInputEventListener inputEventListener){
		Set<MIDIInputDevice> usbDevicesList_f = new HashSet<MIDIInputDevice>();
		int interfaceCount_f = usbDevice.getInterfaceCount();
		
		for ( int iCount = 0; iCount < interfaceCount_f; iCount++ )
		{
			final UsbInterface usbInterface_f = usbDevice.getInterface(iCount);
			
			final UsbEndpoint usbEndpoint_f = findMIDIEndpoint(usbDevice, usbInterface_f, UsbConstants.USB_DIR_IN, deviceFilters);			
			if( usbEndpoint_f != null){
				usbDevicesList_f.add(new MIDIInputDevice(usbDevice, usbDeviceConnection, usbEndpoint_f, usbInterface_f, inputEventListener));
			}
		}
		return usbDevicesList_f;
	}
	
	public static Set<MIDIOutputDevice> findAllMIDIOutputDevices(UsbDevice usbDevice, UsbDeviceConnection usbDeviceConnection, List<DeviceFilter> deviceFilters){
		Set<MIDIOutputDevice> usbDevicesList_f = new HashSet<MIDIOutputDevice>();
		int interfaceCount_f = usbDevice.getInterfaceCount();
		
		for( int iCount = 0; iCount < interfaceCount_f; iCount++ ){
			final UsbInterface usbInterface_f = usbDevice.getInterface(iCount);
			if ( usbInterface_f == null )
				continue;
			final UsbEndpoint usbEndpoint_f = findMIDIEndpoint(usbDevice, usbInterface_f, UsbConstants.USB_DIR_OUT, deviceFilters );
			if ( usbEndpoint_f != null ){
				usbDevicesList_f.add(new MIDIOutputDevice(usbDevice, usbDeviceConnection, usbEndpoint_f, usbInterface_f));
			}
		}
		return usbDevicesList_f;
	}
	
	private static UsbEndpoint findMIDIEndpoint(final UsbDevice usbDevice, final UsbInterface usbInterface, int direction, List<DeviceFilter> deviceFilters){
		if( usbInterface.getInterfaceClass() == 1 && usbInterface.getInterfaceSubclass() == 3 ){
			for( int endpointIndex = 0; endpointIndex < usbInterface.getEndpointCount(); endpointIndex++ ){
				UsbEndpoint usbEndpoint_f = usbInterface.getEndpoint(endpointIndex);
				if( usbEndpoint_f.getDirection() == direction )
					return usbEndpoint_f;
			}		
		}
		else{
			boolean filterMatched = false;
			for( DeviceFilter deviceFilter: deviceFilters ){
				if( deviceFilter.matches(usbDevice)){
					filterMatched = true;
					break;
				}
			}
			if( filterMatched == false ){
				Log.e(Constants.USB_MIDI_INCOMPATIBLE_INTERFACE, "Interface: " + usbInterface );
				return null;
			}
			
			for ( int endpointIndex = 0; endpointIndex < usbInterface.getEndpointCount(); endpointIndex++ ){
				UsbEndpoint usbEndpoint_f = usbInterface.getEndpoint(endpointIndex);
				if ( usbEndpoint_f.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK || usbEndpoint_f.getType() == UsbConstants.USB_ENDPOINT_XFER_INT ){
					if( usbEndpoint_f.getDirection() == direction )
						return usbEndpoint_f;
				}
			}
				
		}
		return null;	
		
	}	

}

