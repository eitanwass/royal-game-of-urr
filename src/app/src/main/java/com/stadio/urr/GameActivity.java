package com.stadio.urr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    static RelativeLayout relativeLayout;
    static int current_roll = 0;
    ConstraintLayout constraintLayout_dice;
    Tile root_tile;
    static ArrayList<Tile> tiles;
    static ArrayList<Piece> whites;
    static ArrayList<Piece> blacks;
    Piece game_piece_white;
    Piece game_piece_black;
    ImageButton roll_dice_button;
    static ImageView[] dice;

    static boolean did_roll = false;
    static boolean whites_turn = false;
    final static int NUMBER_OF_DICE = 4;
    final static int NUMBER_OF_PIECES = 7;
    final float NUMBER_OF_TILES_ACCROSS = 8;
    final float PERCENTAGE_OF_TILES_FROM_SCREEN = (float) 85 / 100;
    final float TILE_PRECENT = PERCENTAGE_OF_TILES_FROM_SCREEN / NUMBER_OF_TILES_ACCROSS;
    final float PIECE_PRECENT = (float) (TILE_PRECENT * 0.75);
    float width_dp;
    float height_dp;
    static float width_px;
    static float height_px;
    static int soft_buttons_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getSupportActionBar().hide();

        relativeLayout = findViewById(R.id.game_relative_layout);
        constraintLayout_dice = findViewById(R.id.constraint_layout_dice);
        roll_dice_button = findViewById(R.id.dice_roll_button);
        roll_dice_button.setOnClickListener(this);
        root_tile = findViewById(R.id.tile);
        game_piece_white = findViewById(R.id.piece_white);
        game_piece_white.setOnTouchListener(new DragNDrop(width_px, height_px, getSoftButtonsBarHeight(), (Piece) findViewById(R.id.ghost_piece)));
        game_piece_white.setStart_tile((Tile) findViewById(R.id.start_white));
        game_piece_black = findViewById(R.id.piece_black);
        game_piece_black.setOnTouchListener(new DragNDrop(width_px, height_px, getSoftButtonsBarHeight(), (Piece) findViewById(R.id.ghost_piece)));
        game_piece_black.setStart_tile((Tile) findViewById(R.id.start_black));

        dice = new ImageView[NUMBER_OF_DICE];
        dice[0] = findViewById(R.id.dice1);
        dice[1] = findViewById(R.id.dice2);
        dice[2] = findViewById(R.id.dice3);
        dice[3] = findViewById(R.id.dice4);
        tiles = new ArrayList<>();
        whites = new ArrayList<>();
        blacks = new ArrayList<>();
    }

    /**
     * Sets the width and height of the screen.
     * both in pixels and dp.
     */
    public void getSizes(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width_px = size.x;
        height_px = size.y;
        width_dp = convertPixelsToDp(width_px, getApplicationContext());
        height_dp = convertPixelsToDp(height_px, getApplicationContext());
        soft_buttons_height = getSoftButtonsBarHeight();
    }

    /**
     * Sets the tile array list.
     * That includes sizes and positioning.
     */
    public void setTiles(){
        float dice_height = constraintLayout_dice.getHeight();
        float tile_size = (width_dp) * TILE_PRECENT;
        float margin_bottom = convertDpToPixel((height_dp - dice_height - tile_size * 3) / 2, getApplicationContext());
        //Log.d(TAG, "setTiles: dice height: " + dice_height);
        Log.d(TAG, "setTiles: tile_size: " + tile_size);
        Log.d(TAG, "setTiles: margin bottom: " + margin_bottom);
        float margin_right = convertDpToPixel((width_dp - tile_size * 8) / 2, getApplicationContext());
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) root_tile.getLayoutParams();
        params.setMargins(0, 0, (int) margin_right, (int) margin_bottom);
        root_tile.setLayoutParams(params);

        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            View childView = relativeLayout.getChildAt(i);
            if (childView instanceof Tile) {
                RelativeLayout.LayoutParams childViewLayoutParams = (RelativeLayout.LayoutParams) childView.getLayoutParams();
                childViewLayoutParams.height = (int) convertDpToPixel((int) tile_size, getApplicationContext());
                childViewLayoutParams.width = (int) convertDpToPixel((int) tile_size, getApplicationContext());
                childView.setLayoutParams(childViewLayoutParams);
                tiles.add((Tile) childView);
            }
        }
    }

    /**
     * Sets an array list of all the pieces from a certain side.
     * @param piece: the piece we want to duplicate.
     * @param pieces: the array list we want to keep all the pieces in.
     */
    private void setPieces(Piece piece, ArrayList<Piece> pieces) {
        float piece_size = width_dp * PIECE_PRECENT;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) piece.getLayoutParams();
        layoutParams.height = (int) convertDpToPixel((int) piece_size, getApplicationContext());
        layoutParams.width = (int) convertDpToPixel((int) piece_size, getApplicationContext());
        RelativeLayout.LayoutParams layoutParams_tile = (RelativeLayout.LayoutParams) piece.getStart_tile().getLayoutParams();
        int leftMargin = (layoutParams_tile.width - layoutParams.width) / 2;
        int topMargin = 0;
        int bottomMargin = 0;
        if (piece.side == Sides.WHITE.getValue()) {
            topMargin = (layoutParams_tile.height - layoutParams.height) / 2;
        } else {
            bottomMargin = (layoutParams_tile.height - layoutParams.height) / 2;
        }
        layoutParams.setMargins(leftMargin, topMargin, 0, bottomMargin);
        piece.setOnTouchListener(new DragNDrop(width_px, height_px, getSoftButtonsBarHeight(), (Piece) findViewById(R.id.ghost_piece)));

        pieces.add(piece);
        piece.invalidate();

        for (int i = 0; i < NUMBER_OF_PIECES - 1; i++) {
            Piece temp_piece = new Piece(piece);
            temp_piece.setLayoutParams(new RelativeLayout.LayoutParams(layoutParams));
            temp_piece.setOnTouchListener(new DragNDrop(width_px, height_px, getSoftButtonsBarHeight(), (Piece) findViewById(R.id.ghost_piece)));
            pieces.add(temp_piece);
            temp_piece.setVisibility(View.VISIBLE);
            temp_piece.invalidate();
            relativeLayout.addView(temp_piece);
        }
        relativeLayout.invalidate();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        getSizes();
        setTiles();
        setPieces(game_piece_white, whites);
        setPieces(game_piece_black, blacks);
        DragNDrop.tiles = tiles;
        changeTurn();
    }

    /**
     * Converts pixels to dp.
     * @param px: the size in pixels.
     * @param context: the context we're in.
     * @return: the result of the conversion.
     */
    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * Converts pixels to dp.
     * @param dp: the size in dp.
     * @param context: the context we're in.
     * @return: the result of the conversion.
     */
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * @return: Returns the height of the soft button.
     */
    @SuppressLint("NewApi")
    private int getSoftButtonsBarHeight() {
        // getRealMetrics is only available with API 17 and +
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dice_roll_button:
                if (!did_roll)
                    current_roll = rollDice();
        }
    }

    private int rollDice() {
        Random random = new Random();
        int count = 0;
        for (int i = 0; i < dice.length; i++) {
            boolean up = random.nextBoolean();
            if (up) {
                dice[i].setImageResource(R.drawable.pyramid_die_up);
                count++;
            }
            else {
                dice[i].setImageResource(R.drawable.pyramid_die_down);
            }
        }
        did_roll = true;
        if (count == 0) {
            changeTurn();
        }
        return count;
    }

    public static void resetDice() {
        current_roll = 0;
        for (int i = 0; i < dice.length; i++) {
            dice[i].setImageResource(R.drawable.pyramid_die_down);
        }
        did_roll = false;
    }

    public static void changeTurn(){

        GameActivity.whites_turn = !whites_turn;
        ArrayList<Piece> turn;
        ArrayList<Piece> not_turn;
        if (whites_turn) {
            turn = whites;
            not_turn = blacks;
        } else {
            turn = blacks;
            not_turn = whites;
        }

        for (Piece p : turn) {
            p.setEnabled(true);
        }

        for (Piece p : not_turn) {
            p.setEnabled(false);
        }

        resetDice();
    }

    public static boolean checkSpecial(Tile t) {
        return (t.tile_type == Attributes.ANOTHER_TURN.getValue() || t.tile_type == Attributes.INVINCIBILITY.getValue());
    }


    public static void anotherTurn() {
        resetDice();
    }
}