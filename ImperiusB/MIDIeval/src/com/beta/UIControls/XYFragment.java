package com.beta.UIControls;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.beta.Controllability.IController;
import com.beta.UIControls.XYSettings.IXYSettingsValueChangedListener;
import com.beta.activities.R;
import com.beta.activities.SelectorDialog;
import com.beta.activities.SelectorDialog.ISelectorDialogListener;
import com.beta.imperius.AbstractSingleMIDIActivity;
import com.beta.xmlUtility.Mapper;

public class XYFragment extends Fragment implements ISelectorDialogListener, IXYSettingsValueChangedListener{
	private LayoutInflater layoutInflaterObj_m;
	private AbstractSingleMIDIActivity activityMainObj_m;
	private IController genericPointer_m;
	private XYController xyControllerObj_m;
	private Bundle bundleForDialogObj_m = new Bundle();
	private XYSubController e_XYSubController_m;
	private  CLinearSeekBar seekBarX_m;
	private  WindowedSeekBar seekBarY_m;
	private TextView text_X_m;
	private TextView text_Y_m;
	private TextView text_DoubleTap_m;
	private SeekBar seekBarFlingObj_m;
	private HashMap<Integer, Integer> subControllerMapObj_m = new HashMap<Integer, Integer>();
	private XYCurrentSettingDetails xyCurreSettingDetailsObj_m;
	private XYSettings xySettingFrameLayout_m;
	private ToggleButton toggleButtonX_m;
	private ToggleButton toggleButtonY_m;
	private ToggleButton toggleButtonDT_m;
	private ToggleButton toggleButtonST_m;
	private ToggleButton toggleButtonFling_m;
	
	public void onAttach(Activity activity){
		super.onAttach(activity);
		this.activityMainObj_m = (AbstractSingleMIDIActivity) activity;
		
	
	}

	public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceBundle){
		xyCurreSettingDetailsObj_m = new XYCurrentSettingDetails();		
		View containerView = layoutInflater.inflate(R.layout.xy_fragment, container, false);
		this.layoutInflaterObj_m = layoutInflater;
		//this.xySettingFrameLayout_m = (XYSettings)containerView.findViewById(R.id.xy_settings);
		//Set the current setting object to the Frame Layout XY setting object
		//this.xySettingFrameLayout_m.setXYCurrentSettingDetails(xyCurreSettingDetailsObj_m);
		
		this.xyControllerObj_m = (XYController)containerView.findViewById(R.id.xy_controller);
		//Set the current setting object to the XY controller too
		this.xyControllerObj_m.setXYCurrentSettingDetails(xyCurreSettingDetailsObj_m);
		for ( Integer keyValue: this.xyControllerObj_m.getSubControllerMap().keySet()){
			subControllerMapObj_m.put(keyValue, Integer.valueOf(-1));
		}
		this.activityMainObj_m.getMapper().getControllerMapObj().put(xyControllerObj_m, subControllerMapObj_m);
		this.genericPointer_m = this.xyControllerObj_m;
//		this.switchX = (ToggleButton)containerView.findViewById(R.id.switch_01);
//		this.switchY = (Switch)containerView.findViewById(R.id.switch_02);
//		this.switchDoubleTap = (Switch)containerView.findViewById(R.id.switch_03);
//		this.switchFling = (Switch)containerView.findViewById(R.id.switch_04);
//		this.seekBarX_m = (CLinearSeekBar)containerView.findViewById(R.id.range_seek);
//		this.seekBarY_m = (WindowedSeekBar)containerView.findViewById(R.id.windowedseekbar_02);
//		this.seekBarFlingObj_m = (SeekBar)containerView.findViewById(R.id.seekBar_01);
//		text_X_m = (TextView)containerView.findViewById(R.id.text_01);
//		text_Y_m = (TextView)containerView.findViewById(R.id.text_02);
//		text_DoubleTap_m = (TextView)containerView.findViewById(R.id.text_03);
//		this.fn_RegisterSwitchListeners();
//		this.fn_RegisterSeekBarListeners();		
		this.toggleButtonX_m = (ToggleButton)containerView.findViewById(R.id.button_X);
		this.toggleButtonX_m.setChecked(true);
		this.toggleButtonY_m = (ToggleButton)containerView.findViewById(R.id.button_Y);
		this.toggleButtonY_m.setChecked(true);
		this.toggleButtonDT_m = (ToggleButton)containerView.findViewById(R.id.button_DT);
		this.toggleButtonST_m = (ToggleButton)containerView.findViewById(R.id.button_ST);
		this.toggleButtonFling_m = (ToggleButton)containerView.findViewById(R.id.button_fl);
		this.fn_RegisterSwitchListeners();
		return containerView;
	}
	
//	public void fn_RegisterSeekBarListeners(){
//		if ( this.seekBarX_m != null ){
//			this.seekBarX_m.setSeekBarChangeListener(new com.beta.UIControls.WindowedSeekBar.SeekBarChangeListener() {
//				
//				@Override
//				public void SeekBarValueChanged(int Thumb1Value, int thumblX,
//						int Thumb2Value, int thumbrX, int width, int thumbY) {
//					// TODO Auto-generated method stub
//					xyControllerObj_m.setXRangeVector(new int[]{Thumb1Value, Thumb2Value});
//				}
//			});
//		}
//		if ( this.seekBarY_m != null ){
//			this.seekBarY_m.setSeekBarChangeListener(new com.beta.UIControls.WindowedSeekBar.SeekBarChangeListener() {
//				
//				@Override
//				public void SeekBarValueChanged(int Thumb1Value, int thumblX,
//						int Thumb2Value, int thumbrX, int width, int thumbY) {
//					// TODO Auto-generated method stub
//					xyControllerObj_m.setYRangeVector(new int[]{Thumb1Value, Thumb2Value});
//					
//				}
//			});
//		}
//		if ( this.seekBarFlingObj_m != null ){
//			this.seekBarFlingObj_m.setMax(2);
//			this.seekBarFlingObj_m.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
//				 
//				@Override
//				public void onStopTrackingTouch(SeekBar seekBar) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void onStartTrackingTouch(SeekBar seekBar) {
//					// TODO Auto-generated method stub
//					
//				}
//				
//				@Override
//				public void onProgressChanged(SeekBar seekBar, int progress,
//						boolean fromUser) {
//					// TODO Auto-generated method stub
//					xyControllerObj_m.i_FlingSpeed_m = 100*progress;
//					
//				}
//			});
//		}
//	}
	public void fn_RegisterSwitchListeners(){
		if ( this.toggleButtonX_m != null ){
			this.toggleButtonX_m.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					xyCurreSettingDetailsObj_m.b_IsXOn_m = isChecked;
				}
			});
		}
		if ( this.toggleButtonY_m != null ){
			this.toggleButtonY_m.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					xyCurreSettingDetailsObj_m.b_IsYOn_m = isChecked;
				}
			});
		}
		if ( this.toggleButtonDT_m != null ){
			this.toggleButtonDT_m.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					xyCurreSettingDetailsObj_m.b_IsDTOn_m = isChecked;
				}
			});
		}
		if ( this.toggleButtonST_m != null ){
			this.toggleButtonST_m.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					xyCurreSettingDetailsObj_m.b_IsSTOn_m = isChecked;
				}
			});
		}
		if ( this.toggleButtonFling_m != null ){
			this.toggleButtonFling_m.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					xyCurreSettingDetailsObj_m.b_IsFlingOn_m = isChecked;
				}
			});
		}
	}
	
	@Override
	public void onSelectionMade(String selectedObject) {
		// TODO Auto-generated method stub
		//This is where the User Mapping needs to happen
		
		int functionValue = Mapper.getContinuosFunctionValue(selectedObject); //Fetch the functionValue from the XML
		switch ( e_XYSubController_m ){
		case X_RANGE_CHANGE:
			this.activityMainObj_m.getMapper().setSubControllerValue(genericPointer_m, this.e_XYSubController_m.getValue(), functionValue);
			this.activityMainObj_m.getMapper().setSubControllerValue(genericPointer_m, XYSubController.FLING_X.getValue(), functionValue);
			text_X_m.setText(selectedObject);
			break;
		case Y_RANGE_CHANGE:
			this.activityMainObj_m.getMapper().setSubControllerValue(genericPointer_m, this.e_XYSubController_m.getValue(), functionValue);
			this.activityMainObj_m.getMapper().setSubControllerValue(genericPointer_m, XYSubController.FLING_Y.getValue(), functionValue);
			text_Y_m.setText(selectedObject);
			break;
		case DOUBLE_TAP:
			text_DoubleTap_m.setText(selectedObject);
			this.activityMainObj_m.getMapper().setSubControllerValue(genericPointer_m, this.e_XYSubController_m.getValue(), functionValue);
			break;
		default:
			break;
		}
		
	}

	@Override
	public void fn_FunctionValueChanged(int functionValue, int tabSelected) {
		// TODO Auto-generated method stub
		int i_SelectedTab_f = tabSelected;
		switch ( i_SelectedTab_f ){
		case 0:
			this.xyCurreSettingDetailsObj_m.setI_XFunctionValue(functionValue);
			break;
		case 1:
			this.xyCurreSettingDetailsObj_m.setI_YFunctionValue(functionValue);
			break;
		case 2:
			this.xyCurreSettingDetailsObj_m.setI_DTFunctionValue(functionValue);
			break;
		case 3:
			this.xyCurreSettingDetailsObj_m.setI_STFunctionValue(functionValue);
			break;
		case 4:
			break;
		default:
			break;
				
		}
	}

	@Override
	public void fn_RangeChanged(int[] range, int tabSelected) {
		// TODO Auto-generated method stub
		int i_SelectedTab_f = tabSelected;
		switch ( i_SelectedTab_f){
		case 0:
			this.xyCurreSettingDetailsObj_m.setxRangeVector(range);
			break;
		case 1:
			this.xyCurreSettingDetailsObj_m.setyRangeVector(range);
			break;
		case 2:
			this.xyCurreSettingDetailsObj_m.setSingleTapThreshold(range[0]);
		case 3:
			this.xyCurreSettingDetailsObj_m.setDoubleTapThreshold(range[0]);
			break;
		case 4:
			this.xyCurreSettingDetailsObj_m.setI_FlingSpeed((range[0]) + 10);
		default:
			break;
		}
	}

	@Override
	public void fn_channelDetailsChanged(int[] channelVector, int tabSelected) {
		// TODO Auto-generated method stub
		int i_SelectedTab_f = tabSelected;
		switch ( i_SelectedTab_f ){
		case 0:
			this.xyCurreSettingDetailsObj_m.setXChannelsVector(channelVector);
			break;
		case 1:
			this.xyCurreSettingDetailsObj_m.setYChannelsVector(channelVector);
			break;
		case 2:
			break;
		case 3:
			break;
		case 4:
			break;
		default:
			break;
		}
	}
}
