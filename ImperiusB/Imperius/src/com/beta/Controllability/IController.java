package com.beta.Controllability;

import java.util.PriorityQueue;
import java.util.Queue;


/*Interface: IController
 *Function: Defines the basic functions of a controller class of instances, 
 *			eg) UI component, Sensor component, LFO and Automation
 *Author: Hrishik Mishra 
 */
public abstract class IController {
	//Members
	private static Queue<ControlValuePacket> queueObj_m;
	private Object lockObject_m = new Object();
	
	
	//Constructor
	//As soon as the first instance of the IController exists, initialise the queue.
	public IController(){
		this.fn_InitializeQueue();
	}
	//Functions
	/*Function: fn_InitializeQueue
	 *Returns a boolean if queue is initialised
	 *True: if Queue is created/ False: if Queue creation has failed 
	 */
	public final boolean fn_InitializeQueue(){
		if ( queueObj_m != null )
			return true;
		else{
			synchronized(lockObject_m){
				if ( queueObj_m == null ){
					this.queueObj_m = new PriorityQueue<ControlValuePacket>();
				}
			}
		}
		if ( this.queueObj_m == null )
			return false;
		else 
			return true;
	}
	
	//Queue operations which will be implemented by controller classes 
	//based on the data they generate as Controllers.
	public abstract void fn_PutToQueue();
	public abstract ControlValuePacket fn_DequeuFromQueue();
	//Properties
	/**
	 * @return the queueObj_m
	 */
	Queue getQueue() {
		return queueObj_m;
	}
	
	

}
