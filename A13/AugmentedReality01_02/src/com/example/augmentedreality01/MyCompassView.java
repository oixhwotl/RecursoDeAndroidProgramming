package com.example.augmentedreality01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class MyCompassView extends View {
	private Paint mPaint;
	private float[] mOrientation;

	public MyCompassView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(2);
		mPaint.setTextSize(25);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setColor(Color.WHITE);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mOrientation == null) {
			return;
		}

		int screenWidth = getMeasuredWidth();
		int screenHeight = getMeasuredHeight();
		int xPoint = screenWidth / 2;
		int yPoint = screenHeight / 2;
		float radius = (float) (Math.max(xPoint, yPoint) * 0.4);

		int test = 2;
		if (test == 1) {
			RectF ovalBound = new RectF(xPoint - radius, yPoint - radius,
					xPoint + radius, yPoint + radius);
			canvas.drawOval(ovalBound, mPaint);
			// canvas.drawCircle(xPoint, yPoint, radius, mPaint);

			canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(),
					mPaint);
			canvas.drawLine(
					xPoint,
					yPoint,
					(float) (xPoint + radius
							* Math.sin((double) (-mOrientation[0]) / 180
									* Math.PI)),
					(float) (yPoint - radius
							* Math.cos((double) (-mOrientation[0]) / 180
									* Math.PI)), mPaint);

			// canvas.drawText(String.valueOf(mOrientation), xPoint, yPoint,
			// mPaint);
		} else {
			// canvas.drawLine(xPoint, 0, xPoint, screenHeight, mPaint);
			// canvas.drawLine(0, yPoint, screenWidth, yPoint, mPaint);
			// Rotate the canvas with the azimut
			// canvas.rotate(-azimut*360/(2*3.14159f), centerx, centery);
			canvas.rotate(-mOrientation[0], xPoint, yPoint);
			mPaint.setColor(Color.BLUE);
			canvas.drawLine(xPoint - 20, yPoint, xPoint, 0, mPaint);
			canvas.drawLine(xPoint + 20, yPoint, xPoint, 0, mPaint);
			canvas.drawText("N", xPoint, yPoint - 20, mPaint);

			mPaint.setColor(Color.RED);
			canvas.drawLine(xPoint - 20, yPoint, xPoint, screenHeight, mPaint);
			canvas.drawLine(xPoint + 20, yPoint, xPoint, screenHeight, mPaint);
			canvas.drawText("S", xPoint, yPoint + 20, mPaint);

		}
	}

	public void updateData(float[] aOrientation) {
		this.mOrientation = aOrientation;
		invalidate();
	}

}
