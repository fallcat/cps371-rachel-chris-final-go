package edu.gordon.cs.betago;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

/**
 * Created by weiqiuyou on 4/10/16.
 */
public class BoardView extends View {
    //properties
    private enum Stone {EMPTY, BLACK, WHITE}
    private Paint myPaint = new Paint();
    private Stone [][] board = new Stone [19][19];
    private char [] coordChars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T'};
    private List<Integer> removeX = new ArrayList<Integer>();
    private List<Integer> removeY = new ArrayList<Integer>();
    Bitmap image;
    boolean black = true;
    boolean submitted = false;
    boolean started = false;
    boolean label = true;
    Stone winner = Stone.EMPTY;

    private int canvasWidth;
    private int canvasHeight;
    private int x0;
    private int y0;
    private int blockWidth;
    private int stoneSize;
    private int starRadius;
    private int xNow;
    private int yNow;
    private int xNext;
    private int yNext;
    int turnStoneSize;
    float textSize;
    float textWidth;
    float textHeight;
    Context thisContext;

    public BoardView(Context context) {
        super(context);
        thisContext = context;
        init(null, 0);
    }

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        thisContext = context;
        init(attrs, 0);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        thisContext = context;
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        myPaint.setColor(Color.BLACK);
        myPaint.setStrokeWidth(2);
        for (int i = 0; i < 19; i ++)
            for (int j = 0; j < 19; j ++)
                board[i][j] = Stone.EMPTY;
    }

    /* Submit the move
     */
    public void submit(){
        submitted = true;
        removeCapture(xNow, yNow);
        invalidate();
    }

    /* Pass the move
     */
    public void pass() {
        black = black ? false : true;
        if (!submitted) {
            board[xNow][yNow] = Stone.EMPTY;
        }
        invalidate();
    }

    /* Resign
     */
    public void resign() {
        if (!submitted)
            board[xNow][yNow] = Stone.EMPTY;
        if (black && !submitted || !black && submitted)
            winner = Stone.WHITE;
        else
            winner = Stone.BLACK;
        invalidate();
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
        textSize = canvasWidth/35;
        turnStoneSize = blockWidth*2;


        // background
        Bitmap temp;
        temp = BitmapFactory.decodeResource(getResources(), R.drawable.kaya_background);
        image = Bitmap.createScaledBitmap(temp, canvasWidth, canvasWidth, true);
        canvas.drawBitmap(image, 0, y0 - 19 * blockWidth, null);

        // lines
        myPaint.setColor(Color.BLACK);
        myPaint.setStrokeWidth(canvasWidth / 300);
        for (int i = 0; i < 19; i ++) {
            // horizontal lines
            canvas.drawLine(x0, y0-i*blockWidth, x0+18*blockWidth, y0-i*blockWidth, myPaint);
            // vertical lines
            canvas.drawLine(x0+i*blockWidth, y0, x0+i*blockWidth, y0-18*blockWidth, myPaint);
        }

        // label
        myPaint.setStrokeWidth(canvasWidth / 200);
        myPaint.setTextSize(textSize);
        myPaint.setStyle(Paint.Style.FILL);

        if (label)
            for (int i = 0; i < 19; i ++) {
                textWidth = myPaint.measureText(new Integer(i+1).toString());
                canvas.drawText(new Integer(i+1).toString(), (float)(x0-0.5*blockWidth-0.5*textWidth),
                        (float)(y0-i*blockWidth+0.5*textWidth), myPaint);
                canvas.drawText(new Integer(i+1).toString(), (float)(x0+18.5*blockWidth-0.5*textWidth),
                        (float)(y0-i*blockWidth+0.5*textWidth), myPaint);
                textWidth = myPaint.measureText(String.valueOf(coordChars[i]));
                canvas.drawText(String.valueOf(coordChars[i]), (float)(x0+i*blockWidth-0.5*textWidth),
                        (float)(y0+0.5*blockWidth+0.5*textWidth), myPaint);
                canvas.drawText(String.valueOf(coordChars[i]), (float)(x0+i*blockWidth-0.5*textWidth),
                        (float)(y0-18.5*blockWidth+0.5*textWidth), myPaint);
            }

        // stars
        myPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < 3; i ++)
            for (int j = 0; j < 3; j ++)
                canvas.drawCircle(x0+(3+i*6)*blockWidth, y0-(3+j*6)*blockWidth, starRadius, myPaint);

        // stones
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

        Rect bounds = new Rect();
        int text_height = 0;
        int text_width = 0;

        myPaint.setTypeface(Typeface.DEFAULT);// your preference here
        myPaint.setTextSize((int)(turnStoneSize * 0.8));// have this the same as your text size
        String text = "";

        if (winner == Stone.EMPTY) {
            if (black && !submitted || !black && submitted) {
                text = "Black's Move";
                temp = BitmapFactory.decodeResource(getResources(), R.drawable.black_slate);
            }
            else {
                text = "White's Move";
                temp = BitmapFactory.decodeResource(getResources(), R.drawable.white_shell);
            }
            myPaint.getTextBounds(text, 0, text.length(), bounds);
            textHeight = bounds.height();
            textWidth = bounds.width();
            canvas.drawText(text, canvasWidth-turnStoneSize-textWidth-(int)(0.5*blockWidth),
                    y0-19*blockWidth-myPaint.getFontMetrics().descent, myPaint);
            image = Bitmap.createScaledBitmap(temp, turnStoneSize, turnStoneSize, true);
            canvas.drawBitmap(image, canvasWidth - turnStoneSize, y0 - 19 * blockWidth - turnStoneSize, null);
        }

        else {
            if (winner == Stone.BLACK) {
                text = "Black wins by resignation";
            }
            else {
                text = "White wins by resignation";
            }
            myPaint.getTextBounds(text, 0, text.length(), bounds);
            textHeight = bounds.height();
            textWidth = bounds.width();
            canvas.drawText(text, canvasWidth / 2 - textWidth / 2,
                    //y0 + blockWidth + Math.abs(myPaint.getFontMetrics().ascent) +
                    y0-19*blockWidth-myPaint.getFontMetrics().descent, myPaint);
                    //Math.abs(myPaint.getFontMetrics().descent)*2, myPaint);
        }

        //removeX.clear();
        //removeY.clear();

        if (started) {
            myPaint.setStrokeWidth(canvasWidth / 200);
            myPaint.setStyle(Paint.Style.STROKE);
            if (board[xNow][yNow] == Stone.BLACK) {
                myPaint.setColor(Color.WHITE);
                canvas.drawCircle(x0+xNow*blockWidth, y0-yNow*blockWidth, (int)(stoneSize*0.3), myPaint);
            }
            else if (board[xNow][yNow] == Stone.WHITE) {
                myPaint.setColor(Color.BLACK);
                canvas.drawCircle(x0+xNow*blockWidth, y0-yNow*blockWidth, (int)(stoneSize*0.3), myPaint);
            }
            /*boolean select = selectString(xNow, yNow, board[xNow][yNow]);
            //if (selectString(xNow, yNow, board[xNow][yNow])) {
                myPaint.setColor(Color.RED);
                myPaint.setStyle(Paint.Style.FILL);
                for (int i = 0; i < removeX.size(); i ++)
                    canvas.drawCircle(x0+removeX.get(i)*blockWidth, y0-removeY.get(i)*blockWidth,
                            (int)(stoneSize*0.1), myPaint);
            //}
            System.out.println("select"+select);*/

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP) {
            started = true;
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (winner == Stone.EMPTY) {
                // only works if the touch event is within boundary of the board
                if (x >= x0-blockWidth/2 && x <= x0+18.5*blockWidth
                        && y <= y0+blockWidth/2 && y >= y0-18.5*blockWidth) {
                    xNext = (int)Math.round((x-x0)/((double)blockWidth));
                    yNext = (int)Math.round((y0-y)/((double)blockWidth));

                    // Can only put down stone if the intersection is empty
                    if (board[xNext][yNext] == Stone.EMPTY) {
                        // if not yet submitted, delete original stone, change to new stone
                        if(!submitted) {
                            board[xNow][yNow] = Stone.EMPTY;
                            Stone tempColor = black ? Stone.BLACK : Stone.WHITE;
                            if (!isLegalMove(xNext, yNext, tempColor)) {
                                board[xNow][yNow] = black ? Stone.BLACK : Stone.WHITE;
                                CharSequence text = "You can't commit suicide!";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(thisContext, text, duration);
                                toast.show();
                            }
                            else {
                                board[xNext][yNext] = black ? Stone.BLACK : Stone.WHITE;
                                xNow = xNext;
                                yNow = yNext;
                            }
                        }
                        // if already submitted, keep original stone, add new stone
                        else {
                            submitted = false;
                            black = black ? false : true;
                            Stone tempColor = black ? Stone.BLACK : Stone.WHITE;
                            if (!isLegalMove(xNext, yNext, tempColor)) {
                                submitted = true;
                                black = black ? false : true;
                                CharSequence text = "You can't commit suicide!";
                                int duration = Toast.LENGTH_SHORT;
                                Toast toast = Toast.makeText(thisContext, text, duration);
                                toast.show();
                            }
                            else {
                                board[xNext][yNext] = black ? Stone.BLACK : Stone.WHITE;
                                xNow = xNext;
                                yNow = yNext;
                            }
                        }
                    }
                }
            }
        }
        invalidate();
        return true;
    }

    /* Check if the move is legal
     * Parameters: x - x coordinate of the stone to be checked
     *             y - y coordinate of the stone to be checked
     *             color - color that the stone is supposed to be
     * return true if the move is legal, return false if the move is illegal
     */
    private boolean isLegalMove(int x, int y, Stone color) {
        if (board[x][y] == Stone.EMPTY) {
            board[x][y] = color;
            Stone otherColor = getOther(color);
            removeX.clear();
            removeY.clear();
            // if the string with the stone being put down cannot be immediately captured
            if (!selectString(x, y, color)) {
                board[x][y] = Stone.EMPTY;
                return true;
            }
            // if the string can be captured, see if other stones around can be captured
            else {
                if (x > 0 && board[x-1][y] == otherColor) {
                    removeX.clear();
                    removeY.clear();
                    if (selectString(x-1, y, otherColor)) {
                        board[x][y] = Stone.EMPTY;
                        return true;
                    }
                }
                if (y > 0 && board[x][y-1] == otherColor) {
                    removeX.clear();
                    removeY.clear();
                    if (selectString(x, y-1, otherColor)) {
                        board[x][y] = Stone.EMPTY;
                        return true;
                    }
                }
                if (x < 18 && board[x+1][y] == otherColor) {
                    removeX.clear();
                    removeY.clear();
                    if (selectString(x+1, y, otherColor)) {
                        board[x][y] = Stone.EMPTY;
                        return true;
                    }
                }
                if (y < 18 && board[x][y+1] == otherColor) {
                    removeX.clear();
                    removeY.clear();
                    if (selectString(x, y+1, otherColor)) {
                        board[x][y] = Stone.EMPTY;
                        return true;
                    }
                }
            }
            board[x][y] = Stone.EMPTY;
        }
        return false;
    }

    /* Remove stones captured by the stone at (x,y)
     * Parameters: x - x coordinate of the stone just been put down
     *             y - y coordinate of the stone just been put down
     */
    private void removeCapture(int x, int y) {
        Stone otherColor = getOther(board[x][y]);
        if (x > 0 && board[x-1][y] == otherColor) {
            removeX.clear();
            removeY.clear();
            if (selectString(x-1, y, otherColor)) {
                for (int i = 0; i < removeX.size(); i++) {
                    board[removeX.get(i)][removeY.get(i)] = Stone.EMPTY;
                }
            }

        }
        if (y > 0 && board[x][y-1] == otherColor) {
            removeX.clear();
            removeY.clear();
            if (selectString(x, y-1, otherColor)) {
                for (int i = 0; i < removeX.size(); i++) {
                    board[removeX.get(i)][removeY.get(i)] = Stone.EMPTY;
                }
            }
        }
        if (x < 18 && board[x+1][y] == otherColor) {
            removeX.clear();
            removeY.clear();
            if (selectString(x+1, y, otherColor)) {
                for (int i = 0; i < removeX.size(); i++) {
                    board[removeX.get(i)][removeY.get(i)] = Stone.EMPTY;
                }
            }
        }
        if (y < 18 && board[x][y+1] == otherColor) {
            removeX.clear();
            removeY.clear();
            if (selectString(x, y+1, otherColor)) {
                for (int i = 0; i < removeX.size(); i++) {
                    board[removeX.get(i)][removeY.get(i)] = Stone.EMPTY;
                }
            }
        }
    }
    /* Select the string of stones connected to stone at (x,y)
     * Parameters: x - x coordinate of the stone to be selected
     *             y - y coordinate of the stone to be selected
     *             color - color of the stones to be selected
     * Return:     true - has no liberty and can be captured
     *             false - has liberty and cannot be captured
     */
    private boolean selectString(int x, int y, Stone color) {
        if (board[x][y] == color) {
            if (isInRemoveList(x, y))
                return true;
            else {
                boolean a, b, c, d;
                a = b = c = d = true;
                removeX.add(x);
                removeY.add(y);
                if (x > 0)
                    a = selectString(x-1, y, color);
                if (y > 0)
                    b = selectString(x, y-1, color);
                if (x < 18)
                    c = selectString(x+1, y, color);
                if (y < 18)
                    d = selectString(x, y+1, color);
                return (a && b && c && d);
            }
        }
        else if (board[x][y] == Stone.EMPTY){
            return false;
        }
        else
            return true;

    }

    /* Check if the stone is already in remove list
     * Parameters: x - x coordinate of the stone to be checked
     *             y - y coordinate of the stone to be checked
     * Return:     true - is in remove list
     *             false - not in remove list
     */
    boolean isInRemoveList(int x, int y) {
        for (int i = 0; i < removeX.size(); i ++ ) {
            if (removeX.get(i) == x && removeY.get(i) == y)
                return true;
        }
        return false;
    }

    /* Get the other colored stone
     * Parameter: currentColor - current stone color
     * Return:    Stone - color other than current
     */
    private Stone getOther(Stone currentColor) {
        if (currentColor == Stone.BLACK)
            return Stone.WHITE;
        else if (currentColor == Stone.WHITE)
            return Stone.BLACK;
        else
            return Stone.EMPTY;
    }


}
