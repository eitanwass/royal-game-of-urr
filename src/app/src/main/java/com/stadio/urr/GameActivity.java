package com.stadio.urr;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class GameActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;
    Tile root_tile;
    static ArrayList<Tile> tiles;
    ArrayList<Piece> whites;
    ArrayList<Piece> blacks;
    Piece game_piece_white;
    Piece game_piece_black;

    final int NUMBER_OF_PIECES = 7;
    final float TILE_PRECENT = (float) (0.85 / 8);
    final float PIECE_PRECENT = (float) (TILE_PRECENT * 0.9);
    float width_dp;
    float height_dp;
    private float width_px;
    private float height_px;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getSupportActionBar().hide();

        relativeLayout = findViewById(R.id.game_relative_layout);
        root_tile = findViewById(R.id.tile);
        game_piece_white = findViewById(R.id.piece_white);
        game_piece_white.setOnTouchListener(new DragNDrop(width_px, height_px, getSoftButtonsBarHeight()));
        game_piece_black = findViewById(R.id.piece_black);
        game_piece_black.setOnTouchListener(new DragNDrop(width_px, height_px, getSoftButtonsBarHeight()));

        tiles = new ArrayList<>();
        whites = new ArrayList<>();
        blacks = new ArrayList<>();
    }

    public void getSizes(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width_px = size.x;
        height_px = size.y;
        width_dp = convertPixelsToDp(width_px, getApplicationContext());
        height_dp = convertPixelsToDp(height_px, getApplicationContext());
        Log.d("Sizes", "onWindowFocusChanged: height:" + height_dp);
        Log.d("Sizes", "onWindowFocusChanged: width:" + width_dp);
    }

    public void setTiles(){
        float tile_size = width_dp * TILE_PRECENT;
        float margin_bottom = convertDpToPixel((height_dp - tile_size * 3) / 2, getApplicationContext());
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

    private void setPieces(Piece piece, ArrayList<Piece> pieces) {
        float piece_size = width_dp * PIECE_PRECENT;
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) piece.getLayoutParams();
        layoutParams.height = (int) convertDpToPixel((int) piece_size, getApplicationContext());
        layoutParams.width = (int) convertDpToPixel((int) piece_size, getApplicationContext());
        piece.setLayoutParams(layoutParams);
        pieces.add(piece);

        for (int i = 0; i < NUMBER_OF_PIECES - 1; i++) {
            Piece temp_piece = new Piece(piece);
            temp_piece.setOnTouchListener(new DragNDrop(width_px, height_px, getSoftButtonsBarHeight()));
            temp_piece.setVisibility(View.VISIBLE);
            relativeLayout.addView(temp_piece);
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        getSizes();
        setTiles();
        setPieces(game_piece_white, whites);
        setPieces(game_piece_black, blacks);
        DragNDrop.tiles = tiles;
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

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
}