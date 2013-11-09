package com.beta.UIControls;

import java.util.ArrayList;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beta.UIControls.CLinearSeekBar.OnSeekBarChangeListener;
import com.beta.UIControls.CNumberPicker.IOnValueSelectionChanged;
import com.beta.activities.R;
import com.beta.imperius.AbstractSingleMIDIActivity;
import com.beta.xmlUtility.Mapper;

public class XYSettings extends Fragment{
	
	public interface IXYSettingsValueChangedListener{
		/*subID: value of the view in the settings
		 *0: Number Picker, change function value
		 *1: Progress bar, change range settings
		 *2: Check box, change the channel settings
		 */
		void fn_FunctionValueChanged(int functionValue, int tabSelected);
		void fn_RangeChanged(int[] range, int tabSelected);
		void fn_channelDetailsChanged(int[] channelVector, int tabSelected);
	}
	private IXYSettingsValueChangedListener xySettingsValueListener_m;
	private LayoutInflater layoutInflaterObj_m;
	private Activity creatingActivity_m;
	private Context contextObj_m;	
	private TreeMap<Integer, String> ccPickerList_m;//The map to get actual function value from picker value
	private String[] ccActualPickerList_m;
	private String[] ccDisplayPickerList_m;
	private String[] ccDisplayNameList_m;
	
	private final int  i_XY_LAYOUT_ID_m = 32000;
	
	private AbstractSingleMIDIActivity abstractMIDIActivity_m;
	private CNumberPicker numberPickerFunctionSelector_m;
	private CLinearSeekBar linearSeekBarForRange_m;
	private CheckBox[] checkBoxVectorForChannels_m = new CheckBox[4];
	private LinearLayout checkBoxLinearLayout_m;
	private FrameLayout frameLayoutContent_m;
	private XYSettings xySettingFrameLayout_m;
	int i_FragmentID_m = -1; 
	int i_LayoutID_m = -1;
	private boolean[] b_ChannelVector_m = new boolean[]{false, false, false, false};
	private View containerViewObj_m;
	
	//MIDI information from the fragment
	private int[] i_RangeOfControl_m;
	private int i_FunctionValue_m;
	private XYCurrentSettingDetails xyCurrentSettingDetails_m;
	
	//UI Elements that need to be modified:
	private TextView textViewObj_m;

	
	public void  fn_InitializeSettingFragment(){
		ccPickerList_m = new TreeMap<Integer, String>();
		if ( this.i_LayoutID_m == 0){
			ccActualPickerList_m = Mapper.getContinuosCCNumbers();
			ccDisplayNameList_m = Mapper.getContinuos();
			ccDisplayPickerList_m = new String[this.ccActualPickerList_m.length];
		}
		if ( this.i_LayoutID_m == 1){
			ccActualPickerList_m = Mapper.getDigitalDCNumbers();
			ccDisplayNameList_m = Mapper.getDigital();
			ccDisplayPickerList_m = new String[this.ccActualPickerList_m.length];
		}
		
		
	}
	public XYSettings(){
		this(0);//Initialize with the first fragmentID.
	}
	public XYSettings(int fragmentID){
		super();
		
		this.i_FragmentID_m = fragmentID;//To initialize the fragment number to dynamically propulate the view
		switch( this.i_FragmentID_m){
		case 0:
			this.i_LayoutID_m = 0;
			break;
		case 1:
			this.i_LayoutID_m = 0;
			break;
		case 2:
			this.i_LayoutID_m = 1;
			break;
		case 3:
			this.i_LayoutID_m = 1;
			break;
		case 4:
			this.i_LayoutID_m = 1; 
			break;
			default:
				this.i_LayoutID_m = -1;
				break;// - 1 is invalid layout ID, we need to check before we inflate
		}
		//this.frameLayoutContent_m.setId(this.i_XY_LAYOUT_ID_m);
		this.fn_InitializeSettingFragment();
	}
	@Override
	public void onAttach(Activity activity){
		super.onAttach(activity);
		this.abstractMIDIActivity_m = (AbstractSingleMIDIActivity)activity;
		xySettingsValueListener_m = (IXYSettingsValueChangedListener)abstractMIDIActivity_m.getSupportFragmentManager().findFragmentById(R.id.xy_controller_fragment);
	}	
	
	@Override
	public View onCreateView(LayoutInflater layoutInflater, ViewGroup containerView, Bundle restorePreviousInstance){
		this.containerViewObj_m = containerView;
		int i_LayoutToBeInflatedID_f = -1;
		if ( this.i_LayoutID_m == 0){
			i_LayoutToBeInflatedID_f = (R.layout.xy_settings_cc);
		}
		if ( this.i_LayoutID_m == 1){
			i_LayoutToBeInflatedID_f = (R.layout.xy_settings_d);
		}		
		if ( i_LayoutID_m == -1 )
			return null;
		View containerViewObj_f = layoutInflater.inflate(i_LayoutToBeInflatedID_f, null);
		//Fetch the child views for logical control of the Group View
		
		//Fetch the reference to the Function Value Number picker and initialize
		this.numberPickerFunctionSelector_m = (CNumberPicker)containerViewObj_f.findViewById(R.id.function_value_picker);			
		int i_Visibility_f = (this.i_FragmentID_m == 4)?View.INVISIBLE:View.VISIBLE;
		this.numberPickerFunctionSelector_m.setVisibility(i_Visibility_f);
			
		//Fetch the reference to the range Progress bar and initialize
		if ( this.i_LayoutID_m == 0 ){
			this.linearSeekBarForRange_m = (CLinearSeekBar)containerViewObj_f.findViewById(R.id.range_seek);
		}
		else if ( this.i_LayoutID_m == 1){
			this.linearSeekBarForRange_m = (CLinearSeekBar)containerViewObj_f.findViewById(R.id.range_seek_threshold);
		}
		if (this.i_FragmentID_m == 4){
			this.linearSeekBarForRange_m.setMax(100);
			this.linearSeekBarForRange_m.fn_SetProgress(new int[]{10, 0});
			
		}
		//Fetch the reference to the check box and it's linear layout and initialize
		this.checkBoxLinearLayout_m = (LinearLayout)containerViewObj_f.findViewById(R.id.checkBox_linearLayout);
		this.checkBoxVectorForChannels_m[0] = (CheckBox)containerViewObj_f.findViewById(R.id.checkBox_Channel_01);
		this.checkBoxVectorForChannels_m[0].setChecked(true);
		this.checkBoxVectorForChannels_m[0].setVisibility(i_Visibility_f);
		this.checkBoxVectorForChannels_m[1] = (CheckBox)containerViewObj_f.findViewById(R.id.checkBox_Channel_02);
		this.checkBoxVectorForChannels_m[1].setVisibility(i_Visibility_f);
		this.checkBoxVectorForChannels_m[2] = (CheckBox)containerViewObj_f.findViewById(R.id.checkBox_Channel_03);
		this.checkBoxVectorForChannels_m[2].setVisibility(i_Visibility_f);
		this.checkBoxVectorForChannels_m[3] = (CheckBox)containerViewObj_f.findViewById(R.id.checkBox_Channel_04);
		this.checkBoxVectorForChannels_m[3].setVisibility(i_Visibility_f);
		this.fn_RegisterListeners();
		//After registering listeners, we apply the default values to the current settings
		for(int iCount = 0; iCount < ccActualPickerList_m.length; iCount++ ){
			this.ccPickerList_m.put(Integer.parseInt(this.ccActualPickerList_m[iCount]), ccDisplayNameList_m[iCount]);
			this.ccDisplayPickerList_m[iCount] = String.valueOf(iCount);
		}		
		this.numberPickerFunctionSelector_m.setDictionaryListOfItems(this.ccPickerList_m);
		//To block the Soft keyboard from cropping up
		//this.frameLayoutContent_m.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);	
		this.numberPickerFunctionSelector_m.setFocusable(false);
		this.numberPickerFunctionSelector_m.setClickable(false);
				
		//Set default value for range of X and Y 
		if ( getXYSettingsValueListener() != null)
			getXYSettingsValueListener().fn_RangeChanged(this.linearSeekBarForRange_m.getProgress(), this.i_FragmentID_m);
		return containerViewObj_f;
	}
	
	public void fn_RegisterListeners(){
		
		//Register for the number picker
		if ( this.numberPickerFunctionSelector_m != null ){
			this.numberPickerFunctionSelector_m.setValueChangedListenerRef(new IOnValueSelectionChanged() {
				
				@Override
				public void onValueSelectionChanged(String newVal) {
					// TODO Auto-generated method stub
					//xyCurrentSettingDetails_m.setI_XFunctionValue((Integer.parseInt(ccPickerList_m.get(Integer.valueOf(newVal)))));
					//i_FunctionValue_m =(Integer.parseInt(ccPickerList_m.get(Integer.valueOf(newVal))));
					i_FunctionValue_m =Integer.valueOf(newVal);
					if ( getXYSettingsValueListener() != null ){
						getXYSettingsValueListener().fn_FunctionValueChanged(i_FunctionValue_m, i_FragmentID_m);
					}
					
				}
			});	
			
		}
		//Register for the range seek bar
		if ( this.linearSeekBarForRange_m != null ){
			this.linearSeekBarForRange_m.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				
				@Override
				public void onStopTrackingTouch(CLinearSeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onStartTrackingTouch(CLinearSeekBar seekBar) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onProgressChanged(CLinearSeekBar seekBar, int[] progress,
						boolean fromUser) {
					// TODO Auto-generated method stub
					i_RangeOfControl_m = progress;
					if ( getXYSettingsValueListener() != null)
						getXYSettingsValueListener().fn_RangeChanged(i_RangeOfControl_m, i_FragmentID_m);
				}
			});
		}
		if ( this.checkBoxVectorForChannels_m != null ){
			if ( this.checkBoxVectorForChannels_m[0] != null  &&
					this.checkBoxVectorForChannels_m[1] != null &&
					this.checkBoxVectorForChannels_m[2] != null &&
					this.checkBoxVectorForChannels_m[3] != null )
			{
				for ( int iCount = 0; iCount < 4; iCount++ ){
					this.checkBoxVectorForChannels_m[iCount].setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// TODO Auto-generated method stub
							ArrayList<Integer> channelVector_f = new ArrayList<Integer>();
							for(int iCount = 0; iCount < checkBoxVectorForChannels_m.length; iCount++){
								if ( checkBoxVectorForChannels_m[iCount].isChecked() ){
									b_ChannelVector_m[iCount] = true;
									channelVector_f.add(iCount);
								}
								else{
									b_ChannelVector_m[iCount] = false;
									if ( channelVector_f.contains(iCount))
										channelVector_f.remove(iCount);
								}							
							}
							int[] channelsToWrite_f = new int[channelVector_f.size()];
							for ( int iCount = 0; iCount < channelVector_f.size(); iCount++){
								channelsToWrite_f[iCount] = channelVector_f.get(iCount);
							}
							if ( getXYSettingsValueListener() != null )
								getXYSettingsValueListener().fn_channelDetailsChanged(channelsToWrite_f, i_FragmentID_m);
						}
					});
				}				
			}
		}
	}
	
	

	/**
	 * @return the xyCurrentSettingDetails_m
	 */
	public XYCurrentSettingDetails getXYCurrentSettingDetails() {
		return xyCurrentSettingDetails_m;
	}
	/**
	 * @param xyCurrentSettingDetails_m the xyCurrentSettingDetails_m to set
	 */
	public void setXYCurrentSettingDetails(XYCurrentSettingDetails xyCurrentSettingDetails_m) {
		this.xyCurrentSettingDetails_m = xyCurrentSettingDetails_m;
	}
	public IXYSettingsValueChangedListener getXYSettingsValueListener() {
		return xySettingsValueListener_m;
	}
	public void setXYSettingsValueListener(IXYSettingsValueChangedListener xySettingsValueListener_m) {
		this.xySettingsValueListener_m = xySettingsValueListener_m;
	}
	
}
