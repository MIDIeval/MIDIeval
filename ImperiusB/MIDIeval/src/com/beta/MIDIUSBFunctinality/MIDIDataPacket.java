package com.beta.MIDIUSBFunctinality;

/*Author: Hrishik Mishra
 *Class: MIDI data packet
 *Functionality: Defines attributes of the 32-bit communication with the USB driver
 */
public class MIDIDataPacket {
	
	//Member variable declaration
	private int codeIndexNumber_m;
	private int cable_m;
	private int midiByte1_m;
	private int midiByte2_m;
	private int midiByte3_m;
	private MIDIEventType midiEventType_m;
	
	
	//Properties of member variables
	public int getCodeIndexNumber() {
		return codeIndexNumber_m;
	}

	public void setCodeIndexNumber(int codeIndexNumber_m) {
		this.codeIndexNumber_m = codeIndexNumber_m;
	}

	public int getCable() {
		return cable_m;
	}

	public void setCable(int cable_m) {
		this.cable_m = cable_m;
	}

	public int getMidiByte1() {
		return midiByte1_m;
	}

	public void setMidiByte1(int midiByte1_m) {
		this.midiByte1_m = midiByte1_m;
	}

	public int getMidiByte2() {
		return midiByte2_m;
	}

	public void setMidiByte2(int midiByte2_m) {
		this.midiByte2_m = midiByte2_m;
	}

	protected int getMidiByte3() {
		return midiByte3_m;
	}

	protected void setMidiByte3(int midiByte3_m) {
		this.midiByte3_m = midiByte3_m;
	}

	public MIDIEventType getMidiEventType() {
		return midiEventType_m;
	}

	public void setMidiEventType(MIDIEventType midiEventType_m) {
		this.midiEventType_m = midiEventType_m;
	}
	

}

/*Author: Hrishik Mishra
 *Enum: MIDI event type based on the CIN( Code index number )
 *Functionality: Type of MIDI event based on the CIN
 */
enum MIDIEventType{
	MISC_FUNC_CODES, 
	CABLE_EVENTS,
	SYSTEM_COMMON_MESSAGE,
	SYSTEM_EXCLUSIVE,
	NOTE_OFF,
	NOTE_ON,
	POLYPHONIC_AFTERTOUCH,	
	CONTROL_CHANGE,
	PROGRAM_CHANGE,
	CHANNEL_PRESSURE,
	PITCHBEND_CHANGE,
	SINGLE_BYTE
}
