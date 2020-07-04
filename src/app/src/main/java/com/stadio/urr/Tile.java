package com.stadio.urr;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

enum Attributes{
    NORMAL (1),
    ANOTHER_TURN (2),
    INVINCIBILITY (4);

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

    public Tile(Context context) {
        super(context);
    }

    public Tile(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.Tile,
                0, 0);

        try {
            tile_type = a.getInt(R.styleable.Tile_type, 1);
        } finally {
            a.recycle();
        }

        if (tile_type == Attributes.NORMAL.getValue()) {
            this.setImageResource(R.drawable.normal_tile);
        } else if (tile_type == Attributes.ANOTHER_TURN.getValue()) {
            this.setImageResource(R.drawable.another_turn_tile);
        } else if (tile_type == Attributes.INVINCIBILITY.getValue()) {
            this.setImageResource(R.drawable.invincibilty_tile);
        }
    }

    public Tile(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public int getTile_type() {
        return tile_type;
    }
}
