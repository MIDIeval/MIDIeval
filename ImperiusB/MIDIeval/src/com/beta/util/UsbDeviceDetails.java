package com.beta.util;


/*Class: UsbDeviceDetails
 *Function: Encapsulates the USB-MIDI device details 
 *Author: Hrishik Mishra
 */
public class UsbDeviceDetails {
	
	//Members
	private String s_VendorID_m;
	private String s_ProductID_m;
	private String s_Class_m;
	private String s_SubClass_m;
	private String s_Protocol_m;
	private String s_Manufacturer_m;
	private String s_Product_m;
	public String getVendorID() {
		
		return s_VendorID_m;
	}
	public void setVendorID(String s_VendorID_m) {
		this.s_VendorID_m = s_VendorID_m;
	}
	public String getProductID() {
		return s_ProductID_m;
	}
	public void setProductID(String s_ProductID_m) {
		this.s_ProductID_m = s_ProductID_m;
	}
	public String getDeviceClass() {
		return s_Class_m;
	}
	public void setDeviceClass(String s_Class_m) {
		this.s_Class_m = s_Class_m;
	}
	/**
	 * @return the s_SubClass_m
	 */
	public String getSubClass() {
		return s_SubClass_m;
	}
	/**
	 * @param s_SubClass_m the s_SubClass_m to set
	 */
	public void setSubClass(String s_SubClass_m) {
		this.s_SubClass_m = s_SubClass_m;
	}
	/**
	 * @return the s_Protocol_m
	 */
	public String getProtocol() {
		return s_Protocol_m;
	}
	/**
	 * @param s_Protocol_m the s_Protocol_m to set
	 */
	public void setProtocol(String s_Protocol_m) {
		this.s_Protocol_m = s_Protocol_m;
	}
	/**
	 * @return the s_Manufacturer_m
	 */
	public String getManufacturer() {
		return s_Manufacturer_m;
	}
	/**
	 * @param s_Manufacturer_m the s_Manufacturer_m to set
	 */
	public void setManufacturer(String s_Manufacturer_m) {
		this.s_Manufacturer_m = s_Manufacturer_m;
	}
	/**
	 * @return the s_Product_m
	 */
	public String getProduct() {
		return s_Product_m;
	}
	/**
	 * @param s_Product_m the s_Product_m to set
	 */
	public void setProduct(String s_Product_m) {
		this.s_Product_m = s_Product_m;
	}
	
	

}
