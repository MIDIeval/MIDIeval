package com.beta.handler;

import java.io.ByteArrayOutputStream;

import android.os.Message;
import android.os.Handler.Callback;
import com.beta.MIDIUSBFunctinality.MIDIInputDevice;
import com.beta.listener.OnMidiInputEventListener;

/*Author: Hrishik Mishra
 *Class: Message callback after event trigger is received
 *Functionality: based on MIDI event type, the appropriate callback handler is invoked.
 */
//<variable>_m is a member variable
//<variable>_f is a functional local variable
public class MIDIMessageCallback implements Callback
{
	//Member variables
	
	/* Interface reference with declaration of all possible MIDI inputs to device*/	
	private final OnMidiInputEventListener midiEventListener_m;
	//The MIDI input device connected to the USB host
	private final MIDIInputDevice sender_m;
	//The System Exclusive implementation, to be done later
	private ByteArrayOutputStream systemExclusive_m = null;
	
	/*Parameterized constructor
	 *@Param 1: event sender
	 *@Param 2: MIDI listener object reference*/
	public MIDIMessageCallback(final MIDIInputDevice sender, final OnMidiInputEventListener midiEventListener){
		this.midiEventListener_m = midiEventListener;
		this.sender_m = sender;
	}
	
	
	/*Function to receive message from the message queue of the current thread
	 * (non-Javadoc)
	 * @see android.os.Handler.Callback#handleMessage(android.os.Message)
	 * @Param 1: message received from the messsage queue of current thread
	 * Return 1: true: carry on processing, false: block processing
	 */
	@Override
	public boolean handleMessage(final Message msg){
		if( this.midiEventListener_m == null )
			return false;
		byte[] readMIDIData_f = (byte[])msg.obj;
		int cable_f;
		int codeIndexNumber_f;
		int byte1_f;
		int byte2_f;
		int byte3_f;
		byte[] byteMessage_f;
		
		for( int iCount = 0; iCount < readMIDIData_f.length; iCount+=4 )
		{
			cable_f = (readMIDIData_f[iCount + 0] >> 4) & 0xf;
			codeIndexNumber_f = readMIDIData_f[iCount + 0] & 0xf;
			
			byte1_f = readMIDIData_f[iCount + 1] & 0xff;
			byte2_f = readMIDIData_f[iCount + 2] & 0xff;
			byte3_f = readMIDIData_f[iCount + 3] & 0xff;
			
			switch( codeIndexNumber_f ){
			case 0x0:// MIDI miscellaneous message
				midiEventListener_m.onMIDIMiscellaneousFunctionCodes(sender_m, cable_f, byte1_f, byte2_f, byte3_f);
				break;
			case 0X1:// MIDI cable events
				this.midiEventListener_m.onMIDICableEvents(sender_m, cable_f, byte1_f, byte2_f, byte3_f);
				break;
			case 0x2: // 2-Byte System Common MIDI message
				byteMessage_f = new byte[]{(byte) byte1_f, (byte) byte2_f};
				this.midiEventListener_m.onMIDICommonMessageEvents(sender_m, cable_f,byteMessage_f);
				break;
			case 0x3: // 3-Byte System Common MIDI message
				byteMessage_f = new byte[]{(byte) byte1_f, (byte) byte2_f, (byte) byte3_f};
				this.midiEventListener_m.onMIDICommonMessageEvents(sender_m, cable_f, byteMessage_f);
				break;
			case 0x4://SysEx implementation, to be done later
				break;
			case 0X5://Check for SysEx implementation here also
				byteMessage_f = new byte[]{(byte) byte1_f, (byte) byte2_f, (byte) byte3_f};
				this.midiEventListener_m.onMIDICommonMessageEvents(sender_m, cable_f, byteMessage_f);
				break;
			case 0x6://SysEx implementation, to be done later
				break;
			case 0x7://SysEx implementation, to be done later
				break;
			case 0x8:// Note off event
				this.midiEventListener_m.onNoteOffEvent(sender_m, cable_f, byte1_f & 0xf, byte2_f & 0xff, byte3_f & 0xff);
				break;
			case 0x9:// Note On event
				this.midiEventListener_m.onNoteOnEvent(sender_m, cable_f, byte1_f & 0xf, byte2_f & 0xff, byte3_f & 0xff);
				break;
			case 0xA:// Polyphonic aftertouch
				this.midiEventListener_m.onPolyphonicAftertouchEvent(sender_m, cable_f, byte1_f & 0xf, byte2_f & 0xff, byte3_f & 0xff);
				break;
			case 0xB:// Control change event
				this.midiEventListener_m.onControlChange(sender_m, cable_f, byte1_f & 0xf, byte2_f & 0xff, byte3_f & 0xff);
				break;
			case 0xC:// Program change event
				this.midiEventListener_m.onProgramChange(sender_m, cable_f, byte1_f & 0xf, byte2_f & 0xff, byte3_f & 0xff);
				break;
			case 0xD://Channel pressure change
				this.midiEventListener_m.onChannelPressureChangeEvent(sender_m, cable_f, byte1_f & 0xf, byte2_f & 0xff);
				break;
			case 0xE:// PitchBend change event				
				this.midiEventListener_m.onPitchBendChange(sender_m, cable_f, byte1_f & 0xf, (byte2_f | byte3_f << 8));
				break;
			case 0xF:// Single byte event
				this.midiEventListener_m.onSingleByteEvent(sender_m, cable_f, (byte)byte1_f);
				break;
			default://Do nothing
				break;				
				
			}
			
			
		}
		return false;
	}
}
