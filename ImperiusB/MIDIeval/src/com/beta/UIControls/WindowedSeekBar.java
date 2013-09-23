package com.beta.UIControls;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.beta.activities.R;

public class WindowedSeekBar extends ImageView {

	private String TAG = this.getClass().getSimpleName();
	private Bitmap thumbl = BitmapFactory.decodeResource(getResources(),
			R.drawable.leftithumb);
	private Bitmap thumbr = BitmapFactory.decodeResource(getResources(),
			R.drawable.rightithumb);
	private Bitmap centre = BitmapFactory.decodeResource(getResources(),
			R.drawable.centre_bar);
	private Bitmap leadtrail = BitmapFactory.decodeResource(getResources(),
			R.drawable.leader);
	private Bitmap left_end = BitmapFactory.decodeResource(getResources(),
			R.drawable.left_end);
	private Bitmap right_end = BitmapFactory.decodeResource(getResources(),
			R.drawable.right_end);
	
	private int thumblX, thumbrX;
	private int thumb1Value, thumb2Value;
	private int thumbY;
	private Paint paint = new Paint();
	private int selectedThumb;
	private SeekBarChangeListener scl;
	private int offset;
	private int minwindow = 10;
	
	
	public WindowedSeekBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WindowedSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WindowedSeekBar(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getHeight() > 0)
			init();
	}

	private void init() {
		printLog("View Height =" + getHeight() + "\t\t Thumb Height :"
				+ thumbl.getHeight());
		if (thumbl.getHeight() > getHeight())
			getLayoutParams().height = thumbl.getHeight();

		thumbY = (getHeight() / 2) - (thumbl.getHeight() / 2);
		printLog("View Height =" + getHeight() + "\t\t Thumb Height :"
				+ thumbl.getHeight() + "\t\t" + thumbY);

		// initial position here ( should be perameterized )
		thumblX = thumbl.getWidth();
		thumbrX = getWidth()/2 ;
		invalidate();
	}
	public void setSeekBarChangeListener(SeekBarChangeListener scl){
		this.scl = scl;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(left_end,0,10,paint);
		canvas.drawBitmap(right_end,getWidth()-7,10,paint);
		for (int i=7;i<thumblX - thumbl.getWidth();i++)
			canvas.drawBitmap(leadtrail,i,10,paint);
		for (int i=thumbrX +thumbr.getWidth();i<getWidth()-7;i++)
			canvas.drawBitmap(leadtrail,i,10,paint);
		
		for (int i=thumblX - thumbl.getWidth();i<thumbrX +thumbr.getWidth();i++)
			canvas.drawBitmap(centre,i,10,paint);
		canvas.drawBitmap(thumbl, thumblX - thumbl.getWidth(), 0, paint);
		canvas.drawBitmap(thumbr, thumbrX, 0, paint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int mx = (int) event.getX();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mx >= thumblX - thumbl.getWidth() && mx <= thumblX) {
				selectedThumb = 1;
				offset = mx - thumblX;
				printLog("Select Thumb 1 " + offset);
			} else if (mx >= thumbrX && mx <= thumbrX + thumbl.getWidth() ) {
				selectedThumb = 2;
				offset = thumbrX - mx;
				printLog("Select Thumb 2");
			}
			break;
		case MotionEvent.ACTION_MOVE:
			printLog("Mouse Move : " + selectedThumb);
			if (selectedThumb == 1) {
				thumblX = mx - offset;
				if (thumblX < thumbl.getWidth())
				   thumblX = thumbl.getWidth();
			} else if (selectedThumb == 2) {
				thumbrX = mx + offset;
			}
			break;
		case MotionEvent.ACTION_UP:
			selectedThumb = 0;
			break;
		}
		
		if (selectedThumb == 2)
			{
			if (thumbrX > getWidth()- thumbr.getWidth())
				thumbrX = getWidth()- thumbr.getWidth();
			if (thumbrX <= thumbl.getWidth()+1+minwindow)
				thumbrX = thumbl.getWidth()+1+minwindow;
			if (thumbrX <= thumblX+minwindow)
				thumblX = thumbrX-1-minwindow;
			}
		else if (selectedThumb == 1)
			{
			if (thumblX < thumbl.getWidth())
				thumblX = thumbl.getWidth();
			if(thumblX >= getWidth()- (thumbr.getWidth()+minwindow))
				thumblX = getWidth()- (thumbr.getWidth()+minwindow);
			if (thumblX > thumbrX-minwindow)
				thumbrX = thumblX+1+minwindow;
			}	
		invalidate();
		if(scl !=null){
			calculateThumbValue();
			scl.SeekBarValueChanged(thumb1Value,thumblX, thumb2Value, thumbrX, getWidth(),thumbY );
		}
		return true;
	}
	
	private void calculateThumbValue(){
		int width = getWidth() - (thumbl.getWidth() + thumbr.getWidth());
		thumb1Value = (127*(thumblX-thumbl.getWidth())/width);
		thumb2Value = (127*(thumbrX-thumbl.getWidth())/width);
	}
	
	private void printLog(String log){
		Log.i(TAG, log);
	}
	
	public interface SeekBarChangeListener{
		void SeekBarValueChanged(int Thumb1Value,int thumblX, int Thumb2Value, int thumbrX, int width, int thumbY);
	}
	
	
}
