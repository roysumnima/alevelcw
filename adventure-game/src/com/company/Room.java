package com.company;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.Math.min;

public class Room {
    private boolean exists;

    private final int rows; private final int columns;

    private final char name;
    private final String[][] roomArray;

    private int mcX; private int mcY; //only applies if room contains '*'

    //ARRAYLISTS
    private final ArrayList<RoomObject> bricks;

    private final ArrayList<Enemy> guards;
    private final ArrayList<Lookout> lookouts;
    private final ArrayList<Patrol> patrols;

    private final ArrayList<Door> doors;
    private final ArrayList<Item> items;

    //stats
    private boolean visited;

    public Room(char filename, int rows, int columns, Image[][] imgs, char[][] fileInfo) {
        exists = true;
        visited = false;

        this.rows = rows;
        this.columns = columns;
        this.name = filename;

        bricks = new ArrayList<>();
        guards = new ArrayList<>();
        lookouts = new ArrayList<>();
        patrols = new ArrayList<>();
        doors = new ArrayList<>();
        items = new ArrayList<>();

        mcX = -1;
        mcY = -1; //set it to a 'null': should only be > 0 for start room

        if (filename == '*') {
            roomArray = new String[rows][columns];
        } else {
            roomArray = getRoom(String.valueOf(name));
            setup(imgs, fileInfo);
        }

        //print2DArr(roomArray); //testing
    }

    //CALLED IN CONSTRUCTOR
    //setting roomArray:
    private String[][] getRoom(String filename) {
        String roomtext = readcsv(filename);
        //System.out.println(roomtext); //testing
        return stringToArr(roomtext);
    }
    private String readcsv(String filename) {
        String text = "";
        String pathname = "src/rooms/"+filename+".csv";

        try {
            Scanner sc = new Scanner(new File(pathname));
            while (sc.hasNext()) {
                String data = sc.nextLine();
                text = text + data + "\n";
            }
            sc.close();  //closes the scanner
        } catch (FileNotFoundException error) {
            //System.out.println("File not found: " + pathname); //testing
            exists=false;
        }
        return text;
    }

    //going through room array and instantiating everything in the room
    private void setup(Image[][] imgs, char[][] fileInfo) {

        Image wallImg = imgs[0][0];

        Image doorImg = imgs[0][1];
        Image finalDoorImg = imgs[0][2];

        Image guardImg = imgs[0][3];
        Image[] lookoutImgs = imgs[1];
        Image[] patrolImgs = imgs[2];

        Image[] itemsImgs = imgs[3];

        char[] weaknesses = fileInfo[0];
        char[] keys = fileInfo[1];

        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {

                String symbolString = roomArray[y][x];
                char symbol = symbolString.charAt(0);

                if (symbol == '*') /* then start or end */ {
                    if (symbolString.length()>1) {
                        if (symbolString.charAt(1)=='*') /* then ending door*/ {
                            Door temp = new Door(x, y, symbol, finalDoorImg);
                            temp.setKey(keys[36]);
                            doors.add(temp);

                            //System.out.println("Found end in room " + this.getName() + " at (" + temp.getX() + "," + temp.getY() + ")"); //testing
                        }
                    } else /*MC*/ {
                        mcX = x; mcY = y;
                    }

                } else if (symbol == '/') {
                    RoomObject temp = new RoomObject(x, y, wallImg);
                    bricks.add(temp);

                } else if (symbol == '%') /* then Guard */{
                    Enemy temp = new Enemy(x, y, weaknesses[0], guardImg);
                    guards.add(temp);

                } else if (symbol == '&') {
                    if (symbolString.length()>1) {
                        boolean n = true; //('n' stand for 'new')
                        for (Patrol patrolTemp: patrols) {
                            if (patrolTemp.getType()==symbolString.charAt(1)) /*then point B for Patrol*/ {
                                patrolTemp.setEndCoordinates(x,y);
                                n = false;
                            }
                        }
                        if (n) /* then point A for Patrol */{
                            Patrol temp = new Patrol(x, y, weaknesses[2], symbolString.charAt(1), patrolImgs); //takes an array of images
                            patrols.add(temp);
                        }
                    } else /* then Lookout */{
                        Lookout temp = new Lookout(x, y, weaknesses[1], lookoutImgs); //takes an array of images
                        lookouts.add(temp);
                    }
                } else if (symbol >= '0' && symbol <= '9') /* then Door */ {
                    Door temp = new Door(x, y, symbol, doorImg);
                    temp.setKey(keys[symbol-48]);
                    doors.add(temp);

                } else if (symbol >= 'A' && symbol <= 'Z') /* then Door */{
                    Door temp = new Door(x, y, symbol, doorImg);
                    temp.setKey(keys[symbol-55]);
                    doors.add(temp);

                } else if (symbol >= 'a' && symbol <= 'z') /* then Item */ {
                    Item temp = new Item(x, y, symbol, itemsImgs[symbol-97]);
                    //if (temp.getImg()!=null) {
                        items.add(temp);
                    //}
                }
            }
        }

        for (Patrol patrolTemp: patrols) {
            if (!patrolTemp.beenSet()) {
                patrolTemp.setEndCoordinates();
            }
        }

    }

    // GETTERS & SETTERS
    public boolean exists() {
        return exists;
    }

    //stats
    public boolean hasBeenVisted() {
        return visited;
    }
    public void justVisited() {
        visited = true;
    }

    //mc related
    public int getMCX() {
        return mcX;
    }
    public int getMCY() {
        return mcY;
    }
    public boolean isStartRoom() {
        return (mcX >= 0 && mcY >= 0);
    }

    //room info
    public char getName(){
        return name;
    }
    public String[][] getRoomArray() {
        return roomArray;
    }

    //enemy arraylists
    public ArrayList<Enemy> getGuards() {
        return guards;
    }
    public ArrayList<Lookout> getLookouts() {
        return lookouts;
    }
    public ArrayList<Patrol> getPatrols() {
        return patrols;
    }
    public ArrayList<Enemy> getEnemies() {
        ArrayList<Enemy> returnlist = new ArrayList<>();
        returnlist.addAll(guards);
        returnlist.addAll(lookouts);
        returnlist.addAll(patrols);

        return returnlist;
    }
    public void removeEnemy(Enemy e) {
        for (Patrol p: patrols) {
            if (e.getX() == p.getX() && e.getY() == p.getY()) {
                patrols.remove(p);
                break;
            }
        }

        for (Lookout l: lookouts) {
            if (e.getX() == l.getX() && e.getY() == l.getY()) {
                lookouts.remove(l);
                break;
            }
        }

        for (Enemy g: guards) {
            if (e.getX() == g.getX() && e.getY() == g.getY()) {
                guards.remove(g);
                break;
            }
        }

        //System.out.println("Enemy died");
    }

    //item arraylist
    public ArrayList<Item> getItems() {
        return items;
    }
    public void removeItem(int i) {
        items.remove(i);
    }
    public void addItem(Item i) {
        items.add(i);
    }

    //brick arraylist
    public ArrayList<RoomObject> getBricks() {
        return bricks;
    }

    //door arraylist
    public ArrayList<Door> getDoors() {
        return doors;
    }

    public void setDoors(ArrayList<Room> roomsList) {
        ArrayList<Door> doorsCopy = (ArrayList<Door>) doors.clone();

        for (Door d: doorsCopy) {
            d.setRoom(roomsList);
            if (d.getRoom()==null) {
                doors.remove(d);
            }

        }
    }

    /*

    //Could write a method to check if a key that is present in key.txt actually exist in any room in the game.
    //If it doesn't, then delete the key from keys array, and replace with ' '.

    public void cleanKeys() {
        ArrayList<Door> doorsCopy = (ArrayList<Door>) doors.clone();

        //int count = 0;
        for (Door d: doorsCopy) {
            d.setRoom(roomsList);
            if (d.getRoom()==null) {
                doors.remove(d);
            }
            //count++;
        }
    }

    */

    //array handling
    public String[][] stringToArr(String txt) {
        String[][] arrFin = new String[rows][columns];
        String[] rowarr = txt.split("\n"); //splits text into array, where each item in array is 1 row

        //use min() otherwise throws an array out of bounds exception
        for (int i=0; i<min(arrFin.length, rowarr.length); i++) {
            String[] colarr = rowarr[i].split(",");
            for (int j = 0; j < min(arrFin[i].length, colarr.length); j++) {
                arrFin[i][j] = colarr[j];
            }
        }

        clean2DArr(arrFin);
        return arrFin;
    }

    public static void clean2DArr(String[][] arr){
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] == null || arr[i][j] == ""){
                    arr[i][j] = " ";
                }
            }
        }
    }



    //testing purposes
    public void  print2DArr(String[][] arr){
        System.out.println("[ ");
        for (int i=0; i<arr.length; i++) {
            System.out.print("row" + i +": ");
            for (int j=0; j<arr[0].length; j++){
                System.out.print(arr[i][j]);
                System.out.print("-");
            }
            System.out.println();
        }
        System.out.println("]");
    }



}
