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

    public static final int WHITE_MASK = 16;
    public static final int BLACK_MASK = 8;
    public static final int COLOR_MASK = 24;
    public static final int KING_MASK = 1;
    public static final int QUEEN_MASK = 2;
    public static final int ROOK_MASK = 3;
    public static final int KNIGHT_MASK = 4;
    public static final int BISHOP_MASK = 5;
    public static final int PAWN_MASK = 6;
    public static final int PIECE_MASK = 7;

    public static int evaluate(Board board){

        int whiteScore = 0;
        int blackScore = 0;

        for(int i = 0; i < 64; i ++){
            if(((board.getWhitePieces() >> i) & 1) == 1){
                switch (board.getPieceOnTile(i) & PIECE_MASK){
                    case PAWN_MASK:
                        whiteScore += PAWN_VALUE;
                        break;
                    case ROOK_MASK:
                        whiteScore += ROOK_VALUE;
                        break;
                    case KNIGHT_MASK:
                        whiteScore += KNIGHT_VALUE;
                        break;
                    case BISHOP_MASK:
                        whiteScore += BISHOP_VALUE;
                        break;
                    case QUEEN_MASK:
                        whiteScore += QUEEN_VALUE;
                        break;
                }
            }
            else if(((board.getBlackPieces() >> i) & 1) == 1){
                switch (board.getPieceOnTile(i) & PIECE_MASK){
                    case PAWN_MASK:
                        blackScore += PAWN_VALUE;
                        break;
                    case ROOK_MASK:
                        blackScore += ROOK_VALUE;
                        break;
                    case KNIGHT_MASK:
                        blackScore += KNIGHT_VALUE;
                        break;
                    case BISHOP_MASK:
                        blackScore += BISHOP_VALUE;
                        break;
                    case QUEEN_MASK:
                        blackScore += QUEEN_VALUE;
                        break;
                }
            }
        }

        int sideToMove = -1;
        if(board.isWhiteToMove()){
            sideToMove = 1;
        }
        return (whiteScore - blackScore) * sideToMove;
    }
}