package edu.gordon.cs.betago;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by weiqiuyou on 4/10/16.
 */
public class BoardView extends View {
    //properties
    private enum Stone {EMPTY, BLACK, WHITE};
    private Paint myPaint = new Paint();
    private List<Integer> intX = new ArrayList<Integer>();
    private List<Integer> intY = new ArrayList<Integer>();
    private Stone [][] board = new Stone [19][19];
    public int singleX = 30;
    public int singleY = 30;
    Bitmap image;
    boolean black = true;
    boolean submit = false;
    //private Bitmap image;

    private int canvasWidth;
    private int canvasHeight;
    private int x0;
    private int y0;
    private int blockWidth;
    private int stoneSize;
    private int starRadius;
    private int xNow;
    private int yNow;

    public BoardView(Context context) {
        super(context);
        init(null, 0);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context);
        init(attrs, 0);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        myPaint.setColor(Color.BLACK);
        myPaint.setStrokeWidth(2);
        for (int i = 0; i < 19; i ++)
            for (int j = 0; j < 19; j ++)
                board[i][j] = Stone.EMPTY;
    }

    public void setSubmit(boolean s){
        submit = s;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // coordinates of the origins on the lower left corner
        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();
        x0 = canvasWidth/20;
        y0 = canvasHeight/2+canvasWidth/20*9;
        blockWidth = x0;
        stoneSize = (int)(blockWidth*0.99);
        starRadius = canvasWidth/150;


        // background
        Bitmap temp;
        temp = BitmapFactory.decodeResource(getResources(), R.drawable.kaya_background);
        image = Bitmap.createScaledBitmap(temp, canvasWidth, canvasHeight, true);
        canvas.drawBitmap(image, 0, 0, null);

        // lines
        myPaint.setColor(Color.BLACK);
        myPaint.setStrokeWidth(canvasWidth/300);
        // horizontal lines
        for (int i = 0; i < 19; i ++)
            canvas.drawLine(x0, y0-i*blockWidth,
                    x0+18*blockWidth, y0-i*blockWidth, myPaint);
        // vertical lines
        for (int i = 0; i < 19; i ++)
            canvas.drawLine(x0+i*blockWidth, y0,
                    x0+i*blockWidth, y0-18*blockWidth, myPaint);
        // stars
        for (int i = 0; i < 3; i ++)
            for (int j = 0; j < 3; j ++)
                canvas.drawCircle(x0+(3+i*6)*blockWidth, y0-(3+j*6)*blockWidth, starRadius, myPaint);

        for (int i = 0; i < 19; i ++) {
            for (int j = 0; j < 19; j ++) {
                if (board[i][j] == Stone.BLACK) {
                    temp = BitmapFactory.decodeResource(getResources(), R.drawable.black_slate);
                    image = Bitmap.createScaledBitmap(temp, stoneSize, stoneSize, true);
                    canvas.drawBitmap(image, x0 + i*blockWidth - stoneSize / 2,
                            y0 - j*blockWidth - stoneSize / 2, null);
                }
                else if (board[i][j] == Stone.WHITE) {
                    temp = BitmapFactory.decodeResource(getResources(), R.drawable.white_shell);
                    image = Bitmap.createScaledBitmap(temp, stoneSize, stoneSize, true);
                    canvas.drawBitmap(image, x0 + i*blockWidth - stoneSize / 2,
                            y0 - j*blockWidth - stoneSize / 2, null);
                }
            }
        }


        // stones placed on by user
        /*black = true;
        for(int i = 0; i < intX.size(); i ++) {
            if (black) {
                temp = BitmapFactory.decodeResource(getResources(), R.drawable.black_slate);
                image = Bitmap.createScaledBitmap(temp, stoneSize, stoneSize, true);
                canvas.drawBitmap(image, intX.get(i)-stoneSize/2, intY.get(i)-stoneSize/2, null);
                black = false;
            }
            else {
                temp = BitmapFactory.decodeResource(getResources(), R.drawable.white_shell);
                image = Bitmap.createScaledBitmap(temp, stoneSize, stoneSize, true);
                canvas.drawBitmap(image, intX.get(i)-stoneSize/2, intY.get(i)-stoneSize/2, null);
                black = true;
            }

        }*/


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            if (x >= x0-blockWidth/2 && x <= x0+18.5*blockWidth
                    && y <= y0+blockWidth/2 && y >= y0-18.5*blockWidth) {
                /*if(!submit) {
                    board[xNow][yNow]=Stone.EMPTY;

                }
                else{
                    submit = false;
                    black = black ? false : true;
                }*/

                xNow = (int)Math.round((x-x0)/((double)blockWidth));
                yNow = (int)Math.round((y0-y)/((double)blockWidth));
                if (board[xNow][yNow] == Stone.EMPTY) {
                    board[xNow][yNow] = black ? Stone.BLACK : Stone.WHITE;
                    black = black ? false : true;
                }
            }
        }
        invalidate();
        return true;
    }


}
