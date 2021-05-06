package com.example.chess.pieces;

import com.example.chess.MainActivity;

import java.util.ArrayList;

public abstract class Piece {

    private int position;
    private final boolean isWhite;

    public Piece(int position, boolean isWhite){

        this.position = position;
        this.isWhite = isWhite;
    }

    public void setPosition(int position){
        this.position = position;
    }

    public int getPosition(){
        return position;
    }

    public boolean isWhite(){
        return isWhite;
    }

    public String getPieceName(){
        String color = isWhite ? "white_" : "black_";
        String name = "pawn";
        if(this instanceof Rook){
            name = "rook";
        }
        else if(this instanceof Bishop){
            name = "bishop";
        }
        else if(this instanceof Knight){
            name = "knight";
        }
        else if(this instanceof King){
            name = "king";
        }
        else if(this instanceof Queen){
            name = "queen";
        }
        return color + name;
    }

    public void printPiece(){
        System.out.print(getPieceName() + " on " + position + " (" + MainActivity.TILE_NAMES[position] + ")");
    }

    public abstract ArrayList<Integer> getTilesAttacked(int startPosition, boolean isWhite, int[] pieceColorOnTile, int enemyKingTile);
}
