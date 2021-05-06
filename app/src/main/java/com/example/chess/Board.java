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
import com.example.chess.pieces.Piece;
import com.example.chess.pieces.Queen;
import com.example.chess.pieces.Rook;

import java.util.ArrayList;

public class

Board {

    private GameLogic logic = new GameLogic();

    public long whitePieces2;
    public long blackPieces2;
    public long pieces;
    public long kings;
    public long queens;
    public long rooks;
    public long knights;
    public long bishops;
    public long pawns;

    public final int whiteMask = 16;
    public final int blackMask = 8;
    public final int colorMask = 24;
    public final int kingMask = 1;
    public final int queenMask = 2;
    public final int rookMask = 3;
    public final int knightMask = 4;
    public final int bishopMask = 5;
    public final int pawnMask = 6;
    public final int pieceMask = 7;



    private boolean whiteToMove;
    private int enPassantTile;
    private final boolean[] castleRights;
    private int fullMoveCounter;
    private int halfMoveCounter;
    private final int[] attackedByMe = new int[64];
    private final int[] attackedByEnemy = new int[64];
    private final int[] attackedByEnemyPawn = new int[64];
    private final int[] attackedByEnemyBishop = new int[64];
    private final int[] attackedByEnemyKnight = new int[64];
    private final int[] attackedByEnemyRook = new int[64];
    private final int[] attackedByEnemyQueen = new int[64];
    private final int[] attackedByEnemyKing = new int[64];
    private int myKingTile;
    private int enemyKingTile;
    private boolean inCheck;
    private boolean inDoubleCheck;
    private final ArrayList<Integer> blockingTiles = new ArrayList<>();
    private int[] pinnedPieces = new int[64];
    private final ArrayList<Integer> currentTargetSquares = new ArrayList<>();
    private int currentStartSquare = -1;

    public Board(boolean whiteToMove, int enPassantTile, boolean[] castleRights, int halfMoveCounter, int fullMoveCounter,
                 long whitePieces2, long blackPieces2, long kings, long queens, long rooks, long knights, long bishops, long pawns){


        this.whitePieces2 = whitePieces2;
        this.blackPieces2 = blackPieces2;
        this.kings = kings;
        this.queens = queens;
        this.rooks = rooks;
        this.knights = knights;
        this.bishops = bishops;
        this.pawns = pawns;
        this.pieces = whitePieces2 | blackPieces2;




        this.whiteToMove = whiteToMove;
        this.enPassantTile = enPassantTile;
        this.castleRights = castleRights;
        this.fullMoveCounter = fullMoveCounter;
        this.halfMoveCounter = halfMoveCounter;

        updateEssentialVariables();
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
        return (rooks | queens) & (whiteToMove ? blackPieces2 : whitePieces2);
    }

    public long getEnemyDiagonalSliders(){
        return (bishops | queens) & (whiteToMove ? blackPieces2 : whitePieces2);
    }

    public int getPieceOnTile(int tile){

        int piece = 0;

        if(((whitePieces2 >> tile) & 1) == 1){
            piece |= whiteMask;
        }
        else if(((blackPieces2 >> tile) & 1) == 1){
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

    private void updateKingTiles(){

        long myKing = kings & ((whiteToMove) ? whitePieces2 : blackPieces2);
        long enemyKing = kings & ((whiteToMove) ? blackPieces2 : whitePieces2);

        boolean whiteUpdated = false;
        boolean blackUpdated = false;
        int i = 0;
        while(!(whiteUpdated && blackUpdated)){
            if(myKing == logic.masks[i]){
                myKingTile = i;
                whiteUpdated = true;
            }
            if(enemyKing == logic.masks[i]){
                enemyKingTile = i;
                blackUpdated = true;
            }
            i ++;
        }
    }

    public void resetVariables(){

        for(int i = 0; i < 64; i ++){
            attackedByEnemy[i] = 0;
        }

        this.blockingTiles.clear();
        this.inCheck = false;
        this.inDoubleCheck = false;
        this.currentTargetSquares.clear();
        this.currentStartSquare = -1;
    }

    public void updateEssentialVariables(){

        resetVariables();
        updateKingTiles();

        pinnedPieces= King.findPins(myKingTile, (whiteToMove ? whitePieces2 : blackPieces2), pieces, getEnemyHorizontalSliders(), getEnemyDiagonalSliders());

//        TODO update attacked tiles
//        for(Piece enemyPiece : enemyPieces){
//            for(int tileNum : enemyPiece.getTilesAttacked(enemyPiece.getPosition(), enemyPiece.isWhite(), this.pieceColorOnTile, this.myKingTile)){
//                this.attackedByEnemy[tileNum] ++;
//                boolean checkMove = false;
//                if(tileNum == this.myKingTile){
//                    checkMove = true;
//                    if(this.inCheck){
//                        this.inDoubleCheck = true;
//                    }
//                    else{
//                        this.inCheck = true;
//                    }
//                }
//                if(enemyPiece instanceof Pawn){
//                    if(checkMove){
//                        this.blockingTiles.add(enemyPiece.getPosition());
//                    }
//                }
//                else if(enemyPiece instanceof Rook){
//                    if(checkMove){
//                        this.blockingTiles.addAll(GameLogic.getTilesToStopCheck(this.myKingTile, enemyPiece.getPosition()));
//                    }
//                }
//                else if(enemyPiece instanceof Knight){
//                    if(checkMove){
//                        this.blockingTiles.add(enemyPiece.getPosition());
//                    }
//                }
//                else if(enemyPiece instanceof Bishop){
//                    if(checkMove){
//                        this.blockingTiles.addAll(GameLogic.getTilesToStopCheck(this.myKingTile, enemyPiece.getPosition()));
//                    }
//                }
//                else if(enemyPiece instanceof Queen){
//                    if(checkMove){
//                        this.blockingTiles.addAll(GameLogic.getTilesToStopCheck(this.myKingTile, enemyPiece.getPosition()));
//                    }
//                }
//            }
//        }
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

    public ArrayList<Integer> generateLegalMoves(Piece piece, boolean attackOnly){

        ArrayList<Integer> legalMoves = new ArrayList<>();

        if(this.inDoubleCheck){

            if(piece instanceof King){
                for(int possibleTile : ((King) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, new boolean[]{false, false})){
                    if(this.attackedByEnemy[possibleTile] == 0){
                        legalMoves.add(possibleTile);
                    }
                }
            }
        }
        else if(this.inCheck){

            if(piece instanceof King){
                for(int possibleTile : ((King) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, new boolean[]{false, false})){
                    if(this.attackedByEnemy[possibleTile] == 0){
                        legalMoves.add(possibleTile);
                    }
                }
            }
            else if(!this.pinnedPieces.contains(piece.getPosition())){

                ArrayList<Integer> possibleTiles = new ArrayList<>();

                if(piece instanceof Queen){
                    possibleTiles.addAll(((Queen) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile, -1));
                }
                else if(piece instanceof Rook){
                    possibleTiles.addAll(((Rook) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile, -1));
                }
                else if(piece instanceof Bishop){
                    possibleTiles.addAll(((Bishop) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile, -1));
                }
                else if(piece instanceof Knight){
                    possibleTiles.addAll(((Knight) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile));
                }
                else if(piece instanceof Pawn){

                    ArrayList<Integer> possiblePawnTiles = ((Pawn) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile, this.enPassantTile, -1);
                    possibleTiles.addAll(possiblePawnTiles);

                    for(int blockingTile : this.blockingTiles){
                        if(this.isPieceOnTile(blockingTile)){
                            if(this.getPieceOnTile(blockingTile) instanceof Pawn && Math.abs(this.enPassantTile - blockingTile) == 8) {
                                for (int possiblePawnTile : possiblePawnTiles) {
                                    if (possiblePawnTile == this.enPassantTile) {
                                        legalMoves.add(possiblePawnTile);
                                        break;
                                    }
                                }
                            }
                            break;
                        }
                    }
                }

                for(int possibleTile : possibleTiles){
                    if(this.blockingTiles.contains(possibleTile)){
                        legalMoves.add(possibleTile);
                    }
                }
            }
        }
        else if(this.pinnedPieces.contains(piece.getPosition())){

            int pinDirection = this.pinnedPiecesDirection.get(this.pinnedPieces.indexOf(piece.getPosition()));

            if(piece instanceof Pawn){

                legalMoves.addAll(((Pawn) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile, this.enPassantTile, pinDirection));
            }
            else if(piece instanceof Rook){

                legalMoves.addAll(((Rook) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile, pinDirection));
            }
            else if(piece instanceof Bishop){

                legalMoves.addAll(((Bishop) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile, pinDirection));
            }
            else if(piece instanceof Queen){

                legalMoves.addAll(((Queen) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile, pinDirection));
            }
        }
        else{
            if(piece instanceof King){

                boolean[] canCastle = new boolean[]{false, false};
                if(!attackOnly) {
                    if (this.whiteToMove) {

                        if (this.castleRights[0] && this.attackedByEnemy[61] == 0 && this.attackedByEnemy[62] == 0 &&
                                !this.isPieceOnTile(61) && !this.isPieceOnTile(62)) {
                            canCastle[0] = true;
                        }
                        if (this.castleRights[1] && this.attackedByEnemy[58] == 0 && this.attackedByEnemy[59] == 0 &&
                                !this.isPieceOnTile(58) && !this.isPieceOnTile(59) && !this.isPieceOnTile(57)) {
                            canCastle[1] = true;
                        }
                    } else {
                        if (this.castleRights[2] && this.attackedByEnemy[5] == 0 && this.attackedByEnemy[6] == 0 &&
                                !this.isPieceOnTile(5) && !this.isPieceOnTile(6)) {
                            canCastle[0] = true;
                        }
                        if (this.castleRights[3] && this.attackedByEnemy[2] == 0 && this.attackedByEnemy[3] == 0 &&
                                !this.isPieceOnTile(2) && !this.isPieceOnTile(3) && !this.isPieceOnTile(1)) {
                            canCastle[1] = true;
                        }
                    }
                }

                for(int possibleTile : ((King) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, canCastle)){
                    if(this.attackedByEnemy[possibleTile] == 0){
                        legalMoves.add(possibleTile);
                    }
                }
            }
            else {
                if(piece instanceof Pawn){
                    ArrayList<Integer> possiblePawnTiles = ((Pawn) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile, this.enPassantTile, -1);

                    for(int x = 0; x < possiblePawnTiles.size(); x ++){
                        if(possiblePawnTiles.get(x) == this.enPassantTile){

                            int startTile = piece.getPosition();
                            boolean isLegal = true;

                            if(startTile % 8 < this.enPassantTile % 8){

                                int checkingTile1 = startTile - 1;
                                int checkingTile2 = startTile + 2;
                                for(int i = startTile % 8 - 1; i >= 0; i --){
                                    if(this.isPieceOnTile(checkingTile1)){
                                        if(this.getEnemyHorizontalSliders().contains(checkingTile1)){
                                            for(int j = startTile % 8 + 2; j < 8; j ++){
                                                if(this.isPieceOnTile(checkingTile2)){
                                                    if(checkingTile2 == this.myKingTile){
                                                        isLegal = false;
                                                    }
                                                    break;
                                                }
                                                checkingTile2 ++;
                                            }
                                        }
                                        else if(checkingTile1 == this.myKingTile){
                                            for(int j = startTile % 8 + 2; j < 8; j ++){
                                                if(this.isPieceOnTile(checkingTile2)){
                                                    if(this.getEnemyHorizontalSliders().contains(checkingTile2)){
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
                                int checkingTile1 = startTile + 1;
                                int checkingTile2 = startTile - 2;
                                for(int i = startTile % 8 + 1; i < 8; i ++){
                                    if(this.isPieceOnTile(checkingTile1)){
                                        if(this.getEnemyHorizontalSliders().contains(checkingTile1)){
                                            for(int j = startTile % 8 - 2; j >= 0; j --){
                                                if(this.isPieceOnTile(checkingTile2)){
                                                    if(checkingTile2 == this.myKingTile){
                                                        isLegal = false;
                                                    }
                                                    break;
                                                }
                                                checkingTile2 --;
                                            }
                                        }
                                        else if(checkingTile1 == this.myKingTile){
                                            for(int j = startTile % 8 - 2; j >= 0; j --){
                                                if(this.isPieceOnTile(checkingTile2)){
                                                    if(this.getEnemyHorizontalSliders().contains(checkingTile2)){
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
                                possiblePawnTiles.remove(x);
                            }

                            break;
                        }
                    }

                    legalMoves.addAll(possiblePawnTiles);
                }
                else if(piece instanceof Rook){
                    legalMoves.addAll(((Rook) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile, -1));
                }
                else if(piece instanceof Bishop){
                    legalMoves.addAll(((Bishop) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile, -1));
                }
                else if(piece instanceof Knight){
                    legalMoves.addAll(((Knight) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile));
                }
                else if(piece instanceof Queen){
                    legalMoves.addAll(((Queen) piece).getTilesToMove(this.pieceColorOnTile, attackOnly, this.enemyKingTile, -1));
                }
            }
        }
        return legalMoves;
    }

    public ArrayList<Move> generateAllLegalMoves(boolean attackOnly){

        ArrayList<Move> allLegalMoves = new ArrayList<>();

        for(Piece piece : (this.whiteToMove ? this.whitePieces : this.blackPieces)){

            for(int endTile : this.generateLegalMoves(piece, attackOnly)){

                if(piece instanceof Pawn && (piece.isWhite() ? (endTile < 8) : (endTile > 55))){
                    for(int i = 1; i < 5; i ++){
                        allLegalMoves.add(this.createMove(piece.getPosition(), endTile, i));
                    }
                }
                else {
                    allLegalMoves.add(this.createMove(piece.getPosition(), endTile, 0));
                }
            }
        }
        return allLegalMoves;
    }

    public Move createMove(int startPosition, int endPosition, int promotionNumber){

        Move move;

        if(promotionNumber > 0){

            int pieceTakenNum = 0;
            if(((pieces >> endPosition) & 1) == 1){
                int pieceTaken = this.getPieceOnTile(endPosition);
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

            move = new PromotionMove(startPosition, endPosition, this.enPassantTile, this.castleRights, this.halfMoveCounter, promotionNumber, pieceTakenNum);
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

            move = new CastleMove(startPosition, endPosition, this.enPassantTile, this.castleRights, this.halfMoveCounter, castleLocation);
        }
        else if(((pieces >> endPosition) & 1) == 1){
            move = new AttackingMove(startPosition, endPosition, this.enPassantTile, this.castleRights, this.halfMoveCounter, this.getPieceOnTile(endPosition));
        }
        else{
            move = new NormalMove(startPosition, endPosition, this.enPassantTile, this.castleRights, this.halfMoveCounter);
        }

        return move;
    }

    public void makeMove(Move move){

        final int startPosition = move.getStartPosition();
        final int endPosition = move.getEndPosition();

        final Piece pieceToMove = this.getPieceOnTile(startPosition);

        if(pieceToMove instanceof King){
            if(this.whiteToMove){
                this.castleRights[0] = false;
                this.castleRights[1] = false;
            }
            else{
                this.castleRights[2] = false;
                this.castleRights[3] = false;
            }
        }

        if(startPosition == 63 || endPosition == 63){
            this.castleRights[0] = false;
        }
        else if(startPosition == 56 || endPosition == 56){
            this.castleRights[1] = false;
        }
        else if(startPosition == 7 || endPosition == 7){
            this.castleRights[2] = false;
        }
        else if(startPosition == 0 || endPosition == 0){
            this.castleRights[3] = false;
        }

        if(move instanceof NormalMove){

            if(pieceToMove instanceof Pawn && Math.abs(endPosition - startPosition) == 16){
                this.enPassantTile = (endPosition + startPosition) / 2;
            }
            else{
                this.enPassantTile = -1;
            }

            pieceToMove.setPosition(endPosition);
        }
        else if(move instanceof AttackingMove){

            pieceToMove.setPosition(endPosition);

            if(this.whiteToMove){
                for(Piece piece : this.blackPieces){
                    if(piece.getPosition() == endPosition){
                        this.blackPieces.remove(piece);
                        break;
                    }
                }
            }
            else{
                for(Piece piece : this.whitePieces){
                    if(piece.getPosition() == endPosition){
                        this.whitePieces.remove(piece);
                        break;
                    }
                }
            }
            this.enPassantTile = -1;
        }
        else if(move instanceof EnPassantMove){

            pieceToMove.setPosition(endPosition);

            if(this.whiteToMove){
                for(Piece piece : this.blackPieces){
                    if(GameLogic.getRow(piece.getPosition()) == GameLogic.getRow(startPosition) && piece.getPosition() % 8 == endPosition % 8){
                        this.blackPieces.remove(piece);
                        break;
                    }
                }
            }
            else{
                for(Piece piece : this.whitePieces){
                    if(GameLogic.getRow(piece.getPosition()) == GameLogic.getRow(startPosition) && piece.getPosition() % 8 == endPosition % 8){
                        this.whitePieces.remove(piece);
                        break;
                    }
                }
            }

            this.enPassantTile = -1;
        }
        else if(move instanceof CastleMove){

            int direction = ((CastleMove) move).getCastleLocation();

            pieceToMove.setPosition(endPosition);

            if(direction == 0){
                this.getPieceOnTile(63).setPosition(61);
            }
            else if(direction == 1){
                this.getPieceOnTile(56).setPosition(59);
            }
            else if(direction == 2){
                this.getPieceOnTile(7).setPosition(5);
            }
            else{
                this.getPieceOnTile(0).setPosition(3);
            }

            this.enPassantTile = -1;
        }
        else if(move instanceof PromotionMove){

            if(this.whiteToMove){
                for(Piece piece : this.whitePieces){
                    if(piece.getPosition() == startPosition){
                        this.whitePieces.remove(piece);
                        break;
                    }
                }
            }
            else{
                for(Piece piece : this.blackPieces){
                    if(piece.getPosition() == startPosition){
                        this.blackPieces.remove(piece);
                        break;
                    }
                }
            }

            int pieceTaken = ((PromotionMove) move).getPieceTaken();
            if(pieceTaken >= 0){
                if(this.whiteToMove){
                    for(Piece piece : this.blackPieces){
                        if(piece.getPosition() == endPosition){
                            this.blackPieces.remove(piece);
                            break;
                        }
                    }
                }
                else{
                    for(Piece piece : this.whitePieces){
                        if(piece.getPosition() == endPosition){
                            this.whitePieces.remove(piece);
                            break;
                        }
                    }
                }
            }

            int promotionPieceNum = ((PromotionMove) move).getPromotionPieceNum();
            switch (promotionPieceNum){
                case 0:
                    (this.whiteToMove ? this.whitePieces : this.blackPieces).add(new Queen(endPosition, this.whiteToMove));
                    break;
                case 1:
                    (this.whiteToMove ? this.whitePieces : this.blackPieces).add(new Rook(endPosition, this.whiteToMove));
                    break;
                case 2:
                    (this.whiteToMove ? this.whitePieces : this.blackPieces).add(new Knight(endPosition, this.whiteToMove));
                    break;
                case 3:
                    (this.whiteToMove ? this.whitePieces : this.blackPieces).add(new Bishop(endPosition, this.whiteToMove));
                    break;
            }
            this.enPassantTile = -1;
        }

        if(move instanceof AttackingMove || pieceToMove instanceof Pawn){
            this.halfMoveCounter = 0;
        }
        else{
            this.halfMoveCounter ++;
        }

        if(!this.whiteToMove){
            this.fullMoveCounter ++;
        }

        this.whiteToMove = !this.whiteToMove;
        updateEssentialVariables();
    }

    public void unMakeMove(Move move){

        final int startPosition = move.getStartPosition();
        final int endPosition = move.getEndPosition();

        final Piece pieceToUnMove = this.getPieceOnTile(endPosition);

        if(move instanceof NormalMove){

            pieceToUnMove.setPosition(startPosition);
        }
        else if(move instanceof AttackingMove){

            pieceToUnMove.setPosition(startPosition);

            if(!this.whiteToMove){

                switch (((AttackingMove) move).getTakenPieceName()) {
                    case "black_queen":
                        this.blackPieces.add(new Queen(endPosition, false));
                        break;
                    case "black_rook":
                        this.blackPieces.add(new Rook(endPosition, false));
                        break;
                    case "black_bishop":
                        this.blackPieces.add(new Bishop(endPosition, false));
                        break;
                    case "black_knight":
                        this.blackPieces.add(new Knight(endPosition, false));
                        break;
                    default:
                        this.blackPieces.add(new Pawn(endPosition, false));
                        break;
                }
            }
            else{
                switch (((AttackingMove) move).getTakenPieceName()) {
                    case "white_queen":
                        this.whitePieces.add(new Queen(endPosition, true));
                        break;
                    case "white_rook":
                        this.whitePieces.add(new Rook(endPosition, true));
                        break;
                    case "white_bishop":
                        this.whitePieces.add(new Bishop(endPosition, true));
                        break;
                    case "white_knight":
                        this.whitePieces.add(new Knight(endPosition, true));
                        break;
                    default:
                        this.whitePieces.add(new Pawn(endPosition, true));
                        break;
                }
            }
        }
        else if(move instanceof EnPassantMove){

            pieceToUnMove.setPosition(startPosition);

            for(int i = 24; i < 40; i ++){
                if(i % 8 == endPosition % 8 && GameLogic.getRow(i) == GameLogic.getRow(startPosition)){

                    (this.whiteToMove ? this.whitePieces : this.blackPieces).add(new Pawn(i, this.whiteToMove));
                }
            }
        }
        else if(move instanceof CastleMove){

            pieceToUnMove.setPosition(startPosition);

            int direction = ((CastleMove) move).getCastleLocation();

            if(direction == 0){
                this.getPieceOnTile(61).setPosition(63);
            }
            else if(direction == 1){
                this.getPieceOnTile(59).setPosition(56);
            }
            else if(direction == 2){
                this.getPieceOnTile(5).setPosition(7);
            }
            else{
                this.getPieceOnTile(3).setPosition(0);
            }
        }
        else if(move instanceof PromotionMove){

            (this.whiteToMove ? this.blackPieces : this.whitePieces).add(new Pawn(startPosition, !this.whiteToMove));

            if(this.whiteToMove){
                for(Piece piece : this.blackPieces){
                    if(piece.getPosition() == endPosition){
                        this.blackPieces.remove(piece);
                        break;
                    }
                }
            }
            else{
                for(Piece piece : this.whitePieces){
                    if(piece.getPosition() == endPosition){
                        this.whitePieces.remove(piece);
                        break;
                    }
                }
            }

            int pieceTaken = ((PromotionMove) move).getPieceTaken();
            if(pieceTaken >= 0){
                switch (pieceTaken){
                    case 0:
                        (this.whiteToMove ? this.whitePieces : this.blackPieces).add(new Queen(endPosition, this.whiteToMove));
                        break;
                    case 1:
                        (this.whiteToMove ? this.whitePieces : this.blackPieces).add(new Rook(endPosition, this.whiteToMove));
                        break;
                    case 2:
                        (this.whiteToMove ? this.whitePieces : this.blackPieces).add(new Knight(endPosition, this.whiteToMove));
                        break;
                    case 3:
                        (this.whiteToMove ? this.whitePieces : this.blackPieces).add(new Bishop(endPosition, this.whiteToMove));
                        break;
                }
            }
        }

        this.setCastleRights(move.getPreviousCastleRights());
        this.enPassantTile = move.getPreviousEnPassantTile();
        this.halfMoveCounter = move.getPreviousHalfMoveCount();

        if(this.whiteToMove){
            this.fullMoveCounter --;
        }

        this.whiteToMove = !this.whiteToMove;
        updateEssentialVariables();
    }
}