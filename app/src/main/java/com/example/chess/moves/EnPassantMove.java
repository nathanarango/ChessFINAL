package com.example.chess.moves;

public class EnPassantMove  extends Move{

    public EnPassantMove(int startPosition, int endPosition, int previousEnPassantTile, boolean[] previousCastleRights, int previousHalfMoveCount) {

        super(startPosition, endPosition, previousEnPassantTile, previousCastleRights, previousHalfMoveCount);
    }
}