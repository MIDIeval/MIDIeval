package com.beta.thread;

import java.util.NoSuchElementException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import android.util.Log;

import com.beta.ControlMapper.MapperPrototype;
import com.beta.Controllability.ControlValuePacket;
import com.beta.Controllability.IController;
import com.beta.MIDIUSBFunctinality.MIDIOutputDevice;


public class DeviceWriteThread extends Thread {
	private final ReentrantLock deviceWriteLockObj_m = new ReentrantLock();
	private final Condition queueNotFullConditionObj_m = getDeviceWriteLockObj().newCondition();
	private boolean b_ThreadExit_m = false;
	private boolean b_IsToBeSuspended_m = false;
	private ControlValuePacket controlValuePacketObj_m;
	private MIDIOutputDevice midiOutputDeviceObj_m;
	private int functionValue_m = -1;
	private int channelNumber_m = -1;
	private float f_Value_m = -1.0f;
	private static final String s_Tag_m = "DEVICE_WRITE_THREAD";
	private MapperPrototype mapperPrototypeObj_m;
	private float previousTime;
	@Override 
	public void run(){
		while ( !this.b_ThreadExit_m ){
			if ( this.getIsToBeSuspended() )
			{
				if (!getDeviceWriteLockObj().isHeldByCurrentThread()){
					this.getDeviceWriteLockObj().lock(); //If Thread is suspended, give up the Reentrant lock object
				}
				try {				
					Log.i(s_Tag_m, "DEVICE WRITE THREAD waiting");
					//Lock is released from Write Thread
					if ( IController.queueObj_m.size() == 0)
						this.getQueueNotFullCondition().await();//AbstractActivity has to .signal() this object
					else
						this.b_IsToBeSuspended_m = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else{
				if ( !this.getDeviceWriteLockObj().isHeldByCurrentThread()){
					getDeviceWriteLockObj().lock();
					Log.i(s_Tag_m, "<--------Lock count by Write Thread when LOCKING :" + this.getDeviceWriteLockObj().getHoldCount());
				}
				try{
					synchronized(IController.queueObj_m){
						if ( !IController.queueObj_m.isEmpty() ){
							Log.i(s_Tag_m, "DEVICE THREAD QUEUE SIZE: " + IController.queueObj_m.size());
							controlValuePacketObj_m = IController.queueObj_m.poll();
						}
					}				
					
					if ( controlValuePacketObj_m != null )
					{
						Log.i(s_Tag_m, "Controller: " + controlValuePacketObj_m.getControllerType() + "Sub Controller: " + controlValuePacketObj_m.getSubControllerID());
						this.f_Value_m = this.controlValuePacketObj_m.getValueVector();
						Log.i(s_Tag_m, "Value: " + this.f_Value_m);
						//Get the appropriate value using the Mapper facility
						//Then get the right FunctionValue from the Mapper facility
						if ( this.midiOutputDeviceObj_m != null ){
							if ( this.mapperPrototypeObj_m != null ){
								//functionValue_m = this.mapperPrototypeObj_m.getFunctionValue(controlValuePacketObj_m.getiControllerPointer(), controlValuePacketObj_m.getSubControllerID());
								functionValue_m = controlValuePacketObj_m.getI_FunctionValue();
								channelNumber_m = controlValuePacketObj_m.getChannelVector();
								Log.i(s_Tag_m, "Sub Controller ID Function value: " + functionValue_m + ", Value: " + f_Value_m);
								midiOutputDeviceObj_m.fn_ControlChangeMessage(0, channelNumber_m, functionValue_m, (int)f_Value_m);
								Log.i("WRITE TIME", String.valueOf((System.nanoTime() - previousTime)/1000000));
								previousTime = System.nanoTime();
								Log.i(s_Tag_m, "Queue Size:" + String.valueOf(IController.queueObj_m.size()));
							//Keep writing to the Device
							}
						}
						
					}
				}
				catch ( NoSuchElementException ex){
					//Log.e(s_Tag_m, ex.getMessage());
				}
				finally{
					if (IController.queueObj_m.isEmpty()){
						if ( this.getDeviceWriteLockObj().isHeldByCurrentThread())
							this.getDeviceWriteLockObj().unlock();
						this.b_IsToBeSuspended_m = true;
					}
				}			
				
			}
			try {
				//Log.i(s_Tag_m, "END OF ITERATION");
				Thread.sleep(2);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	/**
	 * @return the b_ThreadExit_m
	 */
	public boolean getIsThreadExit() {
		return b_ThreadExit_m;
	}
	/**
	 * @param b_ThreadExit_m the b_ThreadExit_m to set
	 */
	public void setIsThreadExit(boolean b_ThreadExit_m) {
		this.b_ThreadExit_m = b_ThreadExit_m;
	}
	/**
	 * @return the midiOutputDeviceObj_m
	 */
	public MIDIOutputDevice getMidiOutputDevice() {
		return midiOutputDeviceObj_m;
	}
	/**
	 * @param midiOutputDeviceObj_m the midiOutputDeviceObj_m to set
	 */
	public void setMidiOutputDevice(MIDIOutputDevice midiOutputDeviceObj_m) {
		this.midiOutputDeviceObj_m = midiOutputDeviceObj_m;
	}
	/**
	 * @return the b_IsToBeSuspended_m
	 */
	public boolean getIsToBeSuspended() {
		return b_IsToBeSuspended_m;
	}
	/**
	 * @param b_IsToBeSuspended_m the b_IsToBeSuspended_m to set
	 */
	public void setIsToBeSuspended(boolean b_IsToBeSuspended_m) {
		this.b_IsToBeSuspended_m = b_IsToBeSuspended_m;
	}
	/**
	 * @return the queueNotFullConditionObj_m
	 */
	public Condition getQueueNotFullCondition() {
		return queueNotFullConditionObj_m;
	}
	/**
	 * @return the deviceWriteLockObj_m
	 */
	public ReentrantLock getDeviceWriteLockObj() {
		return deviceWriteLockObj_m;
	}
	/**
	 * @return the mapperPrototypeObj_m
	 */
	public MapperPrototype getMapperPrototype() {
		return mapperPrototypeObj_m;
	}
	/**
	 * @param mapperPrototypeObj_m the mapperPrototypeObj_m to set
	 */
	public void setMapperPrototype(MapperPrototype mapperPrototypeObj_m) {
		this.mapperPrototypeObj_m = mapperPrototypeObj_m;
	}

}
