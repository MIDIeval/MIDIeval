package com.sensor.sensorlibrary;

import android.util.Log;


/*Class: AccelerometerDataPacket
 *Function: Stores the Accelerometer value vector
 *Author: Hrishik Mishra 
 */
public class AccelerometerDataPacket {
	//Members
	
	//Stores the accelorometer vector. eg) [ax ay az]' or [ax ay]'
	private float[] f_Accelerometer_vector_m;
	//Size of the accelerometer vector
	private int i_Size_m;
	//Maximum Range of the accelerometer sensor
	private float f_MaximumRange_m;
	
	//Properties
	public float[] getAccelerometerVector() {
		return f_Accelerometer_vector_m;
	}
	public void setAccelerometerVector(float[] f_Accelerometer_vector_m) throws Exception {
		if ( f_Accelerometer_vector_m != null )
			this.f_Accelerometer_vector_m = f_Accelerometer_vector_m;
		else{
			Log.e(Constants.ACCELEROMETER_DATA_PACKET_NULL_TAG, Constants.ACCELEROMETER_DATA_PACKET_NULL_TEXT);
			throw new Exception(Constants.ACCELEROMETER_DATA_PACKET_NULL_TEXT);
		}
	}
	public int getSize() {
		return i_Size_m;
	}
	public void getSize(int i_Size_m) {
		this.i_Size_m = i_Size_m;
	}
	public float getMaximumRange() {
		return f_MaximumRange_m;
	}
	public void setMaximumRange(float f_MaximumRange_m) {
		this.f_MaximumRange_m = f_MaximumRange_m;
	}
	
	
	//Constructor
	//@param1: size of the vector
	//throws exception if size of vector is incorrect
	public AccelerometerDataPacket(int size) throws Exception{
		f_MaximumRange_m = 4.0f;
		if ( size > 0 || size <=3 ){
			f_Accelerometer_vector_m = new float[size];
			Log.i(Constants.ACCELEROMETER_DATA_PACKET_SIZE_TAG, String.valueOf(size));
		}
		else{
			Log.e(Constants.ACCELEROMETER_DATAPACKET_SIZE_INCORRECT_TAG, Constants.ACCELEROMETER_DATAPACKET_SIZE_INCORRECT_TEXT);
			throw new Exception(Constants.ACCELEROMETER_DATAPACKET_SIZE_INCORRECT_TAG);
		}
			
		
	}
	
	//Method definitions
	public String toString(){
		StringBuilder printString;
		if ( f_Accelerometer_vector_m != null ){
			printString = new StringBuilder();
			for(double vectorEntity:f_Accelerometer_vector_m ){
				printString.append(vectorEntity);
				printString.append(",");
			}
			return printString.toString();
		}
		return null;
	}
	

	

}
