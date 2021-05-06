package com.example.chess.pieces;

import com.example.chess.GameLogic;

import java.util.ArrayList;

public class King extends Piece{

    public static final int[] MOVE_VECTORS = {-9, -8, -7, -1, 1, 7, 8, 9};

    public King(int position, boolean isWhite){
        super(position, isWhite);
    }

    @Override
    public ArrayList<Integer> getTilesAttacked(int startPosition, boolean useless, int[] useless2, int useless3) {

        ArrayList<Integer> tilesAttacked = new ArrayList<>();

        for(int vector : MOVE_VECTORS){

            if(!(((vector == 7 || vector == -9 || vector == -1) && startPosition % 8 == 0) ||
                    ((vector == -7 || vector == 9 || vector == 1) && startPosition % 8 == 7)) &&
                    (startPosition + vector >= 0 && startPosition + vector < 64)){

                tilesAttacked.add(startPosition + vector);
            }
        }
        return tilesAttacked;
    }

    public ArrayList<Integer> getTilesToMove(int[] pieceColorOnTile, boolean attackOnly, boolean[] castleRights) {

        ArrayList<Integer> tilesToMove = new ArrayList<>();

        int position = this.getPosition();

        for(int vector : MOVE_VECTORS){

            if(!(((vector == 7 || vector == -9 || vector == -1) && position % 8 == 0) ||
                    ((vector == -7 || vector == 9 || vector == 1) && position % 8 == 7)) &&
                    (position + vector >= 0 && position + vector < 64)){

                if(pieceColorOnTile[position + vector] >= 0){

                    if((pieceColorOnTile[position + vector] == 1 && this.isWhite()) || (pieceColorOnTile[position + vector] == 0 && !this.isWhite())){
                        tilesToMove.add(position + vector);
                    }
                }
                else if(!attackOnly){
                    tilesToMove.add(position + vector);
                }
            }
        }

        if(!attackOnly) {
            if (castleRights[0]) {
                tilesToMove.add(this.isWhite() ? 62 : 6);
            }
            if (castleRights[1]) {
                tilesToMove.add(this.isWhite() ? 58 : 2);
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
