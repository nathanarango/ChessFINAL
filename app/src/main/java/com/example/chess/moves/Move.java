package com.example.chess.moves;

public abstract class Move {

    private final int startPosition;
    private final int endPosition;
    private final int previousEnPassantTile;
    private final int previousCastleRights;
    private final int previousHalfMoveCount;

    public Move(int startPosition, int endPosition, int previousEnPassantTile, int previousCastleRights, int previousHalfMoveCount) {

        this.startPosition = startPosition;
        this.endPosition = endPosition;
        this.previousEnPassantTile = previousEnPassantTile;
        this.previousHalfMoveCount = previousHalfMoveCount;
        this.previousCastleRights = previousCastleRights;
    }

    public int getEndPosition(){
        return endPosition;
    }

    public int getStartPosition(){
        return startPosition;
    }

    public int getPreviousCastleRights(){
        return previousCastleRights;
    }

    public int getPreviousEnPassantTile(){
        return previousEnPassantTile;
    }

    public int getPreviousHalfMoveCount(){
        return previousHalfMoveCount;
    }
}
