package fi.sportti.app.ui.customViews;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import fi.sportti.app.R;

/**
 *@author Jukka-Pekka Jaakkola
 * Custom View used to draw round progress bars.
 */

public class CustomProgressBar extends View {

    private float multiplier = 0;
    private boolean init = false;
    private Paint greyPaint, whitePaint, greenPaint;

    //Constructors that are needed because class extends View class.
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
        if(!init){
            init();
        }
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float centerX = viewWidth / 2;
        float centerY = viewHeight / 2;
        //Coordinates of oval for drawArc method. It uses these to draw arc.
        //In this case, these are same as the size of view which is square.
        float left = 0;
        float top = 0;
        float right = viewWidth;
        float bottom = viewHeight;

        //Angle where drawArc method starts drawing. 90 degrees means bottom.
        float startAngle = 90;
        //Sweep angle tells how many degrees to draw from starting point in clockwise direction.
        float drawAngle = 360 * multiplier;
        float radius = viewWidth / 2;
        //Width of fill bar can be increased by making smallRadius even smaller.
        float smallRadius = radius * 0.75f;

        //It is important to call draw methods below in this order!
        //First grey circle is drawn, then correct sized arc on top of it
        //which displays how much progress user has made towards weekly goal.
        //And finally white smaller circle is drawn on top of these both so the result looks like nice
        //round progress bar.

        canvas.drawCircle(centerX, centerY, radius, greyPaint);
        canvas.drawArc(left, top, right, bottom, startAngle, drawAngle, true, greenPaint);
        canvas.drawCircle(centerX, centerY, smallRadius, whitePaint);


    }

    public void setMultiplier(float value){
        this.multiplier = value;
    }

    private void init(){
        init = true;
        greyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        greyPaint.setColor(Color.rgb(240, 240, 240));
        whitePaint.setColor(Color.rgb(198, 222, 241));
        int green = getResources().getColor(R.color.green_for_custom_progress_bar);
        greenPaint.setColor(green);
    }
}
