package com.example.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HSVColorPicker extends View {
	private final static String HS = "hs";
	private final static String V = "v";
	
	private float mV_Width;
	private float mHS_V_Margin;
	private float mHS_Picker_Radius;// HUE——SAT取色环半径
	private float mV_Picker_Width;
	private Drawable mHSV_Bg;// HSV背景
	private String mMove;
	private Paint mHueSatTrackerPaint;// HS轨迹球的画笔
	private Paint mValPaint;// V的画笔
	private Paint mValTrackerPaint;// V轨迹线的画笔

	private RectF mHueSatRect;
	private RectF mValRect;
	
	private Shader mValueShader;// 亮度的渲
	
	private OnColorListener mListener;

	private float mHue = 0f;// 色调 0°～360°
	private float mSaturation = 0f;// 饱和度 0.0～1.0
	private float mValue = 0f;// 亮度 取值范围为0(黑色)～1(白色)

	public interface OnColorListener {
		public void onChangeHS(float h, float s);

		public void onStartHS(float h, float s);

		public void onStartV(float v);

		public void onStopHS(float h, float s);

		public void onStopV(float v);

		public void onChangeV(float v);
	}

	public HSVColorPicker(Context context) {
		this(context, null);
		init(context, null);
	}

	public HSVColorPicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		init(context, attrs);

	}

	public HSVColorPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HSVColorPicker);
		mHS_Picker_Radius = a.getDimension(R.styleable.HSVColorPicker_hs_picker_radius, 10f);
		mV_Width = a.getDimension(R.styleable.HSVColorPicker_v_width, 30f);
		mHS_V_Margin = a.getDimension(R.styleable.HSVColorPicker_hs_v_margin, 20f);
		mV_Picker_Width = a.getDimension(R.styleable.HSVColorPicker_v_picker_width, 10f);
		mHSV_Bg = a.getDrawable(R.styleable.HSVColorPicker_hsv_background);
		a.recycle();
		initPaintTools();
	}

	private void initPaintTools() {
		mHueSatTrackerPaint = new Paint();
		mValPaint = new Paint();
		mValTrackerPaint = new Paint();
		/** HS的滑动条Paint **/
		mHueSatTrackerPaint.setColor(0xffffffff);
		mHueSatTrackerPaint.setStyle(Style.STROKE);
		mHueSatTrackerPaint.setAntiAlias(true);// 抗锯齿
		/** V的滑动条Paint **/
		mValTrackerPaint.setColor(0xffffffff);
		mValTrackerPaint.setStyle(Style.STROKE);
		mValTrackerPaint.setAntiAlias(true);// 抗锯齿
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		float top = getPaddingTop();
		float bottom = h - getPaddingBottom();
		float left = getPaddingLeft();
		float right = w - getPaddingRight();
		mHueSatRect = new RectF(left, top, right, bottom - mV_Width - mHS_V_Margin);
		mValRect = new RectF(left, bottom - mV_Width, right, bottom);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawHueSatPanel(canvas);
		drawValPanel(canvas);
	}

	/**
	 * 绘HS
	 * 
	 * @param canvas
	 */
	private void drawHueSatPanel(Canvas canvas) {// 设置轨迹球和调试版
		BitmapDrawable bd = (BitmapDrawable) mHSV_Bg;
		Bitmap bitmap = bd.getBitmap();
		canvas.drawBitmap(bitmap, null, mHueSatRect, null);
		Point p = hueSatToPoint(mHue, mSaturation);
		canvas.drawCircle(p.x, p.y, mHS_Picker_Radius, mHueSatTrackerPaint);
	}

	/**
	 * 确定HS选中点的位置
	 * 
	 */
	private Point hueSatToPoint(float hue, float sat) {
		Point p = new Point();
		p.x = (int) ((hue * mHueSatRect.width() / 360f) + mHueSatRect.left);
		p.y = (int) ((1f - sat) * mHueSatRect.height() + mHueSatRect.top);
		return p;
	}

	/**
	 * 绘V
	 * 
	 * @param canvas
	 */
	private void drawValPanel(Canvas canvas) {
		int rgb1 = Color.HSVToColor(new float[] { mHue, mSaturation, 1f });
		int rgb0 = Color.HSVToColor(new float[] { mHue, mSaturation, 0f });
		mValueShader = new LinearGradient(mValRect.right, mValRect.top, mValRect.left, mValRect.top, rgb1, rgb0, TileMode.CLAMP);
		mValPaint.setShader(mValueShader);
		canvas.drawRect(mValRect, mValPaint);
		Point p = valToPoint(mValue);
		RectF r = new RectF();
		r.left = p.x - mV_Picker_Width;
		r.right = p.x + mV_Picker_Width;
		r.top = mValRect.top + 5;
		r.bottom = mValRect.bottom - 5;
		canvas.drawRect(r, mValTrackerPaint);
	}

	/**
	 * 确定V选中点的位置
	 */
	private Point valToPoint(float val) {
		Point p = new Point();
		p.x = (int) ((1f - val) * mValRect.width() + mValRect.left);
		p.y = (int) mValRect.top;
		return p;
	}

	@SuppressLint("ClickableViewAccessibility") public boolean onTouchEvent(MotionEvent event) {
		boolean update = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mValRect.contains(event.getX(), event.getY())) {
				mMove = V;
				update = true;
				mValue = pointToVal(event.getX());
				if (mListener != null) {
					mListener.onStartV(mValue);
				}
			} else if (mHueSatRect.contains(event.getX(), event.getY())) {
				mMove = HS;
				update = true;
				float[] result = pointToHueSat(event.getX(), event.getY());
				mHue = result[0];
				mSaturation = result[1];
				if (mListener != null) {
					mListener.onStartHS(mHue, mSaturation);
				}
			} else {
				mMove = "";
				update = false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (V.equals(mMove)) {
				mValue = pointToVal(event.getX());
				update = true;
				if (mListener != null) {
					mListener.onChangeV(mValue);
				}
			} else if (HS.equals(mMove)) {
				update = true;
				float[] result = pointToHueSat(event.getX(), event.getY());
				mHue = result[0];
				mSaturation = result[1];
				if (mListener != null) {
					mListener.onChangeHS(mHue, mSaturation);
				}
			} else {
				update = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (V.equals(mMove)) {
				mValue = pointToVal(event.getX());
				update = true;
				if (mListener != null) {
					mListener.onStopV(mValue);
				}
			} else if (HS.equals(mMove)) {
				update = true;
				float[] result = pointToHueSat(event.getX(), event.getY());
				mHue = result[0];
				mSaturation = result[1];
				if (mListener != null) {
					mListener.onStopHS(mHue, mSaturation);
				}
			}
			break;
		}
		if (update) {
			invalidate();
			return true;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 获取x坐标的val值
	 * 
	 * @param x
	 * @return
	 */
	private float pointToVal(float x) {
		if (x < mValRect.left) {
			return x = 0f;
		} else if (x > mValRect.right) {
			return x = 1f;
		} else {
			return x = 1 - (x - mValRect.left) / mValRect.width();
		}
	}

	/**
	 * 获取x，y坐标的HS值
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private float[] pointToHueSat(float x, float y) {
		float[] result = new float[2];
		if (x < mHueSatRect.left) {
			x = 0f;
		} else if (x > mHueSatRect.right) {
			x = mHueSatRect.width();
		} else {
			x = x - mHueSatRect.left;
		}
		if (y < mHueSatRect.top) {
			y = 0f;
		} else if (y > mHueSatRect.bottom) {
			y = mHueSatRect.height();
		} else {
			y = y - mHueSatRect.top;
		}
		result[0] = x * 360f / mHueSatRect.width();
		result[1] = 1.f - (y * 1.f / mHueSatRect.height());
		return result;
	}

	public float getmHue() {
		return mHue;
	}

	public void setmHue(float mHue) {
		this.mHue = mHue;
		invalidate();
	}

	public float getmSaturation() {
		return mSaturation;
	}

	public void setmSaturation(float mSaturation) {
		this.mSaturation = mSaturation;
		invalidate();
	}

	public float getmValue() {
		return mValue;
	}

	public void setmValue(float mValue) {
		this.mValue = mValue;
		invalidate();
	}

	public void setOnColorListener(OnColorListener listener) {
		mListener = listener;
	}

}
