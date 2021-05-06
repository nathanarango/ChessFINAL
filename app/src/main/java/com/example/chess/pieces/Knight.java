package com.example.chess.pieces;

import java.util.ArrayList;

public class Knight extends Piece{

    public static final int[] MOVE_VECTORS = {-17, -15, -10, -6, 6, 10, 15, 17};

    public Knight(int position, boolean isWhite){
        super(position, isWhite);
    }

    @Override
    public ArrayList<Integer> getTilesAttacked(int startPosition, boolean useless, int[] useless2, int useless3) {

        ArrayList<Integer> tilesAttacked = new ArrayList<>();

        for(int vector : MOVE_VECTORS){

            if(!(((vector == -6 || vector == 10) && startPosition % 8 == 6) ||
                    ((vector == -6 || vector == 10 || vector == 17 || vector == -15) && startPosition % 8 == 7) ||
                    ((vector == 6 || vector == -10 || vector == -17 || vector == 15) && startPosition % 8 == 0) ||
                    ((vector == 6 || vector == -10) && startPosition % 8 == 1)) &&
                    (startPosition + vector >= 0 && startPosition + vector < 64)){

                tilesAttacked.add(startPosition + vector);
            }
        }
        return tilesAttacked;
    }

    public ArrayList<Integer> getTilesToMove(int[] pieceColorOnTile, boolean attackOnly, int enemyKingSquare) {

        ArrayList<Integer> tilesToMove = new ArrayList<>();

        int position = this.getPosition();

        for(int vector : MOVE_VECTORS){

            if(!(((vector == -6 || vector == 10) && position % 8 == 6) ||
                    ((vector == -6 || vector == 10 || vector == 17 || vector == -15) && position % 8 == 7) ||
                    ((vector == 6 || vector == -10 || vector == -17 || vector == 15) && position % 8 == 0) ||
                    ((vector == 6 || vector == -10) && position % 8 == 1)) &&
                    (position + vector >= 0 && position + vector < 64)){

                if(pieceColorOnTile[position + vector] >= 0){

                    if((pieceColorOnTile[position + vector] == 1 && this.isWhite()) || (pieceColorOnTile[position + vector] == 0 && !this.isWhite())){
                        tilesToMove.add(position + vector);
                    }
                }
                else{
                    if(attackOnly){
                        //for(int tileNum : getTilesAttacked(enemyKingSquare, true, new int[]{}, -1)){
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
            }
        }
        return tilesToMove;
    }
}
