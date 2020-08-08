package com.stadio.urr;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import static android.content.ContentValues.TAG;

enum Sides{
    WHITE (0),
    BLACK (1),
    NONE (2);

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
    private Tile start_tile;

    /**
     * creates an empty piece.
     * @param context: the context in which this piece exists.
     */
    public Piece(Context context) {
        super(context);
    }

    /**
     * Creates a Piece with attributes.
     * @param context: the context in which the piece exists.
     * @param attrs: the attributes the piece has.
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
        this.start_tile = piece.start_tile;
        decideImage();
    }

    /**
     *  Decides what image to use depending on the side.
     */
    public void decideImage() {
        if (side == Sides.WHITE.getValue()) {
            this.setImageResource(R.drawable.piece_white);
        } else if (side == Sides.BLACK.getValue()) {
            this.setImageResource(R.drawable.piece_black);
        }
    }

    public void setStart_tile(Tile start_tile) {
        this.start_tile = start_tile;
    }

    public Tile getStart_tile() {
        return start_tile;
    }
}