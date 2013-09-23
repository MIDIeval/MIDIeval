package com.beta.xmlUtility;
import java.util.ArrayList;

import android.content.Context;
import android.util.Log;

public class Mapper 
{
	private static Controllable object = null;
	private static Context context;
	private static ArrayList<Controllable> f_controllable_list;
	public static Context getContext() 
	{
		return context;
	}
	
	public Mapper()
	{
		
	}
	public static void setContext(Context context) {
		Mapper.context = context;
	}
	
	public static String[] getContinuos()
	{
		f_controllable_list = Parser.parseValues(object, "CTRL", 2, getContext());
		ArrayList<Controllable> f_continuos_list = new ArrayList<Controllable>();
		StringBuilder sbObj = new StringBuilder();
		for(Controllable controllableObj  : f_controllable_list)
		{
			
			for(int integerObj : ((Controllable) controllableObj).getF_attributes_list())
			{
				if(integerObj == 0)
				{
					f_continuos_list.add((Controllable)controllableObj);
					sbObj.append(controllableObj.getF_controllerName_s() + " ");
				}
			}  
		Log.d("Object value", controllableObj.toString());
		}
		return sbObj.toString().split(" ");
	}
	
	public static int getContinuosFunctionValue(String function)
	{
		for(Controllable controllableObj  : f_controllable_list)
		{
			  if(controllableObj.getF_controllerName_s().equals(function))
			  {
				  return controllableObj.fn_ReturnFunctionValue(function);
			  }
		Log.d("Object value", controllableObj.toString());
		}
		return -25;
	}
	
	public static ArrayList<Controllable> getDiscrete()
	{
		ArrayList<Controllable> f_controllable_list = Parser.parseValues(object, "CTRL", 2, getContext());
		ArrayList<Controllable> f_continuos_list = new ArrayList<Controllable>();
		for(Object controllableObj  : f_controllable_list)
		{
			for(Object integerObj : ((Controllable) controllableObj).getF_attributes_list())
			{
				if(integerObj.equals((Integer)2))
				{
					f_continuos_list.add((Controllable)controllableObj);
				}
			}  
		Log.d("Object value", controllableObj.toString());
		}
		return f_controllable_list;
	}
	
	public static ArrayList<Controllable> getDigital()
	{
		ArrayList<Controllable> f_controllable_list = Parser.parseValues(object, "CTRL", 2, getContext());
		ArrayList<Controllable> f_continuos_list = new ArrayList<Controllable>();
		for(Object controllableObj  : f_controllable_list)
		{
			for(Object integerObj : ((Controllable) controllableObj).getF_attributes_list())
			{
				if(integerObj.equals((Integer)1))
				{
					f_continuos_list.add((Controllable)controllableObj);
				}
			}  
		Log.d("Object value", controllableObj.toString());
		}
		return f_controllable_list;
	}
	
}
