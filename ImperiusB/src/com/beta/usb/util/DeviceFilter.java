package com.beta.usb.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import com.beta.imperius.R;

public final class DeviceFilter {
	
	private final int usbVendorID_m;
	private final int usbProductID_m;
	private final int usbClass_m;
	private final int usbSubClass_m;
	private final int usbProtocol_m;
	
	
	public DeviceFilter(final int usbVendorID, final int usbProductID, final int usbClass, final int usbSubClass, final int usbProtocol){
		this.usbVendorID_m = usbVendorID;
		this.usbProductID_m = usbProductID;
		this.usbClass_m = usbClass;
		this.usbSubClass_m = usbSubClass;
		this.usbProtocol_m = usbProtocol;
	}
	
	public static List<DeviceFilter> getDeviceFilters(Context context) throws IOException{
		XmlPullParser parser_f = context.getResources().getXml(R.xml.device_filter);
		List<DeviceFilter> deviceFiltersList_f = new ArrayList<DeviceFilter>();
		
		try{
			int hasNext_f = XmlPullParser.START_DOCUMENT;//Indicates that the parser hasn't read anything.
			while( hasNext_f != XmlPullParser.END_DOCUMENT ){
				hasNext_f = parser_f.next();				
				DeviceFilter deviceFilter_f = DeviceFilter.parseXML(parser_f);
				
				if(	deviceFilter_f != null )
					deviceFiltersList_f.add(deviceFilter_f);
			}
		}
		catch ( XmlPullParserException ex){
			Log.e(Constants.XML_PARSER_EXCEPTION_TAG, ex.getMessage());				
		}
		catch ( IOException ex ){
			Log.e(Constants.XML_IO_EXCEPTION_TAG, ex.getMessage());
		}
		return deviceFiltersList_f;
	}
	
	public static DeviceFilter parseXML(XmlPullParser parser){
		int vendorID_f = -1;
		int productID_f = -1;
		int deviceClass_f = -1;
		int deviceSubclass_f = -1;
		int deviceProtocol_f = -1;
		
		int count_f = parser.getAttributeCount();
		for ( int iCount = 0; iCount < count_f; iCount++ ){
			String name_f = parser.getAttributeName(iCount);
			int attributeValue_f = Integer.parseInt(parser.getAttributeValue(iCount));
			
			if ( name_f.equals(Constants.VENDOR_ID))
				vendorID_f = attributeValue_f;
			else if( name_f.equals(Constants.PRODUCT_ID))
				productID_f = attributeValue_f;
			else if( name_f.equals(Constants.DEVICE_CLASS))
				deviceClass_f = attributeValue_f;
			else if( name_f.equals(Constants.DEVICE_SUBCLASS))
				deviceSubclass_f = attributeValue_f;
			else if ( name_f.equals(Constants.DEVICE_PROTOCOL))
				deviceProtocol_f = attributeValue_f;
			
			if ( vendorID_f == -1 && productID_f == -1 && deviceClass_f == -1 && deviceSubclass_f == -1 && deviceProtocol_f == -1 )
				return null;			
		}
		return new DeviceFilter(vendorID_f, productID_f, deviceClass_f, deviceSubclass_f, deviceProtocol_f);
		
		
	}
	
	private boolean matches( int deviceClass, int deviceSubclass, int deviceProtocol ){
		return ( ( deviceClass == this.usbClass_m || this.usbClass_m == -1) && ( deviceSubclass == this.usbSubClass_m || this.usbSubClass_m == -1) && (  deviceProtocol == this.usbProtocol_m || this.usbProtocol_m == -1) );
				
	}
	
	public boolean matches( UsbDevice usbDevice ){
		if( this.usbVendorID_m != -1 && usbDevice.getDeviceId() != this.usbVendorID_m )
			return false;
		if( this.usbProductID_m != -1 && usbDevice.getProductId() != this.usbProductID_m )
			return false;
		if( this.matches(usbDevice.getDeviceClass(), usbDevice.getDeviceSubclass(), usbDevice.getDeviceProtocol()))
			return true;
		
		//If the preliminary match fails, check for all the interfaces in the UsbDevice instance
		int count_f = usbDevice.getInterfaceCount();
		for( int iCount = 0; iCount < count_f; iCount++ ){
			UsbInterface interface_f = usbDevice.getInterface(iCount);
			if ( this.matches(interface_f.getInterfaceClass(), interface_f.getInterfaceSubclass(), interface_f.getInterfaceProtocol()) )
					return true;
			
		}
		return false;
	}

}
