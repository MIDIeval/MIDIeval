package com.beta.UIControls;

import java.awt.font.NumericShaper;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import com.beta.activities.R;
import com.beta.xmlUtility.Mapper;

public class XYSettings extends FrameLayout{

	private class PickerFormatter implements NumberPicker.Formatter{
		private HashMap<Integer, Integer> pickerMap_m;

		@Override
		public String format(int value) {
			// TODO Auto-generated method stub
			
			return this.pickerMap_m.get(value).toString();
		}
		public PickerFormatter(HashMap<Integer, Integer> pickerMap){
			this.pickerMap_m = pickerMap;
		}
	}
	private LayoutInflater layoutInflaterObj_m;
	private Activity creatingActivity_m;
	private Context contextObj_m;	
	private HashMap<Integer, Integer> ccPickerList_m;//The map to get actual function value from picker value
	private String[] ccActualPickerList_m;
	private String[] ccDisplayPickerList_m;
	private String[] ccDisplayNameList_m;
	private PickerFormatter pickerFormatter_m;
	private XYCurrentSettingDetails xyCurrentSettingDetails_m;
	
	//UI Elements that need to be modified:
	private TextView textViewObj_m;
	private NumberPicker numberPickerRef_m;
	private Button buttonPlus_m;
	private Button buttonMinus_m;
	
	public XYSettings(Context context) {
		super(context);
		this.contextObj_m = context;
		
		// TODO Auto-generated constructor stub
	}
	public XYSettings(Context context, AttributeSet attribSet){
		super(context, attribSet);
		this.contextObj_m = context;
		ccPickerList_m = new HashMap<Integer, Integer>();
		ccActualPickerList_m = Mapper.getContinuosCCNumbers();
		ccDisplayNameList_m = Mapper.getContinuos();
		ccDisplayPickerList_m = new String[this.ccActualPickerList_m.length];
		
		//View container_f =  this.layoutInflaterObj_m.inflate(R.layout.xy_settings, null, true);		
	}
	public void onAttachedToWindow(){
		this.layoutInflaterObj_m = (LayoutInflater)this.contextObj_m.getSystemService(this.contextObj_m.LAYOUT_INFLATER_SERVICE);
		View container_f =  this.layoutInflaterObj_m.inflate(R.layout.xy_settings, this, true);
		//Fetch the child views for logical control of the Group View
		this.numberPickerRef_m = (NumberPicker)container_f.findViewById(R.id.numberPicker1);	
		this.textViewObj_m = (TextView)container_f.findViewById(R.id.text_View_01);
		this.buttonMinus_m = (Button)container_f.findViewById(R.id.toggle_Button_1);
		this.buttonPlus_m = (Button)container_f.findViewById(R.id.toggle_Button_2);
		
		for(int iCount = 0; iCount < ccActualPickerList_m.length; iCount++ ){
			this.ccPickerList_m.put(iCount, Integer.valueOf(this.ccActualPickerList_m[iCount]));
			this.ccDisplayPickerList_m[iCount] = String.valueOf(iCount);
		}
		pickerFormatter_m = new PickerFormatter(ccPickerList_m); 
		this.numberPickerRef_m.setMinValue(0);
		this.numberPickerRef_m.setMaxValue(this.ccDisplayPickerList_m.length - 1);
		this.numberPickerRef_m.setWrapSelectorWheel(false);
		this.numberPickerRef_m.setFormatter(pickerFormatter_m);
		this.numberPickerRef_m.setFocusable(false);
		this.numberPickerRef_m.setClickable(false);
		//To block the Soft keyboard from cropping up
		this.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		this.numberPickerRef_m.setOnValueChangedListener(new OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				// TODO Auto-generated method stub
				xyCurrentSettingDetails_m.setI_XFunctionValue(ccPickerList_m.get(newVal));
				textViewObj_m.setText(ccDisplayNameList_m[newVal]);
			}
		});
		this.buttonPlus_m.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int previousValue = numberPickerRef_m.getValue();
				if ( previousValue == ccPickerList_m.get(ccPickerList_m.size() - 1))
					return;
				numberPickerRef_m.setValue(previousValue + 1);
				xyCurrentSettingDetails_m.setI_XFunctionValue(ccPickerList_m.get(previousValue + 1));
				textViewObj_m.setText(ccDisplayNameList_m[previousValue + 1]);
			}
		});
		this.buttonMinus_m.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int previousValue = numberPickerRef_m.getValue();
				if ( previousValue - 1 < 0)
					return;
				numberPickerRef_m.setValue(previousValue - 1);
				xyCurrentSettingDetails_m.setI_XFunctionValue(ccPickerList_m.get(previousValue - 1));
				textViewObj_m.setText(ccDisplayNameList_m[previousValue - 1]);
				
			}
		});
		
		
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
	
}
