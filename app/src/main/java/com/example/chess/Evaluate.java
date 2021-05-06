package com.example.chess;

import com.example.chess.pieces.Bishop;
import com.example.chess.pieces.Knight;
import com.example.chess.pieces.Pawn;
import com.example.chess.pieces.Piece;
import com.example.chess.pieces.Queen;
import com.example.chess.pieces.Rook;

public class Evaluate {

    public static final int QUEEN_VALUE = 900;
    public static final int ROOK_VALUE = 500;
    public static final int BISHOP_VALUE = 300;
    public static final int KNIGHT_VALUE = 300;
    public static final int PAWN_VALUE = 100;

    public static int evaluate(Board board){

        int whiteScore = 0;
        int blackScore = 0;

        for(Piece piece : board.getWhitePieces()) {

            if(piece instanceof Queen){
                whiteScore += QUEEN_VALUE;
            }
            else if(piece instanceof Rook){
                whiteScore += ROOK_VALUE;
            }
            else if(piece instanceof Pawn){
                whiteScore += PAWN_VALUE;
            }
            else if(piece instanceof Knight){
                whiteScore += KNIGHT_VALUE;
            }
            else if(piece instanceof Bishop){
                whiteScore += BISHOP_VALUE;
            }
        }

        for(Piece piece : board.getBlackPieces()) {

            if(piece instanceof Queen){
                blackScore += QUEEN_VALUE;
            }
            else if(piece instanceof Rook){
                blackScore += ROOK_VALUE;
            }
            else if(piece instanceof Pawn){
                blackScore += PAWN_VALUE;
            }
            else if(piece instanceof Knight){
                blackScore += KNIGHT_VALUE;
            }
            else if(piece instanceof Bishop){
                blackScore += BISHOP_VALUE;
            }
        }

        int sideToMove = -1;
        if(board.isWhiteToMove()){
            sideToMove = 1;
        }
        return (whiteScore - blackScore) * sideToMove;
    }
}