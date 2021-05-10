package com.example.chess.moves;

public class AttackingMove extends Move{

    private final int takenPiece;

    public AttackingMove(int startPosition, int endPosition, int previousEnPassantTile, int previousCastleRights, int previousHalfMoveCount, int takenPiece) {

        super(startPosition, endPosition, previousEnPassantTile, previousCastleRights, previousHalfMoveCount);
        this.takenPiece = takenPiece;
    }

    public int getTakenPiece(){
        return takenPiece;
    }
}