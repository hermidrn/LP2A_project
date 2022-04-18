package fr.utbm.lp2a.ludo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Game {

    // TEST: Start directly the game without selecting the first player
    private final boolean LAUNCH_MAIN_GAME = false;

    // Players array
    private Player[] players;

    // Each time someone plays, this value is incremented by 1
    private int gameTurn;

    // Shift of the play order, allows to set the player who will play first and allow players to play several turns in a row
    private int playOrderShift;

    // Last dice result
    private int lastDiceResult;

    // Map of 4 players and corresponding dices rolls to determine the first player at the beginning
    private Map<Player,Integer> firstRolls;

    // Window that displays the game with a graphical user interface 
    private GameWindow ui;

    // AI controller for computer players
    private PlayerAIController computerController;

    // Game constructor : initialize a new game
    Game(int computerNumber) {
        // Create 4 players (including the number of computers that was passed to the constructor) 
        // with different colors and store them in an array
        players = new Player[4];
        for(PlayerColor color : PlayerColor.values()) {
            if (computerNumber>color.getNum()) {
                players[color.getNum()] = new Player(color,true);
            }
            else {
                players[color.getNum()] = new Player(color,false);
            }
        }
        if (computerNumber>0) {
            computerController = new PlayerAIController(this);
        }
        if (LAUNCH_MAIN_GAME) {
            gameTurn = 4;
        }
        else {
            gameTurn = 0;
        }
        
        firstRolls = new HashMap<Player,Integer>();
        this.ui = new GameWindow(this);
    }

    // Get a player by ID
    public Player getPlayer(int id) {
        return players[id];
    }

    // Get a player by color
    public Player getPlayer(PlayerColor color) {
        return players[color.getNum()];
    }

    // Returns true if all players finished the game
    public boolean isGameFinished() {
        int n=0;
        for(int i=0;i<4;i++) {
            if (this.getPlayer(i).hasFinished()) {n++;}
        }
        return n==3;
    }

    // Increment the turn count
    public void incrementgameTurn() {
        this.gameTurn++;
    }

    public int getGameTurn() {
        return this.gameTurn;
    }

    private void allowAnotherTurn() {
        if (this.getTurnPlayer().isAllowedToGetAnotherRoll()) {
            this.getTurnPlayer().incrementSuccessiveRolls();
            this.playOrderShift-=1;
        }
    }

    public PlayerAIController getComputerController() {
        return computerController;
    }

    // Returns true if at least 1 player is played by an AI
    public boolean hasComputers() {
        return computerController!=null;
    }

    // Get the player who should play during this turn
    public Player getTurnPlayer() {
        return this.getPlayer((this.gameTurn+playOrderShift)%4);
    }

    // All actions following a player's turn are in this method
    public void finishTurn() {

        // End of the first player selection phase, determine the first player or restart the phase
        if (this.gameTurn==3) {
            
            // If only 1 player rolled the highest number make it the first player 
            Map.Entry<Player,Integer> highestPlayerEntry = null;
            boolean restartGame = false;

            // For each map entry
            for (Map.Entry<Player,Integer> entry : firstRolls.entrySet()) {
                if (highestPlayerEntry==null || entry.getValue().compareTo(highestPlayerEntry.getValue()) > 0) {
                    highestPlayerEntry = entry;
                    restartGame = false;
                }
                else if (entry.getValue().compareTo(highestPlayerEntry.getValue()) == 0) {
                    restartGame = true;
                }
            }

            // If the first player can't be determined, restart the game (go back to turn 0)
            if (restartGame) {
                this.gameTurn = -1; // Set to -1 so the turn count is going to be incremented to 0
                this.firstRolls.clear(); // Clear the set of first rolls to begin another roll phase
            }
            // Else shift the player order so the first player begins the game
            else {
                this.playOrderShift=highestPlayerEntry.getKey().getColor().getNum(); // Set the first player
            }
        }

    
        // Get another roll if the player gets a 6 less than 3 times in a row
        if (this.isFirstPlayerDetermined()) {
            if (this.getLastDiceResult()==6) {
                    this.allowAnotherTurn();
            }
            else {
                this.getTurnPlayer().resetSuccessiveRolls();
            }
        }

        this.incrementgameTurn();
    }

    // When a piece is selected, make the action corresponding to the dice result (move).
    public void play(Piece piece) {
        if (this.isPlayableInGameContext(piece)) {
            piece.move(this.getLastDiceResult());

            // Check if the piece is on another piece

            // If the piece is on the main track after the move
            if (!piece.isInSafeZone() && !piece.isInColoredArea()) {

                // For each piece on the same square
                for (Piece otherPiece : this.piecesOnSquare(piece)) {
                    if (piece != otherPiece) {
                        // Two pieces of the same color
                        if (otherPiece.getColor() == piece.getColor()) {
                            if (!piece.isBlocked() && !otherPiece.isBlocked()) {
                                piece.createBlock(otherPiece);
                            }
                        }
                        // Two pieces of different color
                        else if (!otherPiece.isBlocked() || ( piece.isBlocked() && otherPiece.isBlocked() ) ) {
                            otherPiece.capture();
                            ui.showDialog(piece.getPlayer()+" "+PropertiesReader.getString("capturedPiece")+" "+otherPiece);
                            this.getTurnPlayer().incrementCapturedPieces();
                        }
                    }
                }


            }

            if (piece.isInHome()) {
                piece.breakBlock();
            }
        }
        else {
            System.err.println("Can't play this piece");
        }
    }

    // Returns an ArrayList of all pieces that are on the n square
    public List<Piece> piecesOnSquare(int square) {
        List<Piece> list = new ArrayList<Piece>();

        for(int i=0;i<4;i++) {
            for(int j=0;j<4;j++) {
                Piece piece = this.getPlayer(i).getPiece(j);
                
                // If the other piece is on the same square on the main track outside of a safe zone
                if (piece.getAbsolutePosition()==square && !piece.isInColoredArea()) {
                    list.add(piece);
                }
            }
        }

        return list;
    }

    // Overloaded method: Returns an ArrayList of all pieces that are on the same square as piece
    public List<Piece> piecesOnSquare(Piece piece) {
        List<Piece> list = new ArrayList<Piece>();

        for(int i=0;i<4;i++) {
            for(int j=0;j<4;j++) {
                Piece otherPiece = this.getPlayer(i).getPiece(j);
                
                if (
                    ( otherPiece.getAbsolutePosition()==piece.getAbsolutePosition() && !otherPiece.isInColoredArea() && !piece.isInColoredArea() ) || // If the other piece is on the same square on the main track outside of a safe zone
                    ( otherPiece.isInColoredArea() && otherPiece.getPosition()==piece.getPosition() && otherPiece.getColor()==piece.getColor() && !piece.isInHome()) // if the piece is on the same square on the same colored area
                    ) {
                    list.add(otherPiece);
                }
            }
        }

        return list;
    }

    public int getLastDiceResult() {
        return this.lastDiceResult;
    }

    // Generate a random number between 1 and 6 
    public int rollDice() {
        Random random = new Random();
        int n = random.nextInt(6)+1;
        this.lastDiceResult = n;
        if (!isFirstPlayerDetermined()) {
            firstRolls.put(this.getTurnPlayer(),n); // Save the result to determine the first player in the game
        }
        return n;
    }

    public boolean isFirstPlayerDetermined() {
        return this.gameTurn>3;
    }

    // If the piece is playable while taking into account the current game situation
    public boolean isPlayableInGameContext(Piece p) {

        boolean isColorPlayable = this.getPlayer(p.getColor())==this.getTurnPlayer();

        int squaresToMove = 0;

        // Determine the number of squares to move

        // If it's a block
        if (p.isBlocked()) {
            // If the number is even
            if (this.getLastDiceResult()%2==0) {
                squaresToMove = this.getLastDiceResult()/2;
            }
            // If the number is odd: the player can't play
            else {
                return false;
            }
        }
        // If it's a regular piece
        else {
            squaresToMove = this.getLastDiceResult();
        }

        // If the piece is in the starting block
        if (p.getPosition()==-1) {
            return isColorPlayable && this.getLastDiceResult()==6;
        }
        // General case: if the piece is on the track or on the colored area
        else if (p.isOnTrack()) {
            return isColorPlayable
            && p.getPosition()+squaresToMove<=56 // Doesn't go further than the home square
            && ( !isBlockInRange(p.getAbsolutePosition(), squaresToMove) || p.isBlocked() ) // Doesn't go on or past a block if it's a regular piece
            ; 
        }
        return false;
    }

    // Returns true if a block is on a square in the range in front of the position
    // This method is used to block other pieces on the board if they need to pass on or through a block
    private boolean isBlockInRange(int position, int range) {
        for (int i=position;i<=position+range;i++) {
            for (Piece pieceOnSquare : this.piecesOnSquare(i)) {
                if (pieceOnSquare.isBlocked()) {
                    return true;
                }
            }
        }
        return false;
    }

}
