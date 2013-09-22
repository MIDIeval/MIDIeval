package com.beta.xmlUtility;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.util.Log;

import com.beta.activities.R;


public class Parser 
{
	public static ArrayList<Controllable> parseValues(Controllable object, String tagName, int noOfAttributes, Context context)
	{
		XmlResourceParser parser = context.getResources().getXml(R.xml.maudio_venom);
		ArrayList<Controllable> f_controllables_list= new ArrayList<Controllable>();;
		ArrayList<Integer> f_attributes_list= null;
		String f_controllabeName_s = null;
		int y=0;
		try {
				int eventType = parser.getEventType();
				boolean done = false;
				while (eventType != XmlPullParser.END_DOCUMENT && !done) 
				{
					String name = "";
					switch (eventType) 
					{
					case XmlPullParser.START_DOCUMENT:
						break;
					
					case XmlPullParser.START_TAG:
						name = parser.getName();
						if (name.equalsIgnoreCase(tagName))
						{
							f_attributes_list = new ArrayList<Integer>();
							int i=0;
							
								for (i = 0; i < noOfAttributes; i++) {
									try{ 
									f_attributes_list.add(Integer.parseInt(parser.getAttributeValue(i)));
								}catch(NumberFormatException nfe)
								{
									Log.d("Attribute value", "null");	
								}
							}
							f_controllabeName_s = parser.nextText();
						}
						if(!(parser.getEventType()==3))
						{break;}
					case XmlPullParser.END_TAG:
						name = parser.getName();
						if(name.equalsIgnoreCase(tagName))
						{
							object = new Controllable(f_controllabeName_s, f_attributes_list);
							f_controllables_list.add(y++,object);
						}
						break;
					}
					System.out.println(eventType);
					System.out.println(name);
					eventType = parser.next();
				}
				
			
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e);
		}
		return f_controllables_list;
	}
}
