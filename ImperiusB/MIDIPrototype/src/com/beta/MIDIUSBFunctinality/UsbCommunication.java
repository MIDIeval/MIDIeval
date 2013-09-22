package com.beta.MIDIUSBFunctinality;


import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;
import com.beta.util.*;

/*Author: Hrishik Mishra
 *Class: UsbCommunication
 *Functionality: Defines attributes of USB communication using the Android host.
 */

//<variable>_m is a member variable
//<variable>_f is a functional local variable
public class UsbCommunication 
{
	//Member variable declaration
	
	/*UsbDevice represents a USB device connected to the Android Host as a peripheral.
	 *The class describes the capabilities of the device connected
     *Each device contains several USB interfaces*/	 	
	protected final UsbDevice usbDevice_m;
	
	/*An interface describes grouping of endpoints to achieve a functional aspect of the device 
	 *A UsbDevice object may have several UsbInterfaces depending upon the configuration
	 *A UsbInterface shall have several UsbEndpoint objects(channels) to communicate*/
	protected final UsbInterface usbInterface_m;
	
	/*Helper class to transfer data/control messages on the device connected to the Android host*/ 
	protected final UsbDeviceConnection usbDeviceConnection_m; 
	
	/*UsbEndpoint is a channel of communication to read/write data to the device
	 *0: control communication transfer
	 *Bulk endpoints: non-trivial data transfer
	 *Isochronus endpoints: not supported
	 *Interrupt endpoints: small amounts of data*/	
	protected final UsbEndpoint usbEndpoint_m;
	
	/*Parameterised constructor
	 *@Param 1: usb device
	 *@Param 2: usb device connection
	 *@Param 3: usb endpoint
	 *@Param 4: usb interface*/
	public UsbCommunication(final UsbDevice usbDevice, final UsbDeviceConnection usbDeviceConnection, final UsbEndpoint usbEndpoint, final UsbInterface usbInterface)
	{
		this.usbDevice_m = usbDevice;
		this.usbInterface_m = usbInterface;
		this.usbDeviceConnection_m = usbDeviceConnection;
		this.usbEndpoint_m = usbEndpoint;
		if( this.usbEndpoint_m == null ){
			throw new IllegalArgumentException("Endpoint was not found");
		}
		Log.i(com.beta.util.Constants.TAG, "Usb Device: " + this.getUsbDevice_m() + ", Usb Interface: " + this.usbInterface_m + ", Usb Endpoint: " + this.usbEndpoint_m + ", Usb Device Connection: " + this.usbDeviceConnection_m );
		
		this.usbDeviceConnection_m.claimInterface(this.usbInterface_m, true);//claimInterface(interface, force):
		//force is passed as true to disconnect kernel driver
	}
	
	//Properties for member variables
	protected UsbDevice getUsbDevice_m() {
		return usbDevice_m;
	}
	
	public UsbInterface getUsbInterface_m() {
		return usbInterface_m;
	}


	public UsbEndpoint getUsbEndpoint_m() {
		return usbEndpoint_m;
	}
	 
	//Method definitions
	  
	/*Function that writes message by bulk transfer to the peripheral USB device
	 *@Param 1: bytes to be written to the USB port*/	
	public final boolean fn_WriteUSBMessage(byte[] usbDataPacket){
		int usbDeviceBulkTransferStatus_f = 0;
		usbDeviceBulkTransferStatus_f = this.usbDeviceConnection_m.bulkTransfer(this.usbEndpoint_m, usbDataPacket, usbDataPacket.length, 0);
		
		Log.i(com.beta.util.Constants.USBWRITE, usbDataPacket.toString());
		
		if( usbDeviceBulkTransferStatus_f < 0){
			return false;
		}
		
		return true;					
	}
	
	/*Function that reads message by bulk transfer from the peripheral USB device
	 *Return 1: bytes read from the USB port*/	
	public final byte[] fn_ReadUSBMessage(){
		
		byte[] usbDataPacket_f = new byte[64];
		int usbDeviceBulkTransferStatus_f = 0;
		byte[] readUSBBuffer_f = null;
		
		usbDeviceBulkTransferStatus_f = this.usbDeviceConnection_m.bulkTransfer(this.usbEndpoint_m, usbDataPacket_f, usbDataPacket_f.length, 0);
		if( usbDeviceBulkTransferStatus_f > 0 ){			
			readUSBBuffer_f = new byte[usbDeviceBulkTransferStatus_f];
			System.arraycopy(usbDataPacket_f, 0, readUSBBuffer_f, 0, usbDataPacket_f.length);			
		}
		Log.i(com.beta.util.Constants.USBREAD, readUSBBuffer_f.toString());
		return readUSBBuffer_f;
	}
	
	
	 
}
