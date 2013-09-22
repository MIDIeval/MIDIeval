package com.beta.UIControls;

import java.util.HashMap;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.beta.Controllability.ControlValuePacket;
import com.beta.Controllability.ControllerMode;
import com.beta.Controllability.ControllerType;
import com.beta.Controllability.IController;

/*Class : Abstract class for UI Controller class controller
 *Function: Store the relevant details of class of controller
 *Author: Hrishik Mishra 
 */
public abstract class UIController extends View implements IController {
	protected ControllerType e_ControllerType_m;
	protected UIControllerType e_UIControllerType_m;
	private HashMap<Integer, ControllerMode> subControllerMapObj_m;
	protected ControlValuePacket controlValuePacketObj_m;
	public UIController(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.e_ControllerType_m = ControllerType.USER_INTERFACE;
		// TODO Auto-generated constructor stub
	}
	/**
	 * @return the subControllerMapObj_m
	 */
	public HashMap<Integer, ControllerMode> getSubControllerMap() {
		return subControllerMapObj_m;
	}
	/**
	 * @param subControllerMapObj_m the subControllerMapObj_m to set
	 */
	public void setSubControllerMap(HashMap<Integer, ControllerMode> subControllerMapObj_m) {
		this.subControllerMapObj_m = subControllerMapObj_m;
	}

	

}
