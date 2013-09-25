package com.beta.ControlMapper;

import java.util.HashMap;

import com.beta.Controllability.IController;

public class MapperPrototype {
	private HashMap<IController, HashMap<Integer, Integer>> controllerMapObj_m;

	public MapperPrototype(){
		this.controllerMapObj_m = new HashMap<IController, HashMap<Integer, Integer>>();
	}
	/**
	 * @return the controllerMapObj_m
	 */
	public HashMap<IController, HashMap<Integer, Integer>> getControllerMapObj() {
		return controllerMapObj_m;
	}

	/**
	 * @param controllerMapObj_m the controllerMapObj_m to set
	 */
	public void setControllerMapObj(HashMap<IController, HashMap<Integer, Integer>> controllerMapObj_m) {
		this.controllerMapObj_m = controllerMapObj_m;
	}
	public void setSubControllerValue(IController controller, int subControllerID, int functionValue){
	
		for ( IController controllerInList :this.controllerMapObj_m.keySet()){
			HashMap<Integer, Integer> tempSubControllerMap = this.controllerMapObj_m.get(controllerInList);
			if ( controllerInList.getClass().isAssignableFrom(controller.getClass())) {
				for ( int intValue: controller.fn_FetchSubControllerMap().keySet()){
					if ( subControllerID == intValue){
						tempSubControllerMap.put(intValue, functionValue);
					}
				}
			}
		}
	}
	public int getFunctionValue(IController controller, int subControllerID ){
		if (controller == null)
			return -24;
		for ( IController controllerInList :this.controllerMapObj_m.keySet()){
			HashMap<Integer, Integer> tempSubControllerMap = this.controllerMapObj_m.get(controllerInList);
			if ( controllerInList.getClass().isAssignableFrom(controller.getClass())) {
				for ( int intValue: controller.fn_FetchSubControllerMap().keySet()){
					if ( subControllerID == intValue){
						return tempSubControllerMap.get(subControllerID);
					}
				}
			}
		}
		return -25;
	}

}
