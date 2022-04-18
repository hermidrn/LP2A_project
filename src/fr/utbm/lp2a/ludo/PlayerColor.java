package fr.utbm.lp2a.ludo;

import java.awt.Color;

public enum PlayerColor {
    RED(0,PropertiesReader.getString("colorRed"),new Color(188,0,0)),
    BLUE(1,PropertiesReader.getString("colorBlue"),new Color(58,92,165)),
    GREEN(2,PropertiesReader.getString("colorGreen"),new Color(81,168,35)),
    YELLOW(3,PropertiesReader.getString("colorYellow"),new Color(229,181,50));

    private int number;
    private String name;
    private Color awtColor;
    
    PlayerColor(int i, String name, Color awtColor) {
        this.number=i;
        this.name=name;
        this.awtColor=awtColor;
    }

    PlayerColor(int i, String s) {
        this.number=i;
        this.name=s;
    }

    // Get the number corresponding to the color
    public int getNum() {
        return number;
    }

    // Get the name of the color
    public String toString() {
        return name;
    }

    // Get the corresponding AWT color for the graphical user interface
    public Color getAwtColor() {
        return this.awtColor;
    }

}
