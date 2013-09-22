package com.beta.MIDIUSBFunctinality;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.os.Handler;
import android.os.Message;

import com.beta.handler.MIDIMessageCallback;
import com.beta.listener.OnMidiInputEventListener;


public class MIDIInputDevice {
	
	//Member variable declarations
	private final UsbCommunication usbCommObj_m;
	private MIDIDataPacket midiDataPacket_m;
	private final WaiterThread waiterThread_m; 
	
	//Properties of member variables	
	public UsbCommunication getUsbCommObj() {
		return usbCommObj_m;
	}
	
	public MIDIDataPacket getMidiDataPacket() {
		return midiDataPacket_m;
	}
	public void setMidiDataPacket(MIDIDataPacket midiDataPacket_m) {
		this.midiDataPacket_m = midiDataPacket_m;
	}
	
	public MIDIInputDevice(MIDIDataPacket midiDataPacket, UsbCommunication usbCommunicationObj, OnMidiInputEventListener midiEventListener){
		this.midiDataPacket_m = midiDataPacket;		
		this.waiterThread_m = new WaiterThread(new Handler(new MIDIMessageCallback(this, midiEventListener)));
		this.usbCommObj_m = usbCommunicationObj;
		
		//Start running the thread to read the data from the MIDI device
		this.waiterThread_m.start();
	}
	
	public MIDIInputDevice(UsbCommunication usbCommunicationObj, OnMidiInputEventListener midiEventListener){
		this.waiterThread_m = new WaiterThread(new Handler(new MIDIMessageCallback(this, midiEventListener)));
		this.usbCommObj_m = usbCommunicationObj;
		
		//Start running the thread to read the data from the MIDI device
		this.waiterThread_m.start();
	}
	
	public MIDIInputDevice(final UsbDevice usbDevice, final UsbDeviceConnection usbDeviceConnection,final UsbEndpoint usbEndpoint,final UsbInterface usbInterface, OnMidiInputEventListener midiEventListener){
		this(new UsbCommunication(usbDevice, usbDeviceConnection, usbEndpoint, usbInterface), midiEventListener);
	}
	
	public void stop(){
		//synchronized(this.waiterThread_m){
			this.waiterThread_m.stopFlag_m = true;
			while ( this.waiterThread_m.isAlive() ){
				try{
					Thread.sleep(100);
				}
				catch ( InterruptedException ex ){
					
				}
			}
			this.usbCommObj_m.getUsbDeviceConnection().releaseInterface(this.usbCommObj_m.usbInterface_m);	
			
		//}
	}
	
	private class WaiterThread extends Thread
	{
		public boolean stopFlag_m;
		private Handler receiveHandler_m;
		
		public WaiterThread(final Handler handler){
			stopFlag_m = false;
			this.receiveHandler_m = handler;		
		}
		
		@Override 
		public void run()
		{
			while(true){
				synchronized(this){
					if( this.stopFlag_m )
						return;	
				}
				if ( usbCommObj_m.usbEndpoint_m == null)
					continue;
				byte[] byteRead_f = usbCommObj_m.fn_ReadUSBMessage();
				//Check if byte is empty
				if (byteRead_f != null ){
					Message message = new Message();
					message.obj = byteRead_f;
				
				//Send message
					this.receiveHandler_m.sendMessage(message);
				}
				
				//If many devices are connected, the aforementioned code section may be used.
				//try{
					//sleep(10);							
				//}
				//catch(Exception ex)
				//{
				
			}
			
		
		}
	}


}

