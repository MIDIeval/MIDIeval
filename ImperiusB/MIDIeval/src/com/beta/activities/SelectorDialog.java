package com.beta.activities;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SelectorDialog extends DialogFragment {

	private String[] listOfControllables_m;
	private String s_TitleHeader_m;
	Bundle bundle;
	public Bundle getBundle() {
		return bundle;
	}

	public void setBundle(Bundle bundle) {
		this.bundle = bundle;
		
	}

	//Callback interface implementation to pass selected object to the calling activity/framgment
	public interface ISelectorDialogListener{
		void onSelectionMade(String selectedObject);
	}
	public ISelectorDialogListener selectorDialogListener_m;
//	public SelectorDialog(Bundle passedBundleObj)
//	{
//		super();
//		
//		//Once bundle is used for fetch, clear it for other dialogs to use
//		passedBundleObj.clear();
//	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstance){
		//Use the Builder class for convenient dialog construction
		
		AlertDialog.Builder selectorBuilder_f = new AlertDialog.Builder(getActivity());
		this.bundle = this.getArguments();
		this.s_TitleHeader_m = bundle.getString(super.getString(R.string.SELECTOR_DIALOG_BUNDLE_NAME)+"_string");
		this.listOfControllables_m = bundle.getStringArray(getString(R.string.SELECTOR_DIALOG_BUNDLE_NAME)+"_stringarray");
		selectorBuilder_f.setTitle(s_TitleHeader_m);
		selectorBuilder_f.setTitle(this.s_TitleHeader_m);
		selectorBuilder_f.setItems(this.listOfControllables_m, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				selectorDialogListener_m.onSelectionMade(listOfControllables_m[which]);
			}
		});
		return selectorBuilder_f.create();
	}

	public String[] getListOfControllables_m() {
		return listOfControllables_m;
	}

	public void setListOfControllables_m(String[] listOfControllables_m) {
		this.listOfControllables_m = listOfControllables_m;
	}

	public String getS_TitleHeader_m() {
		return s_TitleHeader_m;
	}

	public void setS_TitleHeader_m(String s_TitleHeader_m) {
		this.s_TitleHeader_m = s_TitleHeader_m;
	}
	
	
}
