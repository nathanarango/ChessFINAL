package com.example.chess.pieces;

import com.example.chess.GameLogic;

public class King extends Piece{

    public static final int[] MOVE_VECTORS = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(int position, boolean isWhite){
        super(position, isWhite);
    }

    public static long getTilesAttacked(int startPosition) {

        long tilesAttacked = 0L;

        for(int vector : MOVE_VECTORS){

            if(!(((vector == 7 || vector == -9 || vector == -1) && startPosition % 8 == 0) ||
                    ((vector == -7 || vector == 9 || vector == 1) && startPosition % 8 == 7)) &&
                    (startPosition + vector >= 0 && startPosition + vector < 64)){

                tilesAttacked |= (1L << (startPosition + vector));
            }
        }
        return tilesAttacked;
    }

    public static long getTilesToMove(final int startPos, final long myPieces, final long enemyPieces, final boolean attackOnly, final boolean[] castleRights) {

        long tilesToMove = 0L;

        for(int vector : MOVE_VECTORS){

            if(!(((vector == 7 || vector == -9 || vector == -1) && startPos % 8 == 0) ||
                    ((vector == -7 || vector == 9 || vector == 1) && startPos % 8 == 7)) &&
                    (startPos + vector >= 0 && startPos + vector < 64)){

                if(((myPieces >> (startPos + vector)) & 1) == 0){
                    tilesToMove |= (1L << (startPos + vector));
                }
            }
        }

        if(attackOnly){
            tilesToMove &= enemyPieces;
        }
        else {
            if (castleRights[0]) {
                tilesToMove |= (startPos == 4 ? (1L << 6) : (1L << 62));
            }
            if (castleRights[1]) {
                tilesToMove |= (startPos == 4 ? (1L << 2) : (1L << 58));
            }
        }

        return tilesToMove;
    }

    public static int[] findPins(final int startPos, final long myPieces, final long allPieces, final long horizontalSlider, final long diagonalSliders){

        int[] pins = new int[64];

        for(int vector : MOVE_VECTORS){

            int position = startPos;

            int pinPieceTile = 64;

            while(position + vector >= 0 && position + vector < 64){

                if(((vector == 7 || vector == -9 || vector == -1) && position % 8 == 0) ||
                        ((vector == -7 || vector == 9 || vector == 1) && position % 8 == 7)){
                    break;
                }

                if(((allPieces >> (position + vector)) & 1) == 1){

                    if(pinPieceTile < 64){
                        if(((((horizontalSlider >> (position + vector)) & 1) == 1) && (Math.abs(vector) == 8 || Math.abs(vector) == 1)) ||
                                ((((diagonalSliders >> (position + vector)) & 1) == 1) && (Math.abs(vector) == 7 || Math.abs(vector) == 9))){

                            pins[pinPieceTile] = GameLogic.getPinDirection(vector);
                        }
                        break;
                    }
                    else {
                        if(((myPieces >> (position + vector)) & 1) == 1){
                            pinPieceTile = position + vector;
                        }
                        else {
                            break;
                        }
                    }
                }

                position += vector;
            }
        }
        return pins;
    }
}
