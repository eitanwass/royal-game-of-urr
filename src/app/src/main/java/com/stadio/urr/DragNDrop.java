package com.stadio.urr;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowInsets;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DragNDrop implements View.OnTouchListener {
    static ArrayList<Tile> tiles;
    static Tile current_tile;
    boolean first = true;
    Piece ghost_piece;

    /**
     * Initializes a DragNDrop object.
     */
    public DragNDrop(Piece ghost_piece) {
        this.ghost_piece = ghost_piece;
    }

    /**
     * Overrides the onTouch method.
     * @param view: the view we want to move.
     * @param motionEvent: What happend.
     * @return: returns true if handled false if not.
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        /*if (first) {
            this.current_tile = ((Piece) view).getStart_tile();
            first = false;
        }*/
        current_tile = findTile((Piece) view);
        final float x = motionEvent.getRawX();
        final float y = motionEvent.getRawY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                ((Piece) view).dx = x - view.getX();
                ((Piece) view).dy = y - view.getY();
                GameActivity.relativeLayout.bringChildToFront(view);
                setGhost((Piece) view);
                break;
            case MotionEvent.ACTION_MOVE:
                view.setX(x - ((Piece) view).dx);
                view.setY(y - ((Piece) view).dy);
                break;
            case MotionEvent.ACTION_UP:
                snap(view);
                snapToTile(view, current_tile);
                ghost_piece.setVisibility(View.INVISIBLE);
                break;
        }
        view.invalidate();
        return true;
    }

    private void setGhost(Piece piece) {
        int index = findTile(piece).index + GameActivity.current_roll;
        if (index > 15) {
            index = 15;
        }
        GameActivity.relativeLayout.bringChildToFront(ghost_piece);
        Tile ghost_piece_tile = getTileByIndex(index, piece.side);
        if (piece.side == Sides.WHITE.getValue()){
            ghost_piece.setImageResource(R.drawable.piece_white);
        } else {
            ghost_piece.setImageResource(R.drawable.piece_black);
        }
        snapToTile(ghost_piece, ghost_piece_tile);
        ghost_piece.setVisibility(View.VISIBLE);
    }

    private Tile getTileByIndex(int index, int side) {
        for (Tile t : tiles) {
            if (t.index == index && (t.tile_exclusivity == side || t.tile_exclusivity == Sides.NONE.getValue())) {
                return t;
            }
        }
        return null;
    }

    /**
     * Snap the view into a tile.
     * @param view: the view we want to snap.
     */
    private void snap(View view) {
        for (Tile t : tiles) {
            if (checkInside(view, t) && !t.equals(current_tile) && canMove((Piece) view, t)) {
                if (!t.isAvailable() && t.getPiece().side != ((Piece)view).side) {
                    eat((Piece) view, t);
                    GameActivity.changeTurn();
                }
                else if(t.isAvailable() || gotToEnd((Piece) view, t) ) {
                    removePieceFromTile((Piece) view);
                    t.setPiece((Piece) view);
                    current_tile = t;
                    if(GameActivity.checkSpecial(t)){
                        GameActivity.anotherTurn();
                        return;
                    }
                    GameActivity.changeTurn();
                    return;
                }
            }
        }
    }

    /**
     * Eat a tile that is on a piece and send it back to start.
     * @param piece: the piece that moves.
     * @param tile: the tile we want to move onto.
     */
    public void eat(Piece piece, Tile tile) {
        Piece eated_piece = tile.getPiece();
        removePieceFromTile(eated_piece);
        snapToTile(eated_piece, eated_piece.getStart_tile());
        removePieceFromTile(piece);
        tile.setPiece(piece);
        current_tile = tile;
    }

    /**
     * Checks if a piece can move to a certain tile.
     * @param piece: The piece we want to move.
     * @param t: the tile we want to move to.
     * @return: true if it is possible to move to false if not.
     */
    private boolean canMove(Piece piece, Tile t) {
        if (t.tile_exclusivity != piece.side && t.tile_exclusivity != Sides.NONE.getValue()){
            return false;
        }
        if (gotToEnd(piece, t)){
            return true;
        }
        if (t.index != findTile(piece).index + GameActivity.current_roll){
            return false;
        }
        if (t.tile_type == Attributes.INVINCIBILITY.getValue() && !t.isAvailable()) {
            return false;
        }
        return true;
    }

    /**
     * Checks if 11a piece got to the end of the board.
     * @param piece: the piece that moves.
     * @param t: the tile we want to check if it's the last.
     * @return: true if last false if not.
     */
    private boolean gotToEnd(Piece piece, Tile t) {
        return (t.index == 15 && findTile(piece).index + GameActivity.current_roll >= 15);
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
