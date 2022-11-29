package com.company.view;

import com.company.Agent;
import com.company.Direction;
import com.company.Grid;
import com.company.Position;
import processing.core.PApplet;

public class UITakin extends PApplet {

    /** 🛠 Paramètres 🛠 */
    final int cellSize = 100;
    final int nbCell = 5;
    static final int nbAgent = 8;
    /** Paramètres */

    int cols, rows;
    static Grid grid;

    public UITakin(Grid grid) {
        UITakin.grid = grid;
    }

    public void settings(){
        size(nbCell*cellSize, nbCell*cellSize);

        cols = width/cellSize;
        rows = height/cellSize;
    }

    public void setup(){
        frameRate(1);
    }

    public void draw(){
        // On dessine la case
        for (int i = 0; i < cols; i++) {
            // Begin loop for rows
            for (int j = 0; j < rows; j++) {

                // Scaling up to draw a rectangle at (x,y)
                int x = i*cellSize;
                int y = j*cellSize;
                fill(255);
                stroke(0);
                // For every column and row, a rectangle is drawn at an (x,y) location scaled and sized by videoScale.
                rect(x, y, cellSize, cellSize);
            }
        }

        // Dessine les agents

        for (Agent a : grid.getAgents()){
            Thread t = new Thread(a);
            t.start();

            displayAgent(a);
        }
    }

    public static void main(String[] args){
        grid = new Grid();

        grid.generateRandomAgents(UITakin.nbAgent);

        String[] processingArgs = {"UITakin"};
        UITakin uiTakin = new UITakin(grid);
        PApplet.runSketch(processingArgs, uiTakin);
    }

    public void displayAgent(Agent a){
//        //Start
//        fill(Color.GREEN.getRGB());
//        rect(a.getStart().getX()*cellSize, a.getStart().getY()*cellSize, cellSize, cellSize);

        //Target
        fill(a.getColor().getRGB());
        rect(a.getTarget().getX()*cellSize, a.getTarget().getY()*cellSize, cellSize, cellSize);

//        if(a.getImageFile().isEmpty()){
//            PImage imgAgent = loadImage(a.getImageFile());
//            image(imgAgent, a.getCurrentPos().getX()*cellSize, a.getCurrentPos().getY()*cellSize);
//      }
        circle(a.getCurrentPos().getX()*cellSize + cellSize/2, a.getCurrentPos().getY()*cellSize + cellSize/2, cellSize);

    }
}
