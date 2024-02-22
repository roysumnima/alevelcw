package com.company;

import java.awt.AlphaComposite;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.*;
import java.awt.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Main extends JFrame{

    private boolean error;

    //key tracking
    private static Boolean lkd = false;
    private static Boolean rkd = false;
    private static Boolean ukd = false;
    private static Boolean dkd = false;
    private static Boolean skd = false;
    private static Boolean ekd = false;
    private static Boolean ikd = false;
    private static Boolean qkd = false;
    private static Boolean hkd = false;
    //testing
    private static Boolean gkd = false;

    //timer delay
    private final int del = 150;

    //canvas
    private final Canvas canvas = new Canvas();

    //number of squares:
    private static final int columns = 16;
    private static final int rows = 12;

    //size of squares
    private final int size = 60;

    //main character
    private MC mc;

    //declaring images
    private Image bgImg;
    private Image[] mcImgs;
    private Image endImg;

    private Image[][] roomImgs;
    private final char[][] fileInfo;

    //list of rooms
    private final ArrayList<Room> rooms = new ArrayList<>();

    //statistics
    private int totEnemyNo;
    private int totItemNo;
    /*

    //Could store no. of each type in whole game, and give more specific stats at end

    int[] allEnemies = new int[3]; // 0: Guard, 1: Lookout, 2: Patrol
    int[] allItems = new int[26]; // 0: a, [...] 25: z

    */

    //To display message on the GUI
    ArrayList<String> message;
    ArrayList<String> instructions;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        int xdim = size*columns;
        int ydim = size*rows;
        this.setSize(xdim, ydim+25);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Game");

        error = false;
        message = new ArrayList<>();
        instructions = new ArrayList<>();

        //uploading various files
        uploadImages();
        char[] weaknesses = new char[3];
        uploadFileInfo( "weaknesses", weaknesses);
        char[] keys = new char[37];
        uploadFileInfo( "keys", keys);
        fileInfo = new char[][] {weaknesses,keys};
        uploadFileInfo("instructions", instructions);

        setup();

        this.add(canvas);
        this.setVisible(true);

        //key listener
        KeyLis listener = new KeyLis();
        this.addKeyListener(listener);

        //timer
        Timer t = new Timer(del, e -> {
            step();
            canvas.repaint();
        });
        t.start();

    }

    public void uploadImages() {
        //uploading background
        bgImg = uploadImgFile("bg.png");

        //uploading mc's images
        mcImgs = new Image[4];
        mcImgs[0] = uploadImgFile("MC/down.png");
        mcImgs[1] = uploadImgFile("MC/left.png");
        mcImgs[2] = uploadImgFile("MC/right.png", mcImgs[0]);
        mcImgs[3] = uploadImgFile("MC/up.png", mcImgs[0]);

        endImg = uploadImgFile("end.png");

        Image[] statImgs = new Image[4]; //stationary images
        /* wallImg */ statImgs[0] = uploadImgFile("wall.png");
        /* doorImg */ statImgs[1] = uploadImgFile("door.png");
        /* finalDoorImg */ statImgs[2] = uploadImgFile("enddoor.png", statImgs[1]);
        /* guardImg */ statImgs[3] = uploadImgFile("guard/guard.png");

        Image[] lookoutImgs = new Image[4];
        lookoutImgs[0] = uploadImgFile("lookout/down.png");
        lookoutImgs[1] = uploadImgFile("lookout/left.png",lookoutImgs[0]);
        lookoutImgs[2] = uploadImgFile("lookout/right.png",lookoutImgs[0]);
        lookoutImgs[3] = uploadImgFile("lookout/up.png",lookoutImgs[0]);

        Image[] patrolImgs = new Image[4];
        patrolImgs[0] = uploadImgFile("patrol/down.png");
        patrolImgs[1] = uploadImgFile("patrol/left.png", patrolImgs[0]);
        patrolImgs[2] = uploadImgFile("patrol/right.png", patrolImgs[0]);
        patrolImgs[3] = uploadImgFile("patrol/up.png", patrolImgs[0]);

        //uploading items images
        Image[] itemsImgs = new Image[26];
        char filename = 'a';
        for (int i=0; i<26; i++) {
            //System.out.print(filename + ", ");
            itemsImgs[i] = uploadImgFile("items/" + filename + ".png", itemsImgs[0]);
            filename++;
        }

        roomImgs = new Image[][] {statImgs, lookoutImgs, patrolImgs, itemsImgs};

    }

    //Image file handling
    public Image uploadImgFile(String path) {
        String pathname = "src/images/" + path;
        if (new File(pathname).exists()) {
            ImageIcon tempFile = new ImageIcon(pathname);
            return tempFile.getImage();
        } else {
            return null;
        }

    }
    //Overload
    public Image uploadImgFile(String path, Image defaultImg) {
        String pathname = "src/images/" + path;
        if (new File(pathname).exists()) {
            ImageIcon tempFile = new ImageIcon(pathname);
            return tempFile.getImage();
        } else {
            return defaultImg;
        }
    }

    //Text file handling
    public void uploadFileInfo(String filename, char[] arr ) {
        String pathname = "src/"+filename+".txt";
        //System.out.println(pathname); //testing

        try {
            Scanner sc = new Scanner(new File(pathname));
            int count = 0;
            while (sc.hasNext() && count<arr.length) {
                String data = sc.nextLine();
                if (data.length()>0){
                    arr[count] = data.charAt(0);
                } else {
                    arr[count] = ' ';
                }
                //System.out.println(arr[count]); //testing
                count++;
            }
            sc.close();
        } catch (FileNotFoundException exception) {
            System.out.println("error");
            Arrays.fill(arr, ' ');
        }

    }
    //Overload
    public void uploadFileInfo(String filename, ArrayList<String> arr ) {
        String pathname = "src/"+filename+".txt";

        try {
            //System.out.print("Found file:" + pathname);
            Scanner sc = new Scanner(new File(pathname));
            //int count = 0;
            while (sc.hasNext()) {
                String data = sc.nextLine();
                arr.add(data);
                /*System.out.println(arr.get(count)); //testing
                count++;*/
            }
            sc.close();
        } catch (FileNotFoundException exception) {
            arr.clear();
            arr.add(" ");
        }

    }

    public void setup() {

        /*

        //Could use something like the following to only upload rooms that exist?

        final File folder = new File("src/rooms");
        String[] roomsList = folder.list(); //lists all the rooms in a folder

        for (String h: roomsList) {
            System.out.println(h);
        }

        */

        //instantiates all rooms
        Room roomTemp;
        for (int i=0; i<10; i++) {
            roomTemp = new Room((char) (i+48), rows, columns, roomImgs, fileInfo);
            //System.out.println(roomTemp.getName()); //testing
            if (roomTemp.exists()) {
                totEnemyNo += roomTemp.getEnemies().size();
                totItemNo += roomTemp.getItems().size();
                rooms.add(roomTemp);
            }

        }
        for (int i=0; i<26; i++) {
            roomTemp = new Room((char) (i+65), rows, columns, roomImgs, fileInfo);
            //System.out.println(roomTemp.getName()); //testing
            if (roomTemp.exists()) {
                totEnemyNo += roomTemp.getEnemies().size();
                totItemNo += roomTemp.getItems().size();
                rooms.add(roomTemp);
            }
        }

        //checks for 0 rooms:
        if (rooms.size()==0) {
            message.clear();
            message.add("Error: No rooms!");
            message.add("Please make a room. It's filename should be as specified in the rooms/instructions.txt");
            error = true;
        } else { //checks for room MC starts in
            boolean startRoomExists = false;

            for (Room r : rooms) {
                if (r.isStartRoom()) {
                    startRoomExists = true;
                    mc = new MC(r, r.getMCX(), r.getMCY(), mcImgs);
                    r.justVisited();
                    break;
                }
            }

            if (!startRoomExists) {
                message.clear();
                message.add("Error: No start room.");
                message.add("Please make sure at least one room file has a '*' in it, to indicate this is where the game starts.");
                error = true;
            }
        }

        //now that all rooms are instantiated, we can properly set the leadsTo attribute for the doors:
        for (Room r: rooms) {
            r.setDoors(rooms);
        }

    }

    public void reset() {
        System.out.println("Game reset.");
        rooms.clear();
        this.setup();
    }

    //GUI
    class Canvas extends JComponent {

        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(0, 0, 0, 1f));

            if (error) {
                displayText(g2, message, 20, 30);
            } else if (hkd) {
                displayText(g2, instructions, 20, 30);
            } else {
                if (mc.getStatus() == 1) /* display success screen */ {
                    g2.drawImage(endImg, 0, 0, size * columns, size * rows, this);
                    displayText(g2, message, size*6 , size*3);
                } else /* display game */ {
                    displayRoom(g2);
                    if (ikd) /* display inventory */ {
                        showInventory(g2);
                    }
                }
            }

        }

    }

    public void displayText (Graphics2D g2, ArrayList<String> txt, int x, int y) {
        for (int i=0; i<txt.size(); i++) {
            g2.drawString(txt.get(i), x, y+(g2.getFontMetrics().getHeight()*2*i));
        }
    }

    public void displayRoom(Graphics2D g2) {
        //draws background
        for (int x = 0; x<columns; x++) {
            for (int y = 0; y<rows; y++) {
                g2.drawImage(bgImg, x * size, y * size, size, size, this);
            }
        }

        //draws objects in room
        displayRoomObj(g2, mc.getCurrentRoom().getBricks());
        displayRoomObj(g2, mc.getCurrentRoom().getDoors());
        displayRoomObj(g2, mc.getCurrentRoom().getItems());

        displayRoomObj(g2, mc.getCurrentRoom().getGuards());
        displayRoomObj(g2, mc.getCurrentRoom().getPatrols());
        displayRoomObj(g2, mc.getCurrentRoom().getLookouts());

        //draws mc
        if (mc.getImg() != null) {
            g2.drawImage(mc.getImg(), mc.getX() * size, mc.getY() * size, size, size, this);
        } else {
            g2.setColor(Color.black);
            g2.fillRect(mc.getX() * size, mc.getY() * size, size, size);
        }
    }

    public <O extends RoomObject> void displayRoomObj(Graphics2D g2, ArrayList<O> roomObjects){
        AlphaComposite ac;

        for (O roomObject : roomObjects) {
            if (roomObject.isVisible()) {

                if (roomObject.isActive()) {
                    ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
                } else {
                    ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
                }

                g2.setComposite(ac);

                if (roomObject.getImg() != null) {
                    g2.drawImage(roomObject.getImg(), roomObject.getX() * size, roomObject.getY() * size, size, size, this);
                } else {
                    g2.setColor(roomObject.getColor());
                    g2.fillRect(roomObject.getX() * size, roomObject.getY() * size, size, size);
                }

            }
        }

        ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);
        g2.setComposite(ac); //reset it to full opacity so that other objects don't get affected
    }

    public void showInventory(Graphics g2) {
        //darkens the screen:
        g2.setColor(new Color(0, 0, 0, 0.5f));
        g2.fillRect(0, 0, columns*size, rows*size);

        //draws a background for inventory
        for (int y=2; y<rows-2; y++) {
            for (int x=4; x<columns-4; x++) {
                g2.drawImage(bgImg, x * size, y * size, size, size, this);
            }
        }

        //draws items onto inventory space, last item collected is first to be shown
        int i = mc.getSize()-1;
        for (int y=2; y<rows-2; y++) {
            for (int x=4; x<columns-4; x++) {
                if (i>=0) {
                    g2.drawImage(mc.getItem(i).getImg(), x * size, y * size, size, size, this);
                    i--;
                } else {
                    break;
                }
            }
        }
    }

    //key handling
    private static class KeyLis extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> lkd = true;
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> rkd = true;
                case KeyEvent.VK_UP, KeyEvent.VK_W -> ukd = true;
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> dkd = true;

                case KeyEvent.VK_SPACE -> skd = true;
                case KeyEvent.VK_ENTER -> ekd = true;

                case KeyEvent.VK_I -> ikd = true;
                case KeyEvent.VK_Q -> qkd = true;
                case KeyEvent.VK_H -> hkd = true;
                case KeyEvent.VK_G -> gkd = true;
            }

        }

        public void keyReleased(KeyEvent e){
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> lkd = false;
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> rkd = false;
                case KeyEvent.VK_UP, KeyEvent.VK_W -> ukd = false;
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> dkd = false;

                case KeyEvent.VK_SPACE -> skd = false;
                case KeyEvent.VK_ENTER -> ekd = false;

                case KeyEvent.VK_I -> ikd = false;
                case KeyEvent.VK_Q -> qkd = false;
                case KeyEvent.VK_H -> hkd = false;
                case KeyEvent.VK_G -> gkd = false;
            }
        }
    }

    //every step of the timer:
    public void step() {

        if (qkd) /* then quit */ {
            System.exit(0);
        }

        if (!error & !hkd) {
            if (mc.getStatus()==1) /* then user won*/ {
                setStats();
                endScreen();
            } else /* user still plays */ {
                if (!ikd) {
                    playGame();
                }
            }
        }

    }

    //ending
    public void setStats() {
        int roomsVisited = 0;
        for (Room r: rooms) {
            if (r.hasBeenVisted()) {
                roomsVisited++;
            }
        }

        String text;

        message.clear();
        text = "You collected: " + mc.getSize() + "/" + totItemNo + " items \n";
        message.add(text);
        text = "You defeated: " + mc.getEnemiesKilled() + "/" + totEnemyNo + " enemies";
        message.add(text);
        text = "You explored: " + roomsVisited + "/" + rooms.size() + " rooms";
        message.add(text);
        text = "";
        message.add(text);
        text = "Press G to play again!";
        message.add(text);
    }

    public void endScreen() {
        if (gkd) {
            reset();
        }
    }

    //playing game
    public void playGame() {
        //System.out.println(mc.getStatus()); //testing

        //set opacity by setting 'active' attribute
        for (Door d: mc.getCurrentRoom().getDoors()) {
            d.setActive(mc.hasItem(d.getKey()) || d.getKey()==' ');
        }

        for (Enemy e: mc.getCurrentRoom().getEnemies()) {
            //note that this is the inverse of how door is set
            e.setActive(!mc.hasItem(e.getWeakness()));
        }

        //move Lookout
        for (Lookout roomLookout : mc.getCurrentRoom().getLookouts()) {
            roomLookout.lookTowards(mc.getX(), mc.getY());
            //collison detection:
            if (mc.getCurrentRoom().getRoomArray()[roomLookout.nextY()][roomLookout.nextX()].charAt(0) != '/') {
                roomLookout.move();
            }
        }

        //move Patrol
        for (Patrol roomPatrol : mc.getCurrentRoom().getPatrols()) {
            roomPatrol.lookTowards();
            //collison detection:
            if (mc.getCurrentRoom().getRoomArray()[roomPatrol.nextY()][roomPatrol.nextX()].charAt(0) != '/') {
                roomPatrol.move();
            } else {
                roomPatrol.setEndCoordinates(roomPatrol.getX(), roomPatrol.getY());
                roomPatrol.move();
            }
        }

        //keyboard input to move main character
        if (lkd) {
            if (mc.getX()!=0) /*else out of bounds*/ {
                if (mc.getCurrentRoom().getRoomArray()[mc.getY()][(mc.getX() - 1)].charAt(0) != '/') /*else collision*/{
                    mc.moveLeft();
                }
            }
        }

        if (rkd) {
            if (mc.getX()!=columns-1) /*else out of bounds*/ {
                if (mc.getCurrentRoom().getRoomArray()[mc.getY()][(mc.getX() + 1)].charAt(0) != '/') /*else collision*/{
                    mc.moveRight();
                }
            }
        }

        if (ukd) {
            if (mc.getY()!=0) /*else out of bounds*/ {
                if (mc.getCurrentRoom().getRoomArray()[(mc.getY() - 1)][mc.getX()].charAt(0) != '/') /*else collision*/{
                    mc.moveUp();
                }
            }

        }

        if (dkd) {
            if (mc.getY()!=rows-1) /*else out of bounds*/ {
                if (mc.getCurrentRoom().getRoomArray()[(mc.getY() + 1)][mc.getX()].charAt(0) != '/') /*else collision*/ {
                    mc.moveDown();
                }
            }
        }

        if (!(lkd||ukd||rkd||dkd)) {
            mc.isStill();
        }

        //interacting with Enemy
        mc.enemyInteract();


        //interacting with item
        if (skd) {
            mc.itemInteract();
        }

        //interacting with door
        if (ekd) {
            mc.doorInteract();
        }

        //checking if mc died
        if (mc.getStatus() == -1) {
            reset();
        }
    }

}