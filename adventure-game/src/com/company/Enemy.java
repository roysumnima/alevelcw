package com.company;

import java.awt.*;

public class Enemy extends RoomObject{
    private final char weakness;

    public Enemy(int x, int y, char weakness, Image downImg) {
        super(x, y, downImg);
        this.weakness = weakness;
    }

    public char getWeakness() {
        return weakness;
    }

    @Override
    public Color getColor() {
        return new Color(200, 40, 40);
    }

}


