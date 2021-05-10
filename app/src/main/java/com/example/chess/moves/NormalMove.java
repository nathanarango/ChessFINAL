package com.example.chess.moves;

public class NormalMove extends Move{

    public NormalMove(int startPosition, int endPosition, int previousEnPassantTile, int previousCastleRights, int previousHalfMoveCount) {

        super(startPosition, endPosition, previousEnPassantTile, previousCastleRights, previousHalfMoveCount);
    }
}
