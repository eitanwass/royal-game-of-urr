package com.stadio.urr;

import android.graphics.Point;
import android.widget.ImageView;

enum Sides{
    BLACK,
    WHITE,
}

public class Piece {
    ImageView piece_image;
    Sides side;
    Point position;

    Piece(ImageView piece_image, Sides side, Point position){
        this.piece_image = piece_image;
        this.side = side;
        this.position = position;
    }
}
