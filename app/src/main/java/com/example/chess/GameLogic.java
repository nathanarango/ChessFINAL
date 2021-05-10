package com.example.chess;

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

        long whitePieces = 0L;
        long blackPieces = 0L;
        long kings = 0L;
        long queens = 0L;
        long rooks = 0L;
        long knights = 0L;
        long bishops = 0L;
        long pawns = 0L;

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

                        whitePieces |= this.masks[tileCounter];

                        if(Character.toLowerCase(symbol) == 'r'){
                            rooks |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'n'){
                            knights |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'b'){
                            bishops |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'k'){
                            kings |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'q'){
                            queens |= this.masks[tileCounter];
                        }
                        else{
                            pawns |= this.masks[tileCounter];
                        }
                    }
                    else{
                        blackPieces |= this.masks[tileCounter];

                        if(Character.toLowerCase(symbol) == 'r'){
                            rooks |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'n'){
                            knights |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'b'){
                            bishops |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'k'){
                            kings |= this.masks[tileCounter];
                        }
                        else if(Character.toLowerCase(symbol) == 'q'){
                            queens |= this.masks[tileCounter];
                        }
                        else{
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

        int castleRights = 0;

        if(fen.charAt(indexNum) == '-'){
            indexNum ++;
        }
        else{
            while(fen.charAt(indexNum) != ' '){

                if(Character.toLowerCase(fen.charAt(indexNum)) == 'k'){
                    if(Character.isUpperCase(fen.charAt(indexNum))){
                        castleRights ^= 0b1000;
                    }
                    else{
                        castleRights ^= 0b0010;
                    }
                }
                else{
                    if(Character.isUpperCase(fen.charAt(indexNum))){
                        castleRights ^= 0b0100;
                    }
                    else{
                        castleRights ^= 0b0001;
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

        return new Board(whiteToMove,enPassantTile, castleRights, halfMoveCounter, fullMoveCounter, whitePieces,
                blackPieces, kings, queens, rooks, knights, bishops, pawns);
    }

    public static long getTilesToStopCheck(int kingPosition, int enemyPosition){

        long tiles = 0L;

        int difference = enemyPosition - kingPosition;

        if(kingPosition % 8 == enemyPosition % 8){
            int vector = 8 * difference / Math.abs(difference);
            for(int i = kingPosition; i != enemyPosition; i += vector){
                tiles |= (1L << (i + vector));
            }
        }
        else if(getRow(kingPosition) == getRow(enemyPosition)){
            int vector = difference / Math.abs(difference);
            for(int i = kingPosition; i != enemyPosition; i += vector){
                tiles |= (1L << (i + vector));
            }
        }
        else if(difference % 7 == 0 && Math.abs(difference) != 63){
            int vector = 7 * difference / Math.abs(difference);
            for(int i = kingPosition; i != enemyPosition; i += vector){
                tiles |= (1L << (i + vector));
            }
        }
        else if(difference % 9 == 0){
            int vector = 9 * difference / Math.abs(difference);
            for(int i = kingPosition; i != enemyPosition; i += vector){
                tiles |= (1L << (i + vector));
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
            return 1;
        }
        else if(Math.abs(vector) == 7){
            return 2;
        }
        else if(Math.abs(vector) == 8){
            return 3;
        }
        else{
            return 4;
        }
    }
}
