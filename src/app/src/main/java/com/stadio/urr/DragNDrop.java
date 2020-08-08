package com.stadio.urr;

import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class DragNDrop implements View.OnTouchListener {
    float width;
    float height;
    int soft_buttons_height;
    static ArrayList<Tile> tiles;
    Tile current_tile;
    boolean first = true;

    /**
     * Initializes a DragNDrop object.
     * @param width: the width of the screen in pixels.
     * @param height: the height of the screen in piexels.
     * @param soft_buttons_height: the height of the soft buttons.
     */
    public DragNDrop(float width, float height, int soft_buttons_height) {
        this.width = width;
        this.height = height;
        this.soft_buttons_height = soft_buttons_height;
    }

    /**
     * Overrides the onTouch method.
     * @param view: the view we want to move.
     * @param motionEvent: What happend.
     * @return: returns true if handled false if not.
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (first) {
            this.current_tile = ((Piece) view).getStart_tile();
            first = false;
        }
        final float x = motionEvent.getRawX();
        final float y = motionEvent.getRawY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                ((Piece) view).dx = x - view.getX();
                ((Piece) view).dy = y - view.getY();
                GameActivity.relativeLayout.bringChildToFront(view);
                break;
            case MotionEvent.ACTION_MOVE:
                view.setX(x - ((Piece) view).dx);
                view.setY(y - ((Piece) view).dy);
                break;
            case MotionEvent.ACTION_UP:
                snap(view);
                snapToTile(view, current_tile);
                break;
        }
        view.invalidate();
        return true;
    }

    /**
     * Snap the view into a tile.
     * @param view: the view we want to snap.
     */
    private void snap(View view) {
        for (Tile t : tiles) {
            if (checkInside(view, t) && !t.equals(current_tile) && t.isAvailable() && canMove((Piece) view, t)) {
                removePieceFromTile((Piece) view);
                t.setPiece((Piece) view);
                current_tile = t;
                return;
            }
        }
    }

    private boolean canMove(Piece piece, Tile t) {
        if (t.tile_exclusivity != piece.side && t.tile_exclusivity != Sides.NONE.getValue()){
            return false;
        }
        if (t.index == 15 && findTile(piece).index + GameActivity.current_roll >= 15){
            return true;
        }
        if (t.index != findTile(piece).index + GameActivity.current_roll){
            return false;
        }
        return true;
    }

    /**
     * Does the actual snapping.
     * @param view: the view we want to snap.
     * @param tile: the tile we want to snap into.
     */
    public static void snapToTile(View view, Tile tile) {
        view.setX(tile.getX() + (int) ((tile.getWidth() - view.getWidth()) / 2));
        view.setY(tile.getY() + (int) ((tile.getHeight() - view.getHeight()) / 2));
    }

    /**
     * Removes a piece from a tile.
     * @param piece: the piece we want to remove.
     */
    public void removePieceFromTile(Piece piece) {
        findTile(piece).setPiece(null);
    }

    public Tile findTile(Piece piece) {
        for (Tile t : tiles) {
            if (t.getPiece() == null)
                continue;
            if (t.getPiece().equals(piece)) {
                return t;
            }
        }
        return piece.getStart_tile();
    }

    /**
     * Checks if a view is inside a certain tile (used for snapping).
     * @param view: the view we want to check.
     * @param tile: the tile we want to check if it's in.
     * @return: returns true if it is, false if not.
     */
    public boolean checkInside(View view, Tile tile) {
        float middleX = view.getX() + view.getHeight() / 2;
        float middleY = view.getY() + view.getHeight() / 2;
        return (middleX < tile.getX() + tile.getWidth() && middleX > tile.getX() &&
            middleY < tile.getY() + tile.getHeight() && middleY > tile.getY());
    }
}
