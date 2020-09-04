package com.stadio.urr;

import android.content.Context;
import android.util.AttributeSet;

import java.util.ArrayList;

public class MultiplePiecesTile extends Tile {

    ArrayList<Piece> pieces;

    public MultiplePiecesTile(Context context) {
        super(context);
         pieces = new ArrayList<>();
    }

    public MultiplePiecesTile(Context context, AttributeSet attrs) {
        super(context, attrs);
        pieces = new ArrayList<>();
    }

    public MultiplePiecesTile(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        pieces = new ArrayList<>();
    }

    @Override
    public void setPiece(Piece piece) {
        this.pieces.add(piece);
    }

    @SuppressWarnings("unchecked")
    public void setPieces(ArrayList<Piece> pieces) {
        this.pieces = (ArrayList<Piece>) pieces.clone();
    }

    @Override
    public void removePiece() {
        this.pieces.remove(pieces.size() - 1);
    }

    public int getNumberOfPieces() {
        return pieces.size();
    }

    @Override
    public boolean checkPiece(Piece piece) {
        return this.pieces.contains(piece);
    }
}
