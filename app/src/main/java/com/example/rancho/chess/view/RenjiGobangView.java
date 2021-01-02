package com.example.rancho.chess.view;



import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rancho.chess.R;
import com.example.rancho.chess.aty.RenjiGameAty;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

public class RenjiGobangView extends View {
    protected static int GRID_SIZE = 15;    //设置为国际标准
    protected static int GRID_WIDTH = 65; // 棋盘格的宽度
    protected static int CHESS_DIAMETER = 55; // 棋的直径
    protected static int mStartX;// 棋盘定位的左上角X
    protected static int mStartY;// 棋盘定位的左上角Y

    private static int[][] mGridArray; // 网格
    private Stack<String> storageArray;


    int wbflag = 1; //该下白棋了=2，该下黑棋了=1. 这里先下黑棋（黑棋以后设置为机器自动下的棋子）
    int putBlock = 0;
    int regFlag = 0;
    int mWinFlag = 0;
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

    public RenjiGobangView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);

        init();
    }

    //按钮监听器
    MyButtonListener myButtonListener;

    // 初始化黑白棋的Bitmap
    public void init() {
        cunchusandianArraylist = new ArrayList<>();
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
    Button huiqi;
    //刷新那妞
    Button refresh;

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

    public void setWinListner(RenjiGameAty renjiGameAty) {
        ///

        onWinListener = renjiGameAty;
    }

    OnWinListener onWinListener;

    public interface OnWinListener{
        void onWin(int i );
    }

    /**
     * 点下出现棋子
     *
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

                        //这个时候电脑自动下白棋
                        //根据难度选择对应的电脑智商
                        if (nandu == 1) {
                            naocanautomatic(x, y, WHITE);
                        } else {

                            normalautomatic(x, y, WHITE);
                        }
                        //this.mGridArray[x][y] = 1;
                        if (checkWin(BLACK)) { //如果是黑棋赢了
                            mText = STRING_LOSE;
                            showTextView(mText);
                            onWinListener.onWin(1);
                        } else if (checkFull()) {//如果棋盘满了
                            mText = STRING_EQUAL;
                            showTextView(mText);
                        }
                        wbflag = BLACK;//然后下一步接着下黑棋
                    } else if (wbflag == WHITE) {
                        putChess(x, y, WHITE);
                        //这个时候电脑自动下黑棋
                        //根据难度选择对应的电脑智商
                        if (nandu == 1) {
                            naocanautomatic(x, y, BLACK);
                        } else {

                            normalautomatic(x, y, BLACK);
                        }

                        //this.mGridArray[x][y] = 2;
                        if (checkWin(WHITE)) {
                            mText = STRING_WIN;
                            showTextView(mText);
                            onWinListener.onWin(2);
                        } else if (checkFull()) {//如果棋盘满了
                            mText = STRING_EQUAL;
                            showTextView(mText);
                        }

                        wbflag = WHITE;//然后下一步接着下白棋
                    }
                }
            }
        }
        this.invalidate();
        return true;

    }

    /**
     * 判断是否已经下过
     *
     * @param templist
     */
    private void panduanshifouyijingxiaguo(ArrayList<int[]> templist) {
        for (int i = 0; i < storageHadChess.size(); i++) {
            //如有重复，则删掉
            for (int j = 0; j < templist.size(); j++) {
                if (storageHadChess.get(i)[0] == templist.get(j)[0] && storageHadChess.get(i)[1] == templist.get(j)[1]) {
                    templist.remove(j);
                    //递归防止周围没有字落下时直接崩掉
                    if (templist.size() == 0) {
                        templist.add(new int[]{(int) (Math.random() * (GRID_SIZE - 2)), (int) (Math.random() * (GRID_SIZE - 2))});
                        //  Log.d("whalea", " " + (int) (Math.random() * (GRID_SIZE - 2)));
                        panduanshifouyijingxiaguo(templist);
                    }
                }
            }
        }
    }

    /**
     * 脑残模式自动下棋的电脑
     *
     * @param x
     * @param y     玩家下的坐标
     * @param Color 玩家下的棋的颜色    黑1白2
     *              <p/>
     *              x&y<GRID_SIZE - 1
     */
    private void naocanautomatic(int x, int y, int Color) {
        int[][] temp = {{x - 1, y - 1}, {x, y - 1}, {x + 1, y - 1}, {x - 1, y}, {x + 1, y}, {x - 1, y + 1}, {x, y + 1}, {x + 1, y + 1}};
        ArrayList<int[]> templist = new ArrayList<>();
        for (int i = 0; i < temp.length; i++) {
            if (temp[i][0] >= 0 && temp[i][0] < 13 && temp[i][1] >= 0 && temp[i][1] < 13) {
                templist.add(temp[i]);
            }
        }
        //判断是否已经下过
        panduanshifouyijingxiaguo(templist);

        int num = (int) (Math.random() * templist.size());
        int a = templist.get(num)[0];
        int b = templist.get(num)[1];
        putChess(a, b, Color);

    }

    ArrayList<String> cunchusandianArraylist;

    /**
     * 普通模式自动下棋的电脑
     * 黑1白2
     *
     * @param x
     * @param y
     * @param Color
     */
    private void normalautomatic(int x, int y, int Color) {
        int duishouColor = 0;//对手的颜色
        //根据我方颜色推测出对手颜色
        if (Color == 1) {
            duishouColor = 2;
        } else {
            duishouColor = 1;
        }
        //判断我方是否有3个连成一线了
        for (int i = 0; i < GRID_SIZE - 1; i++) //i表示列(根据宽度算出来的)
            for (int j = 0; j < GRID_SIZE - 1; j++) { //i表示行(根据高度算出来的)
                //检测横轴三个相连
                if ((((i + 3) < (GRID_SIZE - 1)) &&
                        (mGridArray[i][j] == Color) && (mGridArray[i + 1][j] == Color) && (mGridArray[i + 2][j] == Color)) ||
                        (((i + 3) < (GRID_SIZE - 1)) &&
                                (mGridArray[i][j] == duishouColor) && (mGridArray[i + 1][j] == duishouColor) && (mGridArray[i + 2][j] == duishouColor))) {
                    //如果有三个点相连了
                    //先判断是否已经测试过这三个点
                    boolean aa = false;
                    for (int p = 0; p < cunchusandianArraylist.size(); p++) {
                        String sandiantemp = cunchusandianArraylist.get(p);
                        String[] sandiantemps = sandiantemp.split(":");
                        //如果这三个点已经存在
                        if ((Integer.parseInt(sandiantemps[0]) == i) &&
                                (Integer.parseInt(sandiantemps[1]) == j) &&
                                (Integer.parseInt(sandiantemps[2]) == (i + 1)) &&
                                (Integer.parseInt(sandiantemps[3]) == j) &&
                                (Integer.parseInt(sandiantemps[4]) == (i + 2)) &&
                                (Integer.parseInt(sandiantemps[5]) == j)) {
                            aa = true;
                        }
                    }
                    if (aa == true) {

                    } else {
                        //在两边端点位置随机下一个
                        ifsangedianxianglian(i - 1, j, i + 3, j, Color);
                        cunchusandianArraylist.add(i + ":" + j + ":" + (i + 1) + ":" + j + ":" + (i + 2) + ":" + j);
                        return;
                    }
                }

                //纵轴3个相连
                if ((((j + 3) < (GRID_SIZE - 1)) &&
                        (mGridArray[i][j] == Color) && (mGridArray[i][j + 1] == Color) && (mGridArray[i][j + 2] == Color)) ||
                        (((j + 3) < (GRID_SIZE - 1)) &&
                                (mGridArray[i][j] == duishouColor) && (mGridArray[i][j + 1] == duishouColor) && (mGridArray[i][j + 2] == duishouColor))) {
                    //如果有三个点相连了
                    //先判断是否已经测试过这三个点
                    boolean aa = false;
                    for (int p = 0; p < cunchusandianArraylist.size(); p++) {
                        String sandiantemp = cunchusandianArraylist.get(p);
                        String[] sandiantemps = sandiantemp.split(":");
                        if ((Integer.parseInt(sandiantemps[0]) == i) &&
                                (Integer.parseInt(sandiantemps[1]) == j) &&
                                (Integer.parseInt(sandiantemps[2]) == i) &&
                                (Integer.parseInt(sandiantemps[3]) == (j + 1)) &&
                                (Integer.parseInt(sandiantemps[4]) == i) &&
                                (Integer.parseInt(sandiantemps[5]) == (j + 2))) {
                            aa = true;
                        }
                    }
                    if (aa == true) {

                    } else {
                        //在两边端点位置随机下一个
                        ifsangedianxianglian(i, j - 1, i, j + 3, Color);
                        cunchusandianArraylist.add(i + ":" + j + ":" + i + ":" + (j + 1) + ":" + i + ":" + (j + 2));
                        return;
                    }
                }

                //左上到右下3个相连
                if ((((j + 3) < (GRID_SIZE - 1)) && ((i + 3) < (GRID_SIZE - 1)) &&
                        (mGridArray[i][j] == Color) && (mGridArray[i + 1][j + 1] == Color) && (mGridArray[i + 2][j + 2] == Color)) ||
                        (((j + 3) < (GRID_SIZE - 1)) && ((i + 3) < (GRID_SIZE - 1)) &&
                                (mGridArray[i][j] == duishouColor) && (mGridArray[i + 1][j + 1] == duishouColor) && (mGridArray[i + 2][j + 2] == duishouColor))) {
                    //如果有三个点相连了
                    //先判断是否已经测试过这三个点
                    boolean aa = false;
                    for (int p = 0; p < cunchusandianArraylist.size(); p++) {
                        String sandiantemp = cunchusandianArraylist.get(p);
                        String[] sandiantemps = sandiantemp.split(":");
                        if ((Integer.parseInt(sandiantemps[0]) == i) &&
                                (Integer.parseInt(sandiantemps[1]) == j) &&
                                (Integer.parseInt(sandiantemps[2]) == (i + 1)) &&
                                (Integer.parseInt(sandiantemps[3]) == (j + 1)) &&
                                (Integer.parseInt(sandiantemps[4]) == (i + 2)) &&
                                (Integer.parseInt(sandiantemps[5]) == (j + 2))) {
                            aa = true;
                        }
                    }
                    if (aa == true) {

                    } else {
                        ifsangedianxianglian(i - 1, j - 1, i + 3, j + 3, Color);
                        cunchusandianArraylist.add(i + ":" + j + ":" + (i + 1) + ":" + (j + 1) + ":" + (i + 2) + ":" + (j + 2));
                        return;
                    }
                }

                //右上到左下3个相连
                if ((((i - 3) >= 0) && ((j + 3) < (GRID_SIZE - 1)) &&
                        (mGridArray[i][j] == Color) && (mGridArray[i - 1][j + 1] == Color) && (mGridArray[i - 2][j + 2] == Color)) ||
                        (((i - 3) >= 0) && ((j + 3) < (GRID_SIZE - 1)) &&
                                (mGridArray[i][j] == duishouColor) && (mGridArray[i - 1][j + 1] == duishouColor) && (mGridArray[i - 2][j + 2] == duishouColor))) {
                    //如果有三个点相连了
                    //先判断是否已经测试过这三个点
                    boolean aa = false;
                    for (int p = 0; p < cunchusandianArraylist.size(); p++) {
                        String sandiantemp = cunchusandianArraylist.get(p);
                        String[] sandiantemps = sandiantemp.split(":");
                        if ((Integer.parseInt(sandiantemps[0]) == i) &&
                                (Integer.parseInt(sandiantemps[1]) == j) &&
                                (Integer.parseInt(sandiantemps[2]) == (i - 1)) &&
                                (Integer.parseInt(sandiantemps[3]) == (j + 1)) &&
                                (Integer.parseInt(sandiantemps[4]) == (i - 2)) &&
                                (Integer.parseInt(sandiantemps[5]) == (j + 2))) {
                            aa = true;
                        }
                    }
                    if (aa == true) {

                    } else {
                        ifsangedianxianglian(i + 1, j - 1, i - 3, j + 3, Color);
                        cunchusandianArraylist.add(i + ":" + j + ":" + (i - 1) + ":" + j + 1 + ":" + (i - 2) + ":" + (j + 2));
                        return;
                    }
                }
            }
        int[][] temp = {{x - 1, y - 1}, {x, y - 1}, {x + 1, y - 1}, {x - 1, y}, {x + 1, y}, {x - 1, y + 1}, {x, y + 1}, {x + 1, y + 1}};
        ArrayList<int[]> templist = new ArrayList<>();
        for (int k = 0; k < temp.length; k++) {
            if (temp[k][0] >= 0 && temp[k][0] < 13 && temp[k][1] >= 0 && temp[k][1] < 13) {
                templist.add(temp[k]);
            }
            //判断是否已经下过
            panduanshifouyijingxiaguo(templist);
            int num = (int) (Math.random() * templist.size());
            int a = templist.get(num)[0];
            int b = templist.get(num)[1];
            putChess(a, b, Color);
            return;
        }
    }



    /**
     * 困难电脑の核心算法
     * 判断有三个点相连之后的操作
     */
    private void ifsangedianxianglian(int x, int y, int m, int n, int Color) {
        ArrayList<int[]> automaticChesslist = new ArrayList<>();
        automaticChesslist.add(new int[]{x, y});
        automaticChesslist.add(new int[]{m, n});
        panduanshifouyijingxiaguo(automaticChesslist);
        int randomindex = Math.round(automaticChesslist.size());
        int a = automaticChesslist.get(randomindex - 1)[0];
        int b = automaticChesslist.get(randomindex - 1)[1];
        putChess(a, b, Color);
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
        paintRect.setStyle(Style.STROKE);
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

    //储存已经下了的棋
    private ArrayList<int[]> storageHadChess = new ArrayList<>();


    public void putChess(int x, int y, int blackwhite) {
        storageHadChess.add(new int[]{x, y});
        mGridArray[x][y] = blackwhite;
        String temp = x + ":" + y;
        storageArray.push(temp);

    }


    /**
     * 检测是否赢了
     *
     * @param wbflag
     * @return
     */
    private boolean checkWin(int wbflag) {
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
        } else
            return false;
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

    /**
     * 1简单难度
     * 2困难难度
     *
     * @param i
     */
    public void setflag(int i) {
        nandu = i;
    }

    private static int nandu = 1;//默认为简单难度

    class MyButtonListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //如果是悔棋
                case R.id.btn1:
                    if(regFlag == 0) {
                        if (storageArray.size() == 0) {
                            Toast.makeText(getContext(), "棋盘上没棋你搁这悔个🔨呢?", Toast.LENGTH_SHORT).show();
                        } else {
                            if (storageArray.size() == 2) {
                                storageArray.pop();
                                storageArray.pop();
                                mGridArray = new int[GRID_SIZE - 1][GRID_SIZE - 1];
                                invalidate();
                            } else {
                                String temp1 = storageArray.pop();
                                String[] temps = temp1.split(":");

                                int a = Integer.parseInt(temps[0]);
                                int b = Integer.parseInt(temps[1]);
                                mGridArray[a][b] = 0;

                                String temp2 = storageArray.pop();
                                String[] temps2 = temp2.split(":");

                                int c = Integer.parseInt(temps2[0]);
                                int d = Integer.parseInt(temps2[1]);
                                mGridArray[c][d] = 0;
                                invalidate();
                            }
                        }
                    }
                    else if (regFlag == 3) {
                        Toast.makeText(getContext(), "都下完了害悔呐?", Toast.LENGTH_SHORT).show();
                    }
                    break;
                //如果是刷新
                case R.id.btn2:
                    putBlock = 0;
                    setVisibility(View.VISIBLE);

                    cunchusandianArraylist.clear();
                    storageHadChess.clear();

                    mStatusTextView.invalidate();
                    init();
                    invalidate();
                    for (int i = 0; i < showtime.length; i++) {
                        showtime[i] = 0;
                    }
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
                    mStatusTextView.setText("对线电脑 当前时间：" + simpleDateFormat.format(new Date()));
                    break;
            }
        }
    }
}
