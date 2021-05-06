package com.example.chess.moves;

public abstract class Move {

    private final int startPosition;
    private final int endPosition;
    private final int previousEnPassantTile;
    private final boolean[] previousCastleRights = new boolean[]{false, false, false, false};
    private final int previousHalfMoveCount;

    public Move(int startPosition, int endPosition, int previousEnPassantTile, boolean[] previousCastleRights, int previousHalfMoveCount) {

        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.previousEnPassantTile = previousEnPassantTile;
        this.previousHalfMoveCount = previousHalfMoveCount;

        for(int i = 0; i < 4; i ++){
            this.previousCastleRights[i] = previousCastleRights[i];
        }
    }

    public int getEndPosition(){
        return endPosition;
    }

    public int getStartPosition(){
        return startPosition;
    }

    public boolean[] getPreviousCastleRights(){
        return previousCastleRights;
    }

    public int getPreviousEnPassantTile(){
        return previousEnPassantTile;
    }

    public int getPreviousHalfMoveCount(){
        return previousHalfMoveCount;
    }
}
