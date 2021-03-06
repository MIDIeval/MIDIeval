package com.beta.Controllability;

public class ControlValuePacket {
	private IController iControllerPointer_m;
	private ControllerType e_ControllerType_m;
	private int i_SubControllerID_m;
	private float f_ValueVector_m;
	private int i_FunctionValue_m;
	private int i_ChannelVector_m = 0;
	public ControlValuePacket(float valueVector){
		this.f_ValueVector_m = valueVector;
	}
	
	
	

	/**
	 * @return the f_ValueVector_m
	 */
	public float getValueVector() {
		return f_ValueVector_m;
	}

	/**
	 * @param f_ValueVector_m the f_ValueVector_m to set
	 */
	public void setValueVector(float f_ValueVector_m) {
		this.f_ValueVector_m = f_ValueVector_m;
	}




	/**
	 * @return the e_ControllerType_m
	 */
	public ControllerType getControllerType() {
		return e_ControllerType_m;
	}




	/**
	 * @param e_ControllerType_m the e_ControllerType_m to set
	 */
	public void setControllerType(ControllerType e_ControllerType_m) {
		this.e_ControllerType_m = e_ControllerType_m;
	}




	/**
	 * @return the i_SubControllerID_m
	 */
	public int getSubControllerID() {
		return i_SubControllerID_m;
	}




	/**
	 * @param i_SubControllerID_m the i_SubControllerID_m to set
	 */
	public void setSubControllerID(int i_SubControllerID_m) {
		this.i_SubControllerID_m = i_SubControllerID_m;
	}




	/**
	 * @return the iControllerPointer_m
	 */
	public IController getiControllerPointer() {
		return iControllerPointer_m;
	}




	/**
	 * @param iControllerPointer_m the iControllerPointer_m to set
	 */
	public void setiControllerPointer(IController iControllerPointer_m) {
		this.iControllerPointer_m = iControllerPointer_m;
	}




	/**
	 * @return the i_FunctionValue_m
	 */
	public int getI_FunctionValue() {
		return i_FunctionValue_m;
	}




	/**
	 * @param i_FunctionValue_m the i_FunctionValue_m to set
	 */
	public void setI_FunctionValue(int i_FunctionValue_m) {
		this.i_FunctionValue_m = i_FunctionValue_m;
	}




	public int getChannelVector() {
		return i_ChannelVector_m;
	}




	public void setChannelVector(int i_ChannelVector_m) {
		this.i_ChannelVector_m = i_ChannelVector_m;
	}

}
