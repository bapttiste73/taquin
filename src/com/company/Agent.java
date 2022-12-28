package com.company;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class Agent extends Thread implements Runnable{

    private Position currentPos;
    private Position start;
    private final Position target;
    private Color color;
    private ArrayList<Message> messages;
    private Grid grid;
    private boolean isWaiting = false; //True si il a envoyer un message et qu'il attend
    private ReentrantLock lock;


    public Agent(Position start, Position target, Grid grid) {
        this.setStart(start);
        this.setCurrentPos(start);
        this.target = target;
        this.setRandomColor();
        this.setGrid(grid);

        this.lock = new ReentrantLock();
        this.messages = new ArrayList<Message>();
    }

    @Override
    public void run() {

        List<Position> path = this.calculateMinPath();
        if(this.getGrid().isOver()){
            return;
        }
        if(this.getMessages().isEmpty()){
            this.setWaiting(false);
        }

        if(!this.getMessages().isEmpty()){
            Message firstMessage = this.getMessages().get(this.getMessages().size()-1);
            System.out.println(firstMessage);
            this.handleMessage(firstMessage);
            this.setWaiting(true);
        }else if(!path.isEmpty()) {
            Position newPosition = path.get(0);
            Agent blockingAgent = this.moveBlockedByAgent(newPosition);

            if(blockingAgent != null){
                Message m = new Message(this, newPosition);
                m.send(blockingAgent); // On envoie le message à l'agent qui bloque
            }else{
                this.move(this.getDirectionFromPosition(this.getCurrentPos(), newPosition));
            }
        }
    }

    public boolean move(Direction d){

        Position newPosition = this.getPositionFromDirection(d);
        boolean moved = false;

        if(this.moveAllowed(newPosition)){
            this.setCurrentPos(newPosition);
            moved = true;
        } else {
            //Si il est bloqué par un agent
            System.out.println("erreur Move interdit");
            System.out.println(this.getCurrentPos());
            System.out.println(newPosition);
            System.out.println("---");
        }
        tempo();
        return moved;


    }

    public boolean moveAllowed(Position p){

        if(this.moveAllowedInGrid(p) && this.moveBlockedByAgent(p) == null){
            return true;
        }else{
            return false;
        }
    }

    public boolean moveAllowedInGrid(Position p){
        return p.getX() >= 0 && p.getX() < Grid.gridSize && p.getY() >= 0 && p.getY() < Grid.gridSize;
    }

    public Agent moveBlockedByAgent(Position p){
//        lock.lock();
        try {
            for (Agent a: this.grid.getAgents()) {
                if(p.equals(a.getCurrentPos())){
                    return a;
                }
            }
            return null;
        } finally {
//            lock.unlock();
        }
    }

    public ArrayList<Position> calculateMinPath(){

        Position nextPos = this.getCurrentPos();

        ArrayList<Position> path = new ArrayList<>();
        int minDist = Integer.MAX_VALUE;

        while(!nextPos.equals(this.getTarget())){
            List<Position> neighbors = this.findNeighbors(nextPos);
            for (Position neighbor: neighbors) {
                int newDist = calculateDistance(neighbor, this.getTarget());

                if(newDist < minDist || (newDist == minDist && Math.random() > 0.5)) {
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

    public List<Position> findNeighbors(Position p){
        List<Position> neighbors = new ArrayList<>();

        neighbors.add(new Position(p.getX(), p.getY() - 1)); //UP
        neighbors.add(new Position(p.getX() + 1, p.getY())); //RIGHT
        neighbors.add(new Position(p.getX(), p.getY() + 1)); //DOWN
        neighbors.add(new Position(p.getX() - 1, p.getY())); //LEFT

        return neighbors
                .stream()
                .filter(this::moveAllowedInGrid)
                .collect(Collectors.toList());
    }

    public Direction getDirectionFromPosition(Position start, Position target){
        if(start.getX() < target.getX()){
            return Direction.RIGHT;
        }else if(start.getX() > target.getX()){
            return Direction.LEFT;
        }else if(start.getY() < target.getY()){
            return Direction.DOWN;
        }else{
            return Direction.UP;
        }
    }

    public Position getPositionFromDirection(Direction d){
        Position p = new Position(this.getCurrentPos().getX(), this.getCurrentPos().getY());
        switch (d){
            case RIGHT:
                p.setX(p.getX()+1);
                break;
            case LEFT:
                p.setX(p.getX()-1);
                break;
            case DOWN:
                p.setY(p.getY()+1);
                break;
            case UP:
                p.setY(p.getY()-1);
                break;
            default:
                System.out.println("probl_me");
                break;
        }
        return p;
    }

    public void handleMessage(Message message){
        if(message != null) {

                Queue<Position> queue = new PriorityQueue<Position>(
                        3,
                        Comparator.comparingInt(p -> ((this.getGrid().positionIsAvailable(p) ? 25 : 50) - 10))
                );

            for (Position p : this.findNeighbors(this.getCurrentPos())) {
                Agent a = this.moveBlockedByAgent(p);
                if ((a == null) || !message.getSender().equals(a)) {
                    queue.add(p);
                }
            }

            boolean moved = false;
            while (!queue.isEmpty() && !moved) {
                Position current = queue.poll();
                boolean free = true;
                if (!this.getGrid().positionIsAvailable(current)) {
                    free = message.getSender().move(this.getDirectionFromPosition(message.getSender().getCurrentPos(),current));
                }
                moved = free && move(this.getDirectionFromPosition(this.getCurrentPos(), current));
            }

            if(moved){
                this.setWaiting(false);
                this.getMessages().remove(message);
                tempo();
            }
        }
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

    public Position getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(Position currentPos) {
        this.currentPos = currentPos;
    }

    public Color getColor() {
        return color;
    }

    public void setGrid(Grid grid) {
        this.grid = grid;
    }

    public Grid getGrid() {
        return grid;
    }

    public void setRandomColor(){
        Random rand = new Random();

        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();

        this.color = new Color(r, g, b);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void addMessage(Message m){
        this.messages.add(m);
    }

    public void setWaiting(boolean waiting) {
        isWaiting = waiting;
    }

    public boolean isWaiting() {
        return isWaiting;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "currentPos=" + currentPos +
                '}';
    }

    private void tempo() {
        long tps = 20 + (long)(Math.random() * 80);
        try {
            sleep(tps);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
