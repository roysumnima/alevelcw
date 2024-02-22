package com.company;

import java.awt.*;

public class Patrol extends Lookout {
    private final int startX;
    private int endX;

    private final int startY;
    private int endY;

    private boolean goBack;

    private final char type;

    public Patrol(int x, int y, char weakness, char type, Image[] images) {
        super(x, y, weakness, 4, images);
        this.startX = x;
        this.startY = y;

        this.endX = -1;
        this.endY = -1;

        this.goBack = false;

        this.type = type;
    }

    //SETTERS
    public void setEndCoordinates(int x, int y){
        endX = x;
        endY = y;
    }
    public void setEndCoordinates(){
        endX = startX;
        endY = startY;
    }

    //GETTERS
    public char getType() {
        return type;
    }

    public boolean beenSet() {
        return endX != -1 && endY != 1;
    }

    //MOVING
    @Override
    public void move(){
        if (x==endX && y==endY) {
            goBack = true;
        } else if (x==startX && y==startY) {
            goBack = false;
        }

        if (!goBack) {
            this.lookTowards(endX, endY);
        } else {
            this.lookTowards(startX, startY);
        }

        super.move();
    }

    //Overloading Lookout's method - this is used in Main
    public void lookTowards() {
        if (!goBack) {
            super.lookTowards(endX, endY);
        } else {
            super.lookTowards(startX, startY);
        }
    }

    /*

    //testing purposes
    public void soutEnd() {
        System.out.println(endX + ", " + endY);
    }

    */

}

