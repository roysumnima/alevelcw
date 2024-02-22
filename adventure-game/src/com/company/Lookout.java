package com.company;

import java.awt.*;

public class Lookout extends Enemy {
    private int movingX;
    private int movingY;

    private int slow;
    private final int del;

    private final Image downImg;
    private final Image leftImg;
    private final Image rightImg;
    private final Image upImg;

    public Lookout(int x, int y, char weakness, int del, Image[] images) {
        super(x, y, weakness, images[0]);

        downImg = images[0];
        leftImg = images[1];
        rightImg = images[2];
        upImg = images[3];

        slow = 0;
        this.del = del;

    }

    public Lookout(int x, int y, char weakness, Image[] images) {
        this(x, y, weakness,  5, images);

    }

    public void move(){
        slow++;

        if (slow >= del) {
            slow=0;
                if (movingX != 0) { x = x + movingX; }
                if (movingY !=0 ) { y = y + movingY; }
        }
    }

    public int nextX(){
        return (x + movingX);
    }

    public int nextY(){
        return (y + movingY);
    }

    public void lookTowards(int mcX, int mcY) {
        movingY= Integer.compare(mcY - this.y, 0);
        if (movingY>0) {
            currentImg=downImg;
        }
        if (movingY<0) {
            currentImg=upImg;
        }

        movingX=Integer.compare(mcX - this.x, 0);
        if (movingX<0) {
            currentImg=leftImg;
        }
        if (movingX>0) {
            currentImg=rightImg;
        }
    }

}
