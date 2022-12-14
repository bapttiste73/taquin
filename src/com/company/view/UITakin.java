package com.company.view;

import com.company.Agent;
import com.company.Grid;
import processing.core.PApplet;


public class UITakin extends PApplet {

    /** 🛠 Paramètres 🛠 */
    final int cellSize = 100;
    final int nbCell = 5;
    static final int nbAgent = 16;
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
        frameRate(100);
        for (Agent a : grid.getAgents()) {
            Thread t = new Thread(a);
            t.start();
        }
    }

    public void background(){
        for (int i = 0; i < cols; i++) {
            for (int j = 0; j < rows; j++) {

                int x = i * cellSize;
                int y = j * cellSize;
                fill(255);
                stroke(0);

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

        if(!a.getMessages().isEmpty()){
            fill(a.getColor().getRGB());
            rect(a.getCurrentPos().getX()*cellSize, a.getCurrentPos().getY()*cellSize, cellSize/3, cellSize/3);
        }
    }
}
