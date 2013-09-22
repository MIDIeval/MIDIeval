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

import com.beta.listener.OnDeviceAttachedListener;
import com.beta.listener.OnDeviceDetachedListener;
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
	
	/*Parameterized constructor
	 *@Param 1: current context of invocation
	 *@Param 2: Device attached listener object reference
	 *@Param 3: Device detached listener object reference	 */	
	public MIDIDeviceConnectionWatcher(final Context context, final OnDeviceAttachedListener deviceAttachedListener, final OnDeviceDetachedListener deviceDetachedListener){
		grantedDeviceMap_m = new HashMap<String, UsbDevice>();
		thread_m = new MIDIDeviceConnectionWatchThread(context, deviceAttachedListener, deviceDetachedListener);
		
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
	
	
	/*Author: Hrishik Mishra
	 *Class: Respond to broadcast made in the Android platform
	 *Functionality: Regulate the behavior of the application based on the broadcast received from Android	 */
	final class USBMIDIGrantedReceiver extends BroadcastReceiver{
		private final String deviceName_f;
		private final UsbDevice usbDevice_f;
		private final OnDeviceAttachedListener deviceAttachedListener_f;
		
		/*Parameterized constructor
		 *@Param 1: Device name connected
		 *@Param 2: UsbDevice object reference
		 *@Param 3: On device attached listener object*/
		public USBMIDIGrantedReceiver(final String deviceName, final UsbDevice usbDevice, final OnDeviceAttachedListener deviceAttachedListener){
			this.deviceName_f = deviceName;
			this.usbDevice_f = usbDevice;
			this.deviceAttachedListener_f = deviceAttachedListener;
			
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
						this.deviceAttachedListener_f.onDeviceAttached(usbDevice_f);
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
		private OnDeviceAttachedListener deviceAttachedListener_m;
		private OnDeviceDetachedListener deviceDetachedListener_m;
		private HashSet<String> deviceNameSet_m;
		private boolean stopFlag_m;
		
		/*Parameterized constructor
		 *@Param 1:context of invocation
		 *@Param 2: Device attached listener object reference
		 *@Param 3: Device detached listener object reference
		 */
		public MIDIDeviceConnectionWatchThread(final Context context, final OnDeviceAttachedListener deviceAttachedListener, final OnDeviceDetachedListener deviceDetachedListener){
			this.context_m = context;
			this.deviceAttachedListener_m = deviceAttachedListener;
			this.deviceDetachedListener_m = deviceDetachedListener;
			this.deviceNameSet_m = new HashSet<String>();
			this.stopFlag_m = false;
		}
		
		
		@Override
		public void run(){
			super.run();
			while(this.stopFlag_m = false){
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
			//getDeviceList returns a list of all the attached USB devices to the Android Host
			HashMap<String, UsbDevice> usbDevicesMap_f = this.usbManager_m.getDeviceList();
			
			//check attached device
			for( String deviceName: usbDevicesMap_f.keySet()){
				if( !this.deviceNameSet_m.contains(deviceName) ){
					this.deviceNameSet_m.add(deviceName);
					UsbDevice usbDevice_f = usbDevicesMap_f.get(deviceName);
					
					List<DeviceFilter> deviceFilters_f;
					try {
						deviceFilters_f = DeviceFilter.getDeviceFilters(this.context_m);
					
						Set<UsbInterface> midiInterfaces_f = UsbMIDIDeviceUtil.findAllMIDIInterfaces(usbDevice_f, deviceFilters_f);
						if( midiInterfaces_f.size() > 0 ){
							PendingIntent permissionIntent = PendingIntent.getBroadcast(context_m, 0, new Intent(Constants.USB_PERMISSION_GRANTED_ACTION), 0);
							this.context_m.registerReceiver(new USBMIDIGrantedReceiver(deviceName, usbDevice_f, deviceAttachedListener_m) , new IntentFilter(Constants.USB_PERMISSION_GRANTED_ACTION));
							usbManager_m.requestPermission(usbDevice_f, permissionIntent);
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
			//Check detached device
			List<String> removeDeviceNames_f = new ArrayList<String>();
			for( String deviceName: this.deviceNameSet_m ){
				if( usbDevicesMap_f.containsKey(deviceName)){
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
		
		
	}
	

}
