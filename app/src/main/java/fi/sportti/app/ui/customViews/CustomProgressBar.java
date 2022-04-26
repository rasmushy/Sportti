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
        greyPaint.setColor(Color.rgb(220, 220, 220));
        //bluePaint.setStyle(Paint.Style.FILL);
        Paint whitePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        whitePaint.setColor(Color.WHITE);
        //whitePaint.setStyle(Paint.Style.FILL);

        Paint greenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greenPaint.setColor(Color.rgb(151, 237, 81));

        Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(Color.GREEN);
        pointPaint.setStyle(Paint.Style.STROKE);
        pointPaint.setStrokeWidth(80);
        pointPaint.setStrokeCap(Paint.Cap.ROUND);

        float width = getWidth();
        float height = getHeight();
        float x = getWidth() / 2;
        float y = getHeight() / 2;

        canvas.drawCircle(x, y, width/2, greyPaint);

        RectF rectF = new RectF();
        rectF.bottom = height;
        rectF.top = 0;
        rectF.left = 0;
        rectF.right = width;
        canvas.drawArc(rectF, 90, 90, true, greenPaint);
        canvas.drawCircle(x, y, width/2 - width/12, whitePaint);


    }
}
