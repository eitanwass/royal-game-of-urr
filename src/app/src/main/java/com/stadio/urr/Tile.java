package com.stadio.urr;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import org.w3c.dom.Attr;
import org.xmlpull.v1.XmlPullParser;

enum Attributes {
    NORMAL(0),
    ANOTHER_TURN(1),
    INVINCIBILITY(2),
    START(4),
    END(8);

    private final int value;

    private Attributes(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

public class Tile extends androidx.appcompat.widget.AppCompatImageView {
    private Piece piece;
    private int index;
    private int tileType;
    private int tileExclusivity;


    /* --Constructors-- */

    /**
     * creates an empty Tile.
     *
     * @param context: the context in which this tile exists.
     */
    public Tile(Context context) {
        super(context);
    }

    /**
     * Creates a tile with attributes.
     *
     * @param context: the context in which the tile exists.
     * @param attrs:   the attributes the tile has.
     */
    public Tile(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Tile,
                0, 0);

        try {
            tileType = attributes.getInt(R.styleable.Tile_type, 0);
            tileExclusivity = attributes.getInt(R.styleable.Tile_exclusive_to, 3);
            index = attributes.getInt(R.styleable.Tile_tile_index, 1);
        } finally {
            attributes.recycle();
        }

        if (tileType == Attributes.NORMAL.getValue()) {
            this.setImageResource(R.drawable.normal_tile);
        } else if (tileType == Attributes.ANOTHER_TURN.getValue()) {
            this.setImageResource(R.drawable.another_turn_tile);
        } else if (this.isInvincible()) {
            this.setImageResource(R.drawable.invincibility_tile);
        } else {
            this.setImageResource(0);
        }
    }

    public Tile(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /* --Getters & Setters-- */

    /**
     * @return returns the piece inside the tile.
     */
    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public int getIndex() {
        return index;
    }


    /* --Public Methods-- */

    /**
     * Checks if the tile is available.
     *
     * @return if the tile is available return true, else return false.
     */
    public boolean isEmpty() {
        return piece == null;
    }

    /**
     * Can this color side piece land on this tile.
     *
     * @param colorSide The side of the piece, black or white.
     * @return Whether the piece can land on this tile.
     */
    public boolean canLand(int colorSide) {
        return (this.tileExclusivity|colorSide) == this.tileExclusivity;
    }

    /**
     * Checks if the tile grants another turn.
     *
     * @return True if grants another turn false if not.
     */
    public boolean isAnotherTurn() {
        return (this.tileType | Attributes.ANOTHER_TURN.getValue()) == this.tileType;
    }

    /**
     * Checks if this tile grants invincibility.
     *
     * @return True if grants invincibility false if not.
     */
    public boolean isInvincible() {
        return (this.tileType | Attributes.INVINCIBILITY.getValue()) == this.tileType;
    }

    /**
     * Checks if this tile is the end tile.
     *
     * @return True if is the end, false if not.
     */
    public boolean isEnd() {
        return (this.tileType | Attributes.END.getValue()) == this.tileType;
    }

    /**
     * Checks if this tile is the start tile.
     *
     * @return True if is the start, false if not.
     */
    public boolean isStart() {
        return (this.tileType | Attributes.START.getValue()) == this.tileType;
    }

    /**
     * Removes a piece from a tile.
     */
    public void removePiece() {
        this.setPiece(null);
    }

    /**
     * Check if a piece is on this tile.
     * @param piece The piece we want to check if it's on our tile.
     * @return True if it's on false if not.
     */
    public boolean checkPiece(Piece piece) {
        return this.piece == piece;
    }
}
