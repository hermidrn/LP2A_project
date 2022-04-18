package fr.utbm.lp2a.ludo;

public class BoardPosition {

    private int x;
    private int y;

    public static final int GRID_SIZE = 32; // Size of a square on the board in the user interface

    BoardPosition(int x,int y) {
        this.x=x;
        this.y=y;
    }

    BoardPosition() {
        this.x=-1;
        this.y=-1;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    private void moveX(int x) {
        this.x+=x;
    }

    private void moveY(int y) {
        this.y+=y;
    }

    public int getDisplayX() {
        return this.x*GRID_SIZE;
    }

    public int getDisplayY() {
        return this.y*GRID_SIZE;
    }

    private void setXY(int x, int y) {
        this.x=x;
        this.y=y;
    }

    // Get coordinates of a piece on the main track of the board
    public static BoardPosition getXYPosition(int n) {
        BoardPosition p = new BoardPosition();
        if (n>=0 && n<5) {
            p.setXY(7, 14-n);
        }
        else if (n>=5 && n<11) {
            p.setXY(11-n,9);
        }
        else if (n==11) {
            p.setXY(1,8);
        }
        else if (n>=12 && n<18) {
            p.setXY(n-11,7);
        }
        else if (n>=18 && n<24) {
            p.setXY(7,24-n);
        }
        else if (n==24) {
            p.setXY(8,1);
        }
        else if (n>=25 && n<31) {
            p.setXY(9,n-24);
        }
        else if (n>=31 && n<37) {
            p.setXY(n-21,7);
        }
        else if (n==37) {
            p.setXY(15,8);
        }
        else if (n>=38 && n<44) {
            p.setXY(53-n,9);
        }
        else if (n>=44 && n<50) {
            p.setXY(9,n-34);
        }
        else if (n>=49 && n<=51) {
            p.setXY(58-n, 15);
        }
        return p;
    }

    // Returns the starting block coordinates of the i-piece depending on its color
    public static BoardPosition getStartXY(PlayerColor c,int i) {
        BoardPosition p = new BoardPosition();

        // Top-left square in the starting block
        final int A = 2;
        final int B = 11;
        final int[][] BASE_POSITIONS = {{A,B},{A,A},{B,A},{B,B}};


        p.setXY(BASE_POSITIONS[c.getNum()][0],BASE_POSITIONS[c.getNum()][1]);

        // Display the piece on the right square of the starting block
        if (i==1 || i==3) {
            p.moveX(3);
        }
        // Display the piece on the bottom square of the starting block
        if (i==2 || i==3) {
            p.moveY(3);
        }
        // If both of the previous conditions are true, the piece is displayed on the bottom right square of the starting block

        return p;
    }

    // Get the coordinates of a piece on the colored area of its color on the board
    public static BoardPosition getEndXY(Piece p) {
        BoardPosition xy = new BoardPosition();
        int n = p.getColor().getNum(); // number of the color
        int i = p.getPosition();
        if (i<=56) {
            switch (n) {
                case 0:
                xy.setXY(8,65-i);
                break;

                case 1:
                xy.setXY(i-49,8);
                break;

                case 2:
                xy.setXY(8,i-49);
                break;

                case 3:
                xy.setXY(65-i,8);
                break;

            }
        }
        return xy;
    }

    // Returns true if the n square is a safe zone
    public static boolean isSafeSquare(int n) {
        return n%13==0 || n%13==8;
    }

    
}
