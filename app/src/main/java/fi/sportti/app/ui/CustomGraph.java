package fi.sportti.app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.time.LocalDateTime;
import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CustomGraph extends View  {
    public static final String TAG = "testailua";
    public static final int DAYS_OF_WEEK = 7;
    public static final int MONTHS_OF_YEAR = 12;
    public static final int BAR_GRAPH = 1;
    public static final int LINE_GRAPH = 2;
    private static final int X_OFFSET = 100;
    private static final int Y_TOP_OFFSET = 100;
    private static final int Y_BOTTOM_OFFSET = 50;
    private LocalDateTime date;
    private Boolean isInit = false;
    private Boolean drawDailyGraph = true;
    private Boolean drawMonthlyGraph = false;
    private Path path;
    private Paint axisPaint, barPaint, linePaint, textPaint, greyPaint;
    private Canvas canvas;
    private int graphType, graphMaxValue, rectWidth, viewWidth, viewHeight, origoX, origoY, graphHeight, graphWidth;
    private double oneHourHeight;
    private HashMap<LocalDateTime, Long> dataMap;

    public CustomGraph(Context context) {
        super(context);
    }

    public CustomGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CustomGraph(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        if (!isInit){
            init();
        }
        if(graphType == LINE_GRAPH){
            path.reset();
            path.moveTo(origoX, origoY);
        }
        drawAxis();
        if(dataMap != null){
            drawGraph();
            drawHorizontalMarks();
        }
    }

    private void init(){
        initPaints();
        graphMaxValue = 10;
        viewWidth = getWidth();
        viewHeight = getHeight();
        origoX = X_OFFSET;
        origoY = viewHeight - Y_BOTTOM_OFFSET;
        graphHeight = viewHeight - Y_TOP_OFFSET - Y_BOTTOM_OFFSET;
        graphWidth = viewWidth - X_OFFSET - X_OFFSET;
        isInit = true;
        date = LocalDateTime.now();
        path = new Path();
        path.moveTo(origoX, origoY);
    }

    public void setGraphType(int number){
        if(number == BAR_GRAPH){
            graphType = BAR_GRAPH;
        }
        else if(number == LINE_GRAPH){
            graphType = LINE_GRAPH;
        }
        setGraphStyle(DAYS_OF_WEEK);
    }

    public void setGraphStyle(int style){
        if(style == DAYS_OF_WEEK){
            drawDailyGraph = true;
            drawMonthlyGraph = false;
            graphMaxValue = 10;
            rectWidth = graphWidth / (DAYS_OF_WEEK * 2);
        }
        else if(style == MONTHS_OF_YEAR){
            drawDailyGraph = false;
            drawMonthlyGraph = true;
            graphMaxValue = 200;
            rectWidth = graphWidth / (MONTHS_OF_YEAR * 2);
        }
        date = LocalDateTime.now();
        setCorrectStartDateForGraph();
        oneHourHeight = 1.0 * graphHeight / graphMaxValue;
    }

    private void drawAxis() {
        //y-axis
        canvas.drawLine(origoX, origoY, origoX, Y_TOP_OFFSET -50, axisPaint);
        //x-axis
        canvas.drawLine(origoX, origoY, viewWidth - origoX, origoY, axisPaint);
        canvas.drawText("Tunnit", 30, 30, textPaint);
    }

    private void drawGraph(){
        setCorrectStartDateForGraph();
        int endForLoop = 0;
        if(drawDailyGraph) endForLoop = DAYS_OF_WEEK;
        else if(drawMonthlyGraph) endForLoop = MONTHS_OF_YEAR;

        canvas.drawText(String.valueOf(date.getYear()), viewWidth /2, 30, textPaint);
        int currentXPosition = origoX + rectWidth;
        path.moveTo(currentXPosition, origoY);

        //Draw bars for days of week or months of year.
        LocalDateTime date = LocalDateTime.now();
        for(int i = 0; i < endForLoop; i++){
            if(drawDailyGraph) date = this.date.plusDays(i);
            else if(drawMonthlyGraph) date = this.date.plusMonths(i);
            drawDataPointAndText(currentXPosition, date);
            currentXPosition += rectWidth *2;
        }
        if(graphType == LINE_GRAPH){
            canvas.drawPath(path, linePaint);
        }
    }

    private void setCorrectStartDateForGraph(){
        //Set date to first day of current week or first month of year.
        int year = date.getYear();
        int monthOfYear = date.getMonthValue();
        int currentDayOfWeek = date.getDayOfWeek().getValue();
        if(drawDailyGraph){
            date = LocalDateTime.of(year, monthOfYear, date.getDayOfMonth(), 12, 0, 0, 0);
            date = date.minusDays(currentDayOfWeek-1);
        }
        else if(drawMonthlyGraph){
            date = LocalDateTime.of(year, monthOfYear, 1, 12, 0, 0 ,0);
            date = date.minusMonths(monthOfYear-1);
        }
    }

    private void drawDataPointAndText(int xPos, LocalDateTime date){
        String text = getTextForBar(date);
        long hour = 0;
        if(dataMap.containsKey(date)){
            hour = dataMap.get(date);
        }
        if(graphType == BAR_GRAPH){
            drawBar(hour, xPos);
        }
        else if(graphType == LINE_GRAPH){
            drawLine(hour, xPos);
        }
        canvas.drawText(text, xPos, origoY + 50, textPaint);
        canvas.drawText(String.valueOf(hour), xPos, origoY - 30, textPaint);
    }

    private void drawBar(long hour, int xPos){
        Rect mBar = new Rect();
        mBar.left = xPos;
        mBar.right = mBar.left + rectWidth;
        mBar.bottom = origoY;
        mBar.top = origoY - (int)(oneHourHeight * hour);
        canvas.drawRect(mBar, barPaint);
    }

    private void drawLine(long hour, int xPos){
        int newY = origoY - (int)(oneHourHeight * hour);
        path.lineTo(xPos, newY);
        path.moveTo(xPos, newY);
        int radius = 13;
        canvas.drawCircle(xPos, newY, radius, barPaint);
    }

    private void drawHorizontalMarks() {
        greyPaint.setStrokeWidth(3);
        int maxValue = 10;
        if(drawDailyGraph) maxValue = 10;
        else if(drawMonthlyGraph) maxValue = 200;
        /*Always draw 5 horizontal lines on graph.
         * If maxValue is 10, draw lines for 2, 4, 6, 8 and 10.
         * If maxValue is 200, draw lines for 40, 80, 120, 160, 200.
         * Etc...*/
        int stepSize = maxValue/5;
        int currentY = origoY - (int)(oneHourHeight *stepSize);
        for (int i = stepSize; i <= maxValue; i += stepSize) {
            int startX = origoX;
            int startY = currentY;
            int endX = origoX + graphWidth;
            int endY = startY;
            canvas.drawLine(startX, startY, endX, endY, greyPaint);
            int xPositionForText;
            if(i >= 100) xPositionForText = origoX-90;
            else if(i >= 10) xPositionForText = origoX-70;
            else xPositionForText = origoX-50;

            //Add +10 to currentY so text aligns better with lines.
            canvas.drawText(String.valueOf(i), xPositionForText, currentY+10, textPaint);
            currentY -= oneHourHeight *stepSize;
        }
    }

    public void setDataMap(HashMap<LocalDateTime, Long> dataMap) {
        this.dataMap = dataMap;
    }

    public void showNextPeriod(){
        if(drawDailyGraph){
            date = date.plusDays(7);
        }
        else if(drawMonthlyGraph){
            date = date.plusYears(1);
        }
    }

    public void showPreviousPeriod(){
        if(drawDailyGraph){
            date = date.minusDays(7);
        }
        else if(drawMonthlyGraph){
            date = date.minusYears(1);
        }
    }

    private String getTextForBar(LocalDateTime date){
        String text = "";
        if(drawDailyGraph){
            text = date.getDayOfMonth() + "." + date.getMonthValue();
        }
        else if(drawMonthlyGraph){
            text = String.valueOf(date.getMonthValue());
        }
        return text;
    }

    private void initPaints(){
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(Color.rgb(173, 201, 139));

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.rgb(173, 201, 139));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(10);

        greyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        greyPaint.setColor(Color.rgb(128, 128, 128));

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStrokeWidth(1);
        textPaint.setTextSize(40);

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.rgb(200, 200, 200));
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeWidth(10);
    }
}
