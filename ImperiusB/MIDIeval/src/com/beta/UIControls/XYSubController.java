package com.beta.UIControls;

public enum XYSubController {
	X_RANGE_CHANGE(1), Y_RANGE_CHANGE(2), DOUBLE_TAP(3), SINGLE_TAP(4), FLING(5), ACTION_UP(6);
	
	private int subControllerID;
	XYSubController(int subControllerID){
		this.subControllerID = subControllerID;
	}
	public int getValue(){
		return this.subControllerID;
	}

}
