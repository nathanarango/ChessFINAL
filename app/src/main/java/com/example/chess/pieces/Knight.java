package com.example.chess.pieces;

import com.example.chess.GameLogic;

import java.util.ArrayList;

public class Knight extends Piece{

    public static final int[] MOVE_VECTORS = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(int position, boolean isWhite){
        super(position, isWhite);
    }

    public static long getTilesAttacked(final int startPosition) {

        long tilesAttacked = 0L;

        for(int vector : MOVE_VECTORS){

            if(!(((vector == -6 || vector == 10) && startPosition % 8 == 6) ||
                    ((vector == -6 || vector == 10 || vector == 17 || vector == -15) && startPosition % 8 == 7) ||
                    ((vector == 6 || vector == -10 || vector == -17 || vector == 15) && startPosition % 8 == 0) ||
                    ((vector == 6 || vector == -10) && startPosition % 8 == 1)) &&
                    (startPosition + vector >= 0 && startPosition + vector < 64)){

                tilesAttacked |= (1L << (startPosition + vector));
            }
        }
        return tilesAttacked;
    }

    public static long getTilesToMove(final int startPos, final long myPieces, final long enemyPieces, final boolean attackOnly, final int enemyKingSquare){

        long tilesToMove = 0L;

        for(int vector : MOVE_VECTORS){

            if(!(((vector == -6 || vector == 10) && startPos % 8 == 6) ||
                    ((vector == -6 || vector == 10 || vector == 17 || vector == -15) && startPos % 8 == 7) ||
                    ((vector == 6 || vector == -10 || vector == -17 || vector == 15) && startPos % 8 == 0) ||
                    ((vector == 6 || vector == -10) && startPos % 8 == 1)) &&
                    (startPos + vector >= 0 && startPos + vector < 64)){

                if(((myPieces >> (startPos + vector)) & 1) == 0){
                    tilesToMove |= (1L << (startPos + vector));
                }
            }
        }

//        TODO for attack only checks
//        if(attackOnly){
//            long checkTiles = getTilesAttacked(enemyKingSquare);
//            tilesToMove &= (checkTiles | enemyPieces);
//        }

        return tilesToMove;
    }
}
