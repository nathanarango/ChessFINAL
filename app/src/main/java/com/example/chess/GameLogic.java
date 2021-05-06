package com.example.chess;

import com.example.chess.pieces.Bishop;
import com.example.chess.pieces.King;
import com.example.chess.pieces.Knight;
import com.example.chess.pieces.Pawn;
import com.example.chess.pieces.Piece;
import com.example.chess.pieces.Queen;
import com.example.chess.pieces.Rook;

import java.util.ArrayList;

public class GameLogic {

    public final long[] masks = fillMasks();

    public long[] fillMasks() {
        long[] temp = new long[64];
        for (int i = 0; i < 64; i++) {
            temp[i] = 1L << i;
        }
        return temp;
    }

    public static String getPieceName(int piece){

        switch(piece){
            case 17:
                return "white_king";
            case 18:
                return "white_queen";
            case 19:
                return "white_rook";
            case 20:
                return "white_knight";
            case 21:
                return "white_bishop";
            case 22:
                return "white_pawn";
            case 9:
                return "black_king";
            case 10:
                return "black_queen";
            case 11:
                return "black_rook";
            case 12:
                return "black_knight";
            case 13:
                return "black_bishop";
            case 14:
                return "black_pawn";
            default:
                return "empty";
        }
    }

    public Board createStartBoard(){

        return createBoardFromFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    public Board createBoardFromFEN(String fen){

        long whitePieces2 = 0L;
        long blackPieces2 = 0L;
        long kings = 0L;
        long queens = 0L;
        long rooks = 0L;
        long knights = 0L;
        long bishops = 0L;
        long pawns = 0L;

        ArrayList<Piece> whitePieces = new ArrayList<>();
        ArrayList<Piece> blackPieces = new ArrayList<>();

        int tileCounter = 0;
        int slashCounter = 0;
        int indexNum = 0;
        for(int i = 0; i < fen.length(); i ++){

            char symbol = fen.charAt(i);

            if(slashCounter < 8){

                if(symbol == '/' || symbol == ' '){
                    slashCounter ++;
                    continue;
                }

                if(Character.isDigit(symbol)){
                    tileCounter += Character.getNumericValue(symbol);
                }
                else{

                    if(!Character.isLowerCase(symbol)){

                        whitePieces2 |= this.masks[tileCounter];

                        if(Character.toLowerCase(symbol) == 'r'){
                            whitePieces.add(new Rook(tileCounter, true));
                            rooks |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'n'){
                            whitePieces.add(new Knight(tileCounter, true));
                            knights |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'b'){
                            whitePieces.add(new Bishop(tileCounter, true));
                            bishops |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'k'){
                            whitePieces.add(new King(tileCounter, true));
                            kings |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'q'){
                            whitePieces.add(new Queen(tileCounter, true));
                            queens |= this.masks[tileCounter];
                        }
                        else{
                            whitePieces.add(new Pawn(tileCounter, true));
                            pawns |= this.masks[tileCounter];
                        }
                    }
                    else{
                        blackPieces2 |= this.masks[tileCounter];

                        if(Character.toLowerCase(symbol) == 'r'){
                            blackPieces.add(new Rook(tileCounter, false));
                            rooks |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'n'){
                            blackPieces.add(new Knight(tileCounter, false));
                            knights |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'b'){
                            blackPieces.add(new Bishop(tileCounter, false));
                            bishops |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'k'){
                            blackPieces.add(new King(tileCounter, false));
                            kings |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'q'){
                            blackPieces.add(new Queen(tileCounter, false));
                            queens |= this.masks[tileCounter];
                        }
                        else{
                            blackPieces.add(new Pawn(tileCounter, false));
                            pawns |= this.masks[tileCounter];
                        }
                    }

                    tileCounter++;
                }
            }
            else{
                indexNum = i;
                break;
            }
        }

        boolean whiteToMove = fen.charAt(indexNum) == 'w';
        indexNum += 2;

        boolean[] castleRights = new boolean[]{false, false, false, false};

        if(fen.charAt(indexNum) == '-'){
            indexNum ++;
        }
        else{
            while(fen.charAt(indexNum) != ' '){

                if(Character.toLowerCase(fen.charAt(indexNum)) == 'k'){
                    if(Character.isUpperCase(fen.charAt(indexNum))){
                        castleRights[0] = true;
                    }
                    else{
                        castleRights[2] = true;
                    }
                }
                else{
                    if(Character.isUpperCase(fen.charAt(indexNum))){
                        castleRights[1] = true;
                    }
                    else{
                        castleRights[3] = true;
                    }
                }

                indexNum ++;
            }
        }

        indexNum ++;

        int enPassantTile = -1;
        if(fen.charAt(indexNum) == '-'){
            indexNum += 2;
        }
        else{
            String tile = Character.toString(fen.charAt(indexNum)) + fen.charAt(indexNum + 1);

            for(int i = 0; i < 64; i ++){
                if(MainActivity.TILE_NAMES[i].equals(tile)){
                    enPassantTile = i;
                }
            }

            indexNum += 3;
        }

        int halfMoveCounter = 0;
        int fullMoveCounter = 1;

        if(fen.length() > indexNum){

            String halfMoves = "";
            while(fen.charAt(indexNum) != ' '){

                halfMoves = halfMoves.concat(Character.toString(fen.charAt(indexNum)));

                indexNum ++;
            }
            halfMoveCounter = Integer.parseInt(halfMoves);

            indexNum ++;

            String fullMoves = "";
            while(indexNum < fen.length()){

                fullMoves = fullMoves.concat(Character.toString(fen.charAt(indexNum)));

                indexNum ++;
            }
            fullMoveCounter = Integer.parseInt(fullMoves);
        }

        return new Board(whitePieces, blackPieces, whiteToMove,enPassantTile, castleRights, halfMoveCounter, fullMoveCounter, whitePieces2,
                blackPieces2, kings, queens, rooks, knights, bishops, pawns);
    }

    public static ArrayList<Integer> getTilesToStopCheck(int kingPosition, int enemyPosition){

        ArrayList<Integer> tiles = new ArrayList<>();

        int difference = enemyPosition - kingPosition;

        if(kingPosition % 8 == enemyPosition % 8){
            int vector = 8 * difference / Math.abs(difference);
            for(int i = kingPosition; i != enemyPosition; i += vector){
                tiles.add(i + vector);
            }
        }
        else if(getRow(kingPosition) == getRow(enemyPosition)){
            int vector = difference / Math.abs(difference);
            for(int i = kingPosition; i != enemyPosition; i += vector){
                tiles.add(i + vector);
            }
        }
        else if(difference % 7 == 0 && Math.abs(difference) != 63){
            int vector = 7 * difference / Math.abs(difference);
            for(int i = kingPosition; i != enemyPosition; i += vector){
                tiles.add(i + vector);
            }
        }
        else if(difference % 9 == 0){
            int vector = 9 * difference / Math.abs(difference);
            for(int i = kingPosition; i != enemyPosition; i += vector){
                tiles.add(i + vector);
            }
        }
        return tiles;
    }

    public static int getRow(int tileNum){

        if(tileNum < 8){
            return 0;
        }
        else if(tileNum < 16){
            return 1;
        }
        else if(tileNum < 24){
            return 2;
        }
        else if(tileNum < 32){
            return 3;
        }
        else if(tileNum < 40){
            return 4;
        }
        else if(tileNum < 48){
            return 5;
        }
        else if(tileNum < 56){
            return 6;
        }
        else {
            return 7;
        }
    }

    public static int getPinDirection(int vector){
        if(Math.abs(vector) == 1){
            return 0;
        }
        else if(Math.abs(vector) == 7){
            return 1;
        }
        else if(Math.abs(vector) == 8){
            return 2;
        }
        else{
            return 3;
        }
    }
}
