package com.stadio.urr;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

enum Attributes{
    NORMAL (1),
    ANOTHER_TURN (2),
    INVINCIBILITY (4),
    START(8),
    END(16);

    private final int value;
    private Attributes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

public class Tile extends androidx.appcompat.widget.AppCompatImageView {
    Piece piece;
    int tile_type;
    int tile_exclusivity;

    /**
     * creates an empty Tile.
     * @param context: the context in which this tile exists.
     */
    public Tile(Context context) {
        super(context);
    }

    /**
     * Creates a tile with attributes.
     * @param context: the context in which the tile exists.
     * @param attrs: the attributes the tile has.
     */
    public Tile(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Tile,
                0, 0);

        try {
            tile_type = a.getInt(R.styleable.Tile_type, 1);
            tile_exclusivity = a.getInt(R.styleable.Tile_exclusive_to, 2);
        } finally {
            a.recycle();
        }

        if (tile_type == Attributes.NORMAL.getValue()) {
            this.setImageResource(R.drawable.normal_tile);
        } else if (tile_type == Attributes.ANOTHER_TURN.getValue()) {
            this.setImageResource(R.drawable.another_turn_tile);
        } else if (tile_type == Attributes.INVINCIBILITY.getValue()) {
            this.setImageResource(R.drawable.invincibility_tile);
        } else {
          this.setImageResource(0);
        }
    }

    public Tile(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @return: returns the piece inside the tile.
     */
    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Checks if the tile is available.
     * @return: if the tile is available return true, else return false.
     */
    public boolean isAvailable(){
        return piece == null;
    }
}
