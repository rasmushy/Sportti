package fi.sportti.app;

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

import fi.sportti.app.ui.utilities.TimeConversionUtilities;

/**
 *@author Jukka-Pekka Jaakkola
 * Own class for creating custom views so we can draw graphs. Different possible graph types are
 * bar and line graphs. However in our app only bar graphs are used.
 * Basic idea on how to build own custom Views learnt from this article.
 * How the coordinates work, how to draw lines on canvas etc..
 * https://medium.com/@mayurjajoomj/custom-graphs-custom-view-android-862e16813cc
 */

@RequiresApi(api = Build.VERSION_CODES.O)
public class CustomGraph extends View  {

    /** Constant variable for graph type that has data displayed for days of week. */
    public static final int DAYS_OF_WEEK = 7;
    /** Constant variable for graph type that has data displayed for months of year. */
    public static final int MONTHS_OF_YEAR = 12;
    /** Constant variable for bar graph style */
    public static final int BAR_GRAPH = 1;
    /** Constant variable for line graph style */
    public static final int LINE_GRAPH = 2;

    private final int xOffset = 100;
    private final int yTopOffset = 100;
    private final int yBottomOffset = 50;
    private ZonedDateTime date;
    private Boolean isInit = false;
    private Path path;
    private Paint axisPaint, barPaint, linePaint, textPaint, horizontalLinesPaint;
    private Canvas canvas;
    private int graphType, graphTimePeriod, graphMaxValue, rectWidth, viewWidth, viewHeight,
                origoX, origoY, graphHeight, graphWidth;
    private double oneHourHeight;
    private HashMap<ZonedDateTime, Integer> dataMap;

    /**
     * Constructor that has to be implemented because this class extends View.
     * Explanation from Android Developer documentation:
     * "To allow Android Studio to interact with your view, at a minimum you must provide
     * a constructor that takes a Context and an AttributeSet object as parameters.
     * This constructor allows the layout editor to create and edit an instance of your view."
     * @param context
     * @param attrs
     */
    public CustomGraph(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
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

    /**
     * Set different type for graph. Options are: bar graph or line graph
     * @param type use constants in CustomGraph class.
     * */
    public void setGraphType(int type){
        if(type == BAR_GRAPH){
            graphType = BAR_GRAPH;
        }
        else if(type == LINE_GRAPH){
            graphType = LINE_GRAPH;
        }
    }

    /**
     * Current time period set to graph. Options: days of week or months of year.
     * @return graphTimePeriod
     */
    public int getGraphTimePeriod(){
        return graphTimePeriod;
    }

    /**
     * Set different time periods for graph. Options: days of week or months of year.
     * @param timePeriod user constants in CustomGraph class.
     */
    public void setGraphTimePeriod(int timePeriod){
        if(timePeriod == DAYS_OF_WEEK){
            graphTimePeriod = DAYS_OF_WEEK;
            graphMaxValue = 5;
            rectWidth = graphWidth / (DAYS_OF_WEEK * 2);
        }
        else if(timePeriod == MONTHS_OF_YEAR){
            graphTimePeriod = MONTHS_OF_YEAR;
            graphMaxValue = 100;
            rectWidth = graphWidth / (MONTHS_OF_YEAR * 2);
        }
        date = ZonedDateTime.now();
    }

    /**
     * Show next time period in graph.
     */
    public void showNextPeriod(){
        if(drawDailyGraph()){
            date = date.plusDays(7);
        }
        else if(drawMonthlyGraph()){
            date = date.plusYears(1);
        }
    }

    /**
     * Show previous time period in graph.
     */
    public void showPreviousPeriod(){
        if(drawDailyGraph()){
            date = date.minusDays(7);
        }
        else if(drawMonthlyGraph()){
            date = date.minusYears(1);
        }
    }

    /**
     * Set data for graph. Data is stored in HashMap where date is key and value is total exercise time in minutes.
     * @param dataMap
     */
    public void setDataMap(HashMap<ZonedDateTime, Integer> dataMap) {
        this.dataMap = dataMap;
    }

    private void drawAxis() {
        //y-axis
        int startX = origoX;
        int startY = origoY;
        int stopX = origoX;
        int stopY = yTopOffset - 50;
        canvas.drawLine(startX, startY, stopX, stopY, axisPaint);

        //x-axis
        stopX = viewWidth - origoX;
        stopY = origoY;
        canvas.drawLine(startX, startY, stopX, stopY, axisPaint);

        //Text on top of y-axis.
        String text = getResources().getString(R.string.customgraph_y_axis_name);
        canvas.drawText(text, 30, 30, textPaint);
    }

    private void drawDataPoints(){
        //Draw data points for days of week or months of year.
        if(drawDailyGraph()){
            drawDataPointsForWeek();
        }
        else if(drawMonthlyGraph()){
            drawDataPointsForYear();
        }
        //At end if graph type is line, draw line that was built with Path object.
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
        if(drawDailyGraph()){
            //Set date to first day of week.
            date = TimeConversionUtilities.getFirstDayOfWeek(date);
        }
        else if(drawMonthlyGraph()){
            date = TimeConversionUtilities.getFirstDayOfMonth(date);
            //Set date to first month of year.
            int monthOfYear = date.getMonthValue();
            date = date.minusMonths(monthOfYear - 1);
        }
    }

    private String getTextForDataPoint(ZonedDateTime date){
        String text = "";
        if(drawDailyGraph()){
            text = date.getDayOfMonth() + "." + date.getMonthValue();
        }
        else if(drawMonthlyGraph()){
            text = String.valueOf(date.getMonthValue());
        }
        return text;
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

    private boolean drawDailyGraph(){
        return graphTimePeriod == DAYS_OF_WEEK;
    }

    private boolean drawMonthlyGraph(){
        return graphTimePeriod == MONTHS_OF_YEAR;
    }
}
