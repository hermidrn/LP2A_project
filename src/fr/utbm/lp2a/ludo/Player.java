package fr.utbm.lp2a.ludo;

public class Player {
    private PlayerColor color;
    private Piece[] pieces;

    // Number of captured pieces
    private int capturedPieces;

    // Number of successive rolls made by the player
    private byte successiveRolls;

    // If true, the player will be controlled by the AI instead of the user interface
    private boolean controlledByComputer;

    Player(PlayerColor color, boolean controlledByComputer) {
        this.color = color;
        pieces = new Piece[4];
        for(int i=0;i<4;i++) {
            pieces[i] = new Piece(this);
        }
        capturedPieces = 0;
        successiveRolls = 0;
        this.controlledByComputer = controlledByComputer;
    }

    public boolean isControlledByComputer() {
        return controlledByComputer;
    }

    public PlayerColor getColor() {
        return this.color;
    }

    public String toString() {
        if (this.isControlledByComputer()) {
            return "🤖"+this.color.toString();
        }
        else {
            return this.color.toString();
        }
    }

    public Piece getPiece(int n) {
        return pieces[n];
    }

    public int piecesInHome() {
        int n=0;
        for(int i=0;i<4;i++) {
            if (pieces[i].isInHome()) {n++;}
        }
        return n;
    }

    public boolean hasFinished() {
        return this.piecesInHome()==4;
    }

    public boolean isAbleToEnterColoredZone() {
        return capturedPieces>0;
    }

    public void incrementCapturedPieces() {
        this.capturedPieces++;
    }

    public boolean hasJustPlacedFirstPiece() {

        byte piecesOnStartingBlock = 4;
        Piece piece = pieces[0];
        for(int i=0;i<4;i++) {
            if (pieces[i].getPosition()!=-1) {
                piecesOnStartingBlock-=1;
                piece = pieces[i];
            }
        }
        return piecesOnStartingBlock==3 && piece.getPosition()==0;
    }

    public void resetSuccessiveRolls() {
        successiveRolls=0;
    }

    public void incrementSuccessiveRolls() {
        successiveRolls+=1;
    }

    // Returns true if the player has made more than 3 successive 6. If it's true the player should pass his turn.
    public boolean isAllowedToGetAnotherRoll() {
        return successiveRolls<3;
    }

}
