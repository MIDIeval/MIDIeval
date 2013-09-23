package com.beta.UIControls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.beta.activities.R;

public class XYViewGroup extends ViewGroup {
	LayoutInflater layoutInflaterObj_m;
	public XYViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.layoutInflaterObj_m = LayoutInflater.from(context);
		// TODO Auto-generated constructor stub
		this.fn_InitViewGroup(context, this.layoutInflaterObj_m);//Function call to manually inflate the layout XML
	}

	@Override
	protected void onLayout(boolean isChanged, int left, int top, int right, int bottom) {
		// TODO Auto-generated method stub
		for ( short s_Count = 0; s_Count < this.getChildCount(); s_Count++) {
			this.getChildAt(s_Count).layout(left, top, right, bottom);
		}

	}
	
	
	public void fn_InitViewGroup(Context context, LayoutInflater layoutInflater){
		layoutInflater.inflate(R.layout.xy_viewgroup, this);
	}

}
