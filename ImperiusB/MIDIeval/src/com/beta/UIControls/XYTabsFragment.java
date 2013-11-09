package com.beta.UIControls;


import android.app.Activity;
import com.beta.activities.R;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

public class XYTabsFragment extends Fragment implements OnTabChangeListener{

	private View root;
	private TabHost tabHost;
	private int currentTab = 1;

	public static final String TAB_X = "X";
	public static final String TAB_Y = "Y";
	public static final String TAB_DOUBLETAP = "doubletap";
	private static final String TAB_SINGLETAP = "singletap";
	public static final String TAB_FLING = "fling";
	
	private XYSettings[] xySettingsVector_m = new XYSettings[5];
	
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		root = inflater.inflate(R.layout.tabs_fragment, null);
		this.xySettingsVector_m[0] = new XYSettings(0);
		this.xySettingsVector_m[1] = new XYSettings(1);
		this.xySettingsVector_m[2] = new XYSettings(2);
		this.xySettingsVector_m[3] = new XYSettings(3);
		this.xySettingsVector_m[4] = new XYSettings(4);
		tabHost = (TabHost) root.findViewById(android.R.id.tabhost);
		setupTabs();
		this.fn_InitializeXYSettingsFragment();
		
		return root;
	}
	@Override
	public void onStart(){
		super.onStart();
//		root.requestLayout();
//		root.invalidate();
	}
	
	public void fn_InitializeXYSettingsFragment() {		
		tabHost.setOnTabChangedListener(this);
		tabHost.setCurrentTab(currentTab);
		updateTab(TAB_Y, R.id.tabX, 1);
	}
	
	private void setupTabs() {
		// TODO Auto-generated method stub
		tabHost.setup();		
		
		tabHost.addTab(newTab(0, TAB_X, R.id.tabX));
		tabHost.addTab(newTab(1, TAB_Y, R.id.tabX));
		tabHost.addTab(newTab(2, TAB_DOUBLETAP, R.id.tabX));		
		tabHost.addTab(newTab(3, TAB_SINGLETAP, R.id.tabX));
		tabHost.addTab(newTab(4, TAB_FLING,R.id.tabX));
		
		for ( int iCount = 0; iCount < 5; iCount++){
			this.fn_SetDrawableForTabs(iCount);
		}
	}
	
	private void fn_SetDrawableForTabs(int tab){
		ImageView iv = null;
		switch(tab)
		{		
		case 0:
			//((ImageView)indicator.findViewById(R.id.tabButton)).setBackgroundResource(R.drawable.tab_x_selected);
			iv = (ImageView)tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_x));
			break;
		case 1:
			//((ImageView)indicator.findViewById(R.id.tabButton)).setBackgroundResource(R.drawable.tab_xy_unselected);
			iv = (ImageView)tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_xy_unselected));
			break;
		case 2:
			//((ImageView)indicator.findViewById(R.id.tabButton)).setBackgroundResource(R.drawable.tab_dt_unselected);
			iv = (ImageView)tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_dt_unselected));
			break;
		case 3:
			iv = (ImageView)tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_st_unselected));
			break;
		case 4:
			//((ImageView)indicator.findViewById(R.id.tabButton)).setBackgroundResource(R.drawable.tab_fl_unselected);
			iv = (ImageView)tabHost.getTabWidget().getChildAt(3).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_fl_unselected));
			break;
		}
		
		
	}

	public TabSpec newTab(int tab,String tag, int contentId)
	{
		View indicator = LayoutInflater.from(getActivity()).inflate(R.layout.tab, null);
//		ImageView iv = null;
//		switch(tab)
//		{		
//		case 0:
//			//((ImageView)indicator.findViewById(R.id.tabButton)).setBackgroundResource(R.drawable.tab_x_selected);
//			iv = (ImageView)tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabButton);
//			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_x_selected));
//			break;
//		case 1:
//			//((ImageView)indicator.findViewById(R.id.tabButton)).setBackgroundResource(R.drawable.tab_xy_unselected);
//			iv = (ImageView)tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabButton);
//			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_xy_unselected));
//			break;
//		case 2:
//			//((ImageView)indicator.findViewById(R.id.tabButton)).setBackgroundResource(R.drawable.tab_dt_unselected);
//			iv = (ImageView)tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabButton);
//			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_dt_unselected));
//			break;
//		case 3:
//			//((ImageView)indicator.findViewById(R.id.tabButton)).setBackgroundResource(R.drawable.tab_fl_unselected);
//			iv = (ImageView)tabHost.getTabWidget().getChildAt(3).findViewById(R.id.tabButton);
//			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_fl_unselected));
//			break;
//		}
//		
//		
		
		TabSpec tabSpec = tabHost.newTabSpec(tag);
		tabSpec.setIndicator(indicator);
		tabSpec.setContent(contentId);
		return tabSpec;
	}
	
	public StateListDrawable setImage()
	{
		
		return null;
	}
	
	private void updateTab(String tag, int tabToBeReplacedId, int tabNumber)
	{
		FragmentManager fm = getChildFragmentManager();
		if(fm.findFragmentByTag(tag) == null)
		{
			fm.beginTransaction().replace(tabToBeReplacedId, this.xySettingsVector_m[tabNumber], tag).commit();
		}
		else{
			//fm.beginTransaction().add(tabToBeReplacedId, xySettingsVector_m[tabNumber], tag).commit();
		}
		fm.executePendingTransactions();
		FragmentManager fragmentManager_f = getFragmentManager();
		if ( fragmentManager_f != null )
			fragmentManager_f.executePendingTransactions();
		
		
	}
	
	@Override
	public void onTabChanged(String tag) {
		// TODO Auto-generated method stub
 		if(TAB_X.equals(tag))
		{
			ImageView iv = (ImageView)tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_x_selected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_xy_unselected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_dt_unselected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(3).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_st_unselected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(4).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_fl_unselected));
			updateTab(tag, R.id.tabX, 0);
			currentTab = 0;
			return;
		}
		if(TAB_Y.equals(tag))
		{
			ImageView iv = (ImageView)tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_x));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_xy_selected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_dt_unselected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(3).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_st_unselected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(4).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_fl_unselected));
			updateTab(tag, R.id.tabX, 1);
			currentTab = 1;
			return;
		}
		if(TAB_DOUBLETAP.equals(tag))
		{
			ImageView iv = (ImageView)tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_x));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_xy_unselected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_dt_selected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(3).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_st_unselected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(4).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_fl_unselected));
			updateTab(tag, R.id.tabX, 2);
			currentTab = 2;
			return;
		}
		if(TAB_SINGLETAP.equals(tag))
		{
			ImageView iv = (ImageView)tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_x));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_xy_unselected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_dt_unselected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(3).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_st_selected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(4).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_fl_unselected));
			
			updateTab(tag, R.id.tabX, 3);
			currentTab = 3;
			return;
		}
		if(TAB_FLING.equals(tag))
		{
			ImageView iv = (ImageView)tabHost.getTabWidget().getChildAt(0).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_x));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(1).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_xy_unselected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(2).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_dt_unselected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(3).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_st_unselected));
			iv = (ImageView)tabHost.getTabWidget().getChildAt(4).findViewById(R.id.tabButton);
			iv.setImageDrawable(getResources().getDrawable(R.drawable.tab_fl_selected));
			updateTab(tag, R.id.tabX, 4);
			currentTab = 4;
			return;
		}
	}
}
