package fr.utbm.lp2a.ludo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenu extends JFrame {
    public static void main(String[] args) {
        new MainMenu();
    }

    JPanel mainPanel;
    JComboBox<String> modeSelect;

    MainMenu() {
        super(PropertiesReader.getString("gameName"));
        this.setLayout(new BorderLayout(64,64));

        JLabel title = new JLabel();
        title.setIcon(new ImageIcon(MainMenu.class.getResource("logo.png")));
        title.setHorizontalTextPosition(JLabel.CENTER);

        mainPanel = new JPanel();

        JButton btnPlay = new JButton(PropertiesReader.getString("startGame"));
        btnPlay.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] modes = new String[5];
        modes[0] = PropertiesReader.getString("mode4Players");
        modes[4] = PropertiesReader.getString("mode4Computers");
        for(int i=1;i<=3;i++) {
            modes[i] = PropertiesReader.getString("mode1Player1")+" "+i+" "+PropertiesReader.getString("mode1Player2");
        }
        modeSelect = new JComboBox<String>(modes);
        modeSelect.setAlignmentX(Component.CENTER_ALIGNMENT);


        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        btnPlay.addActionListener(btnModeActionListener);
        mainPanel.add(modeSelect);
        mainPanel.add(Box.createRigidArea(new Dimension(0,8)));
        mainPanel.add(btnPlay);
        mainPanel.add(Box.createRigidArea(new Dimension(0,32)));

        this.add(title,BorderLayout.PAGE_START);
        this.add(mainPanel,BorderLayout.CENTER);
        this.add(Box.createHorizontalGlue(),BorderLayout.LINE_START);
        this.add(Box.createHorizontalGlue(),BorderLayout.LINE_END);
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon(MainMenu.class.getResource("icon.png")).getImage());

        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }

    ActionListener btnModeActionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            new Game(modeSelect.getSelectedIndex());
            dispose();
        }
        
    };
}
