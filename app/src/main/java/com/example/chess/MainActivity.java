package com.example.chess;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.chess.moves.AttackingMove;
import com.example.chess.moves.CastleMove;
import com.example.chess.moves.EnPassantMove;
import com.example.chess.moves.Move;
import com.example.chess.moves.PromotionMove;
import com.example.chess.pieces.Pawn;
import com.example.chess.pieces.Piece;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    GameLogic logic = new GameLogic();
    Board currentBoard;

    int checkmateCounter = 0;
    int checkCounter = 0;
    int enPassantCounter = 0;
    int attackMoveCounter = 0;
    int castleMoveCounter = 0;
    int promotionMoveCounter = 0;

    int[] checkmateCounterArray = new int[4];
    int[] checkCounterArray = new int[4];
    int[] enPassantCounterArray = new int[4];
    int[] attackMoveCounterArray = new int[4];
    int[] castleMoveCounterArray = new int[4];
    int[] promotionMoveCounterArray = new int[4];

    public static final int[] TILE_IDS = fillTileIds();
    public static final Map<String, Integer> PIECE_IDS = fillPieceIds();
    public static final boolean[] IS_DARK_SQUARE = fillDarkSquare();
    public static final String[] TILE_NAMES = fillTileNames();

    private static boolean[] fillDarkSquare() {

        return new boolean[]{false, true, false, true, false, true, false, true,
                true, false, true, false, true, false, true, false,
                false, true, false, true, false, true, false, true,
                true, false, true, false, true, false, true, false,
                false, true, false, true, false, true, false, true,
                true, false, true, false, true, false, true, false,
                false, true, false, true, false, true, false, true,
                true, false, true, false, true, false, true, false,};
    }

    private static int[] fillTileIds() {

        return new int[]{R.id.a8, R.id.b8, R.id.c8, R.id.d8, R.id.e8, R.id.f8, R.id.g8, R.id.h8,
                R.id.a7, R.id.b7, R.id.c7, R.id.d7, R.id.e7, R.id.f7, R.id.g7, R.id.h7,
                R.id.a6, R.id.b6, R.id.c6, R.id.d6, R.id.e6, R.id.f6, R.id.g6, R.id.h6,
                R.id.a5, R.id.b5, R.id.c5, R.id.d5, R.id.e5, R.id.f5, R.id.g5, R.id.h5,
                R.id.a4, R.id.b4, R.id.c4, R.id.d4, R.id.e4, R.id.f4, R.id.g4, R.id.h4,
                R.id.a3, R.id.b3, R.id.c3, R.id.d3, R.id.e3, R.id.f3, R.id.g3, R.id.h3,
                R.id.a2, R.id.b2, R.id.c2, R.id.d2, R.id.e2, R.id.f2, R.id.g2, R.id.h2,
                R.id.a1, R.id.b1, R.id.c1, R.id.d1, R.id.e1, R.id.f1, R.id.g1, R.id.h1};
    }

    private static String[] fillTileNames() {

        return new String[]{"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1",};
    }

    private static Map<String, Integer> fillPieceIds() {

        final Map<String, Integer> ids = new HashMap<>();

        ids.put("empty", R.drawable.empty);
        ids.put("white_king", R.drawable.white_king);
        ids.put("white_queen", R.drawable.white_queen);
        ids.put("white_rook", R.drawable.white_rook);
        ids.put("white_knight", R.drawable.white_knight);
        ids.put("white_bishop", R.drawable.white_bishop);
        ids.put("white_pawn", R.drawable.white_pawn);
        ids.put("black_king", R.drawable.black_king);
        ids.put("black_queen", R.drawable.black_queen);
        ids.put("black_rook", R.drawable.black_rook);
        ids.put("black_knight", R.drawable.black_knight);
        ids.put("black_bishop", R.drawable.black_bishop);
        ids.put("black_pawn", R.drawable.black_pawn);

        return ids;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.currentBoard = logic.createBoardFromFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
        updateDisplay2();
        testBinary();
    }

    public void onTileClick(View view) {

        unHighlightAllTiles();

        int tileNum = Integer.parseInt(view.getTag().toString());

        //runSpeedTest(5, 5);
        //runCountTest(5, true);
        //runCountTest(2, false);
        //runBreakdownTest(2);
        //System.out.println(positionCountTestBulk(5));

        handleTilePress(tileNum);
    }

    public void updateDisplay2(){

        for(int i = 0; i < 64; i ++){

            ((ImageButton) findViewById(TILE_IDS[i])).setImageResource(PIECE_IDS.get(GameLogic.getPieceName(currentBoard.getPieceOnTile2(i))));
        }
    }

    public void testBinary() {

        long[] masks = new long[64];
        for (int i = 0; i < 64; i++) {
            masks[i] = 1L << i;
        }
    }

    public void handleTilePress(int tileNum){

        if(this.currentBoard.getCurrentTargetSquares().contains(tileNum)){

            boolean isPromotion = (this.currentBoard.getPieceOnTile(this.currentBoard.getCurrentStartSquare()) instanceof Pawn && (this.currentBoard.isWhiteToMove() ? (tileNum < 8) : (tileNum > 55)));

            Move move = this.currentBoard.createMove(this.currentBoard.getCurrentStartSquare(), tileNum, isPromotion ? 0 : -1);
            this.currentBoard.makeMove(move);
            Search searcher = new Search(this.currentBoard);
            long startTime = System.currentTimeMillis();
            this.currentBoard.makeMove(searcher.findBestMove(2));
            long endTime = System.currentTimeMillis();
            displayText("Evaluations: " + searcher.counter + "   Time: " + String.valueOf(endTime - startTime));
            updateDisplay();
        }
        else {
            this.currentBoard.clearCurrentTargetSquares();
            if ((this.currentBoard.getPieceColorOnTile()[tileNum] == 0 && this.currentBoard.isWhiteToMove()) || (this.currentBoard.getPieceColorOnTile()[tileNum] == 1 && !this.currentBoard.isWhiteToMove())) {
                this.currentBoard.setCurrentStartSquare(tileNum);
                this.currentBoard.setCurrentTargetSquares(this.currentBoard.generateLegalMoves(this.currentBoard.getPieceOnTile(tileNum), false));
                if(this.currentBoard.getCurrentTargetSquares().size() == 0){
                    highlightTileRed(tileNum);
                }
                else{
                    highlightTileGreen(tileNum);
                    for(int tile : this.currentBoard.getCurrentTargetSquares()){
                        highlightTile(tile);
                    }
                }
            }
            else {
                this.currentBoard.setCurrentTargetSquares(new ArrayList<>());
            }
        }
    }

    public void runBreakdownTest(int depth){

        ArrayList<Move> allLegalMoves = this.currentBoard.generateAllLegalMoves(false);
        int counter = 0;

        for(Move move : allLegalMoves){

            this.currentBoard.makeMove(move);

            int count = positionCountTestBulk(depth - 1);
            System.out.println(TILE_NAMES[move.getStartPosition()] + TILE_NAMES[move.getEndPosition()] + ": " + count);
            counter += count;

            this.currentBoard.unMakeMove(move);
        }

        System.out.println("\n Nodes searched: " + counter);
    }

    public void runSpeedTest(int depth, int trials){

        final int[][] speed = new int[depth][trials];
        final int[] fastestSpeed = new int[depth];
        final int[] averageSpeed = new int[depth];
        final int[] slowestSpeed = new int[depth];

        for(int x = 0; x < trials; x ++) {

            for (int i = 0; i < depth; i++) {

                long startTime = System.currentTimeMillis();

                int count = positionCountTestBulk(i + 1);

                long endTime = System.currentTimeMillis();

                speed[i][x] = (int) (endTime - startTime);
            }
        }

        System.out.println("Trial Count: " + trials);

        for(int i = 0; i < depth; i ++){

            int[] trialsArray = speed[i];

            int slowest = trialsArray[0];
            int fastest = trialsArray[0];
            int total = 0;

            for(int j : trialsArray){

                if(j > slowest){
                    slowest = j;
                }
                else if(j < fastest){
                    fastest = j;
                }
                total += j;
            }

            fastestSpeed[i] = fastest;
            slowestSpeed[i] = slowest;
            averageSpeed[i] = total / trials;
        }

        for(int i = 0; i < depth; i ++){

            System.out.println("Depth: " + (i + 1)  + "   Average Speed: " + averageSpeed[i] + " ms   Fastest Speed: " + fastestSpeed[i] + " ms   Slowest Speed: " + slowestSpeed[i] + " ms");
        }
    }

    public void runCountTest(int depth, boolean bulkCounting){

        for (int i = 1; i < depth + 1; i++) {

            long startTime = System.currentTimeMillis();

            int count = bulkCounting ? positionCountTestBulk(i) : positionCountTest(i);

            long endTime = System.currentTimeMillis();

            System.out.println("Depth: " + i + "  Result: " + count + " positions  Time: " + (endTime - startTime) + " milliseconds");

            if(!bulkCounting) {

                int tempCheckMateCounter = this.checkmateCounter;
                int tempCheckCounter = this.checkCounter;
                int tempEnPassantCounter = this.enPassantCounter;
                int tempAttackMoveCounter = this.attackMoveCounter;
                int tempCastleMoveCounter = this.castleMoveCounter;
                int tempPromotionMoveCounter = this.promotionMoveCounter;

                for (int y = i; y > 0; y--) {

                    tempCheckMateCounter -= checkmateCounterArray[y - 1];
                    tempCheckCounter -= checkCounterArray[y - 1];
                    tempEnPassantCounter -= enPassantCounterArray[y - 1];
                    tempAttackMoveCounter -= attackMoveCounterArray[y - 1];
                    tempCastleMoveCounter -= castleMoveCounterArray[y - 1];
                    tempPromotionMoveCounter -= promotionMoveCounterArray[y - 1];
                }

                System.out.println("Checkmates: " + tempCheckMateCounter + "   Checks (minus discovered): " + tempCheckCounter + "   enPassant moves: " + tempEnPassantCounter + "   Attacks: " + tempAttackMoveCounter + "   Castles: " + tempCastleMoveCounter + "   Promotions: " + tempPromotionMoveCounter);

                checkmateCounterArray[i - 1] = tempCheckMateCounter;
                checkCounterArray[i - 1] = tempCheckCounter;
                enPassantCounterArray[i - 1] = tempEnPassantCounter;
                attackMoveCounterArray[i - 1] = tempAttackMoveCounter;
                castleMoveCounterArray[i - 1] = tempCastleMoveCounter;
                promotionMoveCounterArray[i - 1] = tempPromotionMoveCounter;

                this.checkmateCounter = 0;
                this.checkCounter = 0;
                this.enPassantCounter = 0;
                this.attackMoveCounter = 0;
                this.castleMoveCounter = 0;
                this.promotionMoveCounter = 0;
            }
        }
    }

    public int positionCountTestBulk(int depth){

        ArrayList<Move> allLegalMoves = this.currentBoard.generateAllLegalMoves(false);
        int numPositions = 0;

        if(depth == 1){
            return allLegalMoves.size();
        }

        for(Move move : allLegalMoves){
            this.currentBoard.makeMove(move);
            numPositions += positionCountTestBulk(depth - 1);
            this.currentBoard.unMakeMove(move);
        }
        return numPositions;
    }

    public int positionCountTest(int depth){

        if(depth == 0){
            return 1;
        }

        ArrayList<Move> allLegalMoves = this.currentBoard.generateAllLegalMoves(false);

        int numPositions = 0;

        for(Move move : allLegalMoves){

            this.currentBoard.makeMove(move);

            if(currentBoard.getPieceOnTile(move.getEndPosition()).getTilesAttacked(move.getEndPosition(), !currentBoard.isWhiteToMove(), currentBoard.getPieceColorOnTile(), currentBoard.getMyKingTile()).contains(currentBoard.getMyKingTile())){
                this.checkCounter ++;
            }

            if(move instanceof AttackingMove){
                this.attackMoveCounter ++;
            }
            else if(move instanceof CastleMove){
                this.castleMoveCounter ++;
            }
            else if(move instanceof EnPassantMove){
                this.enPassantCounter ++;
                this.attackMoveCounter ++;
            }
            else if(move instanceof PromotionMove){
                this.promotionMoveCounter ++;
                if(((PromotionMove) move).getPieceTaken() >= 0){
                    this.attackMoveCounter ++;
                }
            }

            int testCounter = 0;
            for(Piece piece : currentBoard.isWhiteToMove() ? currentBoard.getWhitePieces() : currentBoard.getBlackPieces()){
                testCounter += currentBoard.generateLegalMoves(piece, false).size();
            }
            if(testCounter == 0){
                this.checkmateCounter++;
            }

            numPositions += positionCountTest(depth - 1);

            this.currentBoard.unMakeMove(move);
        }

        return numPositions;
    }

    public void highlightTile(int tileNum){

        ImageButton tile = findViewById(TILE_IDS[tileNum]);
        if(IS_DARK_SQUARE[tileNum]){
            tile.setBackgroundColor(getResources().getColor(R.color.dark_square_highlight));
        }
        else {
            tile.setBackgroundColor(getResources().getColor(R.color.light_square_highlight));
        }
    }

    public void highlightTileGreen(int tileNum){

        ImageButton tile = findViewById(TILE_IDS[tileNum]);
        tile.setBackgroundColor(getResources().getColor(R.color.green_highlight));
    }

    public void highlightTileRed(int tileNum){

        ImageButton tile = findViewById(TILE_IDS[tileNum]);
        tile.setBackgroundColor(getResources().getColor(R.color.red_highlight));
    }

    public void unHighlightTile(int tileNum){

        ImageButton tile = findViewById(TILE_IDS[tileNum]);
        if(IS_DARK_SQUARE[tileNum]){
            tile.setBackgroundColor(getResources().getColor(R.color.dark_square));
        }
        else {
            tile.setBackgroundColor(getResources().getColor(R.color.light_square));
        }
    }

    public void unHighlightAllTiles(){

        for(int i = 0; i < 64; i ++){
            unHighlightTile(i);
        }
    }

    public void updateDisplay(){

        clearDisplay();

        for(Piece piece : this.currentBoard.getWhitePieces()){

            ImageButton tile = findViewById(TILE_IDS[piece.getPosition()]);
            tile.setImageResource(PIECE_IDS.get(piece.getPieceName()));
        }
        for(Piece piece : this.currentBoard.getBlackPieces()){

            ImageButton tile = findViewById(TILE_IDS[piece.getPosition()]);
            tile.setImageResource(PIECE_IDS.get(piece.getPieceName()));
        }
    }

    public void clearDisplay(){
        for(int i = 0; i < 64; i ++){
            ImageButton tile = findViewById(TILE_IDS[i]);
            tile.setImageResource(PIECE_IDS.get("empty"));
        }
    }

    public void displayText(String text){
        TextView textV = findViewById(R.id.textView);
        textV.setText(text);
    }
}