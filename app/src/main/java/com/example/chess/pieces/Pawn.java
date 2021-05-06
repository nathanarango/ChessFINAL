package com.example.chess.pieces;

import com.example.chess.GameLogic;

import java.util.ArrayList;

public class Pawn extends Piece{

    public static final int[] MOVE_VECTORS = {7, 8, 9};

    public Pawn(int position, boolean isWhite){
        super(position, isWhite);
    }

    @Override
    public ArrayList<Integer> getTilesAttacked(int startPosition, boolean isWhite, int[] useless, int useless2) {

        ArrayList<Integer> tilesAttacked = new ArrayList<>();

        int multiplier = isWhite ? -1 : 1;

        for(int vector : MOVE_VECTORS){

            if(vector != 8){

                vector = vector * multiplier;

                int endPosition = startPosition + vector;

                if(!(((vector == -7 || vector == 9) && startPosition % 8 == 7) ||
                        ((vector == 7 || vector == -9) && startPosition % 8 == 0)) &&
                        (endPosition >= 0 && endPosition < 64)){

                    tilesAttacked.add(endPosition);
                }
            }
        }
        return tilesAttacked;
    }

    public ArrayList<Integer> getTilesToMove(int[] pieceColorOnTile, boolean attackOnly, int enemyKingSquare, int enPassantTile, int pinDirection) {

        ArrayList<Integer> tilesToMove = new ArrayList<>();

        int position = this.getPosition();
        int multiplier = this.isWhite() ? -1 : 1;

        for(int vector : MOVE_VECTORS){

            if(pinDirection >= 0 && GameLogic.getPinDirection(vector) != pinDirection){
                continue;
            }

            if(vector == 8) {

                vector = vector * multiplier;

                if(position + vector >= 0 && position + vector < 64 && pieceColorOnTile[position + vector] == -1){

                    if(attackOnly){

                        //for(int tileNum : getTilesAttacked(enemyKingSquare, !this.isWhite(), new int[]{}, -1)){
                            //if(tileNum == position + vector){
                                //tilesToMove.add(position + vector);
                                //break;
                            //}
                        //}
                    }
                    else{
                        tilesToMove.add(position + vector);
                    }

                    if(this.isWhite() && (47 < position && position < 56) || !this.isWhite() && (7 < position && position < 16)){

                        if(pieceColorOnTile[position + vector + vector] == -1){

                            if(attackOnly){

                                //for(int tileNum : getTilesAttacked(enemyKingSquare, !this.isWhite(), new int[]{}, -1)){
                                    //if(tileNum == position + vector + vector){
                                        //tilesToMove.add(position + vector + vector);
                                        //break;
                                    //}
                                //}
                            }
                            else{
                                tilesToMove.add(position + vector + vector);
                            }
                        }
                    }
                }
            }
            else{

                vector = vector * multiplier;
                int endPosition = position + vector;

                if(!(((vector == -7 || vector == 9) && position % 8 == 7) ||
                        ((vector == 7 || vector == -9) && position % 8 == 0)) &&
                        (endPosition >= 0 && endPosition < 64)){

                    if((pieceColorOnTile[endPosition] >= 0 && ((pieceColorOnTile[endPosition] == 1 && this.isWhite()) ||
                            (pieceColorOnTile[endPosition] == 0 && !this.isWhite()))) || endPosition == enPassantTile){

                        tilesToMove.add(endPosition);
                    }
                }
            }
        }
        return tilesToMove;
    }
}
