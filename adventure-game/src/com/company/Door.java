package com.company;

import java.awt.*;
import java.util.ArrayList;

public class Door extends RoomObject {
    private final char leadsToChar;
    private Room leadsTo;
    private char key;

    public Door(int x, int y, char room, Image image) {
        super(x,y,image);

        this.leadsToChar = room;
        this.leadsTo = null;
        this.key = ' ';

        this.active = false;
    }

    public void setRoom(ArrayList<Room> roomsList) {

        if (leadsToChar == '*') /* then final door */ {
            leadsTo = new Room('*', 0,0, null, null);

        } else /* normal door */ {
            for (Room r : roomsList) {
                if (r.getName() == leadsToChar) {
                    //System.out.println(r.getName()); //testing
                    leadsTo = r;
                }
            }
        }

    }

    public Room getRoom() {
        return leadsTo;
    }


    public void setKey(char key) {
        this.key = key;
    }

    public char getKey() {
        return key;
    }

    @Override
    public Color getColor() {
        return new Color(30, 160, 90);
    }
}

