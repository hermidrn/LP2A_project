package fr.utbm.lp2a.ludo;

import java.lang.Math;

public class Piece {

    private int position; 
    /* Piece position on the track
    -1 : the piece is in the starting block
    0-50 (0-51 if the colored area entrance is forbidden): the piece is in the game
    51-55 : the piece is in the colored area at the end
    56 : the piece is in its home */
    
    // The player who owns the piece
    private final Player player;

    // Piece that will form a block with this piece
    private Piece blockPiece;

    // Construct a piece using its player
    Piece(Player player) {
        this.position=-1; // start in the starting block
        this.player = player;
        blockPiece = null;
    }

    // Returns true if the piece is on the track and can be moved
    public boolean isOnTrack() {
        return this.position>=0 && this.position<56;
    }

    // Get the ID of the piece owner
    private int getPlayerId() {
        return this.player.getColor().getNum();
    }

    // Returns the absolute position (=the position relative to the first player)
    public int getAbsolutePosition() {
        if (this.position==-1) {
            return -1;
        }
        else {
            return (Math.floorMod(this.getPlayerId(),4)*13+this.position)%52;
        }
        
    }

    // Get the piece position relative to the player
    public int getPosition() {
        return this.position;
    }

    // Get the piece color
    public PlayerColor getColor() {
        return this.player.getColor();
    }

    // Try to move the piece n squares (after a dice roll)
    public void move(int n) {
        if (n<1 || n>6) {
            System.err.println("Can't move "+n+" squares");
        } else {
            
            if (this.isBlocked()) {
                if (n%2==0) {
                    n=n/2;
                    //this.blockPiece.move(n);
                }
                else {
                    n=0;
                    System.err.println("Can't move a block an even number");
                }
            }

            // If the piece is in the starting block, move it to the first square
            if (this.position==-1) {
                this.position=0;
            }
            // If the piece is in the colored area, move it if it does not goes futher than its home
            else if (this.isInColoredArea()) {
                if (this.getPosition()+n<=56) {
                    this.position+=n;
                }
            }
            // General case: move the piece n squares on the track
            else {
                if (this.player.isAbleToEnterColoredZone()) {
                    this.position+=n;
                    
                } else {
                    this.position=(this.position+n)%52; // Start another lap on the track if it can't enter the colored area
                }
                
            }

            // Sync position of both pieces in the block
            if (this.isBlocked()) {
                this.blockPiece.position = this.position;
            }

        }
    }

    // Move the piece to the starting block after being captured
    public void capture() {
        this.position = -1;
        this.breakBlock();
    }

    // Check if the piece is in the "colored" area near its home
    public boolean isInColoredArea() {
        // If the piece cannot enter the colored area, it continues on the main track and the 51st square is not in the colored zone, it's the square before 0
        if (this.player.isAbleToEnterColoredZone()) {
            return this.position>=51;
        }
        else {
            return false;
        }
        
    }

    public boolean isInHome() {
        return this.position==56;
    }

    public boolean isInSafeZone() {
        return BoardPosition.isSafeSquare(this.position);
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public String toString() {
        if (this.isBlocked()) {
            return this.getColor()+" "+PropertiesReader.getString("objectBlock");
        }
        else {
            return this.getColor()+" "+PropertiesReader.getString("objectPiece");
        }
    }

    // Associate another piece with this piece to make a block
    public void createBlock(Piece p) {
        if (p.getColor() == this.getColor() && p != this) {
            if (!this.isBlocked()) {
                System.out.println("Block created");
                this.blockPiece = p;
                p.blockPiece = this;
            }
            else {
                System.err.println("These pieces are already blocked");
            }
            
        }
        else {
            System.err.println("Can't create a block using these pieces");
        }
    }

    public void breakBlock() {
        if (this.blockPiece!=null) {this.blockPiece.blockPiece = null;} // Remove reference to this piece in the other piece from the block
        this.blockPiece = null; // Remove reference to the other piece in this piece
    }

    public boolean isBlocked() {
        return this.blockPiece!=null;
    }

    public Piece getBlockPiece() {
        return this.blockPiece;
    }
}
