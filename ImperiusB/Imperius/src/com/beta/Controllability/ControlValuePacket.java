package com.beta.Controllability;

public class ControlValuePacket {
	private IController controllerObj_m;
	private float[] f_ValueVector_m;
	
	public ControlValuePacket(float[] valueVector){
		this.f_ValueVector_m = valueVector;
	}
	
	
	/**
	 * @return the controllerObj_m
	 */
	public IController getController() {
		return controllerObj_m;
	}

	/**
	 * @param controllerObj_m the controllerObj_m to set
	 */
	public void setController(IController controllerObj_m) {
		this.controllerObj_m = controllerObj_m;
	}

	/**
	 * @return the f_ValueVector_m
	 */
	public float[] getValueVector() {
		return f_ValueVector_m;
	}

	/**
	 * @param f_ValueVector_m the f_ValueVector_m to set
	 */
	public void setValueVector(float[] f_ValueVector_m) {
		this.f_ValueVector_m = f_ValueVector_m;
	}

}
