package com.example.chess.pieces;

import com.example.chess.GameLogic;

import java.util.ArrayList;

public class Bishop extends Piece{

    public static final int[] MOVE_VECTORS = {-7, -9, 7, 9};

    public Bishop(int position, boolean isWhite){
        super(position, isWhite);
    }

    @Override
    public ArrayList<Integer> getTilesAttacked(int startPosition, boolean useless, int[] pieceColorOnTile, int enemyKingTile) {

        ArrayList<Integer> tilesAttacked = new ArrayList<>();

        for(int vector : MOVE_VECTORS){

            int position = startPosition;

            while(position + vector >= 0 && position + vector < 64){

                if(((vector == 7 || vector == -9) && position % 8 == 0) ||
                        ((vector == -7 || vector == 9) && position % 8 == 7)){
                    break;
                }

                tilesAttacked.add(position + vector);

                if(pieceColorOnTile[position + vector] >= 0 && position + vector != enemyKingTile){

                    break;
                }

                position += vector;
            }
        }
        return tilesAttacked;
    }

    public ArrayList<Integer> getTilesToMove(int[] pieceColorOnTile, boolean attackOnly, int enemyKingSquare, int pinDirection) {

        ArrayList<Integer> tilesToMove = new ArrayList<>();

        for(int vector : MOVE_VECTORS){

            if(pinDirection >= 0 && GameLogic.getPinDirection(vector) != pinDirection){
                continue;
            }

            int position = this.getPosition();

            while(position + vector >= 0 && position + vector < 64){

                if(((vector == 7 || vector == -9) && position % 8 == 0) ||
                        ((vector == -7 || vector == 9) && position % 8 == 7)){
                    break;
                }

                if(pieceColorOnTile[position + vector] >= 0){

                    if((pieceColorOnTile[position + vector] == 1 && this.isWhite()) || (pieceColorOnTile[position + vector] == 0 && !this.isWhite())){
                        tilesToMove.add(position + vector);
                    }
                    break;
                }
                else{
                    if(attackOnly){
                        //for(int tileNum : getTilesAttacked(enemyKingSquare, true, pieceColorOnTile, -1)){
                            //if(tileNum == position + vector){
                                //tilesToMove.add(position + vector);
                                //break;
                            //}
                        //}
                    }
                    else{
                        tilesToMove.add(position + vector);
                    }
                }
                position += vector;
            }
        }
        return tilesToMove;
    }
}
