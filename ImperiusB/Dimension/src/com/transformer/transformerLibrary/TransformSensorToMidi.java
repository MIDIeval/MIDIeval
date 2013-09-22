package com.transformer.transformerLibrary;

import java.text.DecimalFormat;
import java.util.Observable;
import java.util.Observer;

import com.sensor.sensorlibrary.AccelerometerDataPacket;

/*Class: TransformSensorToMidi
 *Function: Convert "Filtered" sensor signals to MIDI understandable values
 *Author: Hrishik Mishra
 */
public class TransformSensorToMidi extends Observable implements Observer {
	
	//Methods
	//Type of Transformation from filtered sensor signal to the MIDI signal
	private TRANSFORM_FUNCTION transformFunction_m; 
	private AccelerometerDataPacket dataPacket_m;
	private float[] f_MIDI_Vector_m;
	private float f_MIDIRange_m;
	
	//Properties
	TRANSFORM_FUNCTION getTransformFunction() {
		return transformFunction_m;
	}
	void setTransformFunction(TRANSFORM_FUNCTION transformFunction_m) {
		this.transformFunction_m = transformFunction_m;
	}
	
	//Constructor
	public TransformSensorToMidi(){
		this.transformFunction_m = TRANSFORM_FUNCTION.LINEAR_MODULO;
		f_MIDIRange_m = 127.0f;
	}
	
	//Methods
	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		if (data != null){
			//If data Packet is for Accelerometer
			if( data instanceof AccelerometerDataPacket){
				dataPacket_m = (AccelerometerDataPacket)data;
				switch(transformFunction_m){
				case  LINEAR_MODULO:
					f_MIDI_Vector_m = fn_Transform_Modulo_Vector(dataPacket_m.getAccelerometerVector(), dataPacket_m.getMaximumRange(), this.f_MIDIRange_m );
					setChanged();
					break;
				default:
						
					
				}
				if( f_MIDI_Vector_m != null ){
					if( hasChanged() )
						notifyObservers(f_MIDI_Vector_m);
				}
					
			}
			
		}
		
	}
	//Transforms filtered single value to MIDI value by a Modulo function
	public static float fn_Transform_Modulo_Value(float value, float f_SensorRange_f, float f_MIDIRange_f){
		float f_Slope_f = f_MIDIRange_f/(f_SensorRange_f/2.0f);
		float f_MIDIValue_f = f_Slope_f*Math.abs(value);
		
		return TransformSensorToMidi.fn_CheckMIDI(f_MIDIValue_f);		
	}
	//Transforms filtered vector to MIDI value by a Modulo function
	public static float[] fn_Transform_Modulo_Vector(float[] values, float f_SensorRange_f, float f_MIDIRange_f){
		float[] returnVector_f;
		if (values != null){
			returnVector_f = new float[3];
			for(byte b_VectorSize = 0; b_VectorSize < values.length; b_VectorSize++ ){
				returnVector_f[b_VectorSize] = fn_Transform_Modulo_Value(values[b_VectorSize], f_SensorRange_f, f_MIDIRange_f );
			}
			return returnVector_f;
		}else
			return null;
	}
	
	public static float fn_CheckMIDI(float value){
		if ( value > 127.0f )
			value = 127.0f;//Maximum MIDI signal is 127 on the upper end
		if ( value < 0)
			value = 0.0f;//Minimum MIDI signal is 0 on the lower end
		return value;
	}
	
	public String vectorToString(float[] vector){
		
		String outputValue_f;
		if ( vector == null )
			return null;
		StringBuilder sb_ReturnString_f;
		String pattern_f = "###.000";
		DecimalFormat floatFormatter_f = new DecimalFormat(pattern_f);
		sb_ReturnString_f = new StringBuilder();
		byte count_f = 0;
		for ( float value: vector){
			count_f++;
			sb_ReturnString_f.append(floatFormatter_f.format(value));
			if ( count_f != vector.length )
				sb_ReturnString_f.append(",");			
			
		}
		return sb_ReturnString_f.toString();
	}
	

}

enum TRANSFORM_FUNCTION{
	LINEAR_MODULO;
}
