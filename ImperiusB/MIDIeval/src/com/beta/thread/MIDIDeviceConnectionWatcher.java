package com.beta.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.beta.MIDIUSBFunctinality.MIDIInputDevice;
import com.beta.imperius.AbstractSingleMIDIActivity;
import com.beta.listener.OnMIDIDeviceAttachedListener;
import com.beta.listener.OnMIDIDeviceDetachedListener;
import com.beta.usb.util.DeviceFilter;
import com.beta.util.Constants;
import com.beta.util.UsbMIDIDeviceUtil;


/*Author: Hrishik Mishra
 *Class: Poll the USB device list to find if a device has been attached/Detached
 *Functionality: associate event handlers to the USB attach/detach event and poll the USB list
 */
//<variable>_m is a member variable
//<variable>_f is a functional local variable
public class MIDIDeviceConnectionWatcher {
	
	//Member variable declaration
	private final MIDIDeviceConnectionWatchThread thread_m;
	private final HashMap<String, UsbDevice> grantedDeviceMap_m;
	public OnMIDIDeviceDetachedListener onMIDIDeviceDetachedListenerRef_m = null;
	public AbstractSingleMIDIActivity caller_m;
	/*Parameterized constructor
	 *@Param 1: current context of invocation
	 *@Param 2: Device attached listener object reference
	 *@Param 3: Device detached listener object reference	 */	
	public MIDIDeviceConnectionWatcher(final Context context, final OnMIDIDeviceAttachedListener deviceAttachedListener, final OnMIDIDeviceDetachedListener deviceDetachedListener, UsbManager usbManager, AbstractSingleMIDIActivity caller){
		this.caller_m = caller;
		grantedDeviceMap_m = new HashMap<String, UsbDevice>();
		thread_m = new MIDIDeviceConnectionWatchThread(context, deviceAttachedListener, deviceDetachedListener, usbManager, this);
		this.onMIDIDeviceDetachedListenerRef_m = deviceDetachedListener;
		thread_m.start();
	}
	
	/*Function to check the state of devices and remove/add to the list based on event*/
	public void checkConnectedDevicesImmediately(){
		thread_m .checkConnectedDevices();
	}
	
	//Function to stop the current thread by setting the flag.
	public void stop(){
		thread_m.stopFlag_m = true;
	}
	
	public void fn_RegisterOnDeviceDetachListener(UsbDevice usbDevice){
		caller_m.fn_RegisterOnDetachListener(usbDevice);
	}
	public void fn_SetOnDeviceDetachedListener(OnMIDIDeviceDetachedListener detachedListenerRef){
		this.thread_m.deviceDetachedListener_m = detachedListenerRef;
	}
	
	
	/*Author: Hrishik Mishra
	 *Class: Respond to broadcast made in the Android platform
	 *Functionality: Regulate the behavior of the application based on the broadcast received from Android	 */
	final class USBMIDIGrantedReceiver extends BroadcastReceiver{
		public String deviceName_f;
		public UsbDevice usbDevice_f;
		private final OnMIDIDeviceAttachedListener deviceAttachedListener_f;
		private final MIDIDeviceConnectionWatchThread callerObj_f;
		
		/*Parameterized constructor
		 *@Param 1: Device name connected
		 *@Param 2: UsbDevice object reference
		 *@Param 3: On device attached listener object*/
		public USBMIDIGrantedReceiver(final String deviceName, final UsbDevice usbDevice, final OnMIDIDeviceAttachedListener deviceAttachedListener, MIDIDeviceConnectionWatchThread caller){
			this.deviceName_f = deviceName;
			this.usbDevice_f = usbDevice;
			this.deviceAttachedListener_f = deviceAttachedListener;
			this.callerObj_f = caller;
			
		}
		
		/*Function to receive Intent when a broadcast is made from the Android OS.
		 * (non-Javadoc)
		 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
		 */
		@Override
		public void onReceive(final Context context, final Intent intent){
			String action_f = intent.getAction();
			if( Constants.USB_PERMISSION_GRANTED_ACTION.equals(action_f)){
				boolean granted_f = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false);
				if( granted_f ){
					if( this.deviceAttachedListener_f != null && this.usbDevice_f != null){
						grantedDeviceMap_m.put(deviceName_f, usbDevice_f);
						this.deviceAttachedListener_f.onDeviceAttached(this.usbDevice_f);
						this.callerObj_f.fn_RegisterOnDetachListener(this.usbDevice_f);
						this.callerObj_f.context_m.unregisterReceiver(this);

						//Here we need to call a method from MIDIDeviceConnectionWatchThread instance
						
					}
				}
			}
		}
	}
	

	/*Author: Hrishik Mishra
	 *Class: Respond to broadcast made in the Android platform
	 *Functionality: Regulate the behavior of the application based on the broadcast received from Android	 */
	final class MIDIDeviceConnectionWatchThread extends Thread{
		private Context context_m;
		private UsbManager usbManager_m;
		public OnMIDIDeviceAttachedListener deviceAttachedListener_m;
		public OnMIDIDeviceDetachedListener deviceDetachedListener_m;
		private HashSet<String> deviceNameSet_m;
		private boolean stopFlag_m;
		private MIDIDeviceConnectionWatcher caller_m;
		private USBMIDIGrantedReceiver usbMIDIGrantedReceiverObject_m;
		
		/*Parameterized constructor
		 *@Param 1:context of invocation
		 *@Param 2: Device attached listener object reference
		 *@Param 3: Device detached listener object reference
		 */
		public MIDIDeviceConnectionWatchThread(final Context context, final OnMIDIDeviceAttachedListener deviceAttachedListener, final OnMIDIDeviceDetachedListener deviceDetachedListener, final UsbManager usbManager, final MIDIDeviceConnectionWatcher caller){
			this.context_m = context;
			this.deviceAttachedListener_m = deviceAttachedListener;
			this.deviceDetachedListener_m = deviceDetachedListener;
			this.deviceNameSet_m = new HashSet<String>();
			this.stopFlag_m = false;
			this.usbManager_m = usbManager;
			this.caller_m = caller;
			usbMIDIGrantedReceiverObject_m = new USBMIDIGrantedReceiver(null, null, deviceAttachedListener_m, this);
			//The first two parameters will have to be passed during invoking the Registration process.
	
		}
		
		
		@Override
		public void run(){
			super.run();
			while(this.stopFlag_m == false && this.isAlive()){
				checkConnectedDevices();
				try{
					Thread.sleep(1000);					
				}
				catch(InterruptedException ex){
					Log.e(Constants.THREAD_INTERRUPTED, this.getClass().toString());
				}
			
			}
		}
		
		/*Function to check for connected/disconnected devices from the Android host*/
		synchronized void checkConnectedDevices(){
			try{
				//getDeviceList returns a list of all the attached USB devices to the Android Host
				HashMap<String, UsbDevice> usbDevicesMap_f = this.usbManager_m.getDeviceList();
				
				//	check attached device
				for( String deviceName: usbDevicesMap_f.keySet()){
					if( !this.deviceNameSet_m.contains(deviceName) ){
						this.deviceNameSet_m.add(deviceName);
						UsbDevice usbDevice_f = usbDevicesMap_f.get(deviceName);
						
						List<DeviceFilter> deviceFilters_f;
						try {
							deviceFilters_f = DeviceFilter.getDeviceFilters(this.context_m);
							
							Set<UsbInterface> midiInterfaces_f = UsbMIDIDeviceUtil.findAllMIDIInterfaces(usbDevice_f, deviceFilters_f);
							//Set<MIDIInputDevice> midiInputDeviceSet_f = UsbMIDIDeviceUtil.findAllMIDIInputDevices(usbDevice_f, usbManager_m.openDevice(usbDevice_f), deviceFilters_f, inputEventListener)
							if( midiInterfaces_f.size() > 0 ){
								PendingIntent permissionIntent = PendingIntent.getBroadcast(context_m, 0, new Intent(Constants.USB_PERMISSION_GRANTED_ACTION), 0);
								this.usbMIDIGrantedReceiverObject_m.deviceName_f = deviceName;
								this.usbMIDIGrantedReceiverObject_m.usbDevice_f = usbDevice_f;
								this.context_m.registerReceiver( usbMIDIGrantedReceiverObject_m, new IntentFilter(Constants.USB_PERMISSION_GRANTED_ACTION));
								usbManager_m.requestPermission(usbDevice_f, permissionIntent);								
							}
						} catch (IOException e) {
						// 	TODO Auto-generated catch block
							e.printStackTrace();
						}
					
				}
			}
			
			//Check detached device
			List<String> removeDeviceNames_f = new ArrayList<String>();
			for( String deviceName: this.deviceNameSet_m ){
				if( !usbDevicesMap_f.containsKey(deviceName)){
					removeDeviceNames_f.add(deviceName);
					UsbDevice usbDevice_f = grantedDeviceMap_m.remove(deviceName);
					
					if( usbDevice_f != null ){
						deviceDetachedListener_m.onDetachedDevice(usbDevice_f);
					}
				
						
				}
			}
			//Remove the device from the list
			for( String deviceName : removeDeviceNames_f){
				deviceNameSet_m.remove(deviceName);
			}
				
		}
		catch( Exception ex){
			Log.e("MIDIDeviceConnectionWatcher","Error in CheckConnectedDevices", ex);
		}
		
		
	}
		
	public void fn_RegisterOnDetachListener(UsbDevice usbDevice){
		caller_m.fn_RegisterOnDeviceDetachListener(usbDevice);
	}
	
	}
	

}
