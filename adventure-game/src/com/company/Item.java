package com.company;

import java.awt.*;

public class Item extends RoomObject{
    private final char name;

    public Item(int x, int y, char name, Image img) {
        super(x,y, img);
        this.name = name;
    }

    public char getName() {
        return name;
    }

    @Override
    public Color getColor() {
        return new Color(0,0,0,0f);
    }

}

