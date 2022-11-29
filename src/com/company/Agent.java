package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Agent extends Thread implements Runnable{
    private Position currentPos;
    private Position start;
    private Position target;
    private String imageFile;
    private Color color;

    private Grid grid;

    public Agent(Position start, Position target, Grid grid) {
        this.setStart(start);
        this.setCurrentPos(start);
        this.setTarget(target);

        this.setRandomColor();

        this.setGrid(grid);
    }

    public Agent(Position start, Position target, String imageFile) {
        this.imageFile = imageFile;
        this.setCurrentPos(start);
        this.setStart(start);
        this.setTarget(target);
    }

    @Override
    public void run() {
        List<Position> path = this.calculateMinPath();
        if(!path.isEmpty()){
            Direction d = getDirectionFromPosition(this.getCurrentPos(),path.get(0));
            System.out.println(d);
            this.move(d);
        }
    }

    public Agent move(Direction d){
        Position newPos = this.getCurrentPos();

        switch (d){
            case UP:
                newPos = new Position(this.getCurrentPos().getX(), this.getCurrentPos().getY() - 1);
                break;
            case RIGHT:
                newPos = new Position(this.getCurrentPos().getX() + 1, this.getCurrentPos().getY());
                break;
            case DOWN:
                newPos = new Position(this.getCurrentPos().getX(), this.getCurrentPos().getY() + 1);
                break;
            case LEFT:
                newPos = new Position(this.getCurrentPos().getX() - 1, this.getCurrentPos().getY());
                break;
        }

        if(this.moveAllowed(newPos)){
            this.setCurrentPos(newPos);
        }

        return this;
    }

    public boolean moveAllowed(Position newPos){

        boolean moveAllowed = true;

        if(newPos.getX() >= 0 && newPos.getX() < Grid.gridSize && newPos.getY() >= 0 && newPos.getY() < Grid.gridSize){
            for (Agent a: this.grid.getAgents()) {
                if(newPos.equals(a.getCurrentPos())){
                    moveAllowed = false;
                }
            }
        }else{
            moveAllowed = false;
        }

        return moveAllowed;
    }

    public ArrayList<Position> calculateMinPath(){

        Position nextPos = this.getCurrentPos();

        ArrayList<Position> path = new ArrayList<>();
        int minDist = Integer.MAX_VALUE;

        while(!nextPos.equals(this.getTarget())){
            List<Position> neighbors = this.findNeighborsFromPos(nextPos);
            for (Position neighbor: neighbors) {
                int newDist = calculateDistance(neighbor, this.getTarget());

                if(newDist < minDist){
                    minDist = newDist;
                    nextPos = neighbor;
                }
            }

            //Une fois le meilleur voisin choisi on l'ajoute au chemin
            path.add(nextPos);

        }

        return path;
    }

    public int calculateDistance(Position posA, Position posB){
        return (Math.abs(posA.getX() - posB.getX()) + Math.abs(posA.getY() - posB.getY()));
    }

    public List<Position> findNeighbors(){
        List<Position> neighbors = new ArrayList<>();

        neighbors.add(new Position(this.getCurrentPos().getX(), this.getCurrentPos().getY() - 1)); //UP
        neighbors.add(new Position(this.getCurrentPos().getX() + 1, this.getCurrentPos().getY())); //RIGHT
        neighbors.add(new Position(this.getCurrentPos().getX(), this.getCurrentPos().getY() + 1)); //DOWN
        neighbors.add(new Position(this.getCurrentPos().getX() - 1, this.getCurrentPos().getY())); //LEFT

        return neighbors
                .stream()
                .filter(this::moveAllowed)
                .collect(Collectors.toList());
    }

    public List<Position> findNeighborsFromPos(Position p){
        List<Position> neighbors = new ArrayList<>();

        neighbors.add(new Position(p.getX(), p.getY() - 1)); //UP
        neighbors.add(new Position(p.getX() + 1, p.getY())); //RIGHT
        neighbors.add(new Position(p.getX(), p.getY() + 1)); //DOWN
        neighbors.add(new Position(p.getX() - 1, p.getY())); //LEFT

        return neighbors
                .stream()
                .filter(this::moveAllowed)
                .collect(Collectors.toList());
    }

    public Direction getDirectionFromPosition(Position start, Position target){
        if(start.getX() < target.getX()){
            return Direction.RIGHT;
        }else if(start.getX() > target.getX()){
            return Direction.LEFT;
        }else if(start.getY() < target.getY()){
            return Direction.DOWN;
        }else if(start.getY() > target.getY()){
            return Direction.UP;
        }
        //Sinon direction aleatoire
        return Direction.values()[(int)(Math.random()*4)];
    }

    public Position getStart() {
        return start;
    }

    public void setStart(Position start) {
        this.start = start;
    }

    public Position getTarget() {
        return target;
    }

    public void setTarget(Position target) {
        this.target = target;

    }

    public String getImageFile() {
        return imageFile;
    }

    public Position getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(Position currentPos) {
        this.currentPos = currentPos;
    }

    public Color getColor() {
        return color;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public void setRandomColor(){
        Random rand = new Random();

        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();

        this.color = new Color(r, g, b);
    }
}
