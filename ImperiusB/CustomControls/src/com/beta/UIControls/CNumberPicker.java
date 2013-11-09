package com.beta.UIControls;



import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.customcontrol.seekbar.R;

public class CNumberPicker extends TableLayout {
	
	//Member of layout
	private TextView previousTextView_m;
	private TextView selectedTextView_m;
	private TextView nextTextView_m;
	private Button incrementButton_m;
	private Button decrementButton_m;
	private TextView selectedStringTextView_m;
	private ImageView dividerImageView_m;
	
	//Background for layout entities
	private Drawable previousTextBackgroundDrawable_m;
	private Drawable nextTextBackgroundDrawable_m;
	private Drawable selectedTextBackgroundDrawable_m;
	private Drawable selectedStringBackgroundDrawable_m;
	private Drawable decrementButtonBackgroundDrawable_m;
	private Drawable incrementButtonBackgroundDrawable_m;
	private Drawable separatorBackgroundDrawable_m;
	
	//List management of the view
	private TreeMap<Integer, String> dictionaryListOfItems_m;
	private TreeMap<Integer, String> dictionaryListOfIndex_m; 
	private int i_SelectedIndex_m = -1;
	private int i_PreviousIndex_m = -1;
	private int i_NextIndex_m = -1;
	
	//Interface to expose methods
	public interface IOnValueSelectionChanged{
		public void onValueSelectionChanged(String string);
	}
	private IOnValueSelectionChanged valueChangedListenerRef_m;
	//Constructor for instantiation
	public CNumberPicker(Context context){
		this(context, null);		
	}
	public CNumberPicker(Context context, AttributeSet attributeSet){
		this(context, attributeSet, 0);
	}
	public CNumberPicker(Context context, AttributeSet attributeSet, int defStyle){
		super(context, attributeSet);
		
		TypedArray arrAttributeSet_f = context.obtainStyledAttributes(attributeSet, R.styleable.CNumberPicker, defStyle, 0);
		
		try{
			fn_InitNumberPicker();
			
			//Fetch all the drawable objects to fill the entities of the layout
			Drawable obtainedDrawable_f = arrAttributeSet_f.getDrawable(R.styleable.CNumberPicker_decrement_button_background);
			if (obtainedDrawable_f != null ){
				this.decrementButtonBackgroundDrawable_m = obtainedDrawable_f;
			}			
			obtainedDrawable_f = arrAttributeSet_f.getDrawable(R.styleable.CNumberPicker_increment_button_background);
			if ( obtainedDrawable_f != null ){
				this.incrementButtonBackgroundDrawable_m = obtainedDrawable_f;
			}			
			obtainedDrawable_f = arrAttributeSet_f.getDrawable(R.styleable.CNumberPicker_previous_text_background);
			if ( obtainedDrawable_f != null ){
				this.previousTextBackgroundDrawable_m = obtainedDrawable_f;
			}			
			obtainedDrawable_f = arrAttributeSet_f.getDrawable(R.styleable.CNumberPicker_next_text_background);
			if ( obtainedDrawable_f != null ){
				this.nextTextBackgroundDrawable_m = obtainedDrawable_f;
			}
			obtainedDrawable_f = arrAttributeSet_f.getDrawable(R.styleable.CNumberPicker_selected_String_text_background);
			if ( obtainedDrawable_f != null){
				this.selectedStringBackgroundDrawable_m = obtainedDrawable_f;
			}
			obtainedDrawable_f = arrAttributeSet_f.getDrawable(R.styleable.CNumberPicker_number_picker_separator);
			if ( obtainedDrawable_f != null ){
				this.separatorBackgroundDrawable_m = obtainedDrawable_f;
			}
			//Obtain context-based layout inflater to inflate the pre-defined layout
			LayoutInflater layoutInflater_f = LayoutInflater.from(context);
			layoutInflater_f.inflate(R.layout.spinner_layout, this);
			
			//Get the individual view elements from the layout 
			this.incrementButton_m = (Button)this.findViewById(R.id.btn_increment); 
			this.decrementButton_m = (Button)this.findViewById(R.id.btn_decrement);
			this.selectedTextView_m = (TextView)this.findViewById(R.id.text_row_02);
			this.previousTextView_m = (TextView)this.findViewById(R.id.text_row_01);
			this.nextTextView_m = (TextView)this.findViewById(R.id.text_row_03);
			this.selectedStringTextView_m = (TextView)this.findViewById(R.id.text_row_06);
			
		}	
		finally{
			arrAttributeSet_f.recycle();
		}
		
		
	}
	
	public void fn_InitNumberPicker(){
		
	}
	
	/*Function: fn_RegisterButtonClickListener
	 *Functionality: To register the button with an onClickListener
	 *@1: The button itself
	 *@2: The direction of change, i.e increment(1) or decrement(-1)
	 */
	public void fn_RegisterButtonClickListener(Button button, final int direction){
		if ( button == null )
			return;
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if ( dictionaryListOfItems_m == null )
					return;
				if ( direction == 1){
					int index = fn_GetNextIndexInDictionary(i_SelectedIndex_m);
					if ( index < 0 || index >= dictionaryListOfItems_m.size() )
						return;
					else{
						i_SelectedIndex_m = index;
					}
				}
				else if ( direction == -1 ){
					int index = fn_GetPreviousIndexInDictionary(i_SelectedIndex_m);
					if ( index < 0)
						return;
					else{
						i_SelectedIndex_m = index;
					}
				}
				fn_PopulateTextViews(i_SelectedIndex_m );
			}
		});
	}
	
	public int fn_GetNextIndexInDictionary(int index){
		if ( index  + 1 > this.getDictionaryListOfItems().size())
			return -1;
		else 
			return ++index;
	}
	public int fn_GetPreviousIndexInDictionary(int index){
		if ( this.i_SelectedIndex_m - 1 < 0)
			return -1;
		else
			return --index;
			
		
	}
	
	public void fn_PopulateTextViews(int selectedIndex){
		if ( this.selectedStringTextView_m == null || this.previousTextView_m == null || this.nextTextView_m == null || this.selectedTextView_m == null )
			return;
		int index_f = selectedIndex;
		String selectedItem_f = this.fn_SetTextToTextView(this.selectedTextView_m, index_f);
		if ( selectedItem_f != null )
			this.selectedStringTextView_m.setText(this.dictionaryListOfItems_m.get(Integer.parseInt(selectedItem_f)));
		else
			this.selectedStringTextView_m.setText("");			
		index_f = this.fn_GetPreviousIndexInDictionary(selectedIndex);
		this.fn_SetTextToTextView(this.previousTextView_m, index_f);
		index_f = this.fn_GetNextIndexInDictionary(selectedIndex);
		this.fn_SetTextToTextView(this.nextTextView_m, index_f);
		
		if ( this.getValueChangedListenerRef() != null )
			getValueChangedListenerRef().onValueSelectionChanged(this.dictionaryListOfIndex_m.get(this.i_SelectedIndex_m));
		
	}
	public String fn_SetTextToTextView(TextView textView, int indexValue){
		if ( indexValue < 0){			
			textView.setText("");
			return null;
		}
		else{
			String selectedItem_f= this.dictionaryListOfIndex_m.get(indexValue);			
			textView.setText(selectedItem_f);		
			return selectedItem_f;
		}
	}
	
	public TreeMap<Integer, String> getDictionaryListOfItems() {
		return dictionaryListOfItems_m;
	}
	public void setDictionaryListOfItems(TreeMap<Integer, String> dictionaryListOfItems_m) {
		this.dictionaryListOfItems_m = dictionaryListOfItems_m;
		if ( this.dictionaryListOfItems_m != null && this.dictionaryListOfItems_m.size() > 0){
			Set<Integer> keySet_f = this.dictionaryListOfItems_m.keySet();
			Iterator keySetIterator_f = keySet_f.iterator();
			int iCount = 0;
			this.dictionaryListOfIndex_m = new TreeMap<Integer, String>();
			while(keySetIterator_f.hasNext()){
				this.dictionaryListOfIndex_m.put(iCount, keySetIterator_f.next().toString());
				iCount++;
			}
		}
		this.fn_RegisterButtonClickListener(decrementButton_m, -1);
		this.fn_RegisterButtonClickListener(incrementButton_m, +1);
		this.i_SelectedIndex_m = 0;
		this.fn_PopulateTextViews(i_SelectedIndex_m);
	}
	public IOnValueSelectionChanged getValueChangedListenerRef() {
		return valueChangedListenerRef_m;
	}
	public void setValueChangedListenerRef(IOnValueSelectionChanged valueChangedListenerRef_m) {
		this.valueChangedListenerRef_m = valueChangedListenerRef_m;
	}
	static class SavedState extends BaseSavedState {
		int i_SelectedIndex_m;

		/**
		 * Constructor called from {@link ProgressBar#onSaveInstanceState()}
		 */
		SavedState(Parcelable superState) {
			super(superState);
		}

		/**
		 * Constructor called from {@link #CREATOR}
		 */
		private SavedState(Parcel in) {
			super(in);
			this.i_SelectedIndex_m = in.readInt();

		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(this.i_SelectedIndex_m);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
	@Override
	public Parcelable onSaveInstanceState() {
		// Force our ancestor class to save its state
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);

		ss.i_SelectedIndex_m = this.i_SelectedIndex_m;
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		this.i_SelectedIndex_m = ss.i_SelectedIndex_m;
		this.fn_PopulateTextViews(ss.i_SelectedIndex_m);
	}


}
