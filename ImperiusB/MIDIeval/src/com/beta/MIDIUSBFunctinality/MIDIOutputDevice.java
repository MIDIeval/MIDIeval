package com.beta.MIDIUSBFunctinality;

import java.util.Arrays;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import com.beta.util.Constants;

/*Author: Hrishik Mishra
 *Class: MIDI OutputDevice
 *Functionality: Defines attributes of the MIDI output device to send MIDI Out messages from host 
 */

//<variable>_m is a member variable
//<variable>_f is a functional local variable
public final class MIDIOutputDevice
{	
	//Member variable declaration
	private MIDIDataPacket midiDataPacket_m;	
	private UsbCommunication usbCommObj_m;
	
	//Properties of member variables
	public MIDIDataPacket getMidiDataPacket() {
		return midiDataPacket_m;
	}

	public void setMidiDataPacket(MIDIDataPacket midiDataPacket_m) {
		this.midiDataPacket_m = midiDataPacket_m;
	}

	public UsbCommunication getUsbCommObj() {
		return usbCommObj_m;
	}

	public void setUsbCommObj(UsbCommunication usbCommObj_m) {
		this.usbCommObj_m = usbCommObj_m;
	}
	
	/*Parameterized Constructor
	 * @Param1: MIDIDataPacket containing the MIDI information in one object
	 * @Param2: UsbCommunication object to communicate with the device*/
	public MIDIOutputDevice(MIDIDataPacket midiDataPacket, UsbCommunication usbCommunicationObj){
		this.usbCommObj_m = usbCommunicationObj;
		this.midiDataPacket_m = midiDataPacket;
	}
	public MIDIOutputDevice(UsbCommunication usbCommunicationObj){
		this.usbCommObj_m = usbCommunicationObj;
	}
	
	public MIDIOutputDevice(UsbDevice usbDevice, UsbDeviceConnection usbDeviceConnection, UsbEndpoint usbEndpoint, UsbInterface usbInterface){
		this(new UsbCommunication(usbDevice, usbDeviceConnection, usbEndpoint, usbInterface));
	}
	
	public void stop(){
		this.usbCommObj_m.getUsbDeviceConnection().releaseInterface(this.usbCommObj_m.usbInterface_m);
	}
	//Method definitions
	
	/*Function that sends the MIDI message by bulk transfer to the peripheral USB device
	 *@Param 1: code index number: classification of bytes in MIDI byte_x fields
	 *@Param 2: cable number: the virtual MIDI jack associated with the transfer of MIDI data
	 *@Param 3, 4, 5: MIDI data to be transferred*/	
	private void fn_SendMIDIMessage(int codeIndexNumber, int cable, int midiByte1, int midiByte2, int midiByte3){
		
		byte[] writeBuffer_f = new byte[4];
		writeBuffer_f[0] = (byte)(((cable & 0xf) << 4) | (codeIndexNumber & 0xf));
		writeBuffer_f[1] = (byte)midiByte1;
		writeBuffer_f[2] = (byte)midiByte2;
		writeBuffer_f[3] = (byte)midiByte3;
		
		usbCommObj_m.fn_WriteUSBMessage(writeBuffer_f);//Check the boolean return here.
		
		Log.d(Constants.TAG, "MIDI Out message: +" + Arrays.toString(writeBuffer_f));		
	}
	
	/*Function that sends the miscellaneous MIDI message by bulk transfer to the peripheral USB device(future extentions)
	 *@Param 1: cable number: the virtual MIDI jack associated with the transfer of MIDI data
	 *@Param 2, 3, 4: MIDI data to be transferred*/	
	public void fn_sendMIDIMiscFunctionCodes(int cable, int midiByte1, int midiByte2, int midiByte3)
	{
		this.fn_SendMIDIMessage(0x00, cable, midiByte1, midiByte2, midiByte3);
	}
	
	/*Function that sends the cable MIDI message by bulk transfer to the peripheral USB device
	 *@Param 1: cable number: the virtual MIDI jack associated with the transfer of MIDI data
	 *@Param 2, 3, 4: MIDI data to be transferred*/	
	public void fn_SendMIDICableEvents(int cable, int midiByte1, int midiByte2, int midiByte3){
		this.fn_SendMIDIMessage(0x1, cable, midiByte1, midiByte2, midiByte3);
	}
	
	/*Function that sends the 2-byte common MIDI message(eg. MTC, Song select) by bulk transfer to the peripheral USB device
	 *@Param 1: cable number: the virtual MIDI jack associated with the transfer of MIDI data
	 *@Param 2: MIDI data to be transferred*/	
	public void fn_SendMIDICommonMessage(int cable, int[] midiBytes){
		
		if( midiBytes == null )
			return;
		switch(midiBytes.length){
		case 1:
			this.fn_SendMIDIMessage(0x5, cable, midiBytes[0] & 0xff, 0, 0);
			break;
		case 2:
			this.fn_SendMIDIMessage(0x2, cable, midiBytes[0] & 0xff, midiBytes[1] & 0xff, 0);
			break;
		case 3:
			this.fn_SendMIDIMessage(0x3, cable, midiBytes[0] & 0xff, midiBytes[1] & 0xff, midiBytes[2] & 0xff);
			break;
		default:
			break;//Do nothing
				
		}
	}	
	
	/*Function that sends the start/continue System Exclusive message by bulk transfer to the peripheral USB device
	 *@Param 1: cable number: the virtual MIDI jack associated with the transfer of MIDI data
	 *@Param 2, 3, 4: System Exclusive data to be transferred*/	
	public void fn_SendSysExMessage(int cable, byte[] sysExBytes){
		if( sysExBytes == null )
			return;
		//To be implemented after studying SysEx format of data transfer
	}
	
	/*Function that sends the Note-off MIDI message by bulk transfer to the peripheral USB device
	 *@Param 1: cable number: the virtual MIDI jack associated with the transfer of MIDI data ( 0-15)
	 *@Param 2: channel number to edit ( 0-15 )
	 *@Param 3: note number to trigger ( 0-127 )
	 *@Param 4: velocity hit of the note ( 0-127 )*/	
	public void fn_NoteOffMessage(int cable, int channel, int note, int velocity){
		this.fn_SendMIDIMessage(0x8, cable, 0x80 | (channel & 0xf), note, velocity);
	}
	
	/*Function that sends the Note-on MIDI message by bulk transfer to the peripheral USB device
	 *@Param 1: cable number: the virtual MIDI jack associated with the transfer of MIDI data
	 *@Param 2: channel number to edit ( 0-15 )
	 *@Param 3: note number to trigger ( 0-15 )
	 *@Param 4: velocity hit of the note ( 0-127 )*/	
	public void fn_NoteOnMessage(int cable, int channel, int note, int velocity){
		this.fn_SendMIDIMessage(0x9, cable, 0x90 | (channel & 0xf), note, velocity);
	}
	
	/*Function that sends the Polyphonic aftertouch press MIDI message by bulk transfer to the peripheral USB device
	 *@Param 1: cable number: the virtual MIDI jack associated with the transfer of MIDI data
	 *@Param 2: channel number to edit ( 0-15 )
	 *@Param 3: note number to trigger ( 0-15 )
	 *@Param 4: Pressure aftertouch of the note ( 0-127 )*/	
	public void fn_PolyphonicAfterTouchMessage(int cable, int channel, int note, int pressure){
		this.fn_SendMIDIMessage(0xA, cable, 0xA0 | (channel & 0xf), note, pressure);
	}
	
	/*Function that sends the Control change MIDI message by bulk transfer to the peripheral USB device
	 *@Param 1: cable number: the virtual MIDI jack associated with the transfer of MIDI data
	 *@Param 2: channel number to edit ( 0-15 )
	 *@Param 3: note number to trigger ( 0-15 )
	 *@Param 4: Pressure value of the control change ( 0-127 )*/	
	public void fn_ControlChangeMessage(int cable, int channel, int function, int value){
		this.fn_SendMIDIMessage(0xB, cable, (0xB0 | (channel & 0xf)) & 0xFF, function, value);
	}
	
	/*Function that sends the Program Change MIDI message by bulk transfer to the peripheral USB device
	 *@Param 1: cable number: the virtual MIDI jack associated with the transfer of MIDI data( 0-15 )
	 *@Param 2: channel( 0-15 )
	 *@Param 3: program( 0-127 )*/		
	public void fn_ProgramChangeMessage(int cable, int channel, int program){
		this.fn_SendMIDIMessage(0xC, cable, 0xC0 | (channel & 0xf), program, 0);
	}
	
	/*Function that sends the Channel pressure MIDI message by bulk transfer to the peripheral USB device
	 *@Param 1: cable number: the virtual MIDI jack associated with the transfer of MIDI data
	 *@Param 2: channel( 0-15 )
	 *@Param 3: Aftertouch pressure ( 0-127 ) */	
	public void fn_ChannelPressureMessage(int cable, int channel, int pressure){
		this.fn_SendMIDIMessage(0xD, cable, 0xD0 | (channel & 0xf), pressure, 0);
	}
	
	/*Function that sends the Pitch bend change MIDI message by bulk transfer to the peripheral USB device
	 *@Param 1: cable number: the virtual MIDI jack associated with the transfer of MIDI data
	 *@Param 2: channel( 0-15 )
	 *@Param 3: Pitch bend amount(0(low)-8192(center)-16383(high)) 2 bytes*/	
	public void fn_PitchBendChangeValueMessage(int cable, int channel, int amount){
		this.fn_SendMIDIMessage(0xE, cable, 0xE0 | (channel & 0xf), amount & 0xff, (amount >> 0xff) & 0xff);
	}
	
	/*Function that sends the Pitch bend change MIDI message by bulk transfer to the peripheral USB device
	 *@Param 1: cable number: the virtual MIDI jack associated with the transfer of MIDI data
	 *@Param 2: Midi byte*/
	public void fn_SendMIDISingleByte(int cable, int byte1){
		this.fn_SendMIDIMessage(0xF, cable, byte1, 0, 0);
	}

	
	
	

}
