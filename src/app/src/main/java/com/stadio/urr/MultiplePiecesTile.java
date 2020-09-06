package com.stadio.urr;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

public class MultiplePiecesTile extends Tile {

    ArrayList<Piece> pieces;

    /**
     * creates an empty Tile.
     *
     * @param context: the context in which this tile exists.
     */
    public MultiplePiecesTile(Context context) {
        super(context);
         pieces = new ArrayList<>();
    }

    /**
     * Creates a tile with attributes.
     *
     * @param context: the context in which the tile exists.
     * @param attrs:   the attributes the tile has.
     */
    public MultiplePiecesTile(Context context, AttributeSet attrs) {
        super(context, attrs);
        pieces = new ArrayList<>();
    }

    public MultiplePiecesTile(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        pieces = new ArrayList<>();
    }

    /* --Getters & Setters-- */

    @Override
    public Piece getPiece() {
        return this.pieces.get(this.pieces.size() - 1);
    }

    /**
     * Added a piece to the list of pieces on the tile.
     *
     * @param piece The piece we want to add.
     */
    @Override
    public void setPiece(Piece piece) {
        this.pieces.add(piece);
    }

    /**
     * Sets the pieces array list.
     *
     * @param pieces The array list we want to set our list to.
     */
    @SuppressWarnings("unchecked")
    public void setPieces(ArrayList<Piece> pieces) {
        this.pieces = (ArrayList<Piece>) pieces.clone();
    }

    /**
     * Removes the latest piece from the list.
     */
    @Override
    public void removePiece() {
        this.pieces.remove(pieces.size() - 1);
    }

    /**
     * @return The number of pieces on the tile.
     */
    public int getNumberOfPieces() {
        return pieces.size();
    }


    /* --Public Methods-- */

    /**
     * Check if a piece is on this tile.
     * @param piece The piece we want to check if it's on our tile.
     * @return True if it's on false if not.
     */
    @Override
    public boolean checkPiece(Piece piece) {
        return this.pieces.contains(piece);
    }

    /**
     * Checks if the tile has any pieces on it.
     * @return True if empty false if not.
     */
    @Override
    public boolean isEmpty() {
        return pieces.isEmpty();
    }
}
