package fi.sportti.app.ui.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CustomProgressBar extends View {

    private float multiplier = 0;

    public CustomProgressBar(Context context) {
        super(context);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint greyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Paint greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greyPaint.setColor(Color.rgb(240, 240, 240));
        whitePaint.setColor(Color.WHITE);
        greenPaint.setColor(Color.rgb(151, 237, 81));

        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float centerX = viewWidth / 2;
        float centerY = viewHeight / 2;
        RectF rectF = new RectF();
        rectF.bottom = viewHeight;
        rectF.top = 0;
        rectF.left = 0;
        rectF.right = viewWidth;

        //Sweep angle tells how many degrees to draw from starting point in clockwise direction.
        float sweepAngle = 360 * multiplier;
        float radius = viewWidth / 2;
        //By making smallRadius smaller, width of fill bar is increased.
        float smallRadius = radius * 0.75f;

        //Its important to have draw methods below in this order!
        //First grey circle is drawn, then correct sized arc on top of it.
        //And finally white smaller circle is drawn on top of these both.
        canvas.drawCircle(centerX, centerY, radius, greyPaint);
        canvas.drawArc(rectF, 90, sweepAngle, true, greenPaint);
        canvas.drawCircle(centerX, centerY, smallRadius, whitePaint);
    }

    public void setMultiplier(float value){
        this.multiplier = value;
    }
}
