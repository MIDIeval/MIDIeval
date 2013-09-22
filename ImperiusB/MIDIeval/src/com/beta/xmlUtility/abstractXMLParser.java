package com.beta.xmlUtility;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

/*Class: abstractXMLParser
 *Function: Abstract class to handle XML utility and expose basic methods
 *Author: Hrishik Mishra
 */
public abstract class abstractXMLParser {
	//Members
	private static XmlPullParser xmlPullParserObj_m;
	private static XmlPullParserFactory xmlPullParserFactory_m;
	
	
	//private Constructor to prevent other instance formations
	private abstractXMLParser(){
		
	}
	public static XmlPullParser InitXmlPullParserInstance(){
		if ( xmlPullParserObj_m == null ){
			try {
				xmlPullParserFactory_m = XmlPullParserFactory.newInstance();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("abstractXMLParser.InitXmlPullParserInstance", e.getMessage());
			}
			if ( xmlPullParserFactory_m == null )
				return null;
			xmlPullParserFactory_m.setNamespaceAware(true);
			try {
				xmlPullParserObj_m = xmlPullParserFactory_m.newPullParser();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("abstractXMLParser.InitXmlPullParserInstance", e.getMessage());
			}
			if ( xmlPullParserObj_m == null ){
				return null;
			}
			
		}
		return xmlPullParserObj_m; 
	}
	
	
	/**
	 * @return the xmlPullParserObj_m
	 */
	public XmlPullParser getXmlPullParserObj() {
		return xmlPullParserObj_m;
	}

	/**
	 * @param xmlPullParserObj_m the xmlPullParserObj_m to set
	 */
	public void setXmlPullParserObj(XmlPullParser xmlPullParserObj_m) {
		this.xmlPullParserObj_m = xmlPullParserObj_m;
	}
	

}
