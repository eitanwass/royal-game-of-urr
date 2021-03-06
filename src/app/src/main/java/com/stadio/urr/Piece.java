package com.stadio.urr;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

enum Sides{
    WHITE (1),
    BLACK (2);

    private final int value;
    private Sides(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

public class Piece extends androidx.appcompat.widget.AppCompatImageView {
    int side;
    public float dx;
    public float dy;
    private Tile startTile;

    /**
     * creates an empty piece.
     *
     * @param context the context in which this piece exists.
     */
    public Piece(Context context) {
        super(context);
    }

    /**
     * Creates a Piece with attributes.
     *
     * @param context the context in which the piece exists.
     * @param attrs the attributes the piece has.
     */
    public Piece(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Piece,
                0, 0);

        try {
            side = a.getInt(R.styleable.Piece_side, 0);
        } finally {
            a.recycle();
        }

        decideImage();
    }

    public Piece(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * Creates a duplicate if an existing piece.
     * @param piece: the piece we want to duplicate.
     */
    public Piece(Piece piece) {
        super(piece.getContext());
        RelativeLayout.LayoutParams pieceLayoutParams = (RelativeLayout.LayoutParams) piece.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(pieceLayoutParams);
        layoutParams.setMargins(pieceLayoutParams.leftMargin, pieceLayoutParams.topMargin, pieceLayoutParams.rightMargin, pieceLayoutParams.bottomMargin);
        this.setLayoutParams(layoutParams);
        this.side = piece.side;
        this.dx = piece.dx;
        this.dy = piece.dy;
        this.startTile = piece.startTile;
        decideImage();
    }



    /* --Getters & Setters-- */
    public Tile getStartTile() {
        return startTile;
    }

    public void setStartTile(Tile start_tile) {
        this.startTile = start_tile;
    }


    /* --Private Methods-- */

    /**
     *  Decides what image to use depending on the side.
     */
    private void decideImage() {
        if (side == Sides.WHITE.getValue()) {
            this.setImageResource(R.drawable.piece_white);
        } else if (side == Sides.BLACK.getValue()) {
            this.setImageResource(R.drawable.piece_black);
        }
    }
}