package com.stadio.urr;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.ArrayList;

/**
 * Implements the Drag and Drop mechanism for moving the pieces on the board.
 */
public class DragNDrop implements View.OnTouchListener {
    // TEMPORARY SOLUTION.
    private RelativeLayout relativeLayout;

    public static ArrayList<Tile> tiles;
    private Piece ghostPiece;

    private static Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://10.0.2.2");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes a DragNDrop object.
     */
    public DragNDrop(Piece ghostPiece, RelativeLayout relativeLayout) {
        this.ghostPiece = ghostPiece;
        this.relativeLayout = relativeLayout;
    }

    /**
     * Overrides the onTouch method.
     *
     * @param selectedView: the piece we want to move.
     * @param motionEvent:  What happened.
     * @return returns true if handled false if not.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View selectedView, MotionEvent motionEvent) {
        Piece selectedPiece = (Piece) selectedView;
        Tile startingTile = findTile(selectedPiece);

        final float mouseX = motionEvent.getRawX();
        final float mouseY = motionEvent.getRawY();

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                selectedPiece.dx = mouseX - selectedPiece.getX();
                selectedPiece.dy = mouseY - selectedPiece.getY();
                setGhost(selectedPiece);
                relativeLayout.bringChildToFront(selectedPiece);
                break;

            case MotionEvent.ACTION_MOVE:
                selectedPiece.setX(mouseX - selectedPiece.dx);
                selectedPiece.setY(mouseY - selectedPiece.dy);
                break;

            case MotionEvent.ACTION_UP:
                Tile newTile = getNewTile(selectedPiece, startingTile);

                JSONObject emissionJson = new JSONObject();
                try {
                    emissionJson.put("from", startingTile.getIndex());
                    emissionJson.put("to", newTile.getIndex());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSocket.emit("move-piece", emissionJson);

                ghostPiece.setVisibility(View.INVISIBLE);
                GameActivity.updateLabels();
                if (newTile.isStart() || newTile.isEnd()) {
                    GameActivity.pushLabelsToFront();
                }
                snapToTile(selectedPiece, newTile);
                break;
        }
        selectedPiece.invalidate();
        return true;
    }

    /**
     * Create a ghost piece in places that the currently selected piece can go.
     *
     * @param piece The currently selected piece.
     */
    private void setGhost(Piece piece) {
        Tile destinationTile = getTileByRoll(piece);
        relativeLayout.bringChildToFront(ghostPiece);
        if ((destinationTile.isInvincible() && destinationTile.isInvincible())) {
            relativeLayout.bringChildToFront(destinationTile.getPiece());
        }

        int piece_image = piece.side == Sides.WHITE.getValue() ?
                R.drawable.piece_white :
                R.drawable.piece_black;
        ghostPiece.setImageResource(piece_image);

        snapToTile(ghostPiece, destinationTile);
        ghostPiece.setVisibility(View.VISIBLE);
    }

    /**
     * Get tile by its index and the color of pieces that can land on it.
     *
     * @param tileIndex The index of the tile.
     * @param colorSide The color of the pieces that can land on it.
     * @return The tile in question.
     * @throws InvalidParameterException If a tile doesn't exist for the provided index and color side.
     */
    public static Tile getTileByIndex(int tileIndex, int colorSide) {
        for (Tile tile : tiles) {
            if (tile.getIndex() == tileIndex && tile.canLand(colorSide)) {
                return tile;
            }
        }
        throw new InvalidParameterException(
                String.format("Index %d parameter is out of range for the provided color %d", tileIndex, colorSide)
        );
    }

    /**
     * Get the tile the piece will be dropped into.
     *
     * @param piece the piece we are moving.
     * @param startingTile The tile the piece started from.
     * @return The new tile we moved to.
     */
    private Tile getNewTile(Piece piece, Tile startingTile) {
        Tile destinationTile = getTileByRoll(piece);

        if (checkInside(piece, destinationTile) && !startingTile.isEnd()) {

            if (!destinationTile.isEmpty()) {

                if (destinationTile.getPiece().side == (piece).side) {

                    if(destinationTile.isEnd()) {
                        startingTile.removePiece();
                        destinationTile.setPiece(piece);
                        GameActivity.changeTurn();
                        return destinationTile;
                    }
                    return startingTile;
                }

                if (destinationTile.isInvincible()) {
                    return startingTile;
                }
                eat(destinationTile);
            }

            startingTile.removePiece();
            destinationTile.setPiece(piece);
            if (destinationTile.isAnotherTurn()) {
                GameActivity.anotherTurn();
            }
            else {
                GameActivity.changeTurn();
            }
            return destinationTile;
        }
        return startingTile;
    }

    /**
     * Remove the piece from the provided tile back to its owner's start.
     *
     * @param newTile The tile to move to remove.
     */
    public void eat(Tile newTile) {
        Piece eatenPiece = newTile.getPiece();

        newTile.removePiece();
        eatenPiece.getStartTile().setPiece(eatenPiece);
        snapToTile(eatenPiece, eatenPiece.getStartTile());
        GameActivity.pushLabelsToFront();
    }

    /**
     * Get the next tile possible from a piece and the dice roll.
     *
     * @param piece The piece to check the new tile from.
     * @return The new tile the piece move to.
     */
    private Tile getTileByRoll(Piece piece) {
        int possibleTileIndex = findTile(piece).getIndex() + GameActivity.getCurrentRoll();
        possibleTileIndex = Math.min(possibleTileIndex, GameActivity.PATH_LENGTH);

        return getTileByIndex(possibleTileIndex, piece.side);
    }

    /**
     * Does the actual snapping.
     *
     * @param view the view we want to snap.
     * @param tile the tile we want to snap into.
     */
    public static void snapToTile(View view, Tile tile) {
        view.setX(tile.getX() + (int) ((tile.getWidth() - view.getWidth()) / 2));
        view.setY(tile.getY() + (int) ((tile.getHeight() - view.getHeight()) / 2));
    }

    /**

     * Find the tile that contains the provided piece.
     * If no tile contains the piece, return the home "tile".
     *
     * @param piece The game piece we want to find the tile of.
     * @return The tile that contained the game piece.
     */
    public Tile findTile(Piece piece) {
        for (Tile tile : tiles) {
            if (tile.checkPiece(piece)) {
                return tile;
            }
        }
        return piece.getStartTile();
    }

    /**
     * Checks if a view is inside a certain tile (used for snapping).
     *
     * @param view the view we want to check.
     * @param tile the tile we want to check if it's in.
     * @return Returns whether the view is inside the tile.
     */
    public boolean checkInside(View view, Tile tile) {
        float middleX = view.getX() + (float) view.getWidth() / 2;
        float middleY = view.getY() + (float) view.getHeight() / 2;

        return (
                middleX < tile.getX() + tile.getWidth() && middleX > tile.getX()
                        &&
                        middleY < tile.getY() + tile.getHeight() && middleY > tile.getY()
        );
    }
}