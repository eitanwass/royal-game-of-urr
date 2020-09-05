package com.stadio.urr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

enum Starts_Ends {
    START_WHITE, START_BLACK, END_WHITE, END_BLACK
}

public class GameActivity extends AppCompatActivity implements View.OnClickListener {

    /* --View Variables-- */
    private static RelativeLayout relativeLayout;
    private ConstraintLayout constraintLayoutDice;

    private Tile rootTile;

    private static int currentRoll = 0;

    private static ArrayList<Tile> tiles;
    private static ArrayList<Piece> whitePieces;
    private static ArrayList<Piece> blackPieces;
    private Piece gamePieceWhite;
    private Piece gamePieceBlack;
    private static ImageView[] dice;
    private static Map<TextView, MultiplePiecesTile> starts_ends;

    private static boolean didRoll = false;
    private static boolean whitesTurn = false;

    private float width_dp;
    private float height_dp;


    /* --Constants-- */

    private final static int NUMBER_OF_DICE = 4;
    private final static int NUMBER_OF_PIECES = 7;
    private final static float NUMBER_OF_TILES_HORIZONTAL = 8;
    private final static float NUMBER_OF_TILES_VERTICAL = 3;
    public final static int PATH_LENGTH = 15;

    private final static float PERCENTAGE_OF_TILES_FROM_SCREEN = (float) 85 / 100;
    private final static float TILE_PERCENT_OF_SCREEN = PERCENTAGE_OF_TILES_FROM_SCREEN / NUMBER_OF_TILES_HORIZONTAL;
    private final static float PIECE_PERCENTAGE_FROM_TILE = 0.75f;


    /* --Getters and Setters-- */

    public static int getCurrentRoll() {
        return currentRoll;
    }

    /* --Methods-- */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        relativeLayout = findViewById(R.id.game_relative_layout);
        constraintLayoutDice = findViewById(R.id.constraint_layout_dice);
        rootTile = findViewById(R.id.tile);
        findViewById(R.id.dice_roll_button).setOnClickListener(this);

        gamePieceWhite = createPiece(R.id.piece_white, R.id.start_white);
        gamePieceBlack = createPiece(R.id.piece_black, R.id.start_black);

        tiles = new ArrayList<>();
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        dice = new ImageView[NUMBER_OF_DICE];
        dice[0] = findViewById(R.id.dice1);
        dice[1] = findViewById(R.id.dice2);
        dice[2] = findViewById(R.id.dice3);
        dice[3] = findViewById(R.id.dice4);

        starts_ends = new HashMap<>();
        starts_ends.put((TextView) findViewById(R.id.pieces_left_start_white), (MultiplePiecesTile) findViewById(R.id.start_white));
        starts_ends.put((TextView) findViewById(R.id.pieces_left_start_black), (MultiplePiecesTile) findViewById(R.id.start_black));
        starts_ends.put((TextView) findViewById(R.id.pieces_left_end_white), (MultiplePiecesTile) findViewById(R.id.end_white));
        starts_ends.put((TextView) findViewById(R.id.pieces_left_end_black), (MultiplePiecesTile) findViewById(R.id.end_black));
    }

    /**
     * Create a piece by its ID and start location ID.
     *
     * @param pieceId The ID of the piece to create.
     * @param startId The ID of the starting tile.
     * @return The newly created piece.
     */
    @SuppressLint("ClickableViewAccessibility")
    private Piece createPiece(int pieceId, int startId) {
        Piece piece = findViewById(pieceId);
        piece.setOnTouchListener(new DragNDrop((Piece) findViewById(R.id.ghost_piece), relativeLayout));
        piece.setStartTile((Tile) findViewById(startId));
        return piece;
    }

    /**
     * Gets the width and height of the screen in DP.
     */
    public void getSizes(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        width_dp = convertPixelsToDp(size.x, getApplicationContext());
        height_dp = convertPixelsToDp(size.y, getApplicationContext());
    }

    /**
     * Creates the tiles and sets their position and scale relative to the screen and the root tile.
     */
    public void setTiles() {
        float diceHeight = constraintLayoutDice.getHeight();
        float tileSize = (width_dp) * TILE_PERCENT_OF_SCREEN;
        float marginBottom = convertDpToPixel(
                (height_dp - diceHeight - tileSize * NUMBER_OF_TILES_VERTICAL) / 2, getApplicationContext());
        float margin_right = convertDpToPixel(
                (width_dp - tileSize * NUMBER_OF_TILES_HORIZONTAL) / 2, getApplicationContext());

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rootTile.getLayoutParams();
        params.setMargins(0, 0, (int) margin_right, (int) marginBottom);
        rootTile.setLayoutParams(params);

        for (int i = 0; i < relativeLayout.getChildCount(); i++) {
            View childView = relativeLayout.getChildAt(i);
            if (childView instanceof Tile) {
                RelativeLayout.LayoutParams childViewLayoutParams = (RelativeLayout.LayoutParams) childView.getLayoutParams();
                int tileSizePixels = (int) convertDpToPixel((int) tileSize, getApplicationContext());
                childViewLayoutParams.height = childViewLayoutParams.width = tileSizePixels;
                childView.setLayoutParams(childViewLayoutParams);
                tiles.add((Tile) childView);
            }
        }
    }

    /**
     * Sets an array list of all the pieces from a certain side.
     *
     * @param piece: the piece we want to duplicate.
     * @param pieces: the array list we want to keep all the pieces in.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void setPieces(Piece piece, ArrayList<Piece> pieces) {
        float piecePercentOfScreen = (float) (TILE_PERCENT_OF_SCREEN * PIECE_PERCENTAGE_FROM_TILE);
        float pieceSize = width_dp * piecePercentOfScreen;

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) piece.getLayoutParams();
        int pieceSizePixels = (int) convertDpToPixel((int) pieceSize, getApplicationContext());
        layoutParams.height = layoutParams.width = pieceSizePixels;
        RelativeLayout.LayoutParams layoutParams_tile = (RelativeLayout.LayoutParams) piece.getStartTile().getLayoutParams();

        boolean isThisWhite = piece.side == Sides.WHITE.getValue();
        int marginFromEdge = (layoutParams_tile.height - layoutParams.height) / 2;

        int leftMargin = (layoutParams_tile.width - layoutParams.width) / 2;
        int topMargin = isThisWhite ? marginFromEdge : 0;
        int bottomMargin = isThisWhite ? 0 : marginFromEdge;

        layoutParams.setMargins(leftMargin, topMargin, 0, bottomMargin);
        piece.setOnTouchListener(new DragNDrop((Piece) findViewById(R.id.ghost_piece), relativeLayout));

        pieces.add(piece);
        piece.invalidate();

        for (int i = 0; i < NUMBER_OF_PIECES - 1; i++) {
            Piece duplicatePiece = new Piece(piece);

            duplicatePiece.setLayoutParams(new RelativeLayout.LayoutParams(layoutParams));
            duplicatePiece.setOnTouchListener(new DragNDrop((Piece) findViewById(R.id.ghost_piece), relativeLayout));

            pieces.add(duplicatePiece);

            duplicatePiece.setVisibility(View.VISIBLE);
            duplicatePiece.invalidate();

            relativeLayout.addView(duplicatePiece);
        }
        relativeLayout.invalidate();
    }

    /**
     * Set up sizes, tiles, pieces, and start the game.
     * Happens after onCreate.
     *
     * @param hasFocus Does the window have focus.
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        getSizes();
        setTiles();
        setPieces(gamePieceWhite, whitePieces);
        setPieces(gamePieceBlack, blackPieces);
        ((MultiplePiecesTile) findViewById(R.id.start_white)).setPieces(whitePieces);
        ((MultiplePiecesTile) findViewById(R.id.start_black)).setPieces(blackPieces);
        setLabels();
        DragNDrop.tiles = tiles;
        changeTurn();
    }

    /**
     * Sets the positioning of all of the labels.
     */
    private void setLabels() {
        float tileSize = (width_dp) * TILE_PERCENT_OF_SCREEN;
        TextView tempLabel = (TextView) starts_ends.keySet().toArray()[0];
        int leftMargin = (int) convertDpToPixel( tileSize / 2, getApplicationContext()) - tempLabel.getMeasuredWidth() / 2;
        int bottomMargin = (int) convertDpToPixel( tileSize / 2, getApplicationContext()) - tempLabel.getMeasuredHeight() / 2;

        for (TextView label : starts_ends.keySet()) {
            ((RelativeLayout.LayoutParams) label.getLayoutParams()).setMargins(leftMargin,0,0,bottomMargin);
        }
        updateLabels();
        pushLabelsToFront();
    }

    /**
     * Pushes all the labels to the front of the layout.
     */
    public static void pushLabelsToFront() {
        for (TextView label : starts_ends.keySet()) {
            relativeLayout.bringChildToFront(label);
        }
    }

    /**
     * Updates the number on all of the labels.
     */
    @SuppressLint("SetTextI18n")
    public static void updateLabels() {
        for (TextView label : starts_ends.keySet()) {
            int numberOfPieces = Objects.requireNonNull(starts_ends.get(label)).getNumberOfPieces();
            if (numberOfPieces == 0) {
                label.setVisibility(View.INVISIBLE);
            } else {
                label.setVisibility(View.VISIBLE);
            }
            label.setText("" + numberOfPieces);
        }
    }

    /**
     * Converts pixels to dp.
     *
     * @param px the size in pixels.
     * @param context the context we're in.
     * @return the result of the conversion.
     */
    public static float convertPixelsToDp(float px, Context context) {
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * Converts pixels to dp.
     * @param dp the size in dp.
     * @param context the context we're in.
     * @return the result of the conversion.
     */
    public static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * Happens onClick of this object.
     * Rolls the dice if that button was hit.
     *
     * @param view The view that was clicked on.
     */
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.dice_roll_button) {
            if (!didRoll)
                currentRoll = rollDice();
        }
    }

    /**
     * Roll the dice and count the sum of the dice.
     *
     * @return The sum of the dice.
     */
    private int rollDice() {
        Random random = new Random();
        int dieUpId = R.drawable.pyramid_die_up;
        int dieDownId = R.drawable.pyramid_die_down;
        int count = 0;
        int cur = 0;

        for (ImageView die : dice) {
            cur = random.nextInt(2);
            count += cur;
            die.setImageResource(cur == 1 ? dieUpId : dieDownId);
        }
        Log.d("INFO", "rollDice: count=" + count);
        didRoll = true;
        if (count == 0) {
            changeTurn();
        }
        return count;
    }

    /**
     * Reset the dice to be all down, and enable re-rolling.
     */
    public static void resetDice() {
        currentRoll = 0;
        for (ImageView die : dice) {
            die.setImageResource(R.drawable.pyramid_die_down);
        }
        didRoll = false;
    }

    /**
     * Change the turn to the other person.
     * Enable the new person's pieces and the disable the others.
     * Reset the dice.
     */
    public static void changeTurn(){
        GameActivity.whitesTurn = !whitesTurn;

        Log.d("INFO", "changeTurn: cur turn=" + (GameActivity.whitesTurn ? "whites" : "blacks"));
        ArrayList<Piece> turn = whitesTurn ? whitePieces : blackPieces;
        ArrayList<Piece> notTurn = whitesTurn ? blackPieces : whitePieces;

        for (Piece p : turn) {
            p.setEnabled(true);
        }

        for (Piece p : notTurn) {
            p.setEnabled(false);
        }

        resetDice();
        if (checkWin()) {
            Log.d("INFO", "changeTurn: " + (whitesTurn ? "Blacks" : "whites") + " Win!");
        }

    }

    private static boolean checkWin() {
        for (MultiplePiecesTile tile : starts_ends.values()) {
            if (tile.isEnd() && tile.getNumberOfPieces() == NUMBER_OF_PIECES) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gives another turn to the current turn holder.
     */
    public static void anotherTurn() {
        resetDice();
    }
}