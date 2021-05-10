package com.example.chess.pieces;

import com.example.chess.GameLogic;

public class Pawn extends Piece{

    public static final int[] MOVE_VECTORS = {7, 8, 9};

    public Pawn(int position, boolean isWhite){
        super(position, isWhite);
    }

    public static long getTilesAttacked(final int startPosition, final boolean isWhite) {

        long tilesAttacked = 0L;

        int multiplier = isWhite ? -1 : 1;

        for(int vector : MOVE_VECTORS){

            if(vector != 8){

                vector *= multiplier;

                if(!(((vector == -7 || vector == 9) && startPosition % 8 == 7) ||
                        ((vector == 7 || vector == -9) && startPosition % 8 == 0)) &&
                        (startPosition + vector >= 0 && startPosition + vector < 64)){

                    tilesAttacked |= (1L << (startPosition + vector));
                }
            }
        }
        return tilesAttacked;
    }

    public static long getTilesToMove(final int startPos, final long enemyPieces, final long allPieces, final boolean attackOnly,
                                      final int enemyKingSquare, final int enPassantTile, final int pinDirection, final boolean isWhite) {

        long tilesToMove = 0L;

        int multiplier = isWhite ? -1 : 1;

        for(int vector : MOVE_VECTORS){

            if(pinDirection > 0 && GameLogic.getPinDirection(vector) != pinDirection){
                continue;
            }

            if(vector == 8) {

                vector = vector * multiplier;

                if(startPos + vector >= 0 && startPos + vector < 64 && (((allPieces >> (startPos + vector)) & 1) == 0)){

                    tilesToMove |= (1L << (startPos + vector));

                    if(isWhite && (47 < startPos && startPos < 56) || !isWhite && (7 < startPos && startPos < 16)){

                        if(((allPieces >> (startPos + vector + vector)) & 1) == 0){

                            tilesToMove |= (1L << (startPos + vector + vector));
                        }
                    }
                }
            }
            else{

                vector = vector * multiplier;

                if(!(((vector == -7 || vector == 9) && startPos % 8 == 7) ||
                        ((vector == 7 || vector == -9) && startPos % 8 == 0)) &&
                        (startPos + vector >= 0 && startPos + vector < 64)){

                    if(((enemyPieces >> (startPos + vector)) & 1) == 1 || startPos + vector == enPassantTile){

                        tilesToMove |= (1L << (startPos + vector));
                    }
                }
            }
        }

//        TODO for attack only checks
//        if(attackOnly){
//            long checkTiles = getTilesAttacked(enemyKingSquare, !isWhite);
//            tilesToMove &= (checkTiles | enemyPieces);
//        }

        return tilesToMove;
    }
}
