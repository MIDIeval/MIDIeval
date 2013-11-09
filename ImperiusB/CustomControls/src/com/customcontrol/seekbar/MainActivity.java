package com.customcontrol.seekbar;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.RadioGroup;

import com.beta.UIControls.CNumberPicker;
public class MainActivity extends Activity {
	
	CNumberPicker numberPickerRef_m;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_beta);
//        this.numberPickerRef_m = (CNumberPicker)this.findViewById(R.id.number_picker);
//        HashMap<String, String> hashMap_f = new HashMap<String, String>();
//        for (int iCount = 0; iCount < 20; iCount++){
//        	hashMap_f.put(String.valueOf(iCount*2), "Counter:"+iCount);
//        }
//        this.numberPickerRef_m.setDictionaryListOfItems(hashMap_f);
        RadioGroup radioGroup_f = (RadioGroup)this.findViewById(R.id.tab_group);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
