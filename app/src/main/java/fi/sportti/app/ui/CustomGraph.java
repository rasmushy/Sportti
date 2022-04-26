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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;

import fi.sportti.app.R;

/**
 *@author Jukka-Pekka Jaakkola
 * Own class for creating custom views so we can draw graphs.
 */

/*
* Basic idea how to build own custom Views learnt from this article
* https://medium.com/@mayurjajoomj/custom-graphs-custom-view-android-862e16813cc*/

@RequiresApi(api = Build.VERSION_CODES.O)
public class CustomGraph extends View  {
    public static final String TAG = "testailua";
    public static final int DAYS_OF_WEEK = 7;
    public static final int MONTHS_OF_YEAR = 12;
    public static final int BAR_GRAPH = 1;
    public static final int LINE_GRAPH = 2;
    private final int xOffset = 100;
    private final int yTopOffset = 100;
    private final int yBottomOffset = 50;
    private ZonedDateTime date;
    private Boolean isInit = false;
    private Boolean drawDailyGraph = true;
    private Boolean drawMonthlyGraph = false;
    private Path path;
    private Paint axisPaint, barPaint, linePaint, textPaint, horizontalLinesPaint;
    private Canvas canvas;
    private int graphType, graphTimePeriod, graphMaxValue, rectWidth, viewWidth, viewHeight,
                origoX, origoY, graphHeight, graphWidth;
    private double oneHourHeight;
    private HashMap<ZonedDateTime, Integer> dataMap;

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

    //This method is called when graph is first created and everytime its updated with different values.
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
            oneHourHeight = 1.0 * graphHeight / graphMaxValue;
            setCorrectStartDateForGraph();
            drawDataPoints();
            drawHorizontalMarks();
            drawGraphHeader();
        }
    }

    //In History Activity, swipe listener is added to graph and it required that this method is implemented in View.
    @Override
    public boolean performClick(){
        return super.performClick();
    }

    //Called when graph is created for first time. Initialized variables used in graph.
    private void init(){
        date = ZonedDateTime.now();
        initPaints();
        viewWidth = getWidth();
        viewHeight = getHeight();
        origoX = xOffset;
        origoY = viewHeight - yBottomOffset;
        graphHeight = viewHeight - yTopOffset - yBottomOffset;
        graphWidth = viewWidth - (xOffset * 2);
        isInit = true;
        path = new Path();
        setGraphTimePeriod(DAYS_OF_WEEK);
    }

    public void setGraphType(int number){
        if(number == BAR_GRAPH){
            graphType = BAR_GRAPH;
        }
        else if(number == LINE_GRAPH){
            graphType = LINE_GRAPH;
        }
    }

    public int getGraphTimePeriod(){
        return graphTimePeriod;
    }

    public void setGraphTimePeriod(int style){
        if(style == DAYS_OF_WEEK){
            drawDailyGraph = true;
            drawMonthlyGraph = false;
            graphTimePeriod = DAYS_OF_WEEK;
            graphMaxValue = 5;
            rectWidth = graphWidth / (DAYS_OF_WEEK * 2);
        }
        else if(style == MONTHS_OF_YEAR){
            drawDailyGraph = false;
            drawMonthlyGraph = true;
            graphTimePeriod = MONTHS_OF_YEAR;
            graphMaxValue = 100;
            rectWidth = graphWidth / (MONTHS_OF_YEAR * 2);
        }
        date = ZonedDateTime.now();
    }

    private void drawAxis() {
        //y-axis
        canvas.drawLine(origoX, origoY, origoX, yTopOffset-50, axisPaint);
        //x-axis
        canvas.drawLine(origoX, origoY, viewWidth - origoX, origoY, axisPaint);
        String text = getResources().getString(R.string.customgraph_y_axis_name);
        canvas.drawText(text, 30, 30, textPaint);
    }

    private void drawDataPoints(){
        //Draw data points for days of week or months of year.
        if(drawDailyGraph){
            drawDataPointsForWeek();
        }
        else if(drawMonthlyGraph){
            drawDataPointsForYear();
        }
        //At end draw full path/line from Path object.
        if(graphType == LINE_GRAPH){
            canvas.drawPath(path, linePaint);
        }
    }

    private void drawDataPointsForWeek(){
        int currentXPosition = origoX + rectWidth;
        for(int i = 0; i < DAYS_OF_WEEK; i++){
            drawDataPointAndText(currentXPosition, date.plusDays(i));
            //Move xPosition to next data point's position.
            currentXPosition += rectWidth * 2;
        }
    }

    private void drawDataPointsForYear(){
        int currentXPosition = origoX + rectWidth;
        for(int i = 0; i < MONTHS_OF_YEAR; i++){
            drawDataPointAndText(currentXPosition, date.plusMonths(i));
            //Move xPosition to next data point's position.
            currentXPosition += rectWidth * 2;
        }
    }

    private void drawDataPointAndText(int xPos, ZonedDateTime date){
        //Draws single data points and correct text below them.
        int minutes = 0;
        if(dataMap.containsKey(date)){
            minutes = dataMap.get(date);
        }
        double hours = 1.0 * minutes / 60;
        int yPos = origoY - (int)(oneHourHeight * hours);
        if(graphType == BAR_GRAPH){
            drawBar(xPos, yPos);
        }
        else if(graphType == LINE_GRAPH){
            drawLine(xPos, yPos);
        }
        String text = getTextForDataPoint(date);
        canvas.drawText(text, xPos, origoY + 50, textPaint);
        //canvas.drawText(String.valueOf(minutes/60), xPos, origoY - 30, textPaint);
    }

    private void drawBar(int xPos, int yPos){
        Rect bar = new Rect();
        bar.left = xPos;
        bar.right = bar.left + rectWidth;
        bar.bottom = origoY;
        bar.top = yPos;
        canvas.drawRect(bar, barPaint);
    }

    private void drawLine(int xPos, int yPos){
        path.lineTo(xPos, yPos);
        path.moveTo(xPos, yPos);
        int radius = 13;
        canvas.drawCircle(xPos, yPos, radius, barPaint);
    }

    private void drawHorizontalMarks() {
        /* Always draw 5 horizontal lines on graph.
         * If maxValue is 10, draw lines for 2, 4, 6, 8 and 10.
         * If maxValue is 200, draw lines for 40, 80, 120, 160, 200. Etc..*/
        int stepSize = graphMaxValue / 5;
        int currentY = origoY - (int)(oneHourHeight * stepSize);
        for (int i = stepSize; i <= graphMaxValue; i += stepSize) {
            int startX = origoX;
            int startY = currentY;
            int endX = origoX + graphWidth;
            int endY = startY;
            canvas.drawLine(startX, startY, endX, endY, horizontalLinesPaint);

            int xPositionForText;
            //Different xPosition based on number size so number aligns better to axis.
            if(i >= 100){
                xPositionForText = origoX - 90;
            }
            else if(i >= 10){
                xPositionForText = origoX - 70;
            }
            else{
                xPositionForText = origoX - 60;
            }
            //Add +10 to currentY so text aligns better with lines.
            canvas.drawText(String.valueOf(i), xPositionForText, currentY+10, textPaint);
            //Move Y coordinate to next line's position.
            currentY -= oneHourHeight *stepSize;
        }
    }

    private void drawGraphHeader(){
        //Draw current year at top of graph.
        int x = viewWidth /2 - xOffset;
        int y = 30;
        String year = String.valueOf(date.getYear());
        canvas.drawText(year, x, y, textPaint);
    }

    private void setCorrectStartDateForGraph(){
        //Set date to first day of current week or first month of year.
        int year = date.getYear();
        int monthOfYear = date.getMonthValue();
        int currentDayOfWeek = date.getDayOfWeek().getValue();
        ZoneId zone = date.getZone();
        if(drawDailyGraph){
            date = ZonedDateTime.of(year, monthOfYear, date.getDayOfMonth(), 12, 0, 0, 0, zone);
            //Set date to first day of week.
            date = date.minusDays(currentDayOfWeek-1);
        }
        else if(drawMonthlyGraph){
            date = ZonedDateTime.of(year, monthOfYear, 1, 12, 0, 0 ,0, zone);
            //Set date to first month of year.
            date = date.minusMonths(monthOfYear-1);
        }
    }

    public void setDataMap(HashMap<ZonedDateTime, Integer> dataMap) {
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

    private String getTextForDataPoint(ZonedDateTime date){
        String text = "";
        if(drawDailyGraph){
            text = date.getDayOfMonth() + "." + date.getMonthValue();
        }
        else if(drawMonthlyGraph){
            text = String.valueOf(date.getMonthValue());
        }
        return text;
    }

    //All different Paints used in drawing graph.
    private void initPaints(){
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(Color.rgb(173, 201, 139));

        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.rgb(173, 201, 139));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(10);

        horizontalLinesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        horizontalLinesPaint.setColor(Color.rgb(200, 200, 200));
        horizontalLinesPaint.setStyle(Paint.Style.STROKE);
        horizontalLinesPaint.setStrokeWidth(2);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStrokeWidth(1);
        textPaint.setTextSize(40);

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.rgb(180, 180, 180));
        axisPaint.setStyle(Paint.Style.STROKE);
        axisPaint.setStrokeWidth(5);
    }
}
