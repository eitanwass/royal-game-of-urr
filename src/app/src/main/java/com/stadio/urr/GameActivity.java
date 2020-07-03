package com.stadio.urr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {

    RelativeLayout relativeLayout;
    Tile root_tile;
    ArrayList<Tile> tiles;

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

        tiles = new ArrayList<>();
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
        float tile_size = (float) (width_dp * 0.85) / 8;
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

    public void onWindowFocusChanged(boolean hasFocus) {
        getSizes();
        setTiles();
    }

    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }
}