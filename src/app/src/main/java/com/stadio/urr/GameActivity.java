package com.stadio.urr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

enum Starts_Ends {
    START_WHITE, START_BLACK, END_WHITE, END_BLACK
}

public class GameActivity extends AppCompatActivity implements View.OnClickListener, SendMessageDialog.MessageDialogListener {

    public static GameActivity Instance;

    /* --View Variables-- */
    private static RelativeLayout relativeLayout;
    private ConstraintLayout constraintLayoutDice;

    private ImageView userAvatar;
    private TextView userName;
    private TextView userSpeechBubble;

    private ImageView opponentAvatar;
    private TextView opponentName;
    private TextView opponentSpeechBubble;

    private Tile rootTile;

    private static int currentRoll = 0;

    private static ArrayList<Tile> tiles;
    private static ArrayList<Piece> whitePieces;
    private static ArrayList<Piece> blackPieces;
    private Piece gamePieceWhite;
    private Piece gamePieceBlack;
    private static ImageView[] dice;
    private static Map<TextView, MultiplePiecesTile> starts_ends;
    private static TextView messages;

    private static ProgressBar userTimer;
    private static ProgressBar opponentTimer;

    private static boolean didRoll = false;
    private static boolean myTurn = false;
    private static Sides myColor = Sides.WHITE;

    private float width_dp;
    private float height_dp;

    private static boolean playSound;
    private boolean firstSetUp = true;

    private static int[] lastMovement = {-1, -1};


    /* --Constants-- */

    private final int OFFSET = 10;
    private final static int NUMBER_OF_DICE = 4;
    private final static int NUMBER_OF_PIECES = 7;
    private final static float NUMBER_OF_TILES_HORIZONTAL = 8;
    private final static float NUMBER_OF_TILES_VERTICAL = 3;
    public final static int PATH_LENGTH= 15;
    public final static int TURN_TIME_SECONDS = 20;
    public final static int TURN_TIME = TURN_TIME_SECONDS * 1000;


    private final static float PERCENTAGE_OF_TILES_FROM_SCREEN = (float) 75 / 100;
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

        if (GameActivity.Instance == null) {
            Instance = this;
        }

        setContentView(R.layout.activity_game);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        ListenForEvents();

        getReferences();

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            JSONObject obj = null;

            try {
                obj = new JSONObject(bundle.getString("opponentInfo"));
                opponentName.setText(obj.getString("username"));
                String imageBase64 = obj.getString("avatar");
                opponentAvatar.setImageBitmap(Utils.parseBitmapFromBase64(imageBase64));
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        }

        userAvatar.setImageBitmap(AccountDetails.avatar);
        userName.setText(AccountDetails.username);

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

        playSound = bundle.getBoolean(getString(R.string.sound_effects));

        AccountDetails.socket.emit("joined-game");
    }

    /**
     * Set up sizes, tiles, pieces, and start the game.
     * Happens after onCreate.
     *
     * @param hasFocus Does the window have focus.
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        if (firstSetUp) {
            getSizes();
            setTiles();

            whitePieces = createPiecesFromOriginal(gamePieceWhite);
            blackPieces = createPiecesFromOriginal(gamePieceBlack);

            enableMyPieces();

            ((MultiplePiecesTile) findViewById(R.id.start_white)).setPieces(whitePieces);
            ((MultiplePiecesTile) findViewById(R.id.start_black)).setPieces(blackPieces);

            setLabels();
            DragNDrop.tiles = tiles;
            firstSetUp = false;
        }
    }

    /* --Private Methods-- */

    private void getReferences() {
        relativeLayout = findViewById(R.id.game_relative_layout);
        constraintLayoutDice = findViewById(R.id.constraint_layout_dice);

        userAvatar = findViewById(R.id.userAvatar);
        userName = findViewById(R.id.userName);
        userSpeechBubble = findViewById(R.id.userSpeechBubble);

        opponentAvatar = findViewById(R.id.opponentAvatar);
        opponentName = findViewById(R.id.opponentName);
        opponentSpeechBubble = findViewById(R.id.opponentSpeechBubble);

        messages = findViewById(R.id.messages);

        userTimer = findViewById(R.id.userTimer);
        opponentTimer = findViewById(R.id.opponentTimer);

        rootTile = findViewById(R.id.tile);

        findViewById(R.id.dice_roll_button).setOnClickListener(this);
    }

    private void ListenForEvents() {
        AccountDetails.socket.on("your-turn", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SETUP_SIDE", "It is your turn!");
                myTurn = true;

                enableDisablePieces(myColor, true);

                startTimer(userTimer);
                stopTimer(opponentTimer);
            }
        });

        AccountDetails.socket.on("setup-side", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                int colorSide = Integer.parseInt(args[0].toString());

                myColor = colorSide == 0 ? Sides.WHITE : Sides.BLACK;

                enableMyPieces();
                setPlayersColorIndication();

                Log.d("SETUP_SIDE", "Your color is: " + myColor.toString());
                if (myColor == Sides.WHITE) {
                    startTimer(userTimer);
                } else {
                    startTimer(opponentTimer);
                }
            }
        });

        AccountDetails.socket.on("move-piece", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                JSONObject obj = null;
                int from = 0;
                int to = 0;

                try {
                    obj = new JSONObject(args[0].toString());
                    from = obj.getInt("from");
                    to = obj.getInt("to");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                int[] newMovement = new int[]{from, to};

                if (lastMovement[0] == newMovement[0] && lastMovement[1] == newMovement[1]) {
                    Log.d("", "Found duplicate movement. Ignored");
                    return;
                }

                Sides enemySide = myColor == Sides.WHITE ? Sides.BLACK : Sides.WHITE;


                Tile fromTile = DragNDrop.getLandableTileByIndex(from, enemySide.getValue());
                Tile toTile = DragNDrop.getLandableTileByIndex(to, enemySide.getValue());

                lastMovement = newMovement;

                movePiece(fromTile, toTile);

                GameActivity.Instance.updateLabels();

                if (!myTurn && opponentTimer != null) {
                    startTimer(opponentTimer);
                }
            }
        });

        AccountDetails.socket.on("receive-message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String message = args[0].toString();
                displayMessage(opponentSpeechBubble, message);
            }
        });

        AccountDetails.socket.on("opponent-forfeit", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                onOpponentForfeit();
            }
        });
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


    private void setPlayersColorIndication() {
        Instance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView userIndication = findViewById(R.id.userPieceColor);
                userIndication.setImageResource(myColor == Sides.WHITE ? R.drawable.piece_white : R.drawable.piece_black);
                ImageView opponentIndication = findViewById(R.id.opponentPieceColor);
                opponentIndication.setImageResource(myColor != Sides.WHITE ? R.drawable.piece_white : R.drawable.piece_black);
            }
        });
    }

    private void enableMyPieces() {
        enableDisablePieces((myColor == Sides.WHITE) ? Sides.BLACK : Sides.WHITE, false);
        enableDisablePieces(myColor, true);
    }

    private void enableDisablePieces(final Sides color, final boolean enableDisable) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Piece p : (color == Sides.WHITE) ? whitePieces : blackPieces) {
                    p.setEnabled(enableDisable);
                }
            }
        });
    }


    /* --Public Methods-- */

    /**
     * Moves a piece from tile from to tile to.
     *
     * @param from The tile we want to move a piece from.
     * @param to The tile we want to move the piece to.
     */
    public static void movePiece(Tile from, Tile to) {

        if (from.isEmpty()) {
            Log.d("", "FromTile " + from.getIndex() + " is empty!");
//            throw new InvalidParameterException("No piece on tile");
            return;
        }

        playSound(R.raw.pop_sound);
        Piece movedPiece = from.getPiece();

        if (!to.isEmpty() && to.getPiece().side != movedPiece.side) {
            Log.d("", "Eating tile at " + to.getIndex());
            movePiece(to, to.getPiece().getStartTile());
        }

        from.removePiece();
        to.setPiece(movedPiece);
        DragNDrop.snapToTile(movedPiece, to);
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
                (height_dp - diceHeight - tileSize * NUMBER_OF_TILES_VERTICAL - OFFSET) / 2, getApplicationContext());
        float margin_right = convertDpToPixel(
                (width_dp  - tileSize * NUMBER_OF_TILES_HORIZONTAL) / 2, getApplicationContext());

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
     */
    @SuppressLint("ClickableViewAccessibility")
    private ArrayList<Piece> createPiecesFromOriginal(Piece piece) {
        ArrayList<Piece> pieces = new ArrayList<>();

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

        return pieces;
    }

    /**
     * Sets the all of the labels.
     */
    private void setLabels() {
        updateLabels();
        pushLabelsToFront();
    }

    /**
     * Pushes all the labels to the front of the layout.
     */
    public static void pushLabelsToFront() {
        for (TextView label : starts_ends.keySet()) {
        }
    }

    /**
     * Updates the number on all of the labels.
     */
    @SuppressLint("SetTextI18n")
    public void updateLabels() {
        pushLabelsToFront();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (TextView label : starts_ends.keySet()) {
                    relativeLayout.bringChildToFront(label);
                    int numberOfPieces = Objects.requireNonNull(starts_ends.get(label)).getNumberOfPieces();
                    if (numberOfPieces == 0) {
                        label.setVisibility(View.INVISIBLE);
                    } else {
                        label.setVisibility(View.VISIBLE);
                    }
                    label.setText("" + numberOfPieces);
                }
            }
        });
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
            if (myTurn) {
                if (!didRoll)
                    currentRoll = rollDice();
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.not_turn), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Roll the dice and count the sum of the dice.
     *
     * @return The sum of the dice.
     */
    private int rollDice() {
        playSound(R.raw.dice_sound);
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
//        Log.d("INFO", "rollDice: count=" + count);
        didRoll = true;
        if (count == 0) {
            Log.d("INFO", "You rolled 0");
            indicate0();
            changeTurn();
        }
        return count;
    }

    private void indicate0() {
        messages.setVisibility(View.VISIBLE);
        messages.setText(R.string.rolled_0);
        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                messages.setVisibility(View.INVISIBLE);
            }
        };
        handler.postDelayed(r, 2000);
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
        myTurn = false;
        AccountDetails.socket.emit("pass-turn");
        lastMovement = new int[]{-1, -1};

        GameActivity.Instance.enableDisablePieces(myColor, false);

        resetDice();
        if (checkWin()) {
            Log.d("INFO", "changeTurn: " + (myColor == Sides.BLACK ? "Blacks" : "whites") + " Win!");

            // Client won. Send to server.
            AccountDetails.socket.emit("won-game");
        }

        startTimer(opponentTimer);
        stopTimer(userTimer);
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
        startTimer(userTimer);
    }

    public static void playSound(int sound) {
        if (playSound) {
            MediaPlayer mediaPlayer = MediaPlayer.create(GameActivity.Instance, sound);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.release();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        GameExitDialog gameExitDialog=new GameExitDialog(this);
        gameExitDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        gameExitDialog.show();
    }

    public void openMessageDialogOnClick(View view) {
        SendMessageDialog sendMessageDialog = new SendMessageDialog(this);
        sendMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sendMessageDialog.show();
    }

    /**
     * Get's called when the message dialog finish.
     *
     * @param message The message that is being sent.
     * @param dialog the dialog who sent it.
     */
    @Override
    public void onReturnValue(String message, Dialog dialog) {
        AccountDetails.socket.emit("send-message", message);
        dialog.dismiss();
        displayMessage(userSpeechBubble, message);
    }

    /**
     * Displaying a message on a speech bubble.  
     * @param speechBubble The TextView we want to display the message on.
     * @param message The message we want to display.
     */
    private void displayMessage(final TextView speechBubble, final String message) {
        final View parent = (View) speechBubble.getParent();

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parent.setVisibility(View.VISIBLE);

                speechBubble.setText(message);

                Handler handler = new Handler();
                Runnable r=new Runnable() {
                    public void run() {
                        parent.setVisibility(View.INVISIBLE);
                    }
                };

                handler.postDelayed(r, 3000);
            }
        });
    }


    private void onOpponentForfeit() {
        Log.d("", "Opponent forfeited match");
        // Client won. Send to server.
        AccountDetails.socket.emit("won-game");
        finish();
    }

    public void onExitMatch() {
        Log.d("", "Exited match. Forfeit");
        AccountDetails.socket.emit("exit-match");
    }

    public static void startTimer(final ProgressBar timer) {
        if (TURN_TIME != 0) {
            Instance.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ProgressBarAnimation anim = new ProgressBarAnimation(timer, 0, 100);
                    anim.setDuration(TURN_TIME);
                    timer.startAnimation(anim);
                }
            });
        }
    }

    public static void timesUp() {
        if (myTurn) {
            changeTurn();
        }
    }

    public static void stopTimer(final ProgressBar timer) {
        if (TURN_TIME != 0) {
            Instance.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    timer.clearAnimation();
                    timer.setProgress(0);
                }
            });
        }
    }
}
