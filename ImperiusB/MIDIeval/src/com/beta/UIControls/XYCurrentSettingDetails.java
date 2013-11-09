package com.beta.UIControls;

public class XYCurrentSettingDetails {
	private int[] xChannelsVector_m = new int[]{0};
	private int[] yChannelsVector_m = new int[]{0};
	private int i_XFunctionValue_m = 0;
	private int i_YFunctionValue_m = 0;
	private int i_DTFunctionValue_m = -1;
	private int i_STFunctionValue_m = -1;
	private int i_SingleTapThreshold_m = 64;
	private int i_DoubleTapThreshold_m = 64;
	
	public boolean b_IsXOn_m = true;
	public boolean b_IsYOn_m = true;
	public boolean b_IsDTOn_m = false;
	public boolean b_IsSTOn_m = false;
	public boolean b_IsFlingOn_m = false;
	private int[] xRangeVector_m = new int[]{25, 75};//XMin, XMax, YMin, YMax
	private int[] yRangeVector_m = new int[]{25, 75};
	private int i_FlingSpeed_m = 10;
	/**
	 * @return the channelsVector_m
	 */
	public int[] getXChannelsVector() {
		return xChannelsVector_m;
	}
	/**
	 * @param channelsVector_m the channelsVector_m to set
	 */
	public void setXChannelsVector(int[] channelsVector_m) {
		this.xChannelsVector_m = channelsVector_m;
	}
	/**
	 * @return the channelsVector_m
	 */
	public int[] getYChannelsVector() {
		return yChannelsVector_m;
	}
	/**
	 * @param channelsVector_m the channelsVector_m to set
	 */
	public void setYChannelsVector(int[] channelsVector_m) {
		this.yChannelsVector_m = channelsVector_m;
	}
	/**
	 * @return the i_XFunctionValue_m
	 */
	public int getI_XFunctionValue() {
		return i_XFunctionValue_m;
	}
	/**
	 * @param i_XFunctionValue_m the i_XFunctionValue_m to set
	 */
	public void setI_XFunctionValue(int i_XFunctionValue_m) {
		this.i_XFunctionValue_m = i_XFunctionValue_m;
	}
	/**
	 * @return the i_YFunctionValue_m
	 */
	public int getI_YFunctionValue() {
		return i_YFunctionValue_m;
	}
	/**
	 * @param i_YFunctionValue_m the i_YFunctionValue_m to set
	 */
	public void setI_YFunctionValue(int i_YFunctionValue_m) {
		this.i_YFunctionValue_m = i_YFunctionValue_m;
	}
	/**
	 * @return the i_DTFunctionValue_m
	 */
	public int getI_DTFunctionValue() {
		return i_DTFunctionValue_m;
	}
	/**
	 * @param i_DTFunctionValue_m the i_DTFunctionValue_m to set
	 */
	public void setI_DTFunctionValue(int i_DTFunctionValue_m) {
		this.i_DTFunctionValue_m = i_DTFunctionValue_m;
	}
	/**
	 * @return the i_STFunctionValue_m
	 */
	public int getI_STFunctionValue() {
		return i_STFunctionValue_m;
	}
	/**
	 * @param i_STFunctionValue_m the i_STFunctionValue_m to set
	 */
	public void setI_STFunctionValue(int i_STFunctionValue_m) {
		this.i_STFunctionValue_m = i_STFunctionValue_m;
	}
	/**
	 * @return the b_IsFlingOn_m
	 */
	public boolean isB_IsFlingOn() {
		return b_IsFlingOn_m;
	}
	/**
	 * @param b_IsFlingOn_m the b_IsFlingOn_m to set
	 */
	public void setB_IsFlingOn(boolean b_IsFlingOn_m) {
		this.b_IsFlingOn_m = b_IsFlingOn_m;
	}
	/**
	 * @return the xRangeVector_m
	 */
	public int[] getxRangeVector() {
		return xRangeVector_m;
	}
	/**
	 * @param xRangeVector_m the xRangeVector_m to set
	 */
	public void setxRangeVector(int[] xRangeVector_m) {
		this.xRangeVector_m = xRangeVector_m;
	}
	/**
	 * @return the yRangeVector_m
	 */
	public int[] getyRangeVector() {
		return yRangeVector_m;
	}
	/**
	 * @param yRangeVector_m the yRangeVector_m to set
	 */
	public void setyRangeVector(int[] yRangeVector_m) {
		this.yRangeVector_m = yRangeVector_m;
	}
	/**
	 * @return the i_FlingSpeed_m
	 */
	public int getI_FlingSpeed() {
		return i_FlingSpeed_m;
	}
	/**
	 * @param i_FlingSpeed_m the i_FlingSpeed_m to set
	 */
	public void setI_FlingSpeed(int i_FlingSpeed_m) {
		this.i_FlingSpeed_m = i_FlingSpeed_m;
	}
	public int getSingleTapThreshold() {
		return i_SingleTapThreshold_m;
	}
	public void setSingleTapThreshold(int i_SingleTapThreshold_m) {
		this.i_SingleTapThreshold_m = i_SingleTapThreshold_m;
	}
	public int getDoubleTapThreshold() {
		return i_DoubleTapThreshold_m;
	}
	public void setDoubleTapThreshold(int i_DoubleTapThreshold_m) {
		this.i_DoubleTapThreshold_m = i_DoubleTapThreshold_m;
	}
}
