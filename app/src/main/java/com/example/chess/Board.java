package com.example.chess;

import com.example.chess.moves.AttackingMove;
import com.example.chess.moves.CastleMove;
import com.example.chess.moves.EnPassantMove;
import com.example.chess.moves.Move;
import com.example.chess.moves.NormalMove;
import com.example.chess.moves.PromotionMove;
import com.example.chess.pieces.Bishop;
import com.example.chess.pieces.King;
import com.example.chess.pieces.Knight;
import com.example.chess.pieces.Pawn;
import com.example.chess.pieces.Queen;
import com.example.chess.pieces.Rook;

import java.util.ArrayList;

public class

Board {

    private long whitePieces;
    private long blackPieces;
    private long pieces;
    private long kings;
    private long queens;
    private long rooks;
    private long knights;
    private long bishops;
    private long pawns;

    public static final int KING_MASK = 1;
    public static final int QUEEN_MASK = 2;
    public static final int ROOK_MASK = 3;
    public static final int KNIGHT_MASK = 4;
    public static final int BISHOP_MASK = 5;
    public static final int PAWN_MASK = 6;
    public static final int PIECE_MASK = 7;


    private boolean whiteToMove;
    private int enPassantTile;
    private int castleRights;
    private int fullMoveCounter;
    private int halfMoveCounter;
//    private final int[] attackedByMe = new int[64];
//    private final int[] attackedByEnemyOld = new int[64];
//    private final int[] attackedByEnemyPawn = new int[64];
//    private final int[] attackedByEnemyBishop = new int[64];
//    private final int[] attackedByEnemyKnight = new int[64];
//    private final int[] attackedByEnemyRook = new int[64];
//    private final int[] attackedByEnemyQueen = new int[64];
//    private final int[] attackedByEnemyKing = new int[64];
    private long attackedByEnemy;
    private int myKingTile;
    private int enemyKingTile;
    private boolean inCheck;
    private boolean inDoubleCheck;
    private long blockingTiles = 0L;
    private int[] pinnedPieces = new int[64];
    private final ArrayList<Integer> currentTargetSquares = new ArrayList<>();
    private int currentStartSquare = -1;

    public Board(boolean whiteToMove, int enPassantTile, int castleRights, int halfMoveCounter, int fullMoveCounter,
                 long whitePieces, long blackPieces, long kings, long queens, long rooks, long knights, long bishops, long pawns){

        this.whitePieces = whitePieces;
        this.blackPieces = blackPieces;
        this.kings = kings;
        this.queens = queens;
        this.rooks = rooks;
        this.knights = knights;
        this.bishops = bishops;
        this.pawns = pawns;
        this.pieces = whitePieces | blackPieces;

        this.whiteToMove = whiteToMove;
        this.enPassantTile = enPassantTile;
        this.castleRights = castleRights;
        this.fullMoveCounter = fullMoveCounter;
        this.halfMoveCounter = halfMoveCounter;

        updateEssentialVariables();
    }

    public long getWhitePieces(){
        return whitePieces;
    }

    public long getBlackPieces(){
        return blackPieces;
    }

    public boolean isInCheck(){
        return inCheck;
    }

    public ArrayList<Integer> getCurrentTargetSquares(){
        return currentTargetSquares;
    }

    public int getCurrentStartSquare(){
        return currentStartSquare;
    }

    public void clearCurrentTargetSquares(){
        this.currentStartSquare = -1;
        this.currentTargetSquares.clear();
    }

    public void setCurrentStartSquare(int startSquare){
        this.currentStartSquare = startSquare;
    }

    public void setCurrentTargetSquares(ArrayList<Integer> currentTargetSquares){
        this.currentTargetSquares.addAll(currentTargetSquares);
    }

    public boolean isWhiteToMove(){
        return whiteToMove;
    }

    public long getEnemyHorizontalSliders(){
        return (rooks | queens) & (whiteToMove ? blackPieces : whitePieces);
    }

    public long getEnemyDiagonalSliders(){
        return (bishops | queens) & (whiteToMove ? blackPieces : whitePieces);
    }

    public int getPieceOnTile(int tile){

        int piece = 0;

        if(((whitePieces >> tile) & 1) == 1){
            piece |= 0b10000;
        }
        else if(((blackPieces >> tile) & 1) == 1){
            piece |= 0b01000;
        }
        else{
            return piece;
        }

        if(((pawns >> tile) & 1) == 1){
            piece |= PAWN_MASK;
        }
        else if(((bishops >> tile) & 1) == 1){
            piece |= BISHOP_MASK;
        }
        else if(((knights >> tile) & 1) == 1){
            piece |= KNIGHT_MASK;
        }
        else if(((rooks >> tile) & 1) == 1){
            piece |= ROOK_MASK;
        }
        else if(((queens >> tile) & 1) == 1){
            piece |= QUEEN_MASK;
        }
        else {
            piece |= KING_MASK;
        }

        return piece;
    }

    public long getTilesAttacked(int startPosition, int piece){
        switch (piece & PIECE_MASK){
            case PAWN_MASK:
                return Pawn.getTilesAttacked(startPosition, !whiteToMove);
            case ROOK_MASK:
                return Rook.getTilesAttacked(startPosition, pieces, myKingTile);
            case KNIGHT_MASK:
                return Knight.getTilesAttacked(startPosition);
            case BISHOP_MASK:
                return Bishop.getTilesAttacked(startPosition, pieces, myKingTile);
            case QUEEN_MASK:
                return Queen.getTilesAttacked(startPosition, pieces, myKingTile);
            default:
                return King.getTilesAttacked(startPosition);
        }
    }

    private void updateKingTiles(){

        long myKing = kings & ((whiteToMove) ? whitePieces : blackPieces);
        long enemyKing = kings & ((whiteToMove) ? blackPieces : whitePieces);

        boolean whiteUpdated = false;
        boolean blackUpdated = false;
        int i = 0;
        while(!(whiteUpdated && blackUpdated)){
            if(myKing == (1L << i)){
                myKingTile = i;
                whiteUpdated = true;
            }
            if(enemyKing == (1L << i)){
                enemyKingTile = i;
                blackUpdated = true;
            }
            i ++;
        }
    }

    public void resetVariables(){

        this.attackedByEnemy = 0L;
        this.blockingTiles = 0L;
        this.inCheck = false;
        this.inDoubleCheck = false;
        this.currentTargetSquares.clear();
        this.currentStartSquare = -1;
    }

    public void updateEssentialVariables(){

        resetVariables();
        updateKingTiles();

        pinnedPieces = King.findPins(myKingTile, (whiteToMove ? whitePieces : blackPieces), pieces, getEnemyHorizontalSliders(), getEnemyDiagonalSliders());

        long enemyPieces = (whiteToMove ? blackPieces : whitePieces);

        for(int i = 0; i < 64; i ++){
            if(((enemyPieces >> i) & 1) == 1){
                int piece = getPieceOnTile(i);
                long tilesAttacked = getTilesAttacked(i, piece);
                attackedByEnemy |= tilesAttacked;
                boolean checkMove = false;
                for(int j = 0; j < 64; j ++){
                    if(j == myKingTile && ((tilesAttacked >> j) & 1) == 1){
                        checkMove = true;
                        if(inCheck){
                            inDoubleCheck = true;
                        }
                        else{
                            inCheck = true;
                        }
                    }
                }
                if(checkMove){
                    if((piece & PIECE_MASK) == PAWN_MASK || (piece & PIECE_MASK) == KNIGHT_MASK){
                        blockingTiles |= (1L << i);
                    }
                    else{
                        blockingTiles |= GameLogic.getTilesToStopCheck(myKingTile, i);
                    }
                }
            }
        }
    }

//    TODO finish when rest is working
//    public void updateVariablesForEval(){
//
//        for(int i = 0; i < 64; i ++){
//            attackedByMe[i] = 0;
//            attackedByEnemyPawn[i] = 0;
//            attackedByEnemyBishop[i] = 0;
//            attackedByEnemyKnight[i] = 0;
//            attackedByEnemyRook[i] = 0;
//            attackedByEnemyQueen[i] = 0;
//            attackedByEnemyKing[i] = 0;
//        }
//
//        ArrayList<Piece> myPieces = this.whiteToMove ? this.whitePieces : this.blackPieces;
//        for(Piece myPiece : myPieces){
//            for(int tileNum : myPiece.getTilesAttacked(myPiece.getPosition(), myPiece.isWhite(), this.pieceColorOnTile, this.myKingTile)){
//                this.attackedByMe[tileNum] ++;
//            }
//        }
//
//        ArrayList<Piece> enemyPieces = this.whiteToMove ? this.blackPieces : this.whitePieces;
//        for(Piece enemyPiece : enemyPieces){
//            for(int tileNum : enemyPiece.getTilesAttacked(enemyPiece.getPosition(), enemyPiece.isWhite(), this.pieceColorOnTile, this.myKingTile)){
//                if(enemyPiece instanceof Pawn){
//                    this.attackedByEnemyPawn[tileNum] ++;
//                }
//                else if(enemyPiece instanceof Rook){
//                    this.attackedByEnemyRook[tileNum] ++;
//                }
//                else if(enemyPiece instanceof Knight){
//                    this.attackedByEnemyKnight[tileNum] ++;
//                }
//                else if(enemyPiece instanceof Bishop){
//                    this.attackedByEnemyBishop[tileNum] ++;
//                }
//                else if(enemyPiece instanceof Queen){
//                    this.attackedByEnemyQueen[tileNum] ++;
//                }
//                else if(enemyPiece instanceof King){
//                    this.attackedByEnemyKing[tileNum] ++;
//                }
//            }
//        }
//    }

    public long generateLegalMoves(final int piece, final int position, final boolean attackOnly){

        long legalMoves = 0L;

        if(this.inDoubleCheck){

            if((piece & PIECE_MASK) == KING_MASK){
                long possibleTiles = King.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), (whiteToMove ? blackPieces : whitePieces), false, new boolean[]{false, false});
                legalMoves |= (possibleTiles & (~attackedByEnemy));
            }
        }
        else if(this.inCheck){

            if((piece & PIECE_MASK) == KING_MASK){
                long possibleTiles = King.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), (whiteToMove ? blackPieces : whitePieces), false, new boolean[]{false, false});
                legalMoves |= (possibleTiles & (~attackedByEnemy));
            }
            else if(pinnedPieces[position] == 0){

                long possibleTiles = 0L;

                switch(piece & PIECE_MASK){
                    case PAWN_MASK:
                        if(enPassantTile >= 0 && ((Pawn.getTilesAttacked(position, whiteToMove) >> enPassantTile) & 1) == 1){
                            possibleTiles |= (1L << enPassantTile);
                        }
                        else {
                            possibleTiles |= Pawn.getTilesToMove(position, (whiteToMove ? blackPieces : whitePieces),
                                    pieces, false, enemyKingTile, enPassantTile, 0, whiteToMove);
                        }
                        break;
                    case BISHOP_MASK:
                        possibleTiles |= Bishop.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, false, enemyKingTile, 0);
                        break;
                    case KNIGHT_MASK:
                        possibleTiles |= Knight.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces),
                                (whiteToMove ? blackPieces : whitePieces), false, enemyKingTile);
                        break;
                    case ROOK_MASK:
                        possibleTiles |= Rook.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, false, enemyKingTile, 0);
                        break;
                    default:
                        possibleTiles |= Queen.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, false, enemyKingTile, 0);
                        break;
                }

                legalMoves |= (blockingTiles & possibleTiles);
            }
        }
        else if(pinnedPieces[position] > 0){

            switch(piece & PIECE_MASK){
                case PAWN_MASK:
                    legalMoves |= Pawn.getTilesToMove(position, (whiteToMove ? blackPieces : whitePieces),
                                pieces, false, enemyKingTile, enPassantTile, pinnedPieces[position], whiteToMove);
                    break;
                case BISHOP_MASK:
                    legalMoves |= Bishop.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, attackOnly, enemyKingTile, pinnedPieces[position]);
                    break;
                case QUEEN_MASK:
                    legalMoves |= Queen.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, attackOnly, enemyKingTile, pinnedPieces[position]);
                    break;
                case ROOK_MASK:
                    legalMoves |= Rook.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, attackOnly, enemyKingTile, pinnedPieces[position]);
                    break;
            }
        }
        else{
            if((piece & PIECE_MASK) == KING_MASK){

                boolean[] canCastle = new boolean[]{false, false};
                if(!attackOnly) {
                    if (whiteToMove) {
                        if (((castleRights & 0b1000) >> 3 == 1) && ((attackedByEnemy >> 61 & 1) == 0) && ((attackedByEnemy >> 62 & 1) == 0) &&
                                ((pieces >> 61 & 1) == 0) && ((pieces >> 62 & 1) == 0)) {
                            canCastle[0] = true;
                        }
                        if (((castleRights & 0b0100) >> 2 == 1) && ((attackedByEnemy >> 58 & 1) == 0) && ((attackedByEnemy >> 59 & 1) == 0) &&
                                ((pieces >> 58 & 1) == 0) && ((pieces >> 59 & 1) == 0) && ((pieces >> 57 & 1) == 0)) {
                            canCastle[1] = true;
                        }
                    }
                    else {
                        if (((castleRights & 0b0010) >> 1 == 1) && ((attackedByEnemy >> 5 & 1) == 0) && ((attackedByEnemy >> 6 & 1) == 0) &&
                                ((pieces >> 5 & 1) == 0) && ((pieces >> 6 & 1) == 0)) {
                            canCastle[0] = true;
                        }
                        if (((castleRights & 0b0001) == 1) && ((attackedByEnemy >> 2 & 1) == 0) && ((attackedByEnemy >> 3 & 1) == 0) &&
                                ((pieces >> 1 & 1) == 0) && ((pieces >> 2 & 1) == 0) && ((pieces >> 3 & 1) == 0)) {
                            canCastle[1] = true;
                        }
                    }
                }

                long possibleTiles = King.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), (whiteToMove ? blackPieces : whitePieces), attackOnly, canCastle);
                legalMoves |= (possibleTiles & (~attackedByEnemy));
            }
            else {
                if((piece & PIECE_MASK) == PAWN_MASK){
                    long possiblePawnTiles = Pawn.getTilesToMove(position, (whiteToMove ? blackPieces : whitePieces),
                            pieces, attackOnly, enemyKingTile, enPassantTile, 0, whiteToMove);

                    if(((possiblePawnTiles >> enPassantTile) & 1) == 1){
                        boolean isLegal = true;
                        if(position % 8 < enPassantTile % 8){
                            int checkingTile1 = position - 1;
                            int checkingTile2 = position + 2;
                            for(int i = position % 8 - 1; i >= 0; i --){
                                if(((pieces >> checkingTile1) & 1) == 1){
                                    if(((getEnemyHorizontalSliders() >> checkingTile1) & 1) == 1){
                                        for(int j = position % 8 + 2; j < 8; j ++){
                                            if(((pieces >> checkingTile2) & 1) == 1){
                                                if(checkingTile2 == myKingTile){
                                                    isLegal = false;
                                                }
                                                break;
                                            }
                                            checkingTile2 ++;
                                        }
                                    }
                                    else if(checkingTile1 == myKingTile){
                                        for(int j = position % 8 + 2; j < 8; j ++){
                                            if(((pieces >> checkingTile2) & 1) == 1){
                                                if(((getEnemyHorizontalSliders() >> checkingTile2) & 1) == 1){
                                                    isLegal = false;
                                                }
                                                break;
                                            }
                                            checkingTile2 ++;
                                        }
                                    }
                                    break;
                                }
                                checkingTile1 --;
                            }
                        }
                        else{
                            int checkingTile1 = position + 1;
                            int checkingTile2 = position - 2;
                            for(int i = position % 8 + 1; i < 8; i ++){
                                if(((pieces >> checkingTile1) & 1) == 1){
                                    if(((getEnemyHorizontalSliders() >> checkingTile1) & 1) == 1){
                                        for(int j = position % 8 - 2; j >= 0; j --){
                                            if(((pieces >> checkingTile2) & 1) == 1){
                                                if(checkingTile2 == myKingTile){
                                                    isLegal = false;
                                                }
                                                break;
                                            }
                                            checkingTile2 --;
                                        }
                                    }
                                    else if(checkingTile1 == myKingTile){
                                        for(int j = position % 8 - 2; j >= 0; j --){
                                            if(((pieces >> checkingTile2) & 1) == 1){
                                                if(((getEnemyHorizontalSliders() >> checkingTile2) & 1) == 1){
                                                    isLegal = false;
                                                }
                                                break;
                                            }
                                            checkingTile2 --;
                                        }
                                    }
                                    break;
                                }
                                checkingTile1 ++;
                            }
                        }
                        if(!isLegal){
                            possiblePawnTiles ^= (1L << enPassantTile);
                        }
                    }
                    legalMoves |= possiblePawnTiles;
                }
                else if((piece & PIECE_MASK) == ROOK_MASK){
                    legalMoves |= Rook.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, attackOnly, enemyKingTile, 0);
                }
                else if((piece & PIECE_MASK) == BISHOP_MASK){
                    legalMoves |= Bishop.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, attackOnly, enemyKingTile, 0);
                }
                else if((piece & PIECE_MASK) == KNIGHT_MASK){
                    legalMoves |= Knight.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces),
                            (whiteToMove ? blackPieces : whitePieces), attackOnly, enemyKingTile);
                }
                else if((piece & PIECE_MASK) == QUEEN_MASK){
                    legalMoves |= Queen.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, attackOnly, enemyKingTile, 0);
                }
            }
        }
        return legalMoves;
    }

    public ArrayList<Move> generateAllLegalMoves(boolean attackOnly){

        ArrayList<Move> allMoves = new ArrayList<>();

        for(int i = 0; i < 64; i ++){
            if((((whiteToMove ? whitePieces : blackPieces) >> i) & 1) == 1){
                int piece = getPieceOnTile(i);
                long moves = generateLegalMoves(piece, i, attackOnly);

                for(int j = 0; j < 64; j ++){
                    if(((moves >> j) & 1) == 1){
                        if((piece & PIECE_MASK) == PAWN_MASK && (whiteToMove ? (j < 8) : (j > 55))){
                            for(int k = 2; k < 6; k ++){
                                allMoves.add(createMove(i, j, k));
                            }
                        }
                        else{
                            allMoves.add(createMove(i, j, 0));
                        }
                    }
                }
            }
        }
        return allMoves;
    }

    public Move createMove(int startPosition, int endPosition, int promotionNumber){

        Move move;

        if(promotionNumber > 0){

            int pieceTakenNum = 0;
            if(((pieces >> endPosition) & 1) == 1){
                pieceTakenNum = (getPieceOnTile(endPosition) & PIECE_MASK);
            }

            move = new PromotionMove(startPosition, endPosition, enPassantTile, castleRights, halfMoveCounter, promotionNumber, pieceTakenNum);
        }
        else if((((pawns >> startPosition) & 1) == 1) && enPassantTile == endPosition){
            move = new EnPassantMove(startPosition, endPosition, enPassantTile, castleRights, halfMoveCounter);
        }
        else if((((kings >> startPosition) & 1) == 1) && Math.abs(startPosition - endPosition) == 2){

            int castleLocation = 0;
            if(endPosition < 4){
                castleLocation = 3;
            }
            else if(endPosition < 8){
                castleLocation = 2;
            }
            else if(endPosition < 60){
                castleLocation = 1;
            }

            move = new CastleMove(startPosition, endPosition, enPassantTile, castleRights, halfMoveCounter, castleLocation);
        }
        else if(((pieces >> endPosition) & 1) == 1){
            move = new AttackingMove(startPosition, endPosition, enPassantTile, castleRights, halfMoveCounter, getPieceOnTile(endPosition));
        }
        else{
            move = new NormalMove(startPosition, endPosition, enPassantTile, castleRights, halfMoveCounter);
        }

        return move;
    }

    public void makeMove(Move move){

        final int startPosition = move.getStartPosition();
        final int endPosition = move.getEndPosition();
        final int pieceToMove = getPieceOnTile(startPosition);

        if((pieceToMove & PIECE_MASK) == KING_MASK){
            if(whiteToMove){
                castleRights &= 0b0011;
            }
            else{
                castleRights &= 0b1100;
            }
        }

        if(startPosition == 63 || endPosition == 63){
            castleRights &= 0b0111;
        }
        if(startPosition == 56 || endPosition == 56){
            castleRights &= 0b1011;
        }
        if(startPosition == 7 || endPosition == 7){
            castleRights &= 0b1101;
        }
        if(startPosition == 0 || endPosition == 0){
            castleRights &= 0b1110;
        }

        if(move instanceof NormalMove){

            if(((pieceToMove & PIECE_MASK) == PAWN_MASK) && Math.abs(endPosition - startPosition) == 16){
                enPassantTile = (endPosition + startPosition) / 2;
            }
            else{
                enPassantTile = 64;
            }

            long updater = (1L << startPosition) | (1L << endPosition);
            pieces ^= updater;

            if(whiteToMove){
                whitePieces ^= updater;
            }
            else{
                blackPieces ^= updater;
            }

            switch (pieceToMove & PIECE_MASK){
                case PAWN_MASK:
                    pawns ^= updater;
                    break;
                case BISHOP_MASK:
                    bishops ^= updater;
                    break;
                case KNIGHT_MASK:
                    knights ^= updater;
                    break;
                case ROOK_MASK:
                    rooks ^= updater;
                    break;
                case QUEEN_MASK:
                    queens ^= updater;
                    break;
                default:
                    kings ^= updater;
                    break;
            }
        }
        else if(move instanceof AttackingMove){

            long startUpdater = 1L << startPosition;
            long endUpdater = 1L << endPosition;
            long updater = startUpdater | endUpdater;
            pieces ^= startUpdater;

            if(whiteToMove){
                whitePieces ^= updater;
                blackPieces ^= endUpdater;
            }
            else{
                blackPieces ^= updater;
                whitePieces ^= endUpdater;
            }

            switch (pieceToMove & PIECE_MASK){
                case PAWN_MASK:
                    pawns ^= updater;
                    break;
                case BISHOP_MASK:
                    bishops ^= updater;
                    break;
                case KNIGHT_MASK:
                    knights ^= updater;
                    break;
                case ROOK_MASK:
                    rooks ^= updater;
                    break;
                case QUEEN_MASK:
                    queens ^= updater;
                    break;
                default:
                    kings ^= updater;
                    break;
            }

            switch (((AttackingMove) move).getTakenPiece() & PIECE_MASK){
                case PAWN_MASK:
                    pawns ^= endUpdater;
                    break;
                case BISHOP_MASK:
                    bishops ^= endUpdater;
                    break;
                case KNIGHT_MASK:
                    knights ^= endUpdater;
                    break;
                case ROOK_MASK:
                    rooks ^= endUpdater;
                    break;
                default:
                    queens ^= endUpdater;
                    break;
            }

            this.enPassantTile = 64;
        }
        else if(move instanceof EnPassantMove){

            long takingUpdater = (1L << startPosition) | (1L << endPosition);
            long takenUpdater = (1L << (endPosition + (whiteToMove ? 8 : -8)));
            long updater = takingUpdater | takenUpdater;
            pieces ^= updater;
            pawns ^= updater;

            if(whiteToMove){
                whitePieces ^= takingUpdater;
                blackPieces ^= takenUpdater;
            }
            else{
                blackPieces ^= takingUpdater;
                whitePieces ^= takenUpdater;
            }

            enPassantTile = 64;
        }
        else if(move instanceof CastleMove){

            int direction = ((CastleMove) move).getCastleLocation();

            long rookUpdater;
            long kingUpdater;

            if(direction == 0){
                rookUpdater = (1L << 63 | 1L << 61);
                kingUpdater = (1L << 60 | 1L << 62);
            }
            else if(direction == 1){
                rookUpdater = (1L << 56 | 1L << 59);
                kingUpdater = (1L << 60 | 1L << 58);
            }
            else if(direction == 2){
                rookUpdater = (1L << 5 | 1L << 7);
                kingUpdater = (1L << 4 | 1L << 6);
            }
            else{
                rookUpdater = (1L | 1L << 3);
                kingUpdater = (1L << 4 | 1L << 2);
            }

            long updater = rookUpdater | kingUpdater;
            pieces ^= updater;
            rooks ^= rookUpdater;
            kings ^= kingUpdater;

            if(whiteToMove){
                whitePieces ^= updater;
            }
            else{
                blackPieces ^= updater;
            }

            enPassantTile = 64;
        }
        else if(move instanceof PromotionMove){

            long startUpdater = 1L << startPosition;
            long endUpdater = 1L << endPosition;
            long updater = startUpdater | endUpdater;

            pawns ^= startUpdater;
            pieces ^= startUpdater;

            if(whiteToMove){
                whitePieces ^= updater;
                blackPieces ^= endUpdater;
            }
            else{
                blackPieces ^= updater;
                whitePieces ^= endUpdater;
            }

            switch (((PromotionMove) move).getPieceTaken()){
                case ROOK_MASK:
                    rooks ^= endUpdater;
                    break;
                case KNIGHT_MASK:
                    knights ^= endUpdater;
                    break;
                case BISHOP_MASK:
                    bishops ^= endUpdater;
                    break;
                case QUEEN_MASK:
                    queens ^= endUpdater;
                    break;
                default:
                    pieces ^= endUpdater;
                    if(whiteToMove){
                        blackPieces ^= endUpdater;
                    }
                    else{
                        whitePieces ^= endUpdater;
                    }
                    break;
            }

            switch (((PromotionMove) move).getPromotionPieceNum()){
                case QUEEN_MASK:
                    queens ^= endUpdater;
                    break;
                case KNIGHT_MASK:
                    knights ^= endUpdater;
                    break;
                case BISHOP_MASK:
                    bishops ^= endUpdater;
                    break;
                default:
                    rooks ^= endUpdater;
                    break;
            }

            enPassantTile = 64;
        }

        if(move instanceof AttackingMove || ((pieceToMove & PIECE_MASK) == PAWN_MASK)){
            halfMoveCounter = 0;
        }
        else{
            halfMoveCounter ++;
        }

        if(!this.whiteToMove){
            fullMoveCounter ++;
        }

        whiteToMove ^= true;
        updateEssentialVariables();
    }

    public void unMakeMove(Move move){

        final int startPosition = move.getStartPosition();
        final int endPosition = move.getEndPosition();
        final int pieceToUnMove = getPieceOnTile(endPosition);

        if(move instanceof NormalMove){

            long updater = (1L << startPosition) | (1L << endPosition);
            pieces ^= updater;

            if(whiteToMove){
                blackPieces ^= updater;
            }
            else{
                whitePieces ^= updater;
            }

            switch (pieceToUnMove & PIECE_MASK){
                case PAWN_MASK:
                    pawns ^= updater;
                    break;
                case BISHOP_MASK:
                    bishops ^= updater;
                    break;
                case KNIGHT_MASK:
                    knights ^= updater;
                    break;
                case ROOK_MASK:
                    rooks ^= updater;
                    break;
                case QUEEN_MASK:
                    queens ^= updater;
                    break;
                default:
                    kings ^= updater;
                    break;
            }
        }
        else if(move instanceof AttackingMove){

            long startUpdater = 1L << startPosition;
            long endUpdater = 1L << endPosition;
            long updater = startUpdater | endUpdater;
            pieces ^= startUpdater;

            if(whiteToMove){
                blackPieces ^= updater;
                whitePieces ^= endUpdater;
            }
            else{
                whitePieces ^= updater;
                blackPieces ^= endUpdater;
            }

            switch (pieceToUnMove & PIECE_MASK){
                case PAWN_MASK:
                    pawns ^= updater;
                    break;
                case BISHOP_MASK:
                    bishops ^= updater;
                    break;
                case KNIGHT_MASK:
                    knights ^= updater;
                    break;
                case ROOK_MASK:
                    rooks ^= updater;
                    break;
                case QUEEN_MASK:
                    queens ^= updater;
                    break;
                default:
                    kings ^= updater;
                    break;
            }

            switch (((AttackingMove) move).getTakenPiece() & PIECE_MASK){
                case PAWN_MASK:
                    pawns ^= endUpdater;
                    break;
                case BISHOP_MASK:
                    bishops ^= endUpdater;
                    break;
                case KNIGHT_MASK:
                    knights ^= endUpdater;
                    break;
                case ROOK_MASK:
                    rooks ^= endUpdater;
                    break;
                default:
                    queens ^= endUpdater;
                    break;
            }
        }
        else if(move instanceof EnPassantMove){

            long takingUpdater = (1L << startPosition) | (1L << endPosition);
            long takenUpdater = (1L << (endPosition + (whiteToMove ? 8 : -8)));
            long updater = takingUpdater | takenUpdater;
            pieces ^= updater;
            pawns ^= updater;

            if(whiteToMove){
                blackPieces ^= takingUpdater;
                whitePieces ^= takenUpdater;
            }
            else{
                whitePieces ^= takingUpdater;
                blackPieces ^= takenUpdater;
            }
        }
        else if(move instanceof CastleMove){

            int direction = ((CastleMove) move).getCastleLocation();

            long rookUpdater;
            long kingUpdater;

            if(direction == 0){
                rookUpdater = (1L << 63 | 1L << 61);
                kingUpdater = (1L << 60 | 1L << 62);
            }
            else if(direction == 1){
                rookUpdater = (1L << 56 | 1L << 59);
                kingUpdater = (1L << 60 | 1L << 58);
            }
            else if(direction == 2){
                rookUpdater = (1L << 5 | 1L << 7);
                kingUpdater = (1L << 4 | 1L << 6);
            }
            else{
                rookUpdater = (1L | 1L << 3);
                kingUpdater = (1L << 4 | 1L << 2);
            }

            long updater = rookUpdater | kingUpdater;
            pieces ^= updater;
            rooks ^= rookUpdater;
            kings ^= kingUpdater;

            if(whiteToMove){
                blackPieces ^= updater;
            }
            else{
                whitePieces ^= updater;
            }
        }
        else if(move instanceof PromotionMove){

            long startUpdater = 1L << startPosition;
            long endUpdater = 1L << endPosition;
            long updater = startUpdater | endUpdater;

            pawns ^= startUpdater;
            pieces ^= startUpdater;

            if(whiteToMove){
                blackPieces ^= updater;
                whitePieces ^= endUpdater;
            }
            else{
                whitePieces ^= updater;
                blackPieces ^= endUpdater;
            }

            switch (((PromotionMove) move).getPieceTaken()){
                case ROOK_MASK:
                    rooks ^= endUpdater;
                    break;
                case KNIGHT_MASK:
                    knights ^= endUpdater;
                    break;
                case BISHOP_MASK:
                    bishops ^= endUpdater;
                    break;
                case QUEEN_MASK:
                    queens ^= endUpdater;
                    break;
                default:
                    pieces ^= endUpdater;
                    if(whiteToMove){
                        whitePieces ^= endUpdater;
                    }
                    else{
                        blackPieces ^= endUpdater;
                    }
                    break;
            }

            switch (((PromotionMove) move).getPromotionPieceNum()){
                case QUEEN_MASK:
                    queens ^= endUpdater;
                    break;
                case KNIGHT_MASK:
                    knights ^= endUpdater;
                    break;
                case BISHOP_MASK:
                    bishops ^= endUpdater;
                    break;
                default:
                    rooks ^= endUpdater;
                    break;
            }
        }

        castleRights = move.getPreviousCastleRights();
        enPassantTile = move.getPreviousEnPassantTile();
        halfMoveCounter = move.getPreviousHalfMoveCount();

        if(whiteToMove){
            fullMoveCounter --;
        }

        whiteToMove ^= true;
        updateEssentialVariables();
    }
}