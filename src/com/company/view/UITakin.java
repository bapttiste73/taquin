package com.company.view;

import com.company.Agent;
import com.company.Grid;
import processing.core.PApplet;

import static java.lang.Thread.sleep;

public class UITakin extends PApplet {

    /** 🛠 Paramètres 🛠 */
    final int cellSize = 100;
    final int nbCell = 4;
    static final int nbAgent = 9;
    /** 🛠 Paramètres 🛠 */

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
        frameRate(60);
        for (Agent a : grid.getAgents()) {
            Thread t = new Thread(a);
            t.start();
        }
    }

    public void background(){
        for (int i = 0; i < cols; i++) {
            // Begin loop for rows
            for (int j = 0; j < rows; j++) {

                // Scaling up to draw a rectangle at (x,y)
                int x = i * cellSize;
                int y = j * cellSize;
                fill(255);
                stroke(0);

                // For every column and row, a rectangle is drawn at an (x,y) location scaled and sized by videoScale.
                rect(x, y, cellSize, cellSize);
            }
        }
        for(Agent a : grid.getAgents()){
            displayTarget(a);
        }
    }

    public void draw(){
        background();
        for (Agent a : grid.getAgents()){
            a.run();
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

    public void displayTarget(Agent a){
        fill(a.getColor().getRGB());
        rect(a.getTarget().getX()*cellSize, a.getTarget().getY()*cellSize, cellSize, cellSize);
    }

    public void displayAgent(Agent a){
        fill(a.getColor().getRGB());
        circle(a.getCurrentPos().getX()*cellSize + cellSize/2, a.getCurrentPos().getY()*cellSize + cellSize/2, cellSize*0.75f);
    }
}
