package com.beta.listener;

import android.hardware.usb.UsbDevice;


/*Author: Hrishik Mishra
 *Interface: MIDI device detached listener
 *Functionality: declares the event wired method to handle events
 */
public interface OnMIDIDeviceDetachedListener {
	
	/*Function that responds to the event that a USB device has been detached
	 *@Param 1:The device that has been detached*/	
	void onDetachedDevice(final UsbDevice usbDevice);

}
