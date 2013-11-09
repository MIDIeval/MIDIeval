package com.beta.UIControls;

/*
 * Copyright (C) 2006 The Android Open Source Project
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
 * 
 * All the above conditions hold 
 * Addendum: The following source code has been analyzed and modified from the 
 * Android source code for progress bar.
 * 1) The follwing progress bar is only a linear type
 * 2) It supports both the 'horizontal' and 'vertical' orientation by virtue
 *    of the xml orientation_progressbar attribute
 * 3) It also supports 'single' progress, eg) %of completion and 'dual' progress
 *    eg) range of values in both modes mentioned above
 * 4) Be cautious in setting width and height of the view during layout, keeping 
 *    consideration, the orientation  
 */
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;

import com.customcontrol.seekbar.R;

public class CProgressBar extends View {
	private static final int MAX_LEVEL = 10000;

	private long i_ThreadID_m;

	private int i_Orientation_m; // 0:Horizontal; 1:Vertical
	private int i_ProgressType_m; // 0:Single ; 1:Dual

	private int i_Max_m;
	private int i_Min_m;
	private int[] i_Progress_m;
	protected int i_MinWidth_m;
	protected int i_MaxWidth_m;
	protected int i_MinHeight_m;
	protected int i_MaxHeight_m;
	private int i_MinMaxPadding_m;

	protected enum DUAL_PROGRESS_MOVE_TOWARDS {
		MIN, MAX
	}

	protected DUAL_PROGRESS_MOVE_TOWARDS e_InConflictMoveTowards_m = DUAL_PROGRESS_MOVE_TOWARDS.MAX;
	private boolean b_NoInvalidate_m;
	private Bitmap bitmapProgress_m;

	private Drawable progressDrawable_m;
	private Drawable currentDrawable_m;

	public CProgressBar(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}

	public CProgressBar(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, R.style.CProgressBarStyle);
		// Common layout parameters between both orientations and progress type

	}

	public CProgressBar(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
		// Fetch current Thread id
		this.i_ThreadID_m = Thread.currentThread().getId();
		this.fn_InitializeCustomProgressBar();
		TypedArray attributeSetSpecified_f = context.getTheme()
				.obtainStyledAttributes(attributeSet, R.styleable.CProgressBar,
						0, 0);
		try {
			// Fetch the attributes and change behavior accordingly
			this.setOrientation(attributeSetSpecified_f.getInteger(
					R.styleable.CProgressBar_orientation_progressbar, 0));
			this.setProgressType_m(attributeSetSpecified_f.getInteger(
					R.styleable.CProgressBar_progresstype_progressbar, 0));
			// Based on Orientation and ProgressType, select the appropriate
			// progressDrawable to Draw
			switch (this.getOrientation()) {
			case 0:
				switch (this.getProgressType()) {
				case 0:
					this.progressDrawable_m = getResources().getDrawable(
							R.drawable.seekbar_progress_drawable);
					break;
				case 1:
					this.progressDrawable_m = getResources().getDrawable(
							R.drawable.seekbar_progress_dual_drawable);
					break;
				}
				break;
			case 1:
				switch (this.getProgressType()) {
				case 0:
					this.progressDrawable_m = getResources().getDrawable(
							R.drawable.seekbar_progress_vertical_drawable);
					break;
				case 1:
					this.progressDrawable_m = getResources().getDrawable(
							R.drawable.seekbar_progress_dual_vertical_drawable);
					break;
				}
				break;
			}
			// If Progress type is Single, then, progress is a single entity
			if (this.getProgressType() == 0)
				this.setProgress(new int[1]);
			// If Progress type is dual, then, progress is 2 values, Low and
			// High
			// In this case, progress[0] is lower progress and progress[1] is
			// higher progress
			else if (this.getProgressType() == 1)
				this.setProgress(new int[2]);
			this.fn_InitializeProgressBar();

			b_NoInvalidate_m = true;
			// If specified explicity, use the XML specified progressDrawable in
			// the attributeSet
			Drawable drawableObj_f = attributeSetSpecified_f
					.getDrawable(R.styleable.CProgressBar_android_progressDrawable);
			if (drawableObj_f != null) {
				drawableObj_f = this.fn_Tileify(drawableObj_f, false, -1);
			} else {
				drawableObj_f = this.fn_Tileify(progressDrawable_m, false, -1);
			}
			this.fn_SetProgressDrawable(drawableObj_f);
			this.i_MinWidth_m = attributeSetSpecified_f.getInt(
					R.styleable.CProgressBar_android_minWidth, i_MinWidth_m);
			this.i_MaxWidth_m = attributeSetSpecified_f.getInt(
					R.styleable.CProgressBar_android_maxWidth, i_MaxWidth_m);
			this.i_MinHeight_m = attributeSetSpecified_f.getInt(
					R.styleable.CProgressBar_android_minHeight, i_MinHeight_m);
			this.i_MaxHeight_m = attributeSetSpecified_f.getInt(
					R.styleable.CProgressBar_android_maxHeight, i_MaxHeight_m);

			this.fn_SetMax(attributeSetSpecified_f.getInt(
					R.styleable.CProgressBar_android_max, this.getMax()));
			// If Progress is type is dual, set also the minimum value
			if (this.getProgressType() == 1) {
				this.fn_SetMin(attributeSetSpecified_f.getInt(
						R.styleable.CProgressBar_min, this.getMin()));
			}

			if (this.getProgressType() == 1) {
				// Fetch Dual Progress parameters
				// MinMaxPadding controls the minimum difference between
				// ProgressLow and ProgressHigh
				this.setMinMaxPadding(attributeSetSpecified_f.getInt(
						R.styleable.CProgressBar_min_max_padding,
						this.getMinMaxPadding()));
				int i_Progress_High = attributeSetSpecified_f.getInt(
						R.styleable.CProgressBar_progress_high,
						this.getProgress()[1]);
				int i_Progress_Low = attributeSetSpecified_f.getInt(
						R.styleable.CProgressBar_progress_low,
						this.getProgress()[0]);
				this.fn_SetProgress(new int[] { i_Progress_Low, i_Progress_High });
			}
			if (this.getProgressType() == 0) {
				int i_Progress_f = attributeSetSpecified_f.getInt(
						R.styleable.CProgressBar_android_progress,
						this.i_Progress_m[0]);
				this.fn_SetProgress(new int[] { i_Progress_f });
			}
			this.b_NoInvalidate_m = false;

		} finally {
			attributeSetSpecified_f.recycle();
		}

	}

	// Class Defined functions

	/*
	 * Function: fn_InitializeProgressBarFunctionality: To initialize the
	 * progress bar to default valuesAuthor: Hrishik MishraSource: GrepCode,
	 * Android Source
	 */
	public void fn_InitializeProgressBar() {
		this.setMax(100);
		if (this.getProgressType() == 1) {
			this.setMin(0);
			this.setMinMaxPadding(10);
		}

		if (this.getProgressType() == 0) {
			this.getProgress()[0] = 0;
		}
		if (this.getProgressType() == 1) {
			this.getProgress()[0] = 0;
			this.getProgress()[1] = 0;

		}

		this.i_MinWidth_m = 24;
		this.i_MaxWidth_m = 48;
		this.i_MinHeight_m = 24;
		this.i_MaxHeight_m = 48;

	}

	/*
	 * Function: fn_InitializeCustomProgressBarFunctionality: Set customizable
	 * parameters to default values. eg) Orientation, progress typeAuthor:
	 * Hrishik Mishra
	 */
	public void fn_InitializeCustomProgressBar() {
		this.setOrientation(0);
		this.setProgressType_m(0);
	}

	/*
	 * Function: fn_TileifyFuncationality: Converts a drawable to a tiled
	 * version of itself. It will recursively traverse layer and state list
	 * drawables.Author: Hrishik MishraSource: Android Source,
	 * https://joggers-music-app.googlecode.com
	 */
	public Drawable fn_Tileify(Drawable drawable, boolean clip, int clipDirection) {		
		if (drawable instanceof LayerDrawable) {
			LayerDrawable background_f = (LayerDrawable) drawable;
			final int i_N_f = background_f.getNumberOfLayers();// Total number
																// of layers in
																// the drawable
																// object
			Drawable[] outDrawables_f = new Drawable[i_N_f];
			for (int iCount = 0; iCount < i_N_f; iCount++) {
				int i_ID_f = background_f.getId(iCount);
				if ( i_ID_f == android.R.id.progress )
					clipDirection = (this.getOrientation() == 0 )?Gravity.LEFT:Gravity.BOTTOM;
				else if ( i_ID_f == R.id.progress_high )
					clipDirection = (this.getOrientation() == 0)? Gravity.RIGHT:Gravity.TOP;
				outDrawables_f[iCount] = this.fn_Tileify(
						background_f.getDrawable(iCount),
						(i_ID_f == android.R.id.progress || i_ID_f  == R.id.progress_high), clipDirection);
			}

			LayerDrawable newBackground_f = new LayerDrawable(outDrawables_f);
			for (int iCount = 0; iCount < i_N_f; iCount++) {
				newBackground_f.setId(iCount, background_f.getId(iCount));
			}
			return newBackground_f;
		} else if (drawable instanceof BitmapDrawable
				|| drawable instanceof NinePatchDrawable) {
			if (drawable instanceof BitmapDrawable) {
				final Bitmap bitmapProgress_f = ((BitmapDrawable) drawable)
						.getBitmap();
				if (this.bitmapProgress_m == null) {
					this.bitmapProgress_m = bitmapProgress_f;
				}
				final ShapeDrawable shapeDrawable_f = new ShapeDrawable(
						this.fn_GetDrawableShape());
				final BitmapShader bitmapShader_f = new BitmapShader(
						bitmapProgress_f, Shader.TileMode.REPEAT,
						Shader.TileMode.CLAMP);
				shapeDrawable_f.getPaint().setShader(bitmapShader_f);
				if (this.getProgressType() == 0) {
					// If Progress is single, we need only 1 ClipDrawable to
					// render the required progress
					// Only 1 ClipDrawable showing the progress amount is sized
					return (clip) ? new ClipDrawable(drawable,(getOrientation() == 0) ? Gravity.LEFT : Gravity.BOTTOM,
							(getOrientation() == 0) ? ClipDrawable.HORIZONTAL
									: ClipDrawable.VERTICAL) : drawable;
				} else {
					// For Dual Progress, we need 2 ClipDrawables to render the
					// required progress
					// In this case, the progress Marker image is thrown as the
					// background and
					// 2 ClipDrawables for the foreground section on either
					// sides operate
					return (clip) ? new ClipDrawable(drawable,clipDirection,(getOrientation() == 0) ? ClipDrawable.HORIZONTAL: ClipDrawable.VERTICAL) : drawable;
				}
			}
		}
		return drawable;
	}

	private Shape fn_GetDrawableShape() {
		final float[] roundedCorners_f = new float[] { 5, 5, 5, 5, 5, 5, 5, 5 };
		return new RoundRectShape(roundedCorners_f, null, null);
	}

	public void fn_SetProgressDrawable(Drawable drawable) {
		if (drawable != null) {
			drawable.setCallback(this);
		}
		this.progressDrawable_m = drawable;
		this.currentDrawable_m = drawable;
		postInvalidate();
	}

	/*
	 * Function: fn_SetMaxFunctionality: To allow programmatic setting of Max
	 * value
	 * 
	 * @1: maximum value for progressAuthor: Hrishik Mishra
	 */
	public void fn_SetMax(int max) {
		if (max < 0) {
			max = 0;
		}
		if (max != this.getMax()) {
			this.setMax(max);			
			if ( this.getProgressType() == 1){
				if (this.getProgress()[1] > max) {	
					this.getProgress()[1] = max;
				}
			}
			postInvalidate();
		}
	}

	/*
	 * Function: fn_SetMinFunctionality: To allow programmatic setting of Min
	 * value
	 * 
	 * @1: minimum value for progressAuthor: Hrishik Mishra
	 */
	public void fn_SetMin(int min) {
		if (min < 0) {
			min = 0;
		}
		if (min != this.getMin()) {
			this.setMin(min);
			postInvalidate();
		}
		if (this.getProgress()[0] < min) {
			this.getProgress()[0] = min;
		}
	}

	/*
	 * Function: fn_SetProgressFunctionality: To allow programmatic setting of
	 * progress value(s)
	 * 
	 * @1: current value for progressAuthor: Hrishik Mishra
	 */
	public void fn_SetProgress(int[] progress) {
		this.fn_SetProgress(progress, false);
	}

	public interface IThumbConflictListener {
		void onConflictingProgress(int progress[]);
	}

	private IThumbConflictListener thumbConflictListener_m;

	/*
	 * Function: fn_SetProgressFunctionality: To allow programmatic setting of
	 * progress value(s)
	 * 
	 * @1: current value for progress
	 * 
	 * @2: Is it user inputAuthor: Hrishik Mishra
	 */
	synchronized public void fn_SetProgress(int[] progress, boolean isUserInput) {
		// Check border values for the Single progress value and refresh the
		// progressDrawable accordingly
		if (this.getProgressType() == 0) {
			if (progress[0] < 0) {
				progress[0] = 0;
			}
			if (progress[0] > this.getMax()) {
				progress[0] = this.getMax();
			}
			if (progress[0] != this.getProgress()[0]) {
				this.getProgress()[0] = progress[0];
				this.fn_RefreshProgress(android.R.id.progress,
						this.getProgress(), isUserInput);
			}
		} else if (this.getProgressType() == 1) {
			// Check border values for the dual type progress bar.
			// In this case, there shall be at least a minimum specified padding
			// between both the values barring which, logical setting has to be
			// done
			// By default, the correction movement is towards, MIN side of the
			// bar.
			// eg) If progress values of 60 and 40 and provided as low and high
			// respectively,
			// It has to be reset to 30 and 40.
			if (progress[0] < this.getMin()) {
				progress[0] = this.getMin();
				this.e_InConflictMoveTowards_m = DUAL_PROGRESS_MOVE_TOWARDS.MAX;
				if (this.thumbConflictListener_m != null) {
					this.thumbConflictListener_m.onConflictingProgress(progress);
				}
			}
			if (progress[1] > this.getMax()) {
				progress[1] = this.getMax();
				this.e_InConflictMoveTowards_m = DUAL_PROGRESS_MOVE_TOWARDS.MIN;
				if (this.thumbConflictListener_m != null) {
					this.thumbConflictListener_m
							.onConflictingProgress(progress);
				}
			}

			if (progress[1] > this.getMax()) {
				progress[0] = progress[1] - this.getMinMaxPadding();
			} else if (progress[0] < this.getMin()) {
				progress[1] = progress[0] + this.getMinMaxPadding();
			} else if (progress[0] + this.getMinMaxPadding() > progress[1]) {

				if (this.e_InConflictMoveTowards_m == DUAL_PROGRESS_MOVE_TOWARDS.MIN) {
					progress[0] = progress[0] - 1;
				} else if (this.e_InConflictMoveTowards_m == DUAL_PROGRESS_MOVE_TOWARDS.MAX) {
					progress[1] = progress[1] + 1;
				}
				this.fn_SetProgress(new int[] { progress[0], progress[1] });
				if (this.thumbConflictListener_m != null) {
					this.thumbConflictListener_m
							.onConflictingProgress(progress);
				}
			}
			else if (progress[0] != this.getProgress()[0] || progress[1] != this.getProgress()[1]) {
				this.getProgress()[0] = progress[0];
				this.getProgress()[1] = progress[1];
				this.fn_RefreshProgress(android.R.id.progress, this.getProgress(),isUserInput);
				Log.i("PROGRESS BAR", "<------------" + "Progress[0]: "	+ progress[0] + ",  Progress[1]: " + progress[1]+ "------------>");
			}
		}
	}

	private class RefreshProgressRunnable implements Runnable {

		private int i_Id_m;
		private int[] i_Progress_m;
		private boolean b_FromUser_m;

		RefreshProgressRunnable(int id, int[] progress, boolean fromUser) {
			i_Id_m = id;
			i_Progress_m = progress;
			b_FromUser_m = fromUser;
		}

		public void run() {
			fn_DoRefreshProgress(i_Id_m, i_Progress_m, b_FromUser_m);
			// Put ourselves back in the cache when we are done
			refreshProgressRunnable_m = this;
		}

		public void setup(int id, int[] progress, boolean fromUser) {
			i_Id_m = id;
			i_Progress_m = progress;
			b_FromUser_m = fromUser;
		}

	}

	private RefreshProgressRunnable refreshProgressRunnable_m;

	private synchronized void fn_RefreshProgress(int id, int[] progress,
			boolean isUserInput) {
		if (this.i_ThreadID_m == Thread.currentThread().getId()) {
			this.fn_DoRefreshProgress(id, progress, isUserInput);
		} else {
			RefreshProgressRunnable refreshRunnable_f;
			if (this.refreshProgressRunnable_m != null) {
				refreshRunnable_f = this.refreshProgressRunnable_m;
				this.refreshProgressRunnable_m = null;
				refreshRunnable_f.setup(id, progress, isUserInput);
			} else {
				refreshRunnable_f = new RefreshProgressRunnable(id, progress,
						isUserInput);
			}
			post(refreshRunnable_f);
		}
	}

	private synchronized void fn_DoRefreshProgress(int id, int[] progress,
			boolean isUserInput) {
		
		
		if (this.getProgressType() == 0) {
			float[] scale = new float[] { this.getMax() > 0 ? (float) progress[0]
					/ (float) getMax()
					: 0 };
			final Drawable drawable_f = this.getCurrentDrawable();
			if (drawable_f != null) {
				Drawable progressDrawable = null;

				if (drawable_f instanceof LayerDrawable) {
					progressDrawable = ((LayerDrawable) drawable_f)
							.findDrawableByLayerId(id);
				}

				final int level = (int) (scale[0] * MAX_LEVEL);
				(progressDrawable != null ? progressDrawable : drawable_f)
						.setLevel(level);
			} else {
				invalidate();
			}

			if (id == android.R.id.progress || id == R.id.progress_high) {
				onProgressRefresh(scale, isUserInput);
			}
		}
		if (this.getProgressType() == 1) {
			// For dual progress type, set 2 scales for high and low values
			float scaleLow_f = (this.getMax() > 0 && this.getMin() >= 0) ? (float) (progress[0] - this.getMin()) / (float) (getMax() - this.getMin()) : 0;
			float scaleHigh_f = (this.getMax() > 0 && this.getMin() >= 0) ? (float) (this.getMax() - progress[1]) / (float) (getMax() - this.getMin()) : 0;
			final Drawable drawable_f = this.getCurrentDrawable();
			if (drawable_f != null) {
				ClipDrawable[] outDrawables_f = null;
				if (drawable_f instanceof LayerDrawable) {
					final int i_N_f = ((LayerDrawable)drawable_f).getNumberOfLayers();// Total number of layers in the drawable object
					outDrawables_f = new ClipDrawable[2];		
					int clipCounter_f = 0;
					// Fetch the Layers for progressType Dual
					for (int iCount = 0; iCount < i_N_f; iCount++) {
						if (((LayerDrawable) drawable_f).getId(iCount) == android.R.id.progress
								|| ((LayerDrawable) drawable_f).getId(iCount) == R.id.progress_high){
							outDrawables_f[clipCounter_f] = (ClipDrawable)((LayerDrawable)drawable_f).getDrawable(iCount);
							clipCounter_f++;
						}						
					}
				}

				final int levelLow_f = (int) (scaleLow_f * MAX_LEVEL);
				final int levelHigh_f = (int) (scaleHigh_f * MAX_LEVEL);
				(outDrawables_f[0] != null ? outDrawables_f[0] : drawable_f).setLevel(levelLow_f);
				(outDrawables_f[1] != null ? outDrawables_f[1] : drawable_f).setLevel(levelHigh_f);
				Log.d("PROGRESS BAR", "LEVEL LOW: " + levelLow_f + " , LEVEL HIGH: " + levelHigh_f);
			} else {
				invalidate();
			}

			if (id == android.R.id.progress || id == R.id.progress_high) {
				onProgressRefresh(new float[] { scaleLow_f, scaleHigh_f },isUserInput);
			}
		}
	}

	protected void onProgressRefresh(float[] scale, boolean isUserInput) {
		// TODO Auto-generated method stub

	}

	public Drawable getProgressDrawable() {
		return progressDrawable_m;
	}

//	public void setProgressDrawable(Drawable progressDrawable_m) {
//		if (progressDrawable_m != null)
//			progressDrawable_m.setCallback(this);
//		this.progressDrawable_m = progressDrawable_m;
//		this.setCurrentDrawable(progressDrawable_m);
//		postInvalidate();
//	}

	public Drawable getCurrentDrawable() {
		return currentDrawable_m;
	}

	public void setCurrentDrawable(Drawable currentDrawable) {
		this.currentDrawable_m = currentDrawable;
	}

	// Increment decrement functions to programmatically modify progress
	// value(s)
	public synchronized final void fn_IncrementProgressBy(int difference) {
		if (this.getProgressType() == 0)
			this.setProgress(new int[] { this.i_Progress_m[0] + difference });
	}

	public synchronized final void fn_IncrementProgressLowBy(int difference) {
		if (this.getProgressType() == 1) {
			this.setProgress(new int[] { this.i_Progress_m[0] + difference,
					this.i_Progress_m[1] });
		}
	}

	public synchronized final void fn_IncrementProgressHighBy(int difference) {
		if (this.getProgressType() == 1) {
			this.setProgress(new int[] { this.i_Progress_m[0],
					this.i_Progress_m[1] + difference });
		}
	}

	// Override functions
	@Override
	protected boolean verifyDrawable(Drawable who) {
		return who == this.progressDrawable_m || super.verifyDrawable(who);
	}

	@Override
	public void setVisibility(int v) {
		if (getVisibility() != v) {
			super.setVisibility(v);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		// onDraw will translate the canvas so we draw starting at 0,0
		int right = w - this.getPaddingLeft() - this.getPaddingRight();
		int bottom = h - this.getPaddingBottom() - this.getPaddingTop();
		if (this.getOrientation() == 0) {
			bottom = Math.min(bottom,
					this.currentDrawable_m.getIntrinsicHeight());
		} else if (this.getOrientation() == 1) {
			right = Math
					.min(right, this.currentDrawable_m.getIntrinsicWidth());
		}
		if (this.progressDrawable_m != null) {
			progressDrawable_m.setBounds(0, 0, right, bottom);
		}
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		Drawable drawable_f = this.currentDrawable_m;

		int dw = 0;
		int dh = 0;
		if (drawable_f != null) {
			dw = Math
					.max(this.i_MinWidth_m,
							Math.min(this.i_MaxWidth_m,
									drawable_f.getIntrinsicWidth()));
			dh = Math.max(
					this.i_MinHeight_m,
					Math.min(this.i_MaxHeight_m,
							drawable_f.getIntrinsicHeight()));
		}
		dw += this.getPaddingLeft() + this.getPaddingRight();
		dh += this.getPaddingTop() + this.getPaddingTop();

		setMeasuredDimension(resolveSize(dw, widthMeasureSpec),
				resolveSize(dh, heightMeasureSpec));
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Drawable drawable_f = this.currentDrawable_m;
		if (drawable_f != null) {
			drawable_f.draw(canvas);
		}
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();

		int[] state = getDrawableState();

		if (this.progressDrawable_m != null
				&& this.progressDrawable_m.isStateful()) {
			this.progressDrawable_m.setState(state);
		}

	}

	static class SavedState extends BaseSavedState {
		int[] i_Progress_m;

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
			in.readIntArray(this.i_Progress_m);

		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeIntArray(this.i_Progress_m);
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

		ss.i_Progress_m = i_Progress_m;
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());

		this.fn_SetProgress(ss.i_Progress_m);
	}

	// Property set and get functions
	public synchronized int[] getProgress() {
		return i_Progress_m;
	}

	public void setProgress(int[] i_Progress_m) {
		this.i_Progress_m = i_Progress_m;
	}

	public synchronized int getMax() {
		return i_Max_m;
	}

	public synchronized void setMax(int max) {
		if (max < 0) {
			max = 0;
		}
		if (max != this.i_Max_m) {
			this.i_Max_m = max;
			postInvalidate();
			if (this.getProgressType() == 0) {
				if (this.i_Progress_m[0] > max) {
					this.i_Progress_m[0] = max;
				}
			} else if (this.getProgressType() == 1) {
				if (this.i_Progress_m[1] > max) {
					this.e_InConflictMoveTowards_m = DUAL_PROGRESS_MOVE_TOWARDS.MIN;
					this.i_Progress_m[1] = max;
					this.i_Progress_m[0] = max - this.getMinMaxPadding();
				}

			}
		}

	}

	public int getOrientation() {
		return i_Orientation_m;
	}

	public void setOrientation(int i_Orientation_m) {
		this.i_Orientation_m = i_Orientation_m;
	}

	public int getProgressType() {
		return i_ProgressType_m;
	}

	public void setProgressType_m(int i_ProgressType_m) {
		this.i_ProgressType_m = i_ProgressType_m;
	}

	public int getMin() {
		return i_Min_m;
	}

	public void setMin(int i_Min_m) {
		this.i_Min_m = i_Min_m;
	}

	public IThumbConflictListener getThumbConflictListener() {
		return thumbConflictListener_m;
	}

	public void setThumbConflictListener(
			IThumbConflictListener thumbConflictListener_m) {
		this.thumbConflictListener_m = thumbConflictListener_m;
	}

	public int getMinMaxPadding() {
		return i_MinMaxPadding_m;
	}

	public void setMinMaxPadding(int i_MinMaxPadding_m) {
		this.i_MinMaxPadding_m = i_MinMaxPadding_m;
	}
}
