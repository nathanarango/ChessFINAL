package com.example.chess;

import com.example.chess.moves.Move;

import java.util.ArrayList;

public class Search {

    public final int NEGATIVE_INFINITY = -9999999;
    public final int INFINITY = 9999999;

    private final Board currentBoard;

    public int counter = 0;

    public Search(Board board){
        this.currentBoard = board;
    }

    public Move findBestMove(int depth){

        ArrayList<Move> allMoves = this.currentBoard.generateAllLegalMoves(false);

        Move bestMove = allMoves.get(0);
        int bestMoveEval = INFINITY;

        for(Move move : allMoves){
            this.currentBoard.makeMove(move);
            int moveEval = getMoveEval(depth, NEGATIVE_INFINITY, INFINITY);
            if(moveEval < bestMoveEval){
                bestMoveEval = moveEval;
                bestMove = move;
            }
            this.currentBoard.unMakeMove(move);
        }
        return bestMove;
    }

    public int getMoveEval(int depth, int alpha, int beta){

        if(depth == 0){
            return getMoveEvalCaptures(alpha, beta);
        }

        ArrayList<Move> allMoves = this.currentBoard.generateAllLegalMoves(false);
        if(allMoves.isEmpty()){
            if(this.currentBoard.isInCheck()){
                return NEGATIVE_INFINITY;
            }
            return 0;
        }

        for(Move move : allMoves){
            this.currentBoard.makeMove(move);
            int eval = -getMoveEval(depth - 1, -beta, -alpha);
            this.currentBoard.unMakeMove(move);
            if(eval >= beta){
                return beta;
            }
            if(eval > alpha){
                alpha = eval;
            }
        }

        return alpha;
    }

    public int getMoveEvalCaptures(int alpha, int beta){

        this.counter ++;

        int eval = Evaluate.evaluate(this.currentBoard);
        if(eval >= beta){
            return beta;
        }
        if(eval > alpha){
            alpha = eval;
        }

        ArrayList<Move> allCaptureMoves = this.currentBoard.generateAllLegalMoves(true);

        for(Move captureMove : allCaptureMoves){
            this.currentBoard.makeMove(captureMove);
            eval = -getMoveEvalCaptures(-beta, -alpha);
            this.currentBoard.unMakeMove(captureMove);

            if(eval >= beta){
                return beta;
            }
            if(eval > alpha){
                alpha = eval;
            }
        }
        return alpha;
    }
}