package com.stadio.urr;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DragNDrop implements View.OnTouchListener {
    private final int OFFSET = 50;
    float width;
    float height;
    int soft_buttons_height;
    static ArrayList<Tile> tiles;
    float currentX;
    float currentY;

    public DragNDrop(float width, float height, int soft_buttons_height){
        this.width = width;
        this.height = height;
        this.soft_buttons_height = soft_buttons_height;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final float x = motionEvent.getRawX();
        final float y = motionEvent.getRawY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                currentX = view.getX();
                currentY = view.getY();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                ((Piece) view).dx = x - layoutParams.leftMargin;
                ((Piece) view).dy = y - layoutParams.topMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.rightMargin = (int) ((width - soft_buttons_height) - (x - ((Piece) view).dx));
                layoutParams.leftMargin = (int) (x - ((Piece) view).dx);
                layoutParams.topMargin = (int) (y - ((Piece) view).dy);
                layoutParams.bottomMargin = (int) (height - (y - ((Piece) view).dy));
                view.setLayoutParams(layoutParams);
                break;
            case MotionEvent.ACTION_UP:
                snap(view);
                break;
        }

        view.invalidate();
        return true;
    }

    private void snap(View view) {
        for (Tile t : tiles) {
            if (Math.abs(t.getX() - view.getX()) < OFFSET &&
                    Math.abs(t.getY() - view.getY()) < OFFSET && t.isAvailable()) {
                removePieceFromTile((Piece) view);
                view.setX(t.getX() + (t.getWidth() - view.getWidth()) / 2);
                view.setY(t.getY() + (t.getHeight() - view.getHeight()) / 2);
                t.setPiece((Piece) view);
                return;
            }
        }
        view.setX(currentX);
        view.setY(currentY);
    }

    public void removePieceFromTile(Piece piece) {
        for (Tile t : tiles) {
            if (t.getPiece() == null)
                continue;
            if (t.getPiece().equals(piece)) {
                t.setPiece(null);
            }
        }
    }

    public void setTiles(ArrayList<Tile> tiles) {
        this.tiles = tiles;
    }
}
