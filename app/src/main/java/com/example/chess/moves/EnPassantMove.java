package com.example.chess.moves;

public class EnPassantMove  extends Move{

    public EnPassantMove(int startPosition, int endPosition, int previousEnPassantTile, int previousCastleRights, int previousHalfMoveCount) {

        super(startPosition, endPosition, previousEnPassantTile, previousCastleRights, previousHalfMoveCount);
    }
}