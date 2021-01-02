package com.example.rancho.chess.view;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rancho.chess.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

public class RenRenGoBang extends View {
    protected static int GRID_SIZE = 15;    //设置为国际标准
    protected static int GRID_WIDTH = 65; // 棋盘格的宽度
    protected static int CHESS_DIAMETER = 55; // 棋的直径
    protected static int mStartX;// 棋盘定位的左上角X
    protected static int mStartY;// 棋盘定位的左上角Y

    private static int[][] mGridArray; // 网格
    private Stack<String> storageArray;


    int wbflag = 1; //该下白棋了=2，该下黑棋了=1. 这里先下黑棋（黑棋以后设置为机器自动下的棋子）
    int mWinFlag = 0;
    int regFlag = 0;
    int putBlock = 0;//棋盘锁
    private final int BLACK = 1;
    private final int WHITE = 2;


    //private TextView mStatusTextView; //  根据游戏状态设置显示的文字
    private TextView mStatusTextView; //  根据游戏状态设置显示的文字

    private Bitmap btm1;
    private final Paint mPaint = new Paint();

    CharSequence mText;
    CharSequence STRING_WIN = "白棋胜  ";
    CharSequence STRING_LOSE = "黑棋胜  ";
    CharSequence STRING_EQUAL = "不会吧,还能下满棋盘连不到五个??  ";

    public RenRenGoBang(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);

        init();
    }

    //按钮监听器
    MyButtonListener myButtonListener;

    // 初始化黑白棋的Bitmap
    public void init() {
        storageArray = new Stack<>();
        myButtonListener = new MyButtonListener();
        wbflag = BLACK; //初始为先下黑棋
        mWinFlag = 0; //清空输赢标志。
        mGridArray = new int[GRID_SIZE - 1][GRID_SIZE - 1];


        Bitmap bitmap = Bitmap.createBitmap(CHESS_DIAMETER, CHESS_DIAMETER, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Resources r = this.getContext().getResources();

    }

    //设置显示的textView
    public void setTextView(TextView tv) {
        mStatusTextView = tv;
        //mStatusTextView.setVisibility(View.INVISIBLE);
    }

    //悔棋按钮
    private Button huiqi;
    //刷新那妞
    private Button refresh;

    //设置两个按钮
    public void setButtons(Button huiqi, Button refresh) {
        this.huiqi = huiqi;
        this.refresh = refresh;
        huiqi.setOnClickListener(myButtonListener);
        refresh.setOnClickListener(myButtonListener);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mStartX = w / 2 - GRID_SIZE * GRID_WIDTH / 2;
        mStartY = h / 2 - GRID_SIZE * GRID_WIDTH / 2;
    }

    /**
     * 点下出现棋子
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (putBlock == 0) {
            int x;
            int y;
            float x0 = GRID_WIDTH - (event.getX() - mStartX) % GRID_WIDTH;
            float y0 = GRID_WIDTH - (event.getY() - mStartY) % GRID_WIDTH;
            if (x0 < GRID_WIDTH / 2) {
                x = (int) ((event.getX() - mStartX) / GRID_WIDTH);
            } else {
                x = (int) ((event.getX() - mStartX) / GRID_WIDTH) - 1;
            }
            if (y0 < GRID_WIDTH / 2) {
                y = (int) ((event.getY() - mStartY) / GRID_WIDTH);
            } else {
                y = (int) ((event.getY() - mStartY) / GRID_WIDTH) - 1;
            }
            if ((x >= 0 && x < GRID_SIZE - 1)
                    && (y >= 0 && y < GRID_SIZE - 1)) {
                if (mGridArray[x][y] == 0) {
                    if (wbflag == BLACK) {
                        putChess(x, y, BLACK);
                        if (checkWin(BLACK)) { //如果是黑棋赢了
                            mText = STRING_LOSE;
                            showTextView(mText);
                        } else if (checkFull()) {//如果棋盘满了
                            mText = STRING_EQUAL;
                            showTextView(mText);
                        }
                        wbflag = WHITE;
                    } else if (wbflag == WHITE) {
                        putChess(x, y, WHITE);
                        //this.mGridArray[x][y] = 2;
                        if (checkWin(WHITE)) { //如果白棋赢了
                            mText = STRING_WIN;
                            showTextView(mText);
                        } else if (checkFull()) {//如果棋盘满了
                            mText = STRING_EQUAL;
                            showTextView(mText);
                        }
                        wbflag = BLACK;
                    }
                }
            }
        }
        this.invalidate();
        return true;
    }

    @Override
    public void onDraw(Canvas canvas) {
        //canvas.drawColor(Color.YELLOW);
        //先画实木背景
        Paint paintBackground = new Paint();
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.chess_bg);
        canvas.drawBitmap(bitmap, null, new Rect(mStartX, mStartY, mStartX + GRID_WIDTH * GRID_SIZE, mStartY + GRID_WIDTH * GRID_SIZE), paintBackground);
        // 画棋盘
        Paint paintRect = new Paint();
        paintRect.setColor(Color.BLACK);
        paintRect.setStrokeWidth(2);
        paintRect.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int mLeft = i * GRID_WIDTH + mStartX;
                int mTop = j * GRID_WIDTH + mStartY;
                int mRright = mLeft + GRID_WIDTH;
                int mBottom = mTop + GRID_WIDTH;
                canvas.drawRect(mLeft, mTop, mRright, mBottom, paintRect);
            }
        }
        //画棋盘的外边框
        paintRect.setStrokeWidth(4);
        canvas.drawRect(mStartX, mStartY, mStartX + GRID_WIDTH * GRID_SIZE, mStartY + GRID_WIDTH * GRID_SIZE, paintRect);

        //画棋子

        for (int i = 0; i < GRID_SIZE - 1; i++) {
            for (int j = 0; j < GRID_SIZE - 1; j++) {
                if (mGridArray[i][j] == BLACK) {
                    //通过圆形来画
                    {
                        Paint paintCircle = new Paint();
                        paintCircle.setAntiAlias(true);
                        paintCircle.setColor(Color.BLACK);
                        canvas.drawCircle(mStartX + (i + 1) * GRID_WIDTH, mStartY + (j + 1) * GRID_WIDTH, CHESS_DIAMETER / 2, paintCircle);
                    }

                } else if (mGridArray[i][j] == WHITE) {
                    //通过圆形来画
                    {
                        Paint paintCircle = new Paint();
                        paintCircle.setAntiAlias(true);
                        paintCircle.setColor(Color.WHITE);
                        canvas.drawCircle(mStartX + (i + 1) * GRID_WIDTH, mStartY + (j + 1) * GRID_WIDTH, CHESS_DIAMETER / 2, paintCircle);
                    }
                }
            }
        }
    }

    public void putChess(int x, int y, int blackwhite) {
        mGridArray[x][y] = blackwhite;
        String temp = x + ":" + y;
        storageArray.push(temp);

    }

    public boolean checkWin(int wbflag) {
        for (int i = 0; i < GRID_SIZE - 1; i++) //i表示列(根据宽度算出来的)
            for (int j = 0; j < GRID_SIZE - 1; j++) {//i表示行(根据高度算出来的)
                //检测横轴五个相连
                if (((i + 4) < (GRID_SIZE - 1)) &&
                        (mGridArray[i][j] == wbflag) && (mGridArray[i + 1][j] == wbflag) && (mGridArray[i + 2][j] == wbflag) && (mGridArray[i + 3][j] == wbflag) && (mGridArray[i + 4][j] == wbflag)) {
                    mWinFlag = wbflag;
                    putBlock = 1;
                    regFlag = 3;
                }

                //纵轴5个相连
                if (((j + 4) < (GRID_SIZE - 1)) &&
                        (mGridArray[i][j] == wbflag) && (mGridArray[i][j + 1] == wbflag) && (mGridArray[i][j + 2] == wbflag) && (mGridArray[i][j + 3] == wbflag) && (mGridArray[i][j + 4] == wbflag)) {
                    mWinFlag = wbflag;
                    putBlock = 1;
                    regFlag = 3;
                }

                //左上到右下5个相连
                if (((j + 4) < (GRID_SIZE - 1)) && ((i + 4) < (GRID_SIZE - 1)) &&
                        (mGridArray[i][j] == wbflag) && (mGridArray[i + 1][j + 1] == wbflag) && (mGridArray[i + 2][j + 2] == wbflag) && (mGridArray[i + 3][j + 3] == wbflag) && (mGridArray[i + 4][j + 4] == wbflag)) {
                    mWinFlag = wbflag;
                    putBlock = 1;
                    regFlag = 3;
                }

                //右上到左下5个相连
                if (((i - 4) >= 0) && ((j + 4) < (GRID_SIZE - 1)) &&
                        (mGridArray[i][j] == wbflag) && (mGridArray[i - 1][j + 1] == wbflag) && (mGridArray[i - 2][j + 2] == wbflag) && (mGridArray[i - 3][j + 3] == wbflag) && (mGridArray[i - 4][j + 4] == wbflag)) {
                    mWinFlag = wbflag;
                    putBlock = 1;
                    regFlag = 3;
                }
            }
        if (mWinFlag == wbflag) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 检查棋盘是否满了
     *
     * @return
     */
    public boolean checkFull() {
        int mNotEmpty = 0;
        for (int i = 0; i < GRID_SIZE - 1; i++)
            for (int j = 0; j < GRID_SIZE - 1; j++) {
                if (mGridArray[i][j] != 0) mNotEmpty += 1;
            }

        if (mNotEmpty == (GRID_SIZE - 1) * (GRID_SIZE - 1)) return true;
        else return false;
    }

    public void showTextView(CharSequence mT) {
        this.mStatusTextView.setText(mT);
        mStatusTextView.setVisibility(View.VISIBLE);
    }

    private int[] showtime;

    public void setShowTimeTextViewTime(int[] showtime) {
        this.showtime = showtime;
    }

    class MyButtonListener implements OnClickListener {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //如果是悔棋
                case R.id.renren_btn1:
                    if(regFlag == 0) {
                        if (storageArray.size() == 0) {
                            Toast.makeText(getContext(), "棋盘上没棋你搁这悔个🔨呢?", Toast.LENGTH_SHORT).show();
                        } else {
                            if (storageArray.size() == 1) {
                                storageArray.pop();
                                mGridArray = new int[GRID_SIZE - 1][GRID_SIZE - 1];
                                if (wbflag == WHITE)//用于纠正悔棋后的执棋方
                                {
                                    wbflag = BLACK;
                                } else {
                                    wbflag = WHITE;
                                }
                                invalidate();
                            } else {
                                String temp = storageArray.pop();
                                String[] temps = temp.split(":");

                                int a = Integer.parseInt(temps[0]);
                                int b = Integer.parseInt(temps[1]);
                                mGridArray[a][b] = 0;
                                if (wbflag == WHITE)//用于纠正悔棋后的执棋方
                                {
                                    wbflag = BLACK;
                                } else {
                                    wbflag = WHITE;
                                }
                                invalidate();
                            }
                        }
                    }
                    else if (regFlag == 3) {
                        Toast.makeText(getContext(), "都下完了害悔呐?", Toast.LENGTH_SHORT).show();
                    }
                    break;
                //如果是刷新
                case R.id.renren_btn2:
                    putBlock = 0;
                    setVisibility(View.VISIBLE);
                    mStatusTextView.invalidate();
                    init();
                    invalidate();
                    for (int i = 0; i < showtime.length; i++) {
                        showtime[i] = 0;
                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                    mStatusTextView.setText("线下双人  当前时间：" + simpleDateFormat.format(new Date()));
                    break;
            }
        }
    }
}
