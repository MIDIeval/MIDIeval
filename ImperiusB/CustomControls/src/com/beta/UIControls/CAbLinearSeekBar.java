package com.beta.UIControls;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.beta.UIControls.CProgressBar.IThumbConflictListener;
import com.customcontrol.seekbar.R;

public abstract class CAbLinearSeekBar extends CProgressBar implements IThumbConflictListener {

//    private Drawable thumbDrawable_m;
//    private Drawable thumbDrawableHigh_m;
    private ThumbManager[] thumbManagerVector_m;
    
    private Drawable selectedThumb_m;
    private int i_ThumbOffset_m;
    private int mPaddingLeft;
    private int mPaddingTop;
    private int mPaddingRight;
    private int mPaddingBottom;
    private Rect[] rectBounds_m = new Rect[2];;//[0] low Thumb bound; [1] hight Thumb bound
    private boolean b_IsIncreasing_m = false;
    /**
     * On touch, this offset plus the scaled value from the position of the
     * touch will form the progress value. Usually 0.
     */
    float f_TouchProgressOffset_m;

    /**
     * Whether this is user seekable.
     */
    boolean bIsUserSeekable_m = true;

    /**
     * On key presses (right or left), the amount to increment/decrement the
     * progress.
     */
    private int iKeyProgressIncrement_m = 1;
    
    private static final int NO_ALPHA = 0xFF;
    private float f_DisabledAlpha_m;
    
    public CAbLinearSeekBar(Context context) {
        super(context);
    }

    public CAbLinearSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CAbLinearSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray attributeSetSpecified_f = context.obtainStyledAttributes(attrs,
                R.styleable.CLinearSeekBar, defStyle, 0);
        Drawable thumbDrawable_f = attributeSetSpecified_f.getDrawable(R.styleable.CLinearSeekBar_android_thumb);      
        Drawable thumbDrawableHigh_f= attributeSetSpecified_f.getDrawable(R.styleable.CLinearSeekBar_thumb_high);
       
        if ( this.getProgressType() == 0){
        	this.thumbManagerVector_m = new ThumbManager[1];
        	this.thumbManagerVector_m[0] = new ThumbManager(thumbDrawable_f, super.getOrientation(), super.getMinMaxPadding(), THUMB_TYPE.LOW, super.getProgressType());
        }
        else{
        	this.thumbManagerVector_m = new ThumbManager[2];
        	this.thumbManagerVector_m[0] = new ThumbManager(thumbDrawable_f, super.getOrientation(), super.getMinMaxPadding(), THUMB_TYPE.LOW, super.getProgressType());
        	this.thumbManagerVector_m[1] = new ThumbManager(thumbDrawableHigh_f, super.getOrientation(), super.getMinMaxPadding(), THUMB_TYPE.HIGH, super.getProgressType());
        }
        int i_ThumbOffset_f =
        		attributeSetSpecified_f.getDimensionPixelOffset(R.styleable.CLinearSeekBar_android_thumbOffset, 0);
        setThumbOffset(i_ThumbOffset_f);
        this.f_DisabledAlpha_m = attributeSetSpecified_f.getFloat(R.styleable.CLinearSeekBar_android_disabledAlpha, 0.5f);
        if(thumbDrawableHigh_f != null ){        	         	 
        	 setThumbOffset(i_ThumbOffset_f);//Look to set a second offset for the second thumb, the high thumb
        }
        
        	
        attributeSetSpecified_f.recycle();
        
        this.mPaddingBottom = this.getPaddingBottom();
        this.mPaddingLeft = this.getPaddingLeft();
        this.mPaddingTop = this.getPaddingTop();
        this.mPaddingRight = this.getPaddingRight();
        
        this.setThumbConflictListener(this);
    }

    /**
     * Sets the thumb that will be drawn at the end of the progress meter within the SeekBar
     * 
     * @param thumb Drawable representing the thumb
     */


    /**
     * @see #setThumbOffset(int)
     */
    public int getThumbOffset() {
        return this.i_ThumbOffset_m;
    }

    /**
     * Sets the thumb offset that allows the thumb to extend out of the range of
     * the track.
     * 
     * @param thumbOffset The offset amount in pixels.
     */
    public void setThumbOffset(int thumbOffset) {
       	for ( int iCount = 0; iCount < this.thumbManagerVector_m.length; iCount++ ){
    		if ( this.thumbManagerVector_m[iCount] != null ){
    			this.thumbManagerVector_m[iCount].setThumbOffset(thumbOffset);
    		}
    	}
        invalidate();
    }

    /**
     * Sets the amount of progress changed via the arrow keys.
     * 
     * @param increment The amount to increment or decrement when the user
     *            presses the arrow keys.
     */
    public void setKeyProgressIncrement(int increment) {
        this.iKeyProgressIncrement_m = increment < 0 ? -increment : increment;
    }

    /**
     * Returns the amount of progress changed via the arrow keys.
     * <p>
     * By default, this will be a value that is derived from the max progress.
     * 
     * @return The amount to increment or decrement when the user presses the
     *         arrow keys. This will be positive.
     */
    public int getKeyProgressIncrement() {
        return this.iKeyProgressIncrement_m;
    }
    
    @Override
    public synchronized void setMax(int max) {
        super.setMax(max);

        if ((this.iKeyProgressIncrement_m == 0) || (getMax() / this.iKeyProgressIncrement_m > 20)) {
            // It will take the user too long to change this via keys, change it
            // to something more reasonable
            setKeyProgressIncrement(Math.max(1, Math.round((float) getMax() / 20)));
        }
    }
  

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        
        Drawable progressDrawable = getProgressDrawable();
        if (progressDrawable != null) {
            progressDrawable.setAlpha(isEnabled() ? NO_ALPHA : (int) (NO_ALPHA * this.f_DisabledAlpha_m));
        }
        if ( this.thumbManagerVector_m == null )
        	return;
        
        Drawable thumbDrawable = null;
        
        for ( int iCount = 0; iCount < this.thumbManagerVector_m.length; iCount++){
	        if ( this.thumbManagerVector_m[iCount] != null){
	        	thumbDrawable = this.thumbManagerVector_m[iCount].getThumbDrawable();
	        	if (thumbDrawable != null && thumbDrawable.isStateful()){
	        		int[] state = getDrawableState();
	        		this.thumbManagerVector_m[iCount].getThumbDrawable().setState(state);
	        	}
	        }
        }
    }
    
    @Override
    protected void onProgressRefresh(float[] scale, boolean fromUser) {
    	if ( this.thumbManagerVector_m == null )
    		return;
        Drawable thumbDrawable_f = this.selectedThumb_m;  
        if ( thumbDrawable_f == null )
        	return;
        for (int iCount = 0; iCount < this.thumbManagerVector_m.length; iCount++){
        	if (this.thumbManagerVector_m[iCount] != null ){
        		if ( this.thumbManagerVector_m[iCount].getThumbDrawable() != null ){
        			if ( this.thumbManagerVector_m[iCount].getThumbType() == THUMB_TYPE.HIGH)
        				scale[iCount] = 1 - scale[iCount];        			
        			if(this.thumbManagerVector_m[iCount].fn_IsThisThumb(this.selectedThumb_m)){
        				setThumbPos((this.getOrientation() == 0)?getWidth():getHeight(), this.thumbManagerVector_m[iCount], scale[iCount], Integer.MIN_VALUE);
        			}
        			else{
        				if ( this.thumbManagerVector_m[iCount].fn_IsConnectedToOtherThumb(selectedThumb_m)){
        					Log.i("CONNECTED", "THUMBS CONNECTED");
        					setThumbPos((this.getOrientation() == 0)?getWidth():getHeight(), this.thumbManagerVector_m[iCount], scale[iCount], Integer.MIN_VALUE);        					
        				}
        			}
        		}
        	}
        }
        
     
            /*
             * Since we draw translated, the drawable's bounds that it signals
             * for invalidation won't be the actual bounds we want invalidated,
             * so just invalidate this whole view.
             */
            invalidate();
    }
    
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	super.onSizeChanged(w, h, oldw, oldh);
    	
    	   	
        //Get the progress bar details and set the size information as required
    	Drawable d = getCurrentDrawable();
        int i_TrackAvailableDimension_f = (this.getOrientation() == 0 )? Math.min(super.i_MaxHeight_m, h - super.getPaddingTop() - super.getPaddingBottom()):
        	 Math.min(super.i_MaxWidth_m, w - super.getPaddingLeft() - super.getPaddingRight());       
        
        i_TrackAvailableDimension_f = (this.getOrientation() == 0)?Math.min(d.getIntrinsicHeight(), h - super.getPaddingTop() - super.getPaddingBottom()):
        	Math.min(d.getIntrinsicWidth(), w - super.getPaddingLeft() - super.getPaddingRight());       
        if ( this.thumbManagerVector_m == null )
        	return;
        
        //Parameters comoon for both SINGLE and DUAL mode
        Drawable thumbDrawable_f;
        int max = getMax();
        float[] f_Progress_f = new float[this.thumbManagerVector_m.length];        
    	float[] scale = new float[this.thumbManagerVector_m.length];
    	int gapForCenteringTrack = 0;
    	int i_PreviousThumbDimension_f = 0;
    	Rect progressDrawableBounds_f = new Rect();
        for ( int iCount = 0; iCount < this.thumbManagerVector_m.length; iCount++){
        	if ( this.thumbManagerVector_m[iCount] != null ){
        		if ( this.thumbManagerVector_m[iCount].getThumbDrawable() != null ){
        			this.thumbManagerVector_m[iCount].setContainerView(this);
        			this.thumbManagerVector_m[iCount].setMinThumbPadding((int)((super.getMinMaxPadding()/(float)super.getMax())*((this.getOrientation() == 0)?this.getWidth():this.getHeight())));
        			thumbDrawable_f = this.thumbManagerVector_m[iCount].getThumbDrawable();
        			int i_ThumbAvailableDimension_f = thumbDrawable_f == null ? 0 : ((this.getOrientation() == 0)?thumbDrawable_f.getIntrinsicHeight():thumbDrawable_f.getIntrinsicWidth());
        	        
        	        f_Progress_f[iCount] = getProgress()[iCount];
        	        scale[iCount] = (max > 0 ?  (f_Progress_f[iCount]) / (float) max : 0);
        	        int gap = 0;
        	        if ( i_ThumbAvailableDimension_f >= i_PreviousThumbDimension_f){
	        	        if ( i_ThumbAvailableDimension_f > i_TrackAvailableDimension_f ){		        	        
		        	        gapForCenteringTrack = (i_ThumbAvailableDimension_f - i_TrackAvailableDimension_f) / 2;
		        	        progressDrawableBounds_f.left = (this.getOrientation() == 0)?0:gapForCenteringTrack;
		        	        progressDrawableBounds_f.top = (this.getOrientation() == 0)?gapForCenteringTrack:0;
		        	        progressDrawableBounds_f.right =  (this.getOrientation() == 0)? (w - this.getPaddingRight() - this.getPaddingLeft()):
		        	        	(w - this.getPaddingRight() - this.getPaddingLeft() - gapForCenteringTrack);
		        	        progressDrawableBounds_f.bottom = (this.getOrientation() == 0)? (h - this.getPaddingBottom() - gapForCenteringTrack - this.getPaddingTop())
		        	        		:h - this.getPaddingBottom() - this.getPaddingTop();
		        	        	        	        	
	        	        }
	        	        else {
	        	        	progressDrawableBounds_f.left = 0;
	        	        	progressDrawableBounds_f.top = 0;
	        	        	progressDrawableBounds_f.right =  (w - this.getPaddingTop() - this.getPaddingRight());
	        	        	progressDrawableBounds_f.bottom = (h - this.getPaddingBottom() - this.getPaddingTop());
	        	        	gap = (i_TrackAvailableDimension_f - i_ThumbAvailableDimension_f) / 2;
	        	        	
	        	        }
        	        }
        	        if (thumbDrawable_f != null) {
	        			setThumbPos((this.getOrientation() == 0)?w:h, thumbManagerVector_m[iCount], scale[iCount], gap);
	        		}
        	        i_PreviousThumbDimension_f = i_ThumbAvailableDimension_f;
        	        
        		}
        	}
        }
        
        //Draw the track, irrespective of how many thumbs there are in the View
        if (d != null) {
            // 	Canvas will be translated by the padding, so 0,0 is where we start drawing
    			d.setBounds(progressDrawableBounds_f);
    	}          
 
    }

    /**
     * @param gap If set to {@link Integer#MIN_VALUE}, this will be ignored and
     */
    private void setThumbPos(int dimension, ThumbManager thumbManager, float scale, int gap) {
    	if ( thumbManager == null )
    		return;
    	Drawable thumbDrawable_f = thumbManager.getThumbDrawable();
    	if ( thumbDrawable_f == null )
    		return;
    	int i_AvailableDimension_f = ( this.getOrientation() == 0 )? (dimension - mPaddingLeft - mPaddingRight):
    		(dimension - mPaddingTop - mPaddingBottom) ;
    	int i_ThumbSliderDimension_f = (this.getOrientation() == 0)?thumbDrawable_f.getIntrinsicWidth():thumbDrawable_f.getIntrinsicHeight();
    	int i_ThumbStaticDimension_f = (this.getOrientation() == 0)?thumbDrawable_f.getIntrinsicHeight():thumbDrawable_f.getIntrinsicWidth();
    	i_AvailableDimension_f = i_AvailableDimension_f - i_ThumbSliderDimension_f;
    	
    	i_AvailableDimension_f  += thumbManager.getThumbOffset() * 2;
    	int i_ThumbPos_f = (int) (scale * i_AvailableDimension_f);
    	int i_Border_Bound_0, i_Border_Bound_1;
	    if (gap == Integer.MIN_VALUE) {
	    	Rect oldBounds = thumbDrawable_f.getBounds();
	    	i_Border_Bound_0 = (this.getOrientation() == 0)?oldBounds.top:oldBounds.left;
	    	i_Border_Bound_1 = (this.getOrientation() == 0)?oldBounds.bottom:oldBounds.right;
	    } 
	    else 
	    {
	    	i_Border_Bound_0 = gap;
	    	i_Border_Bound_1 = gap + i_ThumbStaticDimension_f;	    
	    }
	    Rect rectBoundsForDraw = thumbManager.getRectBoundsForDraw();
	    rectBoundsForDraw.left = (this.getOrientation() == 0) ? i_ThumbPos_f:i_Border_Bound_0;
	    rectBoundsForDraw.top = (this.getOrientation() == 0)? i_Border_Bound_0:(dimension - i_ThumbSliderDimension_f - i_ThumbPos_f);
	    rectBoundsForDraw.right = (this.getOrientation() == 0)? (i_ThumbPos_f + i_ThumbSliderDimension_f):i_Border_Bound_1;
	    rectBoundsForDraw.bottom = (this.getOrientation() == 0)?i_Border_Bound_1:(dimension - i_ThumbPos_f);
	    //set the drawable region for the Thumb
	    thumbManager.setRectBoundsForDraw(rectBoundsForDraw);
	    thumbDrawable_f.setBounds(rectBoundsForDraw);
	    this.fn_SetDualProgressTypeBound(this.getProgress());//This is the corrected progress vector
	   
    }
    
    public void fn_SetDualProgressTypeBound(int[] progress){    
    	if ( this.getProgressType() == 0 ){
    		this.thumbManagerVector_m[0].setRectBoundsForTouchResponse(null);
    		return;
    	}
    	Rect rectBounds_f[] = new Rect[2];    
    	float difference_f = (progress[1] - progress[0])/2;
    	float midPoint_f = difference_f + progress[0];
    	float scale_f = midPoint_f/this.getMax();
    	float location_f = scale_f*((this.getOrientation() == 0)?this.getWidth():this.getHeight());
    	if ( this.getOrientation() == 0){
    		for(int iCount = 0; iCount < this.thumbManagerVector_m.length; iCount++){
    			this.thumbManagerVector_m[iCount].getRectBoundsForTouchResponse().top = 0;
    			this.thumbManagerVector_m[iCount].getRectBoundsForTouchResponse().bottom = this.getHeight() + 1;
    		}
    	}
    	else if ( this.getOrientation() == 1){
    		for(int iCount = 0; iCount < this.thumbManagerVector_m.length; iCount++){
    			this.thumbManagerVector_m[iCount].getRectBoundsForTouchResponse().left = 0;
    			this.thumbManagerVector_m[iCount].getRectBoundsForTouchResponse().right = this.getWidth()+ 1;
    		}
    	}
    		
    	if ( this.thumbManagerVector_m == null )
    		return;
    	for ( int iCount = 0; iCount < this.thumbManagerVector_m.length; iCount++ ){
    		if ( this.thumbManagerVector_m[iCount] != null ){
    			Log.i(VIEW_LOG_TAG, String.valueOf(this.thumbManagerVector_m[iCount].getRectBoundsForTouchResponse().left)
    					+","+String.valueOf(this.thumbManagerVector_m[iCount].getRectBoundsForTouchResponse().top)
    							+","+String.valueOf(this.thumbManagerVector_m[iCount].getRectBoundsForTouchResponse().right)
    								+","+ String.valueOf(this.thumbManagerVector_m[iCount].getRectBoundsForTouchResponse().bottom));
    			rectBounds_f[iCount] = this.thumbManagerVector_m[iCount].getRectBoundsForTouchResponse();
    			if ( super.getOrientation() == 0 ){
    	    		if ( iCount == 0)
    	    			rectBounds_f[iCount].right = (int)Math.floor(location_f) + 1;
    	    		else{
    	    			rectBounds_f[iCount].left = (int)Math.ceil(location_f);
    	    			rectBounds_f[iCount].right = (int)Math.ceil(this.getWidth()) + 1;
    	    		}

    	    	}
    	    	else if ( super.getOrientation() == 1){
    	    		if ( iCount == 0 ){    	    		
    	    			rectBounds_f[iCount].bottom = (int)Math.ceil(this.getHeight()) + 1;
    	    			rectBounds_f[iCount].top = (int)(this.getHeight() - Math.ceil(location_f));
    	    		}
    	    		else{    	    			
    	    			rectBounds_f[iCount].bottom = (int)(this.getHeight() - Math.ceil(location_f)) + 1;    	    			    	    			
    	    		}

    	    	}
    			this.thumbManagerVector_m[iCount].setRectBoundsForTouchResponse(rectBounds_f[iCount]);
    		}
    	}
    	
    	
    	
    }
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
       
        if ( thumbManagerVector_m == null )
        	return;
        canvas.save();
        // Translate the padding. For the x, we need to allow the thumb to
        // draw in its extra space
        canvas.translate(mPaddingLeft - mPaddingLeft, mPaddingTop);
        for ( int iCount = 0; iCount < this.thumbManagerVector_m.length; iCount++){
        	if ( this.thumbManagerVector_m[iCount] != null ){
        		if (this.thumbManagerVector_m[iCount].getThumbDrawable() != null ){
        			this.thumbManagerVector_m[iCount].getThumbDrawable().draw(canvas);
        		}
        	}
        }
        canvas.restore();
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if ( this.thumbManagerVector_m == null )
        	return;
    	
    	Drawable progressDrawable_f = getCurrentDrawable();
    	int dw = 0;
        int dh = 0;
        int i_PreviousDimension_f = 0;
        for ( int iCount = 0; iCount < this.thumbManagerVector_m.length; iCount++){
        	if ( this.thumbManagerVector_m[iCount] != null ){
        		if ( this.thumbManagerVector_m[iCount].getThumbDrawable() != null ){
        			Drawable thumbDrawable_f = this.thumbManagerVector_m[iCount].getThumbDrawable();
        			int i_ThumbDimension_f = (this.getOrientation() == 0)? thumbDrawable_f.getIntrinsicHeight():thumbDrawable_f.getIntrinsicWidth();   
        	        if ( progressDrawable_f != null ){
        	        	dw = Math.max(super.i_MinWidth_m, Math.min(super.i_MaxWidth_m, progressDrawable_f.getIntrinsicWidth()));
        	        	dh = Math.max(super.i_MinHeight_m, Math.min(super.i_MaxHeight_m, progressDrawable_f.getIntrinsicHeight()));
        	        	if ( this.getOrientation() == 0 ){  
        	        		dh = Math.max(i_ThumbDimension_f, dh);        	
        	        		dh = Math.max(dh, i_PreviousDimension_f);
        	        	}
        	        	else if (this.getOrientation() == 1 ){        		
        	        		dw = Math.max(i_ThumbDimension_f, dw);
        	        		dw = Math.max(dw, i_PreviousDimension_f);
        	        	}
        	        	i_PreviousDimension_f = i_ThumbDimension_f;
        	        }
        		}
        		
        	}
        }
        
        dw += mPaddingLeft + mPaddingRight;
        dh += mPaddingTop + mPaddingBottom;        
        setMeasuredDimension(resolveSize(dw, widthMeasureSpec),
                resolveSize(dh, heightMeasureSpec));
       

       
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.bIsUserSeekable_m || !isEnabled()) {
            return false;
        }
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setPressed(true);
                this.selectedThumb_m = this.thumbManagerVector_m[0].getThumbDrawable();//If touch response bound is null, select first element
                for (int iCount = 0; iCount < this.thumbManagerVector_m.length; iCount++ ){
            		if ( thumbManagerVector_m[iCount] != null ){
            			if ( this.thumbManagerVector_m[iCount].getRectBoundsForTouchResponse() != null ){
	            			if ( thumbManagerVector_m[iCount].getRectBoundsForTouchResponse().contains((int)event.getX(), (int)event.getY())){
	            				this.selectedThumb_m = this.thumbManagerVector_m[iCount].getThumbDrawable();
	            				Log.e(VIEW_LOG_TAG, "SELECTED THUMB: " + this.thumbManagerVector_m[iCount].getThumbType());
	            				break;
	            			}
            			}
            		}
            	}
                onStartTrackingTouch();
                trackTouchEvent(event);
                break;
                
            case MotionEvent.ACTION_MOVE:
                trackTouchEvent(event);
                attemptClaimDrag();
                break;
                
            case MotionEvent.ACTION_UP:
                trackTouchEvent(event);
                onStopTrackingTouch();
                setPressed(false);
                break;
                
            case MotionEvent.ACTION_CANCEL:
                onStopTrackingTouch();
                setPressed(false);
                break;
        }
        return true;
    }

    private void trackTouchEvent(MotionEvent event) {
    	if ( this.thumbManagerVector_m == null )
    		return;    	
    	Log.i(VIEW_LOG_TAG, "X: " + event.getX() + " Y: "+ event.getY());
    	float scale = 0.0f;
    	int[] i_Progress_f = new int[]{-1,-1};
	    float[] progress = new float[]{-1, -1};
	    int i_ViewDimension_f = (this.getOrientation() == 0)?getWidth(): getHeight();
	    int i_AvailableDimension_f = i_ViewDimension_f - ((this.getOrientation() == 0)? ( mPaddingLeft - mPaddingRight ):(mPaddingTop - mPaddingBottom));	     
    	int x = (int)event.getX();
    	int y = (int)event.getY();
    	int coordinateOfChange_f = (this.getOrientation() == 0)?x:y;
    	int i_historySize_f = event.getHistorySize();
    	int iCount = 0;
    	int i_ActualCoordinateValue_f = (this.getOrientation() == 0)?coordinateOfChange_f:i_AvailableDimension_f - coordinateOfChange_f;
    	//Find the selected thumb
    	for ( ; iCount < this.thumbManagerVector_m.length; iCount++){
    		if ( this.thumbManagerVector_m[iCount] != null ){
    			if(this.thumbManagerVector_m[iCount].fn_IsThisThumb(this.selectedThumb_m))
    				break;
    		}
    	}
    	if ( this.selectedThumb_m == null ){
    		Log.e(VIEW_LOG_TAG, "NO SELECTED THUMB");
    		return;
    	}
    	
    	if ( i_historySize_f > 0 ){
    		int i_HistoryValueOfDimension_f = (this.getOrientation() == 0 )?
    				(int)event.getHistoricalX(0, 0):
    					(i_ViewDimension_f - (int)event.getHistoricalY(0, 0));
    		if ( i_HistoryValueOfDimension_f < i_ActualCoordinateValue_f )
    			this.b_IsIncreasing_m = true;
    		else
    			this.b_IsIncreasing_m = false;
    		
        	if ( this.b_IsIncreasing_m ){
        		super.e_InConflictMoveTowards_m = DUAL_PROGRESS_MOVE_TOWARDS.MAX;
        		if ( !thumbManagerVector_m[iCount].isCanIncrease() ){
        			Log.e("CSEEKBAR", "CANNOT INCREASE ANYMORE");
        			return;
        		}
        	}
        	else{
        		super.e_InConflictMoveTowards_m = DUAL_PROGRESS_MOVE_TOWARDS.MIN;        				
        		if ( !thumbManagerVector_m[iCount].isCanDecrease() ){
        			Log.e("CSEEKBAR", "CANNOT DECREASE ANYMORE");
        			return;
        		}
        	}
    	}   	
    	
    	if ( !this.thumbManagerVector_m[iCount].fn_IsCoordinateWithinBound(coordinateOfChange_f)){
    		return;
    	}
    	
    	//Fetch previous progress values
    	i_Progress_f = this.getProgress();   
    	for ( int iCounter = 0; iCounter < i_Progress_f.length; iCounter++){
    		progress[iCounter] = i_Progress_f[iCounter];
    	}
    	
    	int i_Selector_f = iCount ;
    	if (coordinateOfChange_f < ((this.getOrientation() == 0) ? mPaddingRight : mPaddingTop)) {
    		scale = 1.0f;
	    } else if (coordinateOfChange_f > i_AvailableDimension_f - ((this.getOrientation()==0)?mPaddingLeft:mPaddingBottom)) {
	        scale = 0.0f;
	    } else {
	    	
	        scale = (float)(i_ActualCoordinateValue_f - ((this.getOrientation() == 0)?mPaddingLeft:mPaddingBottom) )/ (float)i_AvailableDimension_f;
	        progress[i_Selector_f] = this.f_TouchProgressOffset_m;
	    }
    	
    	final int max = getMax();
	    progress[i_Selector_f] += scale * max;
	    if ( progress[i_Selector_f] < 0) {
	        progress[i_Selector_f] = 0;
	    } else if ( progress[i_Selector_f] > max) {
	        progress[i_Selector_f] = max;
	    }
	    int[] truncatedProgress_f = new int[]{(int)progress[0], (int)progress[1]};
	    this.fn_SetProgress(truncatedProgress_f, true);
	    
	  
    	
    }
    

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     */
    private void attemptClaimDrag() {
        if (super.getParent() != null) {
            super.getParent().requestDisallowInterceptTouchEvent(true);
        }
    }
    
    /**
     * This is called when the user has started touching this widget.
     */
    void onStartTrackingTouch() {
    }

    /**
     * This is called when the user either releases his touch or the touch is
     * canceled.
     */
    void onStopTrackingTouch() {
    	  this.selectedThumb_m = null;
    }

    /**
     * Called when the user changes the seekbar's progress by using a key event.
     */
    void onKeyChange() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int[] progress = getProgress();
        
        if ( this.getProgressType() == 0 ){
	        switch (keyCode) {
	            case KeyEvent.KEYCODE_DPAD_LEFT:
	                if (progress[0] <= 0) break;
	                progress[0] = progress[0] - this.iKeyProgressIncrement_m;
	                super.fn_SetProgress(progress, true);
	                onKeyChange();
	                return true;
	        
	            case KeyEvent.KEYCODE_DPAD_RIGHT:
	                if (progress[0] >= getMax()) break;
	                progress[0] = progress[0] + this.iKeyProgressIncrement_m;
	                this.fn_SetProgress(progress, true);
	                onKeyChange();
	                return true;
	        }
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onConflictingProgress(int[] newProgress){
//    	if ( this.selectedThumb_m == null )
//    		return;
//    	int i_Selector_f = -1;
//    	if ( this.selectedThumb_m.equals(this.thumbDrawable_m)){
//    		this.selectedThumb_m = this.thumbDrawableHigh_m;
//    		i_Selector_f = 1;
//    	}
//    	else if ( this.selectedThumb_m.equals(this.thumbDrawableHigh_m)){
//    		this.selectedThumb_m = this.thumbDrawable_m;
//    		i_Selector_f = 0;
//    	}
//    	int max = super.getMax();
//    	float []scale = new float[]{(max > 0 ?  (newProgress[0]) / (float) max : 0), (this.getMin() >= 0 ?  (newProgress[1]) / (float) max : 0)};
//    	this.setThumbPos((super.getOrientation() == 0)?this.getWidth():this.getHeight(), this.selectedThumb_m, scale[i_Selector_f], Integer.MIN_VALUE);
//    	
//    	int[] progress = new int[]{0, 0};
//    	progress[i_Selector_f] += scale[i_Selector_f] * max;
//        if ( progress[i_Selector_f] < 0) {
//        	 progress[i_Selector_f] = 0;
//        } else if ( progress[0] > max) {
//        	 progress[i_Selector_f] = max;
//        }
//        int[] truncatedProgress_f = new int[]{(int)progress[0], (int)progress[1]};
//        this.fn_SetProgress(newProgress, false);
	
    }
    public class ThumbManager{
    	private THUMB_TYPE e_ThumbType_m;
    	//To be set after drawing on device
    	private Rect rectBoundsForTouchResponse_m;
    	private Rect rectBoundsForDraw_m;
    	private boolean b_CanDecrease_m = true;
    	private boolean b_CanIncrease_m = true;
    	private View containerView_m;
    	private float[] maxBoundVector_m = new float[2];
    	//To be set after drawing on device
    	private Drawable thumbDrawable_m;
    	private int i_MinThumbPadding_m;
    	private int i_Orientation_m;
    	private int i_ThumbOffset_m;
    	private int i_ProgressType_m;
    	
    	public boolean fn_IsConnectedToOtherThumb(Drawable thumb){
    		
    		if ( this.i_Orientation_m == 0 ){
    			Log.i(VIEW_LOG_TAG, " IS CONNECTED TO OTHER THUMB: " + Math.abs((this.thumbDrawable_m.getBounds().left - thumb.getBounds().left)));
    			return ( Math.abs((this.thumbDrawable_m.getBounds().left - thumb.getBounds().left)) <= this.i_MinThumbPadding_m);
    		}
    		else if ( this.i_Orientation_m == 1)
    			return ( Math.abs(this.thumbDrawable_m.getBounds().top - thumb.getBounds().top) <= this.i_MinThumbPadding_m);
    		else 
    			return false;
    	}
    	
    	public boolean fn_IsThisThumb(Drawable selectedThumb){
    		if (selectedThumb == null)
    			return false;
    		return (selectedThumb.equals(this.thumbDrawable_m));
    	}
    	public boolean fn_IsCoordinateWithinBound(int coordinate){
    		return (coordinate >= this.maxBoundVector_m[0] && coordinate <= this.maxBoundVector_m[1] );
    	}
    	public ThumbManager(Drawable thumbDrawable, int orientation, int minThumbPadding, THUMB_TYPE thumbType,int progressType){
    		this.setThumbDrawable(thumbDrawable);
    		this.i_Orientation_m = orientation;
    		this.i_ProgressType_m = progressType;
    		this.i_MinThumbPadding_m = minThumbPadding;  
    		this.e_ThumbType_m = thumbType;
    		this.rectBoundsForDraw_m = new Rect();
    		this.rectBoundsForTouchResponse_m = new Rect();    		
    		
    	}
		public THUMB_TYPE getThumbType() {
			return e_ThumbType_m;
		}

		public void setThumbType(THUMB_TYPE e_ThumbType_m) {
			this.e_ThumbType_m = e_ThumbType_m;
		}

		public Rect getRectBoundsForTouchResponse() {
			return rectBoundsForTouchResponse_m;
		}

		public void setRectBoundsForTouchResponse(
				Rect rectBoundsForTouchResponse_m) {
			this.rectBoundsForTouchResponse_m = rectBoundsForTouchResponse_m;
		}

		public Rect getRectBoundsForDraw() {
			return rectBoundsForDraw_m;
		}

		public void setRectBoundsForDraw(Rect rectBoundsForDraw_m) {
			this.rectBoundsForDraw_m = rectBoundsForDraw_m;			
			if ( this.i_Orientation_m == 0){
				if ( this.i_ProgressType_m == 0 ){
					this.maxBoundVector_m[0] = 0;
					this.maxBoundVector_m[1] =  this.containerView_m.getWidth();
					return;
				}
				if ( this.e_ThumbType_m == THUMB_TYPE.LOW){
					this.maxBoundVector_m[0] = 0;
					this.maxBoundVector_m[1] =  this.containerView_m.getWidth() - this.thumbDrawable_m.getIntrinsicWidth();
					if ( this.rectBoundsForDraw_m.left == 0){
						Log.e(VIEW_LOG_TAG, "CANNOT DECREASE ANYMORE");
						this.b_CanDecrease_m = false;
						this.b_CanIncrease_m = true;
					}
					else{
						this.b_CanDecrease_m = true;
						this.b_CanIncrease_m = true;
					}
				}
				else if ( this.e_ThumbType_m == THUMB_TYPE.HIGH ){
					this.maxBoundVector_m[0] = this.i_MinThumbPadding_m;
					this.maxBoundVector_m[1] = this.containerView_m.getWidth();
					if ( this.rectBoundsForDraw_m.right == this.containerView_m.getMeasuredWidth()){
						Log.e(VIEW_LOG_TAG, "CANNOT INCREASE ANYMORE");
						this.b_CanDecrease_m = true;
						this.b_CanIncrease_m = false;
					}
					else{
						this.b_CanDecrease_m = true;
						this.b_CanIncrease_m = true;
					}
				}
			}
			else if ( this.i_Orientation_m == 1){
				if ( this.i_ProgressType_m == 0 ){
					this.maxBoundVector_m[0] = 0;
					this.maxBoundVector_m[1] =  this.containerView_m.getHeight();
				}
				if ( this.e_ThumbType_m == THUMB_TYPE.LOW){
					this.maxBoundVector_m[0] = this.i_MinThumbPadding_m;
					this.maxBoundVector_m[1] =  this.containerView_m.getHeight();
					if ( this.rectBoundsForDraw_m.bottom == this.containerView_m.getMeasuredHeight()){						
						this.b_CanDecrease_m = false;
						this.b_CanIncrease_m = true;
					}
					else{
						this.b_CanDecrease_m = true;
						this.b_CanIncrease_m = true;
					}
				}
				else if ( this.e_ThumbType_m == THUMB_TYPE.HIGH ){
					this.maxBoundVector_m[0] = 0;
					this.maxBoundVector_m[1] =  this.containerView_m.getHeight() - (this.i_MinThumbPadding_m);
					if ( this.rectBoundsForDraw_m.top == 0){
						this.b_CanDecrease_m = true;
						this.b_CanIncrease_m = false;
					}
					else{
						this.b_CanDecrease_m = true;
						this.b_CanIncrease_m = true;
					}
				}
			}
		}

		public boolean isCanDecrease() {
			return b_CanDecrease_m;
		}

		public void setCanDecrease(boolean b_CanDecrease_m) {
			this.b_CanDecrease_m = b_CanDecrease_m;
		}

		public boolean isCanIncrease() {
			return b_CanIncrease_m;
		}

		public void setCanIncrease(boolean b_CanIncrease_m) {
			this.b_CanIncrease_m = b_CanIncrease_m;
		}

		public Drawable getThumbDrawable() {
			return thumbDrawable_m;
		}

		public void setThumbDrawable(Drawable thumbDrawable_m) {
			this.thumbDrawable_m = thumbDrawable_m;
		}

		public int getMinThumbPadding() {
			return i_MinThumbPadding_m;
		}

		public void setMinThumbPadding(int i_MinThumbPadding_m) {
			this.i_MinThumbPadding_m = i_MinThumbPadding_m;
		}

		public int getOrientation() {
			return i_Orientation_m;
		}

		public void setOrientation(int i_Orientation_m) {
			this.i_Orientation_m = i_Orientation_m;
		}
		

		public View getContainerView() {
			return containerView_m;
		}

		public void setContainerView(View containerView_m) {
			this.containerView_m = containerView_m;    		
		}

		public int getThumbOffset() {
			return i_ThumbOffset_m;
		}

		public void setThumbOffset(int i_ThumbOffset_m) {
			this.i_ThumbOffset_m = i_ThumbOffset_m;
		}
    	
    }
    public enum THUMB_TYPE{
		LOW, HIGH;
	}
    

}
