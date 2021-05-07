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

    public long whitePieces;
    public long blackPieces;
    public long pieces;
    public long kings;
    public long queens;
    public long rooks;
    public long knights;
    public long bishops;
    public long pawns;

    public static final int whiteMask = 16;
    public static final int blackMask = 8;
    public static final int colorMask = 24;
    public static final int kingMask = 1;
    public static final int queenMask = 2;
    public static final int rookMask = 3;
    public static final int knightMask = 4;
    public static final int bishopMask = 5;
    public static final int pawnMask = 6;
    public static final int pieceMask = 7;



    private boolean whiteToMove;
    private int enPassantTile;
    private final boolean[] castleRights;
    private int fullMoveCounter;
    private int halfMoveCounter;
    private final int[] attackedByMe = new int[64];
    private final int[] attackedByEnemyOld = new int[64];
    private final int[] attackedByEnemyPawn = new int[64];
    private final int[] attackedByEnemyBishop = new int[64];
    private final int[] attackedByEnemyKnight = new int[64];
    private final int[] attackedByEnemyRook = new int[64];
    private final int[] attackedByEnemyQueen = new int[64];
    private final int[] attackedByEnemyKing = new int[64];
    private long attackedByEnemy;
    private int myKingTile;
    private int enemyKingTile;
    private boolean inCheck;
    private boolean inDoubleCheck;
    private long blockingTiles = 0L;
    private int[] pinnedPieces = new int[64];
    private final ArrayList<Integer> currentTargetSquares = new ArrayList<>();
    private int currentStartSquare = -1;

    public Board(boolean whiteToMove, int enPassantTile, boolean[] castleRights, int halfMoveCounter, int fullMoveCounter,
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

    public long getAttackedByEnemy(){
        return attackedByEnemy;
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

    public int getFullMoveCounter(){
        return fullMoveCounter;
    }

    public int getHalfMoveCounter(){
        return halfMoveCounter;
    }

    public int getMyKingTile(){
        return myKingTile;
    }

    public void setCastleRights(boolean[] castleRights){

        System.arraycopy(castleRights, 0, this.castleRights, 0, 4);
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
            piece |= whiteMask;
        }
        else if(((blackPieces >> tile) & 1) == 1){
            piece |= blackMask;
        }
        else{
            return piece;
        }

        if(((pawns >> tile) & 1) == 1){
            piece |= pawnMask;
        }
        else if(((bishops >> tile) & 1) == 1){
            piece |= bishopMask;
        }
        else if(((knights >> tile) & 1) == 1){
            piece |= knightMask;
        }
        else if(((rooks >> tile) & 1) == 1){
            piece |= rookMask;
        }
        else if(((queens >> tile) & 1) == 1){
            piece |= queenMask;
        }
        else {
            piece |= kingMask;
        }

        return piece;
    }

    public long getTilesAttacked(int startPosition, int piece){
        switch (piece & pieceMask){
            case pawnMask:
                return Pawn.getTilesAttacked(startPosition, !whiteToMove);
            case rookMask:
                return Rook.getTilesAttacked(startPosition, pieces, myKingTile);
            case knightMask:
                return Knight.getTilesAttacked(startPosition);
            case bishopMask:
                return Bishop.getTilesAttacked(startPosition, pieces, myKingTile);
            case queenMask:
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
                    if((piece & pieceMask) == pawnMask || (piece & pieceMask) == knightMask){
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

            if((piece & pieceMask) == kingMask){
                long possibleTiles = King.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), (whiteToMove ? blackPieces : whitePieces), false, new boolean[]{false, false});
                legalMoves |= (possibleTiles & (~attackedByEnemy));
            }
        }
        else if(this.inCheck){

            if((piece & pieceMask) == kingMask){
                long possibleTiles = King.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), (whiteToMove ? blackPieces : whitePieces), false, new boolean[]{false, false});
                legalMoves |= (possibleTiles & (~attackedByEnemy));
            }
            else if(pinnedPieces[position] == 0){

                long possibleTiles = 0L;

                switch(piece & pieceMask){
                    case pawnMask:
                        if(enPassantTile >= 0 && ((Pawn.getTilesAttacked(position, whiteToMove) >> enPassantTile) & 1) == 1){
                            possibleTiles |= (1L << enPassantTile);
                        }
                        else {
                            possibleTiles |= Pawn.getTilesToMove(position, (whiteToMove ? blackPieces : whitePieces),
                                    pieces, false, enemyKingTile, enPassantTile, 0, whiteToMove);
                        }
                        break;
                    case bishopMask:
                        possibleTiles |= Bishop.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, false, enemyKingTile, 0);
                        break;
                    case knightMask:
                        possibleTiles |= Knight.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces),
                                (whiteToMove ? blackPieces : whitePieces), false, enemyKingTile);
                        break;
                    case rookMask:
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

            switch(piece & pieceMask){
                case pawnMask:
                    legalMoves |= Pawn.getTilesToMove(position, (whiteToMove ? blackPieces : whitePieces),
                                pieces, false, enemyKingTile, enPassantTile, pinnedPieces[position], whiteToMove);
                    break;
                case bishopMask:
                    legalMoves |= Bishop.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, attackOnly, enemyKingTile, pinnedPieces[position]);
                    break;
                case queenMask:
                    legalMoves |= Queen.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, attackOnly, enemyKingTile, pinnedPieces[position]);
                    break;
                case rookMask:
                    legalMoves |= Rook.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, attackOnly, enemyKingTile, pinnedPieces[position]);
                    break;
            }
        }
        else{
            if((piece & pieceMask) == kingMask){

                boolean[] canCastle = new boolean[]{false, false};
                if(!attackOnly) {
                    if (whiteToMove) {
                        if (castleRights[0] && ((attackedByEnemy >> 61 & 1) == 0) && ((attackedByEnemy >> 62 & 1) == 0) &&
                                ((pieces >> 61 & 1) == 0) && ((pieces >> 62 & 1) == 0)) {
                            canCastle[0] = true;
                        }
                        if (castleRights[1] && ((attackedByEnemy >> 58 & 1) == 0) && ((attackedByEnemy >> 59 & 1) == 0) &&
                                ((pieces >> 58 & 1) == 0) && ((pieces >> 59 & 1) == 0) && ((pieces >> 57 & 1) == 0)) {
                            canCastle[1] = true;
                        }
                    }
                    else {
                        if (castleRights[2] && ((attackedByEnemy >> 5 & 1) == 0) && ((attackedByEnemy >> 6 & 1) == 0) &&
                                ((pieces >> 5 & 1) == 0) && ((pieces >> 6 & 1) == 0)) {
                            canCastle[0] = true;
                        }
                        if (castleRights[3] && ((attackedByEnemy >> 2 & 1) == 0) && ((attackedByEnemy >> 3 & 1) == 0) &&
                                ((pieces >> 1 & 1) == 0) && ((pieces >> 2 & 1) == 0) && ((pieces >> 3 & 1) == 0)) {
                            canCastle[1] = true;
                        }
                    }
                }

                long possibleTiles = King.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), (whiteToMove ? blackPieces : whitePieces), attackOnly, canCastle);
                legalMoves |= (possibleTiles & (~attackedByEnemy));
            }
            else {
                if((piece & pieceMask) == pawnMask){
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
                else if((piece & pieceMask) == rookMask){
                    legalMoves |= Rook.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, attackOnly, enemyKingTile, 0);
                }
                else if((piece & pieceMask) == bishopMask){
                    legalMoves |= Bishop.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces), pieces, attackOnly, enemyKingTile, 0);
                }
                else if((piece & pieceMask) == knightMask){
                    legalMoves |= Knight.getTilesToMove(position, (whiteToMove ? whitePieces : blackPieces),
                            (whiteToMove ? blackPieces : whitePieces), attackOnly, enemyKingTile);
                }
                else if((piece & pieceMask) == queenMask){
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
                        if((piece & pieceMask) == pawnMask && (whiteToMove ? (j < 8) : (j > 55))){
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
                int pieceTaken = getPieceOnTile(endPosition);
                if((pieceTaken & pieceMask) == queenMask){
                    pieceTakenNum = queenMask;
                }
                else if((pieceTaken & pieceMask) == rookMask){
                    pieceTakenNum = rookMask;
                }
                else if((pieceTaken & pieceMask) == knightMask){
                    pieceTakenNum = knightMask;
                }
                else{
                    pieceTakenNum = bishopMask;
                }
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

        if((pieceToMove & pieceMask) == kingMask){
            if(whiteToMove){
                castleRights[0] = false;
                castleRights[1] = false;
            }
            else{
                castleRights[2] = false;
                castleRights[3] = false;
            }
        }

        if(startPosition == 63 || endPosition == 63){
            castleRights[0] = false;
        }
        if(startPosition == 56 || endPosition == 56){
            castleRights[1] = false;
        }
        if(startPosition == 7 || endPosition == 7){
            castleRights[2] = false;
        }
        if(startPosition == 0 || endPosition == 0){
            castleRights[3] = false;
        }

        if(move instanceof NormalMove){

            if(((pieceToMove & pieceMask) == pawnMask) && Math.abs(endPosition - startPosition) == 16){
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

            switch (pieceToMove & pieceMask){
                case pawnMask:
                    pawns ^= updater;
                    break;
                case bishopMask:
                    bishops ^= updater;
                    break;
                case knightMask:
                    knights ^= updater;
                    break;
                case rookMask:
                    rooks ^= updater;
                    break;
                case queenMask:
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

            switch (pieceToMove & pieceMask){
                case pawnMask:
                    pawns ^= updater;
                    break;
                case bishopMask:
                    bishops ^= updater;
                    break;
                case knightMask:
                    knights ^= updater;
                    break;
                case rookMask:
                    rooks ^= updater;
                    break;
                case queenMask:
                    queens ^= updater;
                    break;
                default:
                    kings ^= updater;
                    break;
            }

            switch (((AttackingMove) move).getTakenPiece() & pieceMask){
                case pawnMask:
                    pawns ^= endUpdater;
                    break;
                case bishopMask:
                    bishops ^= endUpdater;
                    break;
                case knightMask:
                    knights ^= endUpdater;
                    break;
                case rookMask:
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

            long pawnUpdater = 1L << startPosition;
            long promotionPieceUpdater = 1L << endPosition;
            long takenPieceUpdater = 1L << endPosition;
            pawns ^= pawnUpdater;

            final int pieceTaken = ((PromotionMove) move).getPieceTaken() & pieceMask;
            if(pieceTaken == 0){
                takenPieceUpdater = 0;
            }
            else{
                switch (pieceTaken){
                    case rookMask:
                        rooks ^= takenPieceUpdater;
                        break;
                    case knightMask:
                        knights ^= takenPieceUpdater;
                        break;
                    case bishopMask:
                        bishops ^= takenPieceUpdater;
                        break;
                    default:
                        queens ^= takenPieceUpdater;
                        break;
                }
            }

            if(whiteToMove){
                whitePieces ^= (pawnUpdater | promotionPieceUpdater);
                blackPieces ^= takenPieceUpdater;
            }
            else{
                blackPieces ^= (pawnUpdater | promotionPieceUpdater);
                whitePieces ^= takenPieceUpdater;
            }

            switch (((PromotionMove) move).getPromotionPieceNum() & pieceMask){
                case queenMask:
                    queens ^= promotionPieceUpdater;
                    break;
                case knightMask:
                    knights ^= promotionPieceUpdater;
                    break;
                case rookMask:
                    rooks ^= promotionPieceUpdater;
                    break;
                default:
                    bishops ^= promotionPieceUpdater;
                    break;
            }

            enPassantTile = 64;
        }

        if(move instanceof AttackingMove || ((pieceToMove & pieceMask) == pawnMask)){
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

            switch (pieceToUnMove & pieceMask){
                case pawnMask:
                    pawns ^= updater;
                    break;
                case bishopMask:
                    bishops ^= updater;
                    break;
                case knightMask:
                    knights ^= updater;
                    break;
                case rookMask:
                    rooks ^= updater;
                    break;
                case queenMask:
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

            switch (pieceToUnMove & pieceMask){
                case pawnMask:
                    pawns ^= updater;
                    break;
                case bishopMask:
                    bishops ^= updater;
                    break;
                case knightMask:
                    knights ^= updater;
                    break;
                case rookMask:
                    rooks ^= updater;
                    break;
                case queenMask:
                    queens ^= updater;
                    break;
                default:
                    kings ^= updater;
                    break;
            }

            switch (((AttackingMove) move).getTakenPiece() & pieceMask){
                case pawnMask:
                    pawns ^= endUpdater;
                    break;
                case bishopMask:
                    bishops ^= endUpdater;
                    break;
                case knightMask:
                    knights ^= endUpdater;
                    break;
                case rookMask:
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

            long pawnUpdater = 1L << startPosition;
            long promotionPieceUpdater = 1L << endPosition;
            long takenPieceUpdater = 1L << endPosition;
            pawns ^= pawnUpdater;

            final int pieceTaken = ((PromotionMove) move).getPieceTaken() & pieceMask;
            if(pieceTaken == 0){
                takenPieceUpdater = 0;
            }
            else{
                switch (pieceTaken){
                    case rookMask:
                        rooks ^= takenPieceUpdater;
                        break;
                    case knightMask:
                        knights ^= takenPieceUpdater;
                        break;
                    case bishopMask:
                        bishops ^= takenPieceUpdater;
                        break;
                    default:
                        queens ^= takenPieceUpdater;
                        break;
                }
            }

            if(whiteToMove){
                blackPieces ^= (pawnUpdater | promotionPieceUpdater);
                whitePieces ^= takenPieceUpdater;
            }
            else{
                whitePieces ^= (pawnUpdater | promotionPieceUpdater);
                blackPieces ^= takenPieceUpdater;
            }

            switch (((PromotionMove) move).getPromotionPieceNum() & pieceMask){
                case queenMask:
                    queens ^= promotionPieceUpdater;
                    break;
                case knightMask:
                    knights ^= promotionPieceUpdater;
                    break;
                case rookMask:
                    rooks ^= promotionPieceUpdater;
                    break;
                default:
                    bishops ^= promotionPieceUpdater;
                    break;
            }
        }

        setCastleRights(move.getPreviousCastleRights());
        enPassantTile = move.getPreviousEnPassantTile();
        halfMoveCounter = move.getPreviousHalfMoveCount();

        if(whiteToMove){
            fullMoveCounter --;
        }

        whiteToMove ^= true;
        updateEssentialVariables();
    }
}