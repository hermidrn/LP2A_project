package fr.utbm.lp2a.ludo;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;

// Inherits JButton so it acts mostly like a normal button in the graphical user interface
public class PieceButton extends JButton{

    private Piece piece;

    private ImageIcon pieceIcon;
    private ImageIcon pieceIconHighlight;
    private ImageIcon blockIcon;
    private ImageIcon blockIconHighlight;

    PieceButton(Piece p) {
        super();
        this.setEnabled(false);
        this.piece=p;
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);

        // Set pieces icons to the color of the player
        this.pieceIcon= new ImageIcon(getClass().getResource("piece"+p.getColor().getNum()+".png"));
        this.pieceIconHighlight= new ImageIcon(getClass().getResource("piece"+p.getColor().getNum()+"h.png"));
        this.blockIcon= new ImageIcon(getClass().getResource("block"+p.getColor().getNum()+".png"));
        this.blockIconHighlight= new ImageIcon(getClass().getResource("block"+p.getColor().getNum()+"h.png"));

        this.setIcon(pieceIconHighlight);
        this.setDisabledIcon(pieceIcon);
        this.setBounds(-100, -100, BoardPosition.GRID_SIZE, BoardPosition.GRID_SIZE*2); // Prepare piece display position and size but put it outside of the visible area
        this.setHorizontalTextPosition(JButton.CENTER);
        this.setFont(new Font(this.getFont().getFamily(),Font.BOLD,24));
        this.setForeground(Color.WHITE);
        this.setMargin(new Insets(5,5,5,5));
    }

    public Piece getPiece() {
        return piece;
    }
    

    // Overrides the setEnabled method of the JButton so when it's called the button appearance changes
    public void setEnabled(boolean enabled) {
        if (enabled) {
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
            super.setEnabled(true);
        }
        else {
            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            super.setEnabled(false);
        }
    
    }

    @Override
    public void setSize(int width, int height) {
        ImageIcon iconHighlighted = pieceIconHighlight;
        ImageIcon iconDisabled = pieceIcon;
    
        if (piece.isBlocked() && !piece.isInHome()) {
            iconHighlighted = blockIconHighlight;
            iconDisabled = blockIcon;
        }
        else if (piece.isInHome()) {
            this.setText(piece.getPlayer().piecesInHome()+"");
        }
        super.setSize(width, height);
        this.setIcon(new ImageIcon(iconHighlighted.getImage().getScaledInstance(width,height,Image.SCALE_FAST)));
        this.setDisabledIcon(new ImageIcon(iconDisabled.getImage().getScaledInstance(width,height,Image.SCALE_FAST)));
    }

    // Required by java
    private static final long serialVersionUID = 1L;
}
