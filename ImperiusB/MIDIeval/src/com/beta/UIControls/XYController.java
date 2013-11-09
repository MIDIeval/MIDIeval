package com.beta.UIControls;

import java.util.HashMap;
import java.util.Queue;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;

import com.beta.Controllability.ControlValuePacket;
import com.beta.Controllability.ControllerMode;
import com.beta.Controllability.IController;
import com.beta.activities.R;

/*Class: XYController
 *Function: Allows the user to interact with an XY-grid layout to control parameters 
 *Author: Hrishik Mishra 
 */
public class XYController extends UIController implements
		GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

	// Private Member for shape rendering process
	private Canvas canvasObj_m;
	private Paint paintObj_m;
	private Color paintColorObj_m;
	private Rect[] rectArray_m;
	private float[] originGridLineCoordArray_m;
	private Paint viewBorderPaintObj_m;
	private Paint secondGridPaintObj_m;
	private Rect borderRectangle_m;
	private int i_LayoutGridSideLength_m;
	private Paint paintRadialGradient_m;

	private enum GRIDLINE_TYPE {
		PRIMARY, SECONDARY, TERTIARY, QUARTERNARY, PENTENARY
	}

	private GRIDLINE_TYPE gridLineType_m = GRIDLINE_TYPE.PRIMARY;
	private RadialGradient radialGradientBackground_f;
	private Color[] gradientColorSet_m[];
	private Paint paintObjOnTouch_m;
	private RadialGradient radialGradientForOnTouch_f;
	float[] d_XYCoordinates_m = new float[2];
	private boolean b_IsNotRendered = true;

	// Constants used in rendering process
	private static final int i_SECONDARY_GRID_HALFCOUNT_m = 5;
	private static final int i_TERTIARY_GRID_HALFCOUNT_m = 10;
	private static final int i_QUARTERNARY_GRID_HALFCOUNT_m = 20;
	private static final int i_PENTENARY_GRID_HALFCOUNT_m = 40;

	// Private Member mapped to XML attribute list
	private int i_LayoutHeight_m;
	private int i_LayoutWidth_m;
	private int i_YMax_m;
	private int i_YMin_m;
	private int i_XMax_m;
	private int i_XMin_m;
	private boolean b_IsMultiTouch_m;
	private Origin e_Origin_m = Origin.CENTER;
	private int i_OriginX_m;
	private int i_OriginY_m;
	private String s_FramePicturePath_m;
	private String s_GridPicturePath_m;
	private int[] i_OffsetVector_m = new int[4];
	private int[] i_MeasureVector_m = new int[2];
	public int i_FlingSpeed_m = 10;// Controls the fling speed
	private boolean b_IsDoubleTapOdd_m = false;// true = odd number; false =
												// even number (0 is even)
	private boolean b_IsSingleTapOdd_m = false;// true = odd number; false =
												// even number (0 is even)

	private XYCurrentSettingDetails xyCurrentSettingDetails_m;
	// For events
	private VelocityTracker velocityTrackerObj_m;
	private GestureDetector gestureDectorObj_m;
	private static final int i_VELOCITY_THRESHOLD_FOR_FLING_m = 300;// in pixels
																	// per
																	// second
	private static final int i_DISTANCE_THRESHOLD_FOR_FLING_M = 120;// in pixels
																	// traversed
	private AsyncFlingEffectTask asyncTaskForFlingObj_m;

	// Const values to be used for custom view
	private static final int i_DEFAULT_LAYOUT_HEIGHT_m = 10;
	private static final int i_DEFAULT_LAYOUT_WIDTH_m = 10;
	private static final int i_Y_MAX_m = 10;
	private static final int i_Y_MIN_m = 10;
	private static final int i_X_MAX_m = 10;
	private static final int i_X_MIN_m = 10;
	private static final int i_ORIGIN_Y_m = 0;
	private static final int i_ORIGIN_X_m = 0;
	private static final boolean b_DEFAULT_MULTITOUCH_m = false;
	private static final String s_DEBUG_TAG_M = "XYCONTROLLER";

	// Application specific variables
	private static HashMap<Integer, ControllerMode> xyControllerMapObj_m;
	private boolean b_IsTouchOutsideX_m = false;
	private boolean b_IsTouchOutsideY_m = false;

	private class AsyncFlingEffectTask extends AsyncTask<Float, Void, Void> {
		private boolean b_IsToBeCancelled_m = false;
		private ControlValuePacket controlValuePacketAsyncFlingObj_m;
		private XYSubController e_XYSubController_m;

		@Override
		protected Void doInBackground(Float... Float) {
			// TODO Auto-generated method stub
			int i_CurrentValue_f = Float[0].intValue();
			int i_BorderValue_f = Float[1].intValue();
			int i_SubController_f = Float[2].intValue();
			float f_RawCoordinate_f = Float[3].floatValue();
			int[] i_ChannelVector_f = new int[0];
			if (i_SubController_f == XYSubController.FLING_X.getValue()) {
				e_XYSubController_m = XYSubController.FLING_X;
				i_ChannelVector_f = xyCurrentSettingDetails_m
						.getXChannelsVector();
			} else if (i_SubController_f == XYSubController.FLING_Y.getValue()) {
				i_ChannelVector_f = xyCurrentSettingDetails_m
						.getYChannelsVector();
				e_XYSubController_m = XYSubController.FLING_Y;
			}
			int direction = -1;
			int i_AnimationCoord_f = (i_SubController_f == 5)?0:1;
			float f_AnimationCoord_f;
			int[] xy_range_f = (i_SubController_f == 5)?xyCurrentSettingDetails_m.getxRangeVector():xyCurrentSettingDetails_m.getyRangeVector();
			while (!b_IsToBeCancelled_m) {
				if (Math.abs(i_CurrentValue_f - i_BorderValue_f) == 0)// Float[1]
																		// is
																		// the
																		// Border
																		// value;
					break;
				direction = (i_CurrentValue_f > i_BorderValue_f ? -1: 1);
				//
						
				f_AnimationCoord_f	= fn_GetXForAnimation(fn_ReverseNormalize(fn_ReverseLinearCalculationForAnimation(i_CurrentValue_f + direction, 
						xy_range_f)));
				d_XYCoordinates_m[i_AnimationCoord_f] = (i_SubController_f == 5)?f_AnimationCoord_f:(i_LayoutGridSideLength_m - f_AnimationCoord_f);
				postInvalidate();
				for (int iCount = 0; iCount < i_ChannelVector_f.length; iCount++) {
					controlValuePacketAsyncFlingObj_m = fn_CreateControlValuePacket(
							e_XYSubController_m,
							(i_CurrentValue_f = i_CurrentValue_f
									+ (i_CurrentValue_f > i_BorderValue_f ? -1
											: 1)), i_ChannelVector_f[iCount]);
					Log.i(s_DEBUG_TAG_M,
							"Value to Write: "
									+ controlValuePacketAsyncFlingObj_m
											.getValueVector());
					IController.queueObj_m
							.offer(controlValuePacketAsyncFlingObj_m);
				}
				try {
					Thread.sleep(xyCurrentSettingDetails_m.getI_FlingSpeed());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			return null;
		}

	}

	// Static section
	static {
		XYController.xyControllerMapObj_m = new HashMap<Integer, ControllerMode>(
				5, 0.75f);
		// Initial capacity = 5, Load factor = 0.75f
		// Initialize the SubController map instance
		XYController.xyControllerMapObj_m.put(
				XYSubController.X_RANGE_CHANGE.getValue(),
				ControllerMode.CONTINUOUS);
		XYController.xyControllerMapObj_m.put(
				XYSubController.Y_RANGE_CHANGE.getValue(),
				ControllerMode.CONTINUOUS);
		XYController.xyControllerMapObj_m.put(
				XYSubController.DOUBLE_TAP.getValue(), ControllerMode.DIGITAL);
		XYController.xyControllerMapObj_m.put(
				XYSubController.SINGLE_TAP.getValue(), ControllerMode.DIGITAL);
		XYController.xyControllerMapObj_m.put(
				XYSubController.FLING_X.getValue(), ControllerMode.DIGITAL);
		XYController.xyControllerMapObj_m.put(
				XYSubController.FLING_Y.getValue(), ControllerMode.DIGITAL);
		XYController.xyControllerMapObj_m.put(
				XYSubController.ACTION_UP.getValue(), ControllerMode.DIGITAL);
	}

	// Constructor ( Parameterized )
	// For ADT to interact with the custom view, the class must have a
	// parameterized constructor
	// as following
	public XYController(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		// Set base class elements for application performance
		this.e_UIControllerType_m = UIControllerType.XY_CONTROLLER;

		this.fn_RendererInit();
		this.setPadding(12, 12, 12, 12);
		originGridLineCoordArray_m = new float[8];
		gestureDectorObj_m = new GestureDetector(this);
		TypedArray typeArrayObj_f = context.getTheme().obtainStyledAttributes(
				attributeSet, R.styleable.XYController, 0, 0);
		try {
			for (int iAttributeCount = 0; iAttributeCount < typeArrayObj_f
					.length(); iAttributeCount++) {
				switch (iAttributeCount) {
				case R.styleable.XYController_layout_height:
					// Manage height settings
					this.setLayoutHeight(typeArrayObj_f.getInteger(
							iAttributeCount, this.i_DEFAULT_LAYOUT_HEIGHT_m));
					break;
				case R.styleable.XYController_layout_width:
					// Manage width settings
					this.setLayoutWidth(typeArrayObj_f.getInteger(
							iAttributeCount, this.i_DEFAULT_LAYOUT_WIDTH_m));
					break;
				case R.styleable.XYController_yMax:
					// Set the Max value for Y-axis
					this.setYMax(typeArrayObj_f.getInteger(iAttributeCount,
							this.i_Y_MAX_m));
					break;
				case R.styleable.XYController_yMin:
					// Set the Min value for the Y-axis
					this.setYMin(typeArrayObj_f.getInteger(iAttributeCount,
							this.i_Y_MIN_m));
					break;
				case R.styleable.XYController_xMax:
					// Set the Max value for the X-axis
					this.setXMax(typeArrayObj_f.getInteger(iAttributeCount,
							this.i_X_MAX_m));
					break;
				case R.styleable.XYController_xMin:
					// Set the Min value for the X-axis
					this.setXMin(typeArrayObj_f.getInteger(iAttributeCount,
							this.i_X_MIN_m));
					break;
				case R.styleable.XYController_multiTouch:
					// Set if the XYController instance is multi-touch enabled
					this.setIsMultiTouch(typeArrayObj_f.getBoolean(
							iAttributeCount, this.b_DEFAULT_MULTITOUCH_m));
					break;
				case R.styleable.XYController_origin:
					// Set the origin position on the grid
					TypedValue typeObj_f = null;
					Origin originEnum_f = Origin.CENTER;
					int selectedOrigin_f = typeArrayObj_f.getInt(
							R.styleable.XYController_origin, 0);
					switch (selectedOrigin_f) {
					case 0:
						originEnum_f = Origin.CENTER;
						break;
					case 1:
						originEnum_f = Origin.LEFT_BOTTOM;
						break;
					case 2:
						originEnum_f = Origin.RIGHT_BOTTOM;
						break;
					case 3:
						originEnum_f = Origin.RIGHT_TOP;
						break;
					case 4:
						originEnum_f = Origin.LEFT_TOP;
						break;

					}
					break;
				case R.styleable.XYController_originX:
					// Set the manually defined origin for X
					this.setOriginX(typeArrayObj_f.getInteger(iAttributeCount,
							this.i_ORIGIN_X_m));
					break;
				case R.styleable.XYController_originY:
					// Set the manually defined origin for Y
					this.setOriginY(typeArrayObj_f.getInteger(iAttributeCount,
							this.i_ORIGIN_Y_m));
					break;
				case R.styleable.XYController_framePicturePath:
					// Set the image path for the frame of the grid
					this.setFramePicturePath(typeArrayObj_f
							.getString(iAttributeCount));
					break;
				case R.styleable.XYController_gridPicturePath:
					// Set the grid skin picture for the XY control
					this.setGridPicturePath(typeArrayObj_f
							.getString(iAttributeCount));
					break;
				default:
					// Do nothing
					break;

				}
			}

		} finally {

		}
	}

	/*
	 * Function: onFinishInflate() from View classIt is overridden here to allow
	 * the subController mapping to be initialised after Constructor call
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onFinishInflate()
	 */
	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
		this.fn_SetSubControllerMap();

	}

	long previousTime;

	// Event Handlers
	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event == null)
			return false;
		if (asyncTaskForFlingObj_m != null
				&& !asyncTaskForFlingObj_m.isCancelled()) {
			asyncTaskForFlingObj_m.b_IsToBeCancelled_m = true;
			asyncTaskForFlingObj_m.cancel(true);
		}
		int i_Action_f = MotionEventCompat.getActionMasked(event);
		int i_Index_f = event.getActionIndex();
		// Index to the Pointer to get Pressure, size etc information about
		// UP/DOWN events
		int i_Pointer_f = event.getPointerId(i_Index_f);// Pointer to the
														// information about
														// event.
		gestureDectorObj_m.onTouchEvent(event);
		boolean b_IsOutsideBounds_f = false;

		switch (i_Action_f) {
		case (MotionEvent.ACTION_DOWN):
			b_IsTouchOutsideX_m = false;
			b_IsTouchOutsideY_m = false;
			Log.d(s_DEBUG_TAG_M, "Action was down");

			if (this.velocityTrackerObj_m == null) {
				// Retreive a new velocity tracker to fetch velocity of motion
				this.velocityTrackerObj_m = VelocityTracker.obtain();
				// Recycle the object after processing, so that it can be reused
				// elsewhere
			} else {
				this.velocityTrackerObj_m.clear();// Reset the velocity tracker
													// instance to origin
			}
			return true;
		case (MotionEvent.ACTION_MOVE):
			// Log.d(s_DEBUG_TAG_M, "Action was move");
			// ComputeCurrentVelocity() fetches the current velocity of movement
			// This can be used to fetch invidual component velocities.
			this.velocityTrackerObj_m.addMovement(event);
			this.velocityTrackerObj_m.computeCurrentVelocity(1000);
			// 1: pixels per millisecond, 1000:pixels per second
			// It is advisable to use VelocityTrackerCompat static function,
			// although
			// velocityTracker object instance can be used for the same purpose
			// Log.d(s_DEBUG_TAG_M, "Velocity-X: " +
			// VelocityTrackerCompat.getXVelocity(velocityTrackerObj_m,
			// i_Pointer_f));
			// Log.d(s_DEBUG_TAG_M, "Velocity-Y: " +
			// VelocityTrackerCompat.getYVelocity(velocityTrackerObj_m,
			// i_Pointer_f));
			// For animation on touch down and up
			Log.i("EVENT TIME",
					String.valueOf(((System.nanoTime() - previousTime) / 1000000)));
			previousTime = System.nanoTime();
			this.d_XYCoordinates_m[0] = this.fn_GetX(event.getX(), 0 );
			this.d_XYCoordinates_m[1] = this.fn_GetY(event.getY(), 0);
			// Log.i("XY_CONTROLLER", "X," + d_XYCoordinates_m[0] + ",Y," +
			// d_XYCoordinates_m[1] );
			
			if (this.xyCurrentSettingDetails_m.b_IsXOn_m) {
				for (int iCount = 0; iCount < this.xyCurrentSettingDetails_m
						.getXChannelsVector().length; iCount++) {
					float valueX = this.fn_LinearCalculation(
							this.fn_Normalize(this.fn_GetX(event.getX(), 0)),
							this.xyCurrentSettingDetails_m.getxRangeVector());
					Log.i("XY_CONTROLLER", "X," + valueX);
					IController.queueObj_m.offer(this
							.fn_CreateControlValuePacket(
									XYSubController.X_RANGE_CHANGE, valueX,
									this.xyCurrentSettingDetails_m
											.getXChannelsVector()[iCount]));
				}
			}
			if (this.xyCurrentSettingDetails_m.b_IsYOn_m) {
				for (int iCount = 0; iCount < this.xyCurrentSettingDetails_m
						.getXChannelsVector().length; iCount++) {
					float valueY = this.fn_LinearCalculation(
							-this.fn_Normalize(this.fn_GetY(event.getY(), 0)),
							this.xyCurrentSettingDetails_m.getyRangeVector());
					Log.i("XY_CONTROLLER", "Y," + valueY);
					IController.queueObj_m.offer(this
							.fn_CreateControlValuePacket(
									XYSubController.Y_RANGE_CHANGE, valueY,
									this.xyCurrentSettingDetails_m
											.getXChannelsVector()[iCount]));
				}
			}
			

			this.d_XYCoordinates_m[0] = this.fn_GetX(event.getX(), Integer.MIN_VALUE);			
			this.d_XYCoordinates_m[1] = this.fn_GetY(event.getY(), Integer.MIN_VALUE);	
			// Log.i( "QUEUE_STATUS", "QUEUE SIZE: " +
			// IController.queueObj_m.size());

			invalidate();
			//
			return true;
		case (MotionEvent.ACTION_UP):
			Log.d(s_DEBUG_TAG_M, "Action was up");
			return true;
		case (MotionEvent.ACTION_OUTSIDE):
			Log.d(s_DEBUG_TAG_M, "Action was outside");
			return true;
		case (MotionEvent.ACTION_CANCEL):
			Log.d(s_DEBUG_TAG_M, "Action was aborted/cancelled");
			this.velocityTrackerObj_m.recycle();// To make it re-usable for
												// others.
		default:
			return super.onTouchEvent(event);
			// After ACTION_UP, velocities will be zero, so velocity should be
			// computed only
			// after ACTION_MOVE event is available

		}

	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.d(s_DEBUG_TAG_M, "Gesture was down");
		return false;
	}

	@Override
	public boolean onFling(MotionEvent firstMotion, MotionEvent endMotion,
			float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		if (this.xyCurrentSettingDetails_m.b_IsFlingOn_m) {
			if ((Math.abs(firstMotion.getX() - endMotion.getX())) > this.i_DISTANCE_THRESHOLD_FOR_FLING_M
					&& Math.abs(velocityX) > this.i_VELOCITY_THRESHOLD_FOR_FLING_m) {
				if (firstMotion.getX() > endMotion.getX()) {
					Log.d(s_DEBUG_TAG_M, "THIS IS X fling to left");
					asyncTaskForFlingObj_m = new AsyncFlingEffectTask();
					asyncTaskForFlingObj_m.execute(
							this.fn_LinearCalculation(this.fn_Normalize(this
									.fn_GetX(endMotion.getX(), 0)),
									this.xyCurrentSettingDetails_m
											.getxRangeVector()), this
									.fn_LinearCalculation(-1,
											this.xyCurrentSettingDetails_m
													.getxRangeVector()),
							(float) XYSubController.FLING_X.getValue(), endMotion.getX());
					// Put New control value packet into the queue
				} else {
					Log.d(s_DEBUG_TAG_M, "THIS IS X fling to right");
					asyncTaskForFlingObj_m = new AsyncFlingEffectTask();
					asyncTaskForFlingObj_m.execute(
							this.fn_LinearCalculation(this.fn_Normalize(this
									.fn_GetX(endMotion.getX(), 0)),
									this.xyCurrentSettingDetails_m
											.getxRangeVector()), this
									.fn_LinearCalculation(1,
											this.xyCurrentSettingDetails_m
													.getxRangeVector()),
							(float) XYSubController.FLING_X.getValue(), endMotion.getX());
					// Put New control value packet into the queue
				}
				return true;

			}
			if ((Math.abs(firstMotion.getY() - endMotion.getY())) > this.i_DISTANCE_THRESHOLD_FOR_FLING_M
					&& Math.abs(velocityY) > this.i_VELOCITY_THRESHOLD_FOR_FLING_m) {
				if (firstMotion.getY() > endMotion.getY()) {
					Log.d(s_DEBUG_TAG_M, "THIS IS Y fling to up");
					asyncTaskForFlingObj_m = new AsyncFlingEffectTask();
					asyncTaskForFlingObj_m.execute(
							this.fn_LinearCalculation(-this.fn_Normalize(this
									.fn_GetY(endMotion.getY(), 0)),
									this.xyCurrentSettingDetails_m
											.getyRangeVector()), this
									.fn_LinearCalculation(1,
											this.xyCurrentSettingDetails_m
													.getyRangeVector()),
							(float) XYSubController.FLING_Y.getValue(), endMotion.getY());
					// Put New control value packet into the queue
				} else {
					Log.d(s_DEBUG_TAG_M, "THIS IS Y fling to bottom");
					asyncTaskForFlingObj_m = new AsyncFlingEffectTask();
					asyncTaskForFlingObj_m.execute(
							this.fn_LinearCalculation(-this.fn_Normalize(this
									.fn_GetY(endMotion.getY(), 0)),
									this.xyCurrentSettingDetails_m
											.getyRangeVector()), this
									.fn_LinearCalculation(-1,
											this.xyCurrentSettingDetails_m
													.getyRangeVector()),
							(float) XYSubController.FLING_Y.getValue(), endMotion.getY());
					// Put New control value packet into the queue
				}
				return true;

			}
		}
		Log.d(s_DEBUG_TAG_M, "Gesture was fling not recognized");
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		Log.d(s_DEBUG_TAG_M, "Gesture was long press");

	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		Log.d(s_DEBUG_TAG_M, "Gesture was scroll");
		return false;

	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		Log.d(s_DEBUG_TAG_M, "Gesture was show press");
		// TODO Auto-generated method stub

	}
	private boolean b_IsDoubleTap_m = false;
	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
//		int i_Counter_f = 0;
//		while (true){
//			i_Counter_f++;
//			try {
//				Thread.sleep(1);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			if ( this.b_IsDoubleTap_m )
//				return false;
//			if ( i_Counter_f == 100)
//				break;
//			
//		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onSizeChanged(int, int, int, int) Author text:
	 * invoked during layout when the size has changed, In case, this was added
	 * recently, old values shall be 0
	 */
	@Override
	public void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {

	}

	@Override
	protected void onLayout(boolean isChanged, int left, int top, int right,
			int bottom) {

		this.i_OffsetVector_m[0] = left;
		this.i_OffsetVector_m[1] = top;
		this.i_OffsetVector_m[2] = left - this.i_MeasureVector_m[0];
		this.i_OffsetVector_m[3] = right - this.i_MeasureVector_m[1];
		int i_ViewWidth_f = MeasureSpec.getSize(right - 2 * left);
		int i_ViewHeight_f = MeasureSpec.getSize(bottom - 1 * top);
		int i_ViewSize_f = Math.min(i_ViewWidth_f, i_ViewHeight_f);
		setLayoutGridSideLength(i_ViewSize_f);
		// Store the measured dimensions suggested by the Android framework
		originGridLineCoordArray_m[0] = i_ViewSize_f / 2 + left;
		originGridLineCoordArray_m[1] = top;
		originGridLineCoordArray_m[2] = i_ViewSize_f / 2 + left;
		originGridLineCoordArray_m[3] = i_ViewSize_f + top;

		originGridLineCoordArray_m[4] = left;
		originGridLineCoordArray_m[5] = i_ViewSize_f / 2 + top;
		originGridLineCoordArray_m[6] = i_ViewSize_f + left;
		originGridLineCoordArray_m[7] = i_ViewSize_f / 2 + top;
		this.borderRectangle_m.set(left, top, i_ViewSize_f + left, i_ViewSize_f
				+ top);
		this.d_XYCoordinates_m[0] = i_ViewSize_f / 2 + left;
		this.d_XYCoordinates_m[1] = i_ViewSize_f / 2 + top;
		radialGradientBackground_f = new RadialGradient(
				(i_ViewSize_f / 2 + left) / 1.0f,
				(i_ViewSize_f / 2 + left) / 1.0f, (int) (Math.sqrt(2)
						* i_ViewSize_f / 2), 0xFFD5F6FF, 0xFF2C89A0,
				Shader.TileMode.CLAMP);

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		this.b_IsNotRendered = true;
		int i_ParentWidth_f = MeasureSpec.getSize(widthMeasureSpec);
		int i_ParentHeight_f = MeasureSpec.getSize(heightMeasureSpec);
		int i_ViewSizde_f = Math.min(i_ParentWidth_f, i_ParentHeight_f);
		this.i_MeasureVector_m[0] = i_ViewSizde_f;
		this.i_MeasureVector_m[1] = i_ViewSizde_f;
		originGridLineCoordArray_m[0] = i_ViewSizde_f / 2;
		originGridLineCoordArray_m[1] = 0;
		originGridLineCoordArray_m[2] = i_ViewSizde_f / 2;
		originGridLineCoordArray_m[3] = i_ViewSizde_f;

		originGridLineCoordArray_m[4] = 0;
		originGridLineCoordArray_m[5] = i_ViewSizde_f / 2;
		originGridLineCoordArray_m[6] = i_ParentWidth_f;
		originGridLineCoordArray_m[7] = i_ViewSizde_f / 2;
		// Store the measured dimensions suggested by the Android framework
		this.setMeasuredDimension(i_ViewSizde_f, i_ViewSizde_f);

	}

	@Override
	protected void onDraw(Canvas canvas) {

		canvasObj_m = canvas;// This has to be checked later on. PLEASE DO!!!!

		super.onDraw(canvas);

		// if ( this.isLayoutRequested() || this.b_IsNotRendered ){
		int i_NumberSquare_f = 5;
		// canvas.drawRect(0.0f, 0.0f, 100.0f, 100.0f, paintObj_m);
		// this.paintRadialGradient_m.setShader(this.radialGradientBackground_f);
		this.paintRadialGradient_m.setShader(this.radialGradientBackground_f);
		// canvas.drawPaint(this.paintRadialGradient_m);
		canvas.drawRect(this.borderRectangle_m, this.paintRadialGradient_m);

		this.fn_RenderGridLines(GRIDLINE_TYPE.PRIMARY, canvas);
		this.fn_RenderGridLines(GRIDLINE_TYPE.SECONDARY, canvas);
		this.fn_RenderGridLines(GRIDLINE_TYPE.TERTIARY, canvas);
		this.fn_RenderGridLines(GRIDLINE_TYPE.QUARTERNARY, canvas);
		this.fn_RenderGridLines(GRIDLINE_TYPE.PENTENARY, canvas);
		this.b_IsNotRendered = false;
		// }

		this.canvasObj_m.drawCircle(this.d_XYCoordinates_m[0],
				this.d_XYCoordinates_m[1], 30, paintObjOnTouch_m);

	}

	private void fn_RenderGridLineInLoop(int gridLineCount, Canvas canvas) {
		fn_RenderGridLinesVerticalInLoop(gridLineCount, canvas, 1);// To the
																	// grid-left
																	// of the
																	// PRIMARY
																	// grid
																	// vertical
		fn_RenderGridLinesVerticalInLoop(gridLineCount, canvas, -1);// To the
																	// grid-right
																	// the
																	// PRIMARY
																	// grid
																	// vertical
		fn_RenderGridLinesHorizontalInLoop(gridLineCount, canvas, 1);// To the
																		// grid-top
																		// of
																		// the
																		// PRIMARY
																		// grid
																		// horizontal
		fn_RenderGridLinesHorizontalInLoop(gridLineCount, canvas, -1);// To the
																		// grid-top
																		// of
																		// the
																		// PRIMARY
																		// grid
																		// horizontal
	}

	private void fn_RenderGridLines(GRIDLINE_TYPE gridLineType, Canvas canvas) {
		switch (gridLineType) {
		case PRIMARY:

			canvas.drawLine(this.originGridLineCoordArray_m[0],
					this.originGridLineCoordArray_m[1],
					this.originGridLineCoordArray_m[2],
					this.originGridLineCoordArray_m[3], paintObj_m);
			canvas.drawLine(this.originGridLineCoordArray_m[4],
					this.originGridLineCoordArray_m[5],
					this.originGridLineCoordArray_m[6],
					this.originGridLineCoordArray_m[7], paintObj_m);
			break;
		case SECONDARY:
			this.secondGridPaintObj_m.setAlpha(50);
			fn_RenderGridLineInLoop(i_SECONDARY_GRID_HALFCOUNT_m, canvas);
			break;
		case TERTIARY:
			this.secondGridPaintObj_m.setAlpha(25);
			fn_RenderGridLineInLoop(i_TERTIARY_GRID_HALFCOUNT_m, canvas);
			break;
		case QUARTERNARY:
			this.secondGridPaintObj_m.setAlpha(10);
			fn_RenderGridLineInLoop(i_QUARTERNARY_GRID_HALFCOUNT_m, canvas);
		case PENTENARY:
			this.secondGridPaintObj_m.setAlpha(5);
			fn_RenderGridLineInLoop(i_PENTENARY_GRID_HALFCOUNT_m, canvas);
			break;

		}
	}

	private void fn_RenderGridLinesVerticalInLoop(int gridLineCount,
			Canvas canvas, int direction) {
		for (int iCount = 1; iCount < gridLineCount; iCount++) {
			canvas.drawLine(
					this.originGridLineCoordArray_m[0]
							+ direction
							* iCount
							* (this.getLayoutGridSideLength() / (2.0f * gridLineCount)),
					this.originGridLineCoordArray_m[1],
					this.originGridLineCoordArray_m[2]
							+ direction
							* iCount
							* (this.getLayoutGridSideLength() / (2.0f * gridLineCount)),
					this.originGridLineCoordArray_m[3], secondGridPaintObj_m);
		}

	}

	private void fn_RenderGridLinesHorizontalInLoop(int gridLineCount,
			Canvas canvas, int direction) {
		for (int iCount = 1; iCount < gridLineCount; iCount++) {
			canvas.drawLine(this.originGridLineCoordArray_m[4],
					this.originGridLineCoordArray_m[5] + direction * iCount
							* (this.getHeight() / (2.0f * gridLineCount)),
					this.originGridLineCoordArray_m[6],
					this.originGridLineCoordArray_m[7] + direction * iCount
							* (this.getHeight() / (2.0f * gridLineCount)),
					secondGridPaintObj_m);
		}

	}

	private void fn_RendererInit() {
		this.paintObj_m = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.paintObj_m.setColor(this.paintColorObj_m.WHITE);
		this.paintObj_m.setStyle(Paint.Style.FILL);
		this.rectArray_m = new Rect[10];

		this.viewBorderPaintObj_m = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.viewBorderPaintObj_m.setColor(Color.WHITE);
		this.viewBorderPaintObj_m.setStyle(Paint.Style.STROKE);

		this.secondGridPaintObj_m = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.secondGridPaintObj_m.setColor(Color.WHITE);

		this.paintRadialGradient_m = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.paintRadialGradient_m.setColor(Color.BLACK);

		int i_SizeOfRect_f = 100;
		int i_RectPadding_f = 2;
		int i_ViewHeight_f = this.getHeight();
		int i_ViewWidth_f = this.getWidth();
		for (int iCount = 0; iCount < this.rectArray_m.length; iCount++) {
			this.rectArray_m[iCount] = new Rect(iCount
					* (i_SizeOfRect_f + i_RectPadding_f), 0, 100 + iCount
					* (i_SizeOfRect_f + i_RectPadding_f), 100);
		}
		this.borderRectangle_m = new Rect(0, 0, 0, 0);

		this.paintObjOnTouch_m = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.paintObjOnTouch_m.setColor(Color.WHITE);
		this.paintObjOnTouch_m.setStyle(Paint.Style.STROKE);
		this.paintObjOnTouch_m.setStrokeWidth(3.0f);

	}

	/*
	 * Method: fn_RefreshLayout()Function: To command Android system to redraw
	 * the custom view after properties are changedAuthor: Hrishik Mishra
	 */
	public void fn_RefreshLayout() {
		invalidate();
		requestLayout();
	}

	public float fn_GetX(float x, int identifier) {
		float i_LocalX_f;
		switch (this.e_Origin_m) {
		case CENTER:
			i_LocalX_f = x - this.i_OffsetVector_m[0]
					- this.i_LayoutGridSideLength_m / 2;
			if (Math.abs(i_LocalX_f) > this.i_LayoutGridSideLength_m / 2) {
				i_LocalX_f = Math.signum(i_LocalX_f)
						* this.i_LayoutGridSideLength_m / 2;
				b_IsTouchOutsideX_m = true;
				if (identifier == Integer.MIN_VALUE) {
					if (Math.signum(i_LocalX_f) > 0) {
						x = this.i_OffsetVector_m[0]
								+ this.i_LayoutGridSideLength_m;
					} else {
						x = this.i_OffsetVector_m[0];
					}
				} else {
					x = i_LocalX_f;
				}

			}
			else{
				if (identifier != Integer.MIN_VALUE) 
					x = i_LocalX_f;
			}
			// this.d_XYCoordinates_m[0] = x;
			Log.i("XY-CONTROLLER", "<------X-SHIFT: " + x);
			return (x);
		default:
			return -10f;

		}

	}

	public float fn_GetY(float y, int identifier) {
		float i_LocalY_f;
		switch (this.e_Origin_m) {
		case CENTER:
			i_LocalY_f = y - this.i_OffsetVector_m[1]
					- this.i_LayoutGridSideLength_m / 2;
			if (Math.abs(i_LocalY_f) > this.i_LayoutGridSideLength_m / 2) {
				i_LocalY_f = Math.signum(i_LocalY_f)
						* this.i_LayoutGridSideLength_m / 2;
				b_IsTouchOutsideY_m = true;
				if (identifier == Integer.MIN_VALUE) {
					if (Math.signum(i_LocalY_f) > 0) {
						y = this.i_OffsetVector_m[1]
								+ this.i_LayoutGridSideLength_m;
					} else {
						y = this.i_OffsetVector_m[1];
					}
				} else {
					y = i_LocalY_f;
				}
			}
			else{
				if (identifier != Integer.MIN_VALUE) 
					y = i_LocalY_f;
			}
			// this.d_XYCoordinates_m[1] = y;
			Log.i("XY-CONTROLLER", "<------Y-SHIFT: " + y);
			return (y);
		default:
			return -10f;

		}
	}

	// Property settings for the custom XY-controller view
	/**
	 * @return the i_LayoutHeight_m
	 */
	public int getLayoutHeight() {
		return i_LayoutHeight_m;
	}

	/**
	 * @param i_LayoutHeight_m
	 *            the i_LayoutHeight_m to set
	 */
	public void setLayoutHeight(int i_LayoutHeight_m) {
		this.i_LayoutHeight_m = i_LayoutHeight_m;
		this.fn_RefreshLayout();
	}

	/**
	 * @return the i_LayoutWidth_m
	 */
	public int getLayoutWidth() {
		return i_LayoutWidth_m;
	}

	/**
	 * @param i_LayoutWidth_m
	 *            the i_LayoutWidth_m to set
	 */
	public void setLayoutWidth(int i_LayoutWidth_m) {
		this.i_LayoutWidth_m = i_LayoutWidth_m;
		this.fn_RefreshLayout();
	}

	/**
	 * @return the i_YMax_m
	 */
	public int getYMax() {
		return i_YMax_m;
	}

	/**
	 * @param i_YMax_m
	 *            the i_YMax_m to set
	 */
	public void setYMax(int i_YMax_m) {
		this.i_YMax_m = i_YMax_m;
		this.fn_RefreshLayout();
	}

	/**
	 * @return the i_YMin_m
	 */
	public int getYMin() {
		return i_YMin_m;
	}

	/**
	 * @param i_YMin_m
	 *            the i_YMin_m to set
	 */
	public void setYMin(int i_YMin_m) {
		this.i_YMin_m = i_YMin_m;
		this.fn_RefreshLayout();
	}

	/**
	 * @return the i_XMax_m
	 */
	public int getXMax() {
		return i_XMax_m;
	}

	/**
	 * @param i_XMax_m
	 *            the i_XMax_m to set
	 */
	public void setXMax(int i_XMax_m) {
		this.i_XMax_m = i_XMax_m;
		this.fn_RefreshLayout();
	}

	/**
	 * @return the i_XMin_m
	 */
	public int getXMin() {
		return i_XMin_m;
	}

	/**
	 * @param i_XMin_m
	 *            the i_XMin_m to set
	 */
	public void setXMin(int i_XMin_m) {
		this.i_XMin_m = i_XMin_m;
		this.fn_RefreshLayout();
	}

	/**
	 * @return the b_IsMultiTouch_m
	 */
	public boolean isIsMultiTouch() {
		return b_IsMultiTouch_m;
	}

	/**
	 * @param b_IsMultiTouch_m
	 *            the b_IsMultiTouch_m to set
	 */
	public void setIsMultiTouch(boolean b_IsMultiTouch_m) {
		this.b_IsMultiTouch_m = b_IsMultiTouch_m;
		this.fn_RefreshLayout();

	}

	/**
	 * @return the e_Origin_m
	 */
	public Origin getOrigin() {
		return e_Origin_m;
	}

	/**
	 * @param e_Origin_m
	 *            the e_Origin_m to set
	 */
	public void setOrigin(Origin e_Origin_m) {
		this.e_Origin_m = e_Origin_m;
		this.fn_RefreshLayout();
	}

	/**
	 * @return the i_OriginX_m
	 */
	public int getOriginX() {
		return i_OriginX_m;
	}

	/**
	 * @param i_OriginX_m
	 *            the i_OriginX_m to set
	 */
	public void setOriginX(int i_OriginX_m) {
		this.i_OriginX_m = i_OriginX_m;
		this.fn_RefreshLayout();
	}

	/**
	 * @return the i_OriginY_m
	 */
	public int getOriginY() {
		return i_OriginY_m;
	}

	/**
	 * @param i_OriginY_m
	 *            the i_OriginY_m to set
	 */
	public void setOriginY(int i_OriginY_m) {
		this.i_OriginY_m = i_OriginY_m;
		this.fn_RefreshLayout();
	}

	/**
	 * @return the s_FramePicturePath_m
	 */
	public String getFramePicturePath() {
		return s_FramePicturePath_m;
	}

	/**
	 * @param s_FramePicturePath_m
	 *            the s_FramePicturePath_m to set
	 */
	public void setFramePicturePath(String s_FramePicturePath_m) {
		this.s_FramePicturePath_m = s_FramePicturePath_m;
		this.fn_RefreshLayout();
	}

	/**
	 * @return the s_GridPicturePath_m
	 */
	public String getGridPicturePath() {
		return s_GridPicturePath_m;
	}

	/**
	 * @param s_GridPicturePath_m
	 *            the s_GridPicturePath_m to set
	 */
	public void setGridPicturePath(String s_GridPicturePath_m) {
		this.s_GridPicturePath_m = s_GridPicturePath_m;
	}

	/**
	 * @return the i_LayoutGridSideLength_m
	 */
	public int getLayoutGridSideLength() {
		return i_LayoutGridSideLength_m;
	}

	/**
	 * @param i_LayoutGridSideLength_m
	 *            the i_LayoutGridSideLength_m to set
	 */
	public void setLayoutGridSideLength(int i_LayoutGridSideLength_m) {
		this.i_LayoutGridSideLength_m = i_LayoutGridSideLength_m;
	}

	@Override
	public void fn_PutToQueue() {
		// TODO Auto-generated method stub

	}

	@Override
	public ControlValuePacket fn_DequeueFromQueue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Queue<ControlValuePacket> getQueue() {
		return IController.queueObj_m;
	}

	@Override
	public HashMap<Integer, ControllerMode> fn_FetchSubControllerMap() {
		// TODO Auto-generated method stub
		if (this.getSubControllerMap() == null)
			this.fn_SetSubControllerMap();

		return this.getSubControllerMap();

	}

	/*
	 * Function: To set the SubControllerMap for reference in application
	 * (non-Javadoc)
	 * 
	 * @see com.beta.Controllability.IController#fn_SetSubControllerMap() *
	 */
	@Override
	public void fn_SetSubControllerMap() {
		if (this.getSubControllerMap() != null)
			return;
		// TODO Auto-generated method stub
		this.setSubControllerMap(this.xyControllerMapObj_m);

	}

	@Override
	public float fn_Normalize(float rawData) {
		// TODO Auto-generated method stub
		return rawData / (this.i_LayoutGridSideLength_m / 2);

	}

	public float fn_LinearCalculation(float value, int[] valueRange) {		
		return valueRange[0] + ((valueRange[1] - valueRange[0]) / (2.0f))
				* (value + 1);
	}
	public float fn_ReverseLinearCalculationForAnimation(float value, int[] valueRange){
		return ( -1 + ((value - valueRange[0])*(2.0f/(valueRange[1] - valueRange[0]))));
	}
	public float fn_ReverseNormalize(float data){
		return data*(this.i_LayoutGridSideLength_m / 2);
	}
	public float fn_GetXForAnimation(float data){
		data = data + this.i_OffsetVector_m[0] + this.i_LayoutGridSideLength_m/2;
		return this.fn_GetX(data, Integer.MIN_VALUE);
	}
	public float fn_GetYForAnimation(float data){
		data = data + this.i_OffsetVector_m[1] + this.i_LayoutGridSideLength_m/2;
		return this.fn_GetY(data, Integer.MIN_VALUE);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		b_IsDoubleTap_m = true;
		// TODO Auto-generated method stub
		ControlValuePacket doubleTapControlPacket_f;
		if (this.xyCurrentSettingDetails_m.b_IsDTOn_m) {

			if (!this.b_IsDoubleTapOdd_m) {
				this.b_IsDoubleTapOdd_m = true;
				doubleTapControlPacket_f = this.fn_CreateControlValuePacket(
						XYSubController.DOUBLE_TAP,
						this.xyCurrentSettingDetails_m.getDoubleTapThreshold(),
						0);
			} else {
				this.b_IsDoubleTapOdd_m = false;
				doubleTapControlPacket_f = this
						.fn_CreateControlValuePacket(
								XYSubController.DOUBLE_TAP,
								this.xyCurrentSettingDetails_m
										.getDoubleTapThreshold() - 1, 0);
			}
			IController.queueObj_m.offer(doubleTapControlPacket_f);
			Log.i(s_DEBUG_TAG_M, "DOUBLE TAP");
			b_IsDoubleTap_m = false;
			return true;
		} else {
			b_IsDoubleTap_m = false;
			return false;
		}
		
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		Log.d(s_DEBUG_TAG_M, "Gesture was tap up");
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		ControlValuePacket SingleTapControlPacket_f;
		if (this.xyCurrentSettingDetails_m.b_IsSTOn_m) {

			if (!this.b_IsSingleTapOdd_m) {
				this.b_IsSingleTapOdd_m = true;
				SingleTapControlPacket_f = this.fn_CreateControlValuePacket(
						XYSubController.SINGLE_TAP,
						xyCurrentSettingDetails_m.getSingleTapThreshold(), 0);
			} else {
				this.b_IsSingleTapOdd_m = false;
				SingleTapControlPacket_f = this
						.fn_CreateControlValuePacket(
								XYSubController.SINGLE_TAP,
								this.xyCurrentSettingDetails_m
										.getSingleTapThreshold() - 1, 0);
			}
			IController.queueObj_m.offer(SingleTapControlPacket_f);
			Log.i(s_DEBUG_TAG_M, "SINGLE TAP");
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the xyCurrentSettingDetails_m
	 */
	public XYCurrentSettingDetails getXYCurrentSettingDetails() {
		return xyCurrentSettingDetails_m;
	}

	/**
	 * @param xyCurrentSettingDetails_m
	 *            the xyCurrentSettingDetails_m to set
	 */
	public void setXYCurrentSettingDetails(
			XYCurrentSettingDetails xyCurrentSettingDetails_m) {
		this.xyCurrentSettingDetails_m = xyCurrentSettingDetails_m;
	}

	public ControlValuePacket fn_CreateControlValuePacket(
			XYSubController xySubController, float value, int channel) {
		this.controlValuePacketObj_m = new ControlValuePacket(value);
		this.controlValuePacketObj_m.setControllerType(e_ControllerType_m);
		this.controlValuePacketObj_m.setiControllerPointer(this);
		this.controlValuePacketObj_m.setChannelVector(channel);
		switch (xySubController) {
		case X_RANGE_CHANGE:
			this.controlValuePacketObj_m
					.setSubControllerID(XYSubController.X_RANGE_CHANGE
							.getValue());
			this.controlValuePacketObj_m
					.setI_FunctionValue(this.xyCurrentSettingDetails_m
							.getI_XFunctionValue());
			break;
		case Y_RANGE_CHANGE:
			this.controlValuePacketObj_m
					.setSubControllerID(XYSubController.Y_RANGE_CHANGE
							.getValue());
			this.controlValuePacketObj_m
					.setI_FunctionValue(this.xyCurrentSettingDetails_m
							.getI_YFunctionValue());
			break;
		case DOUBLE_TAP:
			this.controlValuePacketObj_m
					.setSubControllerID(XYSubController.DOUBLE_TAP.getValue());
			this.controlValuePacketObj_m
					.setI_FunctionValue(this.xyCurrentSettingDetails_m
							.getI_DTFunctionValue());
			break;
		case FLING_X:
			this.controlValuePacketObj_m
					.setSubControllerID(XYSubController.FLING_X.getValue());
			this.controlValuePacketObj_m
					.setI_FunctionValue(this.xyCurrentSettingDetails_m
							.getI_XFunctionValue());
			break;
		case FLING_Y:
			this.controlValuePacketObj_m
					.setSubControllerID(XYSubController.FLING_Y.getValue());
			this.controlValuePacketObj_m
					.setI_FunctionValue(this.xyCurrentSettingDetails_m
							.getI_YFunctionValue());
			break;
		case SINGLE_TAP:
			this.controlValuePacketObj_m
					.setSubControllerID(XYSubController.SINGLE_TAP.getValue());
			this.controlValuePacketObj_m
					.setI_FunctionValue(this.xyCurrentSettingDetails_m
							.getI_STFunctionValue());
			break;

		}

		return this.controlValuePacketObj_m;

	}
}
