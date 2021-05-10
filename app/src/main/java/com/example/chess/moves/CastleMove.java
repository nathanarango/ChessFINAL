package com.example.chess.moves;

public class CastleMove extends Move{

    private final int castleLocation;

    public CastleMove(int startPosition, int endPosition, int previousEnPassantTile, int previousCastleRights, int previousHalfMoveCount, int castleLocation) {

        super(startPosition, endPosition, previousEnPassantTile, previousCastleRights, previousHalfMoveCount);
        this.castleLocation = castleLocation;
    }

    public int getCastleLocation(){
        return  castleLocation;
    }
}