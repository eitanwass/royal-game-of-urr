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
    BLACK (1);

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

    public Piece(Context context) {
        super(context);
    }

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

        decideImgae();
    }

    public Piece(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public Piece(Piece piece) {
        super(piece.getContext());
        RelativeLayout.LayoutParams pieceLayoutParams = (RelativeLayout.LayoutParams) piece.getLayoutParams();
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(pieceLayoutParams);
        layoutParams.setMargins(pieceLayoutParams.leftMargin, pieceLayoutParams.topMargin, pieceLayoutParams.rightMargin, pieceLayoutParams.bottomMargin);
        this.setLayoutParams(layoutParams);
        this.side = piece.side;
        this.dx = piece.dx;
        this.dy = piece.dy;
        decideImgae();
    }

    public void decideImgae() {
        if (side == Sides.WHITE.getValue()) {
            this.setImageResource(R.drawable.piece_white);
        } else if (side == Sides.BLACK.getValue()) {
            this.setImageResource(R.drawable.piece_black);
        }
    }

}
