package com.company;

import java.awt.*;
import java.util.ArrayList;

public class MC {

    private int status; // -1:dead, 0:alive, +1:won
    private int enemiesKilled;

    private Room currentRoom;
    private Room prevRoom;
    private int x;
    private int y;

    private final ArrayList<Item> inventory;

    private final Image mcDownImg;
    private final Image mcLeftImg;
    private final Image mcRightImg;
    private final Image mcUpImg;

    private Image currentImg;

    public MC(Room startRoom, int startX, int startY, Image[] imgs) {
        status = 0;

        currentRoom = startRoom;
        prevRoom = startRoom;
        this.x = startX; this.y = startY;

        inventory = new ArrayList<>();

        mcDownImg = imgs[0];
        mcLeftImg = imgs[1];
        mcRightImg = imgs[2];
        mcUpImg = imgs[3];

        currentImg=mcDownImg;
    }

    //GETTERS & SETTERS
    public int getStatus() {
        return status;
    }

    public Image getImg() {
        return currentImg;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public Item getItem(int i) {
        return inventory.get(i);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /*

    //testing purposes

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    */

    //MOVING
    public void moveLeft() {
        currentImg=mcLeftImg;
        this.x--;
    }

    public void moveRight() {
        currentImg=mcRightImg;
        this.x++;
    }

    public void moveUp() {
        currentImg=mcUpImg;
        this.y--;
    }

    public void moveDown() {
        currentImg=mcDownImg;
        this.y++;
    }
    public void isStill() {
        currentImg=mcDownImg;
    }

    //INTERACTION WITH ROOM OBJECTS
    public void itemInteract(){
        boolean itemThere = false;
        ArrayList<Item> roomItems = currentRoom.getItems();

        for (int i=0; i<roomItems.size(); i++) /* mc collects item*/{
            if (interaction(roomItems.get(i))) {
                itemThere = true;
                this.collectItem(roomItems.get(i));
                currentRoom.removeItem(i); //need the i variable in this line, safer not to use an enhanced for loop
                // ^^ also want to update the room's actual items so it is consistent
                break;
            }
        }

        if (!itemThere) /*discards last item in inventory*/{
            Item discarded = this.discardItem();
            if (discarded!=null) {
                currentRoom.addItem(discarded);
            }
        }

        /*

        //testing purposes
        for (Item i: inventory) {System.out.print(i.getName() + ", ");}
        System.out.println();

        */
    }

    public void doorInteract() {
        for (Door d: currentRoom.getDoors()) {
            if (interaction(d) && d.isActive()) {

                if (d.getRoom().getName() == '*') {
                    status = 1;
                    //mc wins
                }

                prevRoom = currentRoom;
                currentRoom = d.getRoom();
                currentRoom.justVisited(); //sets currentRoom's 'visited' attribute to true for statistics

                System.out.println("Entered room " + currentRoom.getName());

                for (Door d2: currentRoom.getDoors()) {
                    if (prevRoom.getName() == d2.getRoom().getName()) {
                        this.x = d2.getX();
                        this.y = d2.getY();
                        System.out.println("This door goes back to: " + d2.getRoom().getName());
                    }
                }

                break;

            }
            if (interaction(d)) {
                System.out.println("The key to room " + d.getRoom().getName() + " is '" + d.getKey() +"'");
            }
        }

        /*

        //Could make mc interact with items and doors using space (i.e. the same key) to be more intuitive.
        //This would mean you need to be more careful in these interact methods:

        if (doorThere) {
        //^^ this is to ensure MC only changes coordinates
        //when at a door, not when picking up items
            for (Door d : currentRoom.getDoors()) {
                if (d.getRoom() == prevRoom.getName()) {
                    setX(d.getX());
                    setY(d.getY());
                    break;
                }
            }
        }

        */

        /*

        //testing purposes
        for (Item i: inventory) {System.out.print(i.getName() + ", ");}
        System.out.println();

        */
    }

    public void enemyInteract(){
        for (Enemy roomEnemy : currentRoom.getEnemies()) {
            if (interaction(roomEnemy)) {

                if (roomEnemy.isActive()) {
                    status = -1;
                    //and mc dies

                } else {
                    currentRoom.removeEnemy(roomEnemy);
                    enemiesKilled++;
                    //and mc kills enemy
                }

            }
        }
    }

    private boolean interaction(RoomObject object) {
        //checks if mc and object have same coordinates
        return object.getX() == x && object.getY() == y;
    }

    //INVENTORY
    public void collectItem(Item i) /*collects & hides Item*/{
        i.hide();
        inventory.add(i);
        //System.out.println(i.getName()); //testing
    }

    public Item discardItem() /*removes last item from inventory & returns it*/ {
        if (inventory.size() > 0) {
            Item removed = inventory.get(inventory.size() - 1);
            removed.setX(x); removed.setY(y);
            removed.show();
            inventory.remove(inventory.size() - 1);
            return removed;
        }
        return null;
    }

    public boolean hasItem(char n){
        for (Item i: inventory) {
            if (i.getName()==n) {
                return true;
            }
        }
        return false;
    }

    public int getSize(){
        return inventory.size();
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }
}
