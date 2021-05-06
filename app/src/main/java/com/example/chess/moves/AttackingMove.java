package com.example.chess.moves;

public class AttackingMove extends Move{

    private final String takenPieceName;

    public AttackingMove(int startPosition, int endPosition, int previousEnPassantTile, boolean[] previousCastleRights, int previousHalfMoveCount, String takenPieceName) {

        super(startPosition, endPosition, previousEnPassantTile, previousCastleRights, previousHalfMoveCount);
        this.takenPieceName = takenPieceName;
    }

    public String getTakenPieceName(){
        return takenPieceName;
    }
}