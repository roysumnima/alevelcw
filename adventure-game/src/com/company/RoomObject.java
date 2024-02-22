package com.company;

import java.awt.*;

public class RoomObject {
    protected int x; protected int y;
    protected boolean visible;

    protected boolean active;

    protected Image currentImg;

    public RoomObject(int x, int y, Image image) {
        this.x = x;
        this.y = y;
        this.visible = true;

        this.currentImg=image;

        this.active = true;
    }


    public Image getImg(){
        return currentImg;
    }
    public Color getColor() { return Color.black; }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }

    public boolean isVisible() {
        return visible;
    }
    public void hide() {
        visible = false;
    }
    public void show() {
        visible = true;
    }

    public void setActive(boolean b) {
        active = b;
    }
    public boolean isActive() {
        return active;
    }

}

