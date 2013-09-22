package com.beta.listener;

import com.beta.MIDIUSBFunctinality.MIDIInputDevice;

public interface OnMidiInputEventListener {
	
	/*Function to handle miscellaneous function codes
	 *@Param 1: event generator
	 *@Param 2: cable number/jack number
	 *@Param 3, 4, 5: byte_x of data to be handled*/	
	void onMIDIMiscellaneousFunctionCodes(final MIDIInputDevice sender, int cable, int byte1, int byte2, int byte3);
	
	
	/*Function to handle Cable events
	 *@Param 1: event generator
	 *@Param 2: cable number/jack number
	 *@Param 3, 4, 5: byte_x of data to be handled*/
	void onMIDICableEvents(final MIDIInputDevice sender, int cable, int byte1, int byte2, int byte3);
	
	/*Function to handle System Common message events
	 *@Param 1: event generator
	 *@Param 2: cable number/jack number
	 *@Param 3: byte array of data to be sent depending on CIN = [0x2, 0x3, 0x5]*/
	void onMIDICommonMessageEvents(final MIDIInputDevice sender, int cable, byte[] bytes);
	
	/*Function to handle System Exclusive message events
	 *@Param 1: event generator
	 *@Param 2: cable number/jack number
	 *@Param 3: system exclusive byte data depending on CIN = [0x4, 0x6, 0x7]*/
	void onSystemExclusiveMessageEvent(final MIDIInputDevice sender, int cable, byte[] sysExBytes);
	
	/*Function to handle Note of events on a note
	 *@Param 1: event generator
	 *@Param 2: cable number/jack number
	 *@Param 3: channel number of the MIDI signal(0-15)
	 *@Param 4: note on which the trigger has been made(0-127)
	 *@Param 5: velocity hit on the note(0-127)	 */
	void onNoteOffEvent(final MIDIInputDevice sender, int cable, int channel, int note, int velocity);
	
	/*Function to handle Note of events on a note
	 *@Param 1: event generator
	 *@Param 2: cable number/jack number
	 *@Param 3: channel number of the MIDI signal(0-15)
	 *@Param 4: note on which the trigger has been made(0-127)
	 *@Param 5: velocity hit on the note(0-127)	 */
	void onNoteOnEvent(final MIDIInputDevice sender, int cable, int channel, int note, int velocity);
	
	
	/*Function to respond to polyphonic aftertouch events
	 *@Param 1: event generator
	 *@Param 2: cable number/jack number
	 *@Param 3: channel number of MIDI signal(0-15)
	 *@Param 4: note on which the trigger has been made(0-127)
	 *@Param 5: pressure amount on the note(0-127)*/
	void onPolyphonicAftertouchEvent(final MIDIInputDevice sender, int cable, int channel, int note, int pressure);
	
	
	/*Function to handle control change events
	 *@Param 1: event generator
	 *@Param 2: cable number/jack number
	 *@Param 3: channel number of MIDi signal(0-15)
	 *@Param 4: function to change(0-127)
	 *@Param 5: value to change(0-127)*/
	void onControlChange(final MIDIInputDevice sender, int cable, int channel, int function, int value);
	
	/*Function to handle program change events
	 *@Param 1: event generator
	 *@Param 2: cable number/jack number
	 *@Param 3: channel number of MIDI signal(0-15)
	 *@Param 4: function to change(0-127)
	 *@Param 5: value to change(0-127)*/
	void onProgramChange(final MIDIInputDevice sender, int cable, int channel, int function, int value);
	
	/*Function to handl channel pressure events
	 *@Param 1: event generator
	 *@Param 2: cable number/jack number
	 *@Param 3: channel number of MIDI signal(0-15)*/
	void onChannelPressureChangeEvent(final MIDIInputDevice sender, int cable, int channel, int pressure);
	
	/*Function to respond to Pitch bend events
	 *@Param 1: event generator
	 *@Param 2: cable number/jack number
	 *@Param 3: channel number of MIDI signals(0-15)
	 *@Param 4: the amount by which the bend needs to take place 
	 *(0(low)-8192(center)-16383(high)) 2 bytes*/
	void onPitchBendChange(final MIDIInputDevice sender, int cable, int channel, int amount);
	
	/*Function to respond to single byte MIDI events
	 *@Param 1: event generator
	 *@Param 2: cable number/jack number
	 *@Param 3: byte received from the MIDI peripheral device*/
	void onSingleByteEvent(final MIDIInputDevice sender, int cable, byte byte_1);

}
