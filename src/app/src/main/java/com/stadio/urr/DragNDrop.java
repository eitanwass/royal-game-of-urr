package com.stadio.urr;

import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class DragNDrop implements View.OnTouchListener {
    float width;
    float height;
    int soft_buttons_height;

    public DragNDrop(float width, float height, int soft_buttons_height){
        this.width = width;
        this.height = height;
        this.soft_buttons_height = soft_buttons_height;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        final float x = motionEvent.getRawX();
        final float y = motionEvent.getRawY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                ((Piece) view).dx = x - layoutParams.leftMargin;
                ((Piece) view).dy = y - layoutParams.topMargin;
                break;
            case MotionEvent.ACTION_MOVE:
                layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                layoutParams.rightMargin = (int) ((width - soft_buttons_height) - (x - ((Piece) view).dx));
                layoutParams.leftMargin = (int) (x - ((Piece) view).dx);
                layoutParams.topMargin = (int) (y - ((Piece) view).dy);
                layoutParams.bottomMargin = (int) (height - (y - ((Piece) view).dy));
                view.setLayoutParams(layoutParams);
                break;
        }

        view.invalidate();
        return true;
    }
}
