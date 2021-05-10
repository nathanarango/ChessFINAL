package com.example.chess.moves;

public class PromotionMove  extends Move{

    private final int promotionPieceNum;
    private final int pieceTaken;

    public PromotionMove(int startPosition, int endPosition, int previousEnPassantTile, int previousCastleRights, int previousHalfMoveCount, int promotionPieceNum, int pieceTaken) {
        super(startPosition, endPosition, previousEnPassantTile, previousCastleRights, previousHalfMoveCount);
        this.promotionPieceNum = promotionPieceNum;
        this.pieceTaken = pieceTaken;
    }

    public int getPromotionPieceNum() {
        return promotionPieceNum;
    }

    public int getPieceTaken(){
        return  pieceTaken;
    }
}