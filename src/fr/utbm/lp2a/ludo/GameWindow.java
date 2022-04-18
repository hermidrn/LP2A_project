package fr.utbm.lp2a.ludo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;

public class GameWindow extends JFrame {

    private JTextArea labelGameInfo;
    private JLabel labelPlayer;
    private JButton btnRoll;
    private JButton btnContinue;
    private JLabel diceIcon;
    private JLayeredPane centerPanel;

    private JPanel rightPanel;
    private JPanel rankPanel;

    private PieceButton[] piecesButtons;

    private Game game;

    GameWindow(Game game) {
        super(PropertiesReader.getString("gameName")); // constructs the game JFrame
        this.game = game;
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
        UIManager.put("Button.disabledText", new ColorUIResource(Color.BLACK));
        
        // Panels
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(0,16,0,16));

        JPanel topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);

        centerPanel = new JLayeredPane();
        centerPanel.setLayout(null);


        // Info panel
        JPanel infoPanel = new JPanel();

        diceIcon = new JLabel();
        diceIcon.setIcon(new ImageIcon(getClass().getResource("ic_action_down.png")));

        labelGameInfo = new JTextArea(PropertiesReader.getString("welcomeFirstRound"));
        labelGameInfo.setDisabledTextColor(Color.BLACK);
        labelGameInfo.setEnabled(false);
        labelGameInfo.setLineWrap(true);
        labelGameInfo.setWrapStyleWord(true);

        infoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setMaximumSize(new Dimension(512,512));
        infoPanel.add(diceIcon);
        infoPanel.add(labelGameInfo);
        // End of info panel


        // Side (right) panel components
        btnRoll = new JButton(PropertiesReader.getString("rollDiceAction"));
        btnRoll.addActionListener(btnRollActionListener);
        btnRoll.setMaximumSize(new Dimension(2048,btnRoll.getMinimumSize().height));
        btnRoll.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRoll.setIcon(new ImageIcon(getClass().getResource("ic_dice.png")));

        btnContinue = new JButton(PropertiesReader.getString("nextGameStep"));
        btnContinue.setEnabled(false);
        btnContinue.addActionListener(btnContinueActionListener);
        btnContinue.setMaximumSize(new Dimension(2048,btnContinue.getMinimumSize().height));
        btnContinue.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnContinue.setIcon(new ImageIcon(getClass().getResource("ic_done.png")));

        rankPanel = new JPanel();
        rankPanel.setLayout(new BoxLayout(rankPanel, BoxLayout.PAGE_AXIS));
        rankPanel.setMaximumSize(new Dimension(2048,btnContinue.getMinimumSize().height));
        rankPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rankPanel.setBackground(new Color(245,245,245));
        
        rightPanel.add(infoPanel);
        rightPanel.add(Box.createRigidArea(new Dimension(0,16)));
        rightPanel.add(btnRoll);
        rightPanel.add(btnContinue);
        rightPanel.add(Box.createRigidArea(new Dimension(0,16)));
        rightPanel.add(rankPanel);
        rightPanel.add(Box.createVerticalGlue());
        // End of right panel


        // Top panel components
        labelPlayer = new JLabel(PropertiesReader.getString("currentPlayerAnnoucement1")+game.getTurnPlayer()+PropertiesReader.getString("currentPlayerAnnoucement2"));
        labelPlayer.setFont(new Font(labelPlayer.getFont().getFontName(), Font.PLAIN, 24));
        labelPlayer.setForeground(game.getTurnPlayer().getColor().getAwtColor());
        topPanel.add(labelPlayer);


        // Center panel (board)

        // Pieces
        piecesButtons = new PieceButton[16] ;

        for (int i=0;i<4;i++) {
            for (int j=0;j<4;j++) {
                int n = j+4*i; // Index in the button array
                piecesButtons[n] = new PieceButton(game.getPlayer(i).getPiece(j));
                piecesButtons[n].addActionListener(btnPieceActionListener);
                centerPanel.add(piecesButtons[n]);
            }
        }

        // Board background image
        JLabel boardImage;
        boardImage = new JLabel(new ImageIcon(getClass().getResource("board.png")));
        boardImage.setBounds(0,0,544,544);
        centerPanel.setPreferredSize(new Dimension(544,544));
        centerPanel.add(boardImage);

        // End of center panel

        // Add panels to the main frame
        this.add(topPanel,BorderLayout.PAGE_START);
        this.add(centerPanel,BorderLayout.CENTER);
        this.add(rightPanel,BorderLayout.LINE_END);
        this.pack();
        this.setVisible(true);
        refreshBoard();
        labelGameInfo.setText(PropertiesReader.getString("welcomeFirstRound")+game.getTurnPlayer()+" "+PropertiesReader.getString("stateFirstPlayer"));
        this.beginTurn();
    }

    // At the beginning of the game: add a roll (to decide the first player) to the display
    // At the end of the game: add players as they win
    private void addItemToRankPanel() {

        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.LINE_AXIS));
        itemPanel.setOpaque(false);
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rankPanel.add(Box.createRigidArea(new Dimension(8,0)));

        // Label
        JLabel labelPlayerName = new JLabel(game.getTurnPlayer().toString());
        labelPlayerName.setForeground(game.getTurnPlayer().getColor().getAwtColor());
        labelPlayerName.setFont(new Font(labelPlayerName.getFont().getFamily(),Font.PLAIN,16));

        if (!game.isFirstPlayerDetermined()) {
            // Dice icon
            JLabel labelPlayerDiceIcon = new JLabel();
            labelPlayerDiceIcon.setIcon(new ImageIcon(getClass().getResource("ic_dice"+game.getLastDiceResult()+".png")));
            itemPanel.add(labelPlayerDiceIcon);
        }
        else {
            byte rank = 1;
            for (Component c : rankPanel.getComponents()) {
                if (c instanceof JPanel) {rank++;}
            }
            labelPlayerName.setText(rank+": "+game.getTurnPlayer());
        }
        itemPanel.add(labelPlayerName);

        rankPanel.add(itemPanel);
        rankPanel.add(Box.createRigidArea(new Dimension(0,4)));

    }

    // Refresh display of the board pieces
    private void refreshBoard() {
        for(int i=0;i<4;i++) {
            for (int j=0;j<4;j++) {
                int n = j+4*i; // Index in the button array
                Piece piece = game.getPlayer(i).getPiece(j); // Current piece to display
                BoardPosition xy = new BoardPosition();

                // Offset to avoid overlapping pieces
                int xOffset = 0;
                int yOffset = 0;
                int offsetSize = BoardPosition.GRID_SIZE;

                // Checks if the piece needs to be displayed on the main track of the board, in the starting block, or in the colored area
                if (piece.getPosition()>=0) {
                    if (piece.isInColoredArea()) {
                        xy = BoardPosition.getEndXY(piece);
                    }
                    else {
                        xy = BoardPosition.getXYPosition(piece.getAbsolutePosition());
                    }

                    // Handle display for multiple pieces on a same square 
                    List<Piece> piecesOnSameSquare = game.piecesOnSquare(piece);

                    // If there are more than a single piece on this square, display them
                    if (piecesOnSameSquare.size()>1) {

                        if(piecesOnSameSquare.size()==2 && piece.isBlocked()) {
                            piecesOnSameSquare.remove(piece.getBlockPiece());
                        }

                        // Don't show blocks as 2 different pieces
                        for (int k=0;k<16;k++) {
                            if (piecesButtons[k].getPiece() == piecesButtons[n].getPiece().getBlockPiece() && piecesButtons[k].isVisible()) {
                                piecesButtons[n].setVisible(false);
                            }
                        }

                        int id = piecesOnSameSquare.indexOf(piece); // get the number of the piece on this square, it will determine its position in the square
                        int sizeDivider = (int) Math.ceil(Math.sqrt(piecesOnSameSquare.size()));
                        offsetSize/=sizeDivider;
                        xOffset = id%sizeDivider*offsetSize;
                        yOffset = (int) Math.floor(id/sizeDivider)*offsetSize;
                    }
                }
                else {
                    xy = BoardPosition.getStartXY(piece.getColor(),j);
                    piecesButtons[n].setSize(BoardPosition.GRID_SIZE, BoardPosition.GRID_SIZE*2);
                    piecesButtons[n].setVisible(true);
                }

                piecesButtons[n].setLocation(xy.getDisplayX()+xOffset, xy.getDisplayY()-offsetSize+yOffset);
                piecesButtons[n].setEnabled(false);
                centerPanel.setLayer(piecesButtons[n], xy.getY()*4+yOffset/offsetSize); // To simulate a 3D effect, pieces at the bottom of the board displays over pieces behind them (top of the board) 

                // Refresh size and appearance UI-independant appearance
                piecesButtons[n].setSize(offsetSize,offsetSize*2);
            }
        }

    }

    // ACTIONLISTENERS

    // ActionListener for ROLL DICE button
    private ActionListener btnRollActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            rollDice();
        }
    };

    // ActionListener for CONTINUE button
    private ActionListener btnContinueActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            finishTurn();
        }
    };

    // ActionListener for pieces buttons
    private ActionListener btnPieceActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            playPiece(((PieceButton) e.getSource()).getPiece()); // Play the piece which corresponds to the clicked piece button
        }
    };

    public void showDialog(String text) {
        JOptionPane.showMessageDialog(this,text);
    }

    // GAME ACTIONS

    // Game action: select a piece to play after a dice roll
    private void playPiece(Piece piece) {
        if (game.isPlayableInGameContext(piece)) {
            labelGameInfo.setText(piece.getPlayer()+" "+PropertiesReader.getString("statePlayed"));
        }
        else {
            labelGameInfo.setText(piece.getPlayer()+" "+PropertiesReader.getString("statePassed"));
        }

        game.play(piece); // Play with the selected piece

        

        // Don't wait to click on the continue button to pass to the next player if the piece was not played by the computer
        if (game.hasComputers()) {
            refreshBoard();
            btnContinue.setEnabled(true);
        }
        else {
            this.finishTurn();
        }

        // If the player's piece reaches the home square
        if (piece.isInHome()) {
            showDialog(PropertiesReader.getString("pieceWin1")+" "+piece+" "+PropertiesReader.getString("pieceWin2"));

            // If the player has finished, add it to the leaderboard
            if (piece.getPlayer().hasFinished()) {
                addItemToRankPanel();
            }
        }
    }

    // Game action: roll the dice
    private void rollDice() {
        int diceNumber = game.rollDice();

        diceIcon.setIcon(new ImageIcon(getClass().getResource("ic_dice"+diceNumber+".png")));
        btnRoll.setEnabled(false);
        

        // Normal case
        if (game.isFirstPlayerDetermined() && game.getTurnPlayer().isAllowedToGetAnotherRoll()) {
            int playablePieces = 0;
            for(int i=0;i<4;i++) { // Iterate among players
                for (int j=0;j<4;j++) { // Iterate among pieces
                    boolean playable = game.isPlayableInGameContext(game.getPlayer(i).getPiece(j));
                    piecesButtons[j+4*i].setEnabled(playable);
                    if (playable) {playablePieces++;}
                }
            }
            if (playablePieces>0) {
                labelGameInfo.setText(game.getTurnPlayer()+" "+PropertiesReader.getString("diceRolled")+" "+diceNumber+"\n"+PropertiesReader.getString("actionSelectPiece"));
                // Automatically play using AI controller if the player is a computer
                if (game.getTurnPlayer().isControlledByComputer()) {
                    this.playPiece(game.getComputerController().getPieceToPlay());
                }
            }
            else {
                labelGameInfo.setText(game.getTurnPlayer()+" "+PropertiesReader.getString("diceRolled")+" "+diceNumber+"\n"+PropertiesReader.getString("actionCantSelectPiece"));
                btnContinue.setEnabled(true);

                // Displays a "pass turn" button
                if (!game.getTurnPlayer().isControlledByComputer()) {
                    btnContinue.setText(PropertiesReader.getString("passGameStep"));
                    btnContinue.setIcon(new ImageIcon(getClass().getResource("ic_skip.png")));
                }
            }

            
        }
        // If the player is not allowed to play this roll (the roll is a six and follows 2 other sixes)
        else if (game.isFirstPlayerDetermined()) {
            showDialog(game.getTurnPlayer()+" "+PropertiesReader.getString("alertPassTurnAfter3sixes"));
            btnContinue.setEnabled(true);
        }
        // Dice roll during first player determination phase
        else {
            addItemToRankPanel();
            labelGameInfo.setText(game.getTurnPlayer()+" "+PropertiesReader.getString("diceRolled")+" "+diceNumber);
            btnContinue.setEnabled(true);
        }

    }

    // Game action: finish the current turn
    private void finishTurn() {
        game.finishTurn();

        btnContinue.setEnabled(false);
        btnRoll.setEnabled(true);
        labelGameInfo.setText(PropertiesReader.getString("actionSelectOption"));
        diceIcon.setIcon(new ImageIcon(getClass().getResource("ic_action_down.png")));

        // Hide the roll history after the first player has been determined
        if (game.getGameTurn()==4) {
            this.showDialog(game.getTurnPlayer()+" "+PropertiesReader.getString("firstPhaseFinished"));
            rankPanel.removeAll();
        }
        // If the first phase was restarted
        else if (game.getGameTurn()==0) {
            this.showDialog(PropertiesReader.getString("firstPhaseRestarted"));
            rankPanel.removeAll();
        }

        refreshBoard();
        

        // On the beginning of the 4rd turn (player has been determined), the button to end the turn (continue)
        // will only be useful if no piece can be moved so it's more a button to pass a turn
        if (game.getGameTurn()>=4) {
            btnContinue.setText(PropertiesReader.getString("nextGameStep"));
            btnContinue.setIcon(new ImageIcon(getClass().getResource("ic_done.png")));
        }

        // Refresh current player info label
        labelPlayer.setText(PropertiesReader.getString("currentPlayerAnnoucement1")+game.getTurnPlayer()+PropertiesReader.getString("currentPlayerAnnoucement2"));
        labelPlayer.setForeground(game.getTurnPlayer().getColor().getAwtColor());

        // If the game is finished
        if (game.isGameFinished()) {
            this.showDialog(PropertiesReader.getString("alertGameFinished"));
            labelPlayer.setText(PropertiesReader.getString("alertGameFinished"));
            labelGameInfo.setText(PropertiesReader.getString("infoLeaderboardAvailable"));
            labelPlayer.setForeground(Color.BLACK);
            btnRoll.setEnabled(false);
        }
        else {
            // After the end of the turn, begin a new turn 
            this.beginTurn();
        }
        
    }

    private void beginTurn() {
        // Roll the dice directly without player intervention if the player is controlled by the computer
        if (game.getTurnPlayer().isControlledByComputer() && !game.isGameFinished()) {
            this.rollDice();
        }
        else if (game.getTurnPlayer().hasFinished()) {
            this.finishTurn();
        }
    }
    
}
