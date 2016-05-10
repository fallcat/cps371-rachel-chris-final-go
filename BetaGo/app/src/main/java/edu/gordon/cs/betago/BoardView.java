package edu.gordon.cs.betago;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
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
    private Paint myPaint2 = new Paint();
    private Paint alphaPaint = new Paint();
    private Stone [][] board = new Stone [19][19];
    private char [] coordChars = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M', 'N',
        'O', 'P', 'Q', 'R', 'S', 'T'};
    private List<Integer> removeX = new ArrayList<Integer>();
    private List<Integer> removeY = new ArrayList<Integer>();
    private List<Integer> emptyX = new ArrayList<Integer>();
    private List<Integer> emptyY = new ArrayList<Integer>();
    private List<Integer> blackX = new ArrayList<Integer>();
    private List<Integer> blackY = new ArrayList<Integer>();
    private List<Integer> whiteX = new ArrayList<Integer>();
    private List<Integer> whiteY = new ArrayList<Integer>();
    private Stone emptyBelongs = Stone.EMPTY;
    Bitmap image;
    boolean black = true;
    boolean submitted = false;
    boolean started = false;
    boolean coord = true;
    Stone winner = Stone.EMPTY;
    int result = 404;

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
    private int turnStoneSize;
    private float textSize;
    private float textWidth;
    private Context thisContext;
    private int numPass = 0;
    private int blackTerr = 0;
    private int whiteTerr = 0;
    private int crosslineX = -1;
    private int crosslineY = -1;
    Bitmap temp;
    Bitmap blackStoneBitmap;
    Bitmap whiteStoneBitmap;

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
    public boolean submit(){
        if (submitted)
            return false;
        else {
            submitted = true;
            removeCapture(xNow, yNow);
            if (numPass > 0)
                numPass = 0;
            invalidate();
            return true;
        }
    }

    /* Pass the move
     */
    public void pass() {
        black = black ? false : true;
        if (!submitted) {
            board[xNow][yNow] = Stone.EMPTY;
        }
        numPass ++;
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

    /* Evaluate
     * Return number of points black wins
     */
    public int evaluate() {
        blackX.clear();
        blackY.clear();
        whiteX.clear();
        whiteY.clear();
        System.out.println("Start evaluating!");
        for (int i = 0; i < 19; i ++)
            for (int j = 0; j < 19; j ++) {
                System.out.println("--------" + i + "," + j + "--------");
                if (!isInBlackList(i, j) && !isInWhiteList(i, j)) {
                    if (board[i][j] == Stone.BLACK) {
                        blackX.add(i);
                        blackY.add(j);
                        System.out.println("black added (" + i + "," + j + ")");
                    }
                    else if (board[i][j] == Stone.WHITE) {
                        whiteX.add(i);
                        whiteY.add(j);
                        System.out.println("white added (" + i + "," + j + ")");
                    }
                    else {
                        emptyX.clear();
                        emptyY.clear();
                        emptyBelongs = Stone.EMPTY;
                        boolean empty = selectEmpty(i,j);
                        System.out.println("return by Empty:" + empty);
                        System.out.println("emptyBelongs:" + emptyBelongs);
                        if (empty) {
                            if (emptyBelongs == Stone.BLACK)
                                for (int k = 0; k < emptyX.size(); k ++) {
                                    blackX.add(emptyX.get(k));
                                    blackY.add(emptyY.get(k));
                                }
                            else
                                for (int k = 0; k < emptyX.size(); k ++) {
                                    whiteX.add(emptyX.get(k));
                                    whiteY.add(emptyY.get(k));
                                }
                        }
                        else
                            return 404;
                    }
                }
            }
        blackTerr = blackX.size();
        System.out.println("blackterr = " + blackTerr);
        whiteTerr = whiteX.size();
        System.out.println("whiteterr = " + whiteTerr);
        return blackTerr-whiteTerr;
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


        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(thisContext);
        String blackStones = getPrefs.getString("prefBlackStones", "3");
        String whiteStones = getPrefs.getString("prefWhiteStones", "3");
        String background = getPrefs.getString("prefBoard", "0");
        String blackName = getPrefs.getString("prefBlackName", "Black");
        String whiteName = getPrefs.getString("prefWhiteName", "White");
        coord = getPrefs.getBoolean("prefCoord", true);

        switch (blackStones) {
            case "0":
                blackStoneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black_glass);
                break;
            case "1":
                blackStoneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black_night);
                break;
            case "2":
                blackStoneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black_plain);
                break;
            case "3":
                blackStoneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black_slate);
                break;
            case "4":
                blackStoneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black_wornglass);
                break;
            default:
                blackStoneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.black_slate);
        }

        switch (whiteStones) {
            case "0":
                whiteStoneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_glass);
                break;
            case "1":
                whiteStoneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_night);
                break;
            case "2":
                whiteStoneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_plain);
                break;
            case "3":
                whiteStoneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_shell);
                break;
            case "4":
                whiteStoneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_wornglass);
                break;
            default:
                whiteStoneBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.white_shell);
        }


        // background
        switch (background) {
            case "0":
                temp = BitmapFactory.decodeResource(getResources(), R.drawable.kaya_background);
                break;
            case "1":
                temp = BitmapFactory.decodeResource(getResources(), R.drawable.wood);
                break;
            case "2":
                temp = BitmapFactory.decodeResource(getResources(), R.drawable.wooden_background);
                break;
            default:
                temp = BitmapFactory.decodeResource(getResources(), R.drawable.kaya_background);
        }
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

        if (coord)
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
        for (int i = 0; i < 3; i ++)
            for (int j = 0; j < 3; j ++)
                canvas.drawCircle(x0+(3+i*6)*blockWidth, y0-(3+j*6)*blockWidth, starRadius, myPaint);

        // cross lines
        if (crosslineX != -1) {
            myPaint.setStrokeWidth(canvasWidth / 300);
            myPaint.setColor(Color.YELLOW);
            // horizontal lines
            canvas.drawLine(x0, y0-crosslineY*blockWidth, x0+18*blockWidth, y0-crosslineY*blockWidth, myPaint);
            // vertical lines
            canvas.drawLine(x0+crosslineX*blockWidth, y0, x0+crosslineX*blockWidth, y0-18*blockWidth, myPaint);
            alphaPaint.setAlpha(50);
            // now lets draw using alphaPaint instance
            if (black && !submitted) {
                image = Bitmap.createScaledBitmap(blackStoneBitmap, stoneSize, stoneSize, true);
            }
            else {
                image = Bitmap.createScaledBitmap(whiteStoneBitmap, stoneSize, stoneSize, true);
            }
            canvas.drawBitmap(image, x0+crosslineX*blockWidth-stoneSize/2,
                    y0-crosslineY*blockWidth-stoneSize/2, alphaPaint);

        }


        // stones
        for (int i = 0; i < 19; i ++) {
            for (int j = 0; j < 19; j ++) {
                if (board[i][j] == Stone.BLACK) {
                    image = Bitmap.createScaledBitmap(blackStoneBitmap, stoneSize, stoneSize, true);
                    canvas.drawBitmap(image, x0 + i*blockWidth - stoneSize / 2,
                            y0 - j*blockWidth - stoneSize / 2, null);
                }
                else if (board[i][j] == Stone.WHITE) {
                    image = Bitmap.createScaledBitmap(whiteStoneBitmap, stoneSize, stoneSize, true);
                    canvas.drawBitmap(image, x0 + i*blockWidth - stoneSize / 2,
                            y0 - j*blockWidth - stoneSize / 2, null);
                }
            }
        }

        myPaint.setColor(Color.BLACK);

        Rect bounds = new Rect();

        myPaint.setTypeface(Typeface.DEFAULT);// your preference here
        myPaint.setTextSize((int)(turnStoneSize * 0.8));// have this the same as your text size
        String text = "";

        if (winner == Stone.EMPTY) {
            if (black && !submitted || !black && submitted) {
                text = blackName+"'s Move";
                image = Bitmap.createScaledBitmap(blackStoneBitmap, turnStoneSize, turnStoneSize, true);
            }
            else {
                text = whiteName+"'s Move";
                image = Bitmap.createScaledBitmap(whiteStoneBitmap, turnStoneSize, turnStoneSize, true);
            }
            myPaint.getTextBounds(text, 0, text.length(), bounds);
            textWidth = bounds.width();
            canvas.drawText(text, canvasWidth-turnStoneSize-textWidth-(int)(0.5*blockWidth),
                    y0-19*blockWidth-myPaint.getFontMetrics().descent, myPaint);
            canvas.drawBitmap(image, canvasWidth - turnStoneSize, y0 - 19 * blockWidth - turnStoneSize, null);
        }

        else {
            if (winner == Stone.BLACK) {
                text = blackName+" wins by resignation";
            }
            else {
                text = whiteName+" wins by resignation";
            }
            myPaint.getTextBounds(text, 0, text.length(), bounds);
            textWidth = bounds.width();
            canvas.drawText(text, canvasWidth / 2 - textWidth / 2,
                    y0-19*blockWidth-myPaint.getFontMetrics().descent, myPaint);
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

        /*myPaint.setColor(Color.RED);
        myPaint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < emptyX.size(); i ++)
            canvas.drawCircle(x0+emptyX.get(i)*blockWidth, y0-emptyY.get(i)*blockWidth,
                    (int)(stoneSize*0.1), myPaint);*/

        ////////////////////////////////////////////////////
        /*if (numPass == 2) {
            result = evaluate();
            if (result != 404) {
                myPaint.setColor(Color.BLACK);
                myPaint.setStyle(Paint.Style.FILL);
                myPaint2.setColor(Color.GRAY);
                myPaint2.setStyle(Paint.Style.STROKE);
                myPaint2.setStrokeWidth(canvasWidth/300);
                for (int i = 0; i < blackX.size(); i ++) {
                    canvas.drawRect(x0 + blackX.get(i)*blockWidth - stoneSize/2,
                            y0 - blackY.get(i)*blockWidth - stoneSize/2,
                            x0 + blackX.get(i)*blockWidth + stoneSize/2,
                            y0 - blackY.get(i)*blockWidth + stoneSize/2, myPaint);
                    canvas.drawRect(x0 + blackX.get(i)*blockWidth - stoneSize/2,
                            y0 - blackY.get(i)*blockWidth - stoneSize/2,
                            x0 + blackX.get(i)*blockWidth + stoneSize/2,
                            y0 - blackY.get(i)*blockWidth + stoneSize/2, myPaint2);
                }
                myPaint.setColor(Color.WHITE);
                for (int i = 0; i < whiteX.size(); i ++) {
                    canvas.drawRect(x0 + whiteX.get(i)*blockWidth - stoneSize/2,
                            y0 - whiteY.get(i)*blockWidth - stoneSize/2,
                            x0 + whiteX.get(i)*blockWidth + stoneSize/2,
                            y0 - whiteY.get(i)*blockWidth + stoneSize/2, myPaint);
                    canvas.drawRect(x0 + blackX.get(i)*blockWidth - stoneSize/2,
                            y0 - whiteY.get(i)*blockWidth - stoneSize/2,
                            x0 + whiteX.get(i)*blockWidth + stoneSize/2,
                            y0 - whiteY.get(i)*blockWidth + stoneSize/2, myPaint2);
                }
            }
        }*/

    }

    public int getResult() {
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (x >= x0-blockWidth/2 && x <= x0+18.5*blockWidth
                    && y <= y0+blockWidth/2 && y >= y0-18.5*blockWidth) {
                crosslineX = (int) Math.round((x - x0) / ((double) blockWidth));
                crosslineY = (int) Math.round((y0 - y) / ((double) blockWidth));
            }
        }
        if (action == MotionEvent.ACTION_MOVE) {
            int x = (int) event.getX();
            int y = (int) event.getY();
            if (x >= x0-blockWidth/2 && x <= x0+18.5*blockWidth
                    && y <= y0+blockWidth/2 && y >= y0-18.5*blockWidth) {
                crosslineX = (int) Math.round((x - x0) / ((double) blockWidth));
                crosslineY = (int) Math.round((y0 - y) / ((double) blockWidth));
            }
        }
        if (action == MotionEvent.ACTION_UP) {
            started = true;
            crosslineX = -1;
            crosslineY = -1;
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
                        ((Play)getContext()).enableSubmit();
                        // if not yet submitted, delete original stone, change to new stone
                        if(!submitted) {
                            board[xNow][yNow] = Stone.EMPTY;
                            Stone tempColor = black ? Stone.BLACK : Stone.WHITE;
                            if (!isLegalMove(xNext, yNext, tempColor)) {
                                board[xNow][yNow] = black ? Stone.BLACK : Stone.WHITE;
                                CharSequence text = "You can't commit suicide!";
                                Toast toast = Toast.makeText(thisContext, text, Toast.LENGTH_SHORT);
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
                                Toast toast = Toast.makeText(thisContext, text, Toast.LENGTH_SHORT);
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
            if (selectString(x, y + 1, otherColor)) {
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
                    a = selectString(x - 1, y, color);
                if (y > 0)
                    b = selectString(x, y - 1, color);
                if (x < 18)
                    c = selectString(x + 1, y, color);
                if (y < 18)
                    d = selectString(x, y + 1, color);
                return (a && b && c && d);
            }
        }
        else if (board[x][y] == Stone.EMPTY){
            return false;
        }
        else
            return true;

    }

    /* Select empty territory
     * Parameters: x - x coordinate of the empty space to be selected
     *             y - y coordinate of the empty space to be selected
     * Return:     true - empty space is selected
     *             false - empty space is not selected
     */
    private boolean selectEmpty(int x, int y) {
        if (board[x][y] == Stone.EMPTY) {
            if (isInEmptyList(x, y))
                return true;
            else {
                boolean a, b, c, d;
                boolean returnValue = true;
                emptyX.add(x);
                emptyY.add(y);
                System.out.println("x:" + x + ",y:" + y);
                if (x > 0) {
                    a = selectEmpty(x - 1, y);
                    System.out.println("a=" + a);
                    //if (a != Stone.EMPTY && a != emptyBelongs && emptyBelongs != Stone.EMPTY) {
                    if (a) {
                        returnValue = a;
                        System.out.println("returnvalue = a");
                    }
                }
                if (y > 0) {
                    b = selectEmpty(x, y - 1);
                    System.out.println("b=" + b);
                    //if (b != Stone.EMPTY && b != emptyBelongs && emptyBelongs != Stone.EMPTY ) {
                    if (b) {
                        returnValue = b;
                        System.out.println("returnvalue = b");
                    }
                }
                if (x < 18) {
                    c = selectEmpty(x+1, y);
                    System.out.println("c=" + c);
                    //if (c != Stone.EMPTY && c != emptyBelongs && emptyBelongs != Stone.EMPTY) {
                    if (c) {
                        returnValue = c;
                        System.out.println("returnvalue = c");
                    }
                }
                if (y < 18) {
                    d = selectEmpty(x, y+1);
                    System.out.println("d=" + d);
                    //if (d != Stone.EMPTY && d != emptyBelongs && emptyBelongs != Stone.EMPTY) {
                    if (d) {
                        returnValue = d;
                        System.out.println("returnvalue = d");
                    }
                }
                return returnValue;
            }
        }
        else if (board[x][y] == emptyBelongs) {
            return true;
        }
        else if (emptyBelongs == Stone.EMPTY) {
            emptyBelongs = board[x][y];
            return true;
        }
        return false;
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

    /* Check if the stone is already in black list
     * Parameters: x - x coordinate of the stone to be checked
     *             y - y coordinate of the stone to be checked
     * Return:     true - is in remove list
     *             false - not in remove list
     */
    boolean isInBlackList(int x, int y) {
        for (int i = 0; i < blackX.size(); i ++ ) {
            if (blackX.get(i) == x && blackY.get(i) == y)
                return true;
        }
        return false;
    }

    /* Check if the stone is already in white list
     * Parameters: x - x coordinate of the stone to be checked
     *             y - y coordinate of the stone to be checked
     * Return:     true - is in remove list
     *             false - not in remove list
     */
    boolean isInWhiteList(int x, int y) {
        for (int i = 0; i < whiteX.size(); i ++ ) {
            if (whiteX.get(i) == x && whiteY.get(i) == y)
                return true;
        }
        return false;
    }

    /* Check if the stone is already in empty list
     * Parameters: x - x coordinate of the stone to be checked
     *             y - y coordinate of the stone to be checked
     * Return:     true - is in empty list
     *             false - not in empty list
     */
    boolean isInEmptyList(int x, int y) {
        for (int i = 0; i < emptyX.size(); i ++ ) {
            if (emptyX.get(i) == x && emptyY.get(i) == y)
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
