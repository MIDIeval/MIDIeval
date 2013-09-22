package com.beta.listener;

import android.hardware.usb.UsbDevice;

/*Author: Hrishik Mishra
 *Interface: MIDI device attached listener
 *Functionality: declares the event wired method to handle events
 */
public interface OnMIDIDeviceAttachedListener {
	
	/*Function that responds to the event that a USB device has been attached
	 *@Param 1:The device that has been attached*/	
	void onDeviceAttached(final UsbDevice usbDevice);

}
