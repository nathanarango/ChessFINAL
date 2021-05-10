package com.example.chess.pieces;

import com.example.chess.GameLogic;

public class Bishop extends Piece{

    public static final int[] MOVE_VECTORS = {-7, -9, 7, 9};

    public Bishop(int position, boolean isWhite){
        super(position, isWhite);
    }

    public static long getTilesAttacked(final int startPosition, final long allPieces, final int enemyKingTile) {

        long tilesAttacked = 0L;

        for(int vector : MOVE_VECTORS){

            int position = startPosition;

            while(position + vector >= 0 && position + vector < 64){

                if(((vector == 7 || vector == -9) && position % 8 == 0) ||
                        ((vector == -7 || vector == 9) && position % 8 == 7)){
                    break;
                }

                tilesAttacked |= (1L << (position + vector));

                if(((allPieces >> (position + vector)) & 1) == 1 && position + vector != enemyKingTile){
                    break;
                }

                position += vector;
            }
        }
        return tilesAttacked;
    }

    public static long getTilesToMove(final int startPos, final long myPieces, final long allPieces, final boolean attackOnly, final int enemyKingSquare, final int pinDirection){

        long tilesToMove = 0L;

        for(int vector : MOVE_VECTORS){

            if(pinDirection > 0 && GameLogic.getPinDirection(vector) != pinDirection){
                continue;
            }

            int position = startPos;

            while(position + vector >= 0 && position + vector < 64){

                if(((vector == 7 || vector == -9) && position % 8 == 0) ||
                        ((vector == -7 || vector == 9) && position % 8 == 7)){
                    break;
                }

                if(((allPieces >> (position + vector)) & 1) == 1){

                    if(((myPieces >> (position + vector)) & 1) == 0){
                        tilesToMove |= (1L << (position + vector));
                    }
                    break;
                }
                else{
                    if(attackOnly){
//                        TODO for attack only checks
//                        long tiles = getTilesAttacked(enemyKingSquare, allPieces, 64);
//                        for(int i = 0; i < 64 && ((tiles >> i) & 1) == 1; i ++){
//                            if(i == position + vector){
//                                tilesToMove |= (1L << i);
//                            }
//                        }
                    }
                    else{
                        tilesToMove |= (1L << (position + vector));
                    }
                }
                position += vector;
            }
        }
        return tilesToMove;
    }
}
