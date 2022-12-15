package com.company;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Agent extends Thread implements Runnable{

    private Position currentPos;
    private Position start;
    private final Position target;
    private Color color;
    private ArrayList<Message> messages;
    private Grid grid;
    private boolean isWaiting = false; //True si il a envoyer un message et qu'il attend

    public Agent(Position start, Position target, Grid grid) {
        this.setStart(start);
        this.setCurrentPos(start);
        this.target = target;
        this.setRandomColor();
        this.setGrid(grid);

        this.messages = new ArrayList<Message>();
    }

    @Override
    public void run() {

        List<Position> path = this.calculateMinPath();

        if(!path.isEmpty()){ //Si on est déjà au bon endroit
            try {
                this.move(this.getDirectionFromPosition(this.getCurrentPos(),path.get(0)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(!this.getMessages().isEmpty()){
            Message m = this.getMessages().get(0);
            while (this.getCurrentPos().equals(m.getDestination())){
                this.move(this.randomMoveFromPosition(this.getCurrentPos()));
            }
            this.getMessages().remove(this.getMessages().size()-1);

            try {
                sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            };
        }
    }

    public void move(Direction d){
        Position newPosition = this.getPositionFromDirection(d);
        Agent blockingAgent = this.moveBlockedByAgent(newPosition);

        if (blockingAgent == null) { //Si l'agent n'est pas bloqué par un autre alors il se déplace normalement
            this.setCurrentPos(newPosition);
        } else { // Si il est bloqué
            Message m = new Message(this, newPosition);
            blockingAgent.receive(m); // Il envoie un message à l'agent qui le bloque
        }

    }

    public boolean moveAllowed(Position p){

        boolean moveAllowed = true;

        if(p.getX() >= 0 && p.getX() < Grid.gridSize && p.getY() >= 0 && p.getY() < Grid.gridSize){
            for (Agent a: this.grid.getAgents()) {
                if(p.equals(a.getCurrentPos())){
                    moveAllowed = false;
                }
            }
        }else{
            moveAllowed = false;
        }

        return moveAllowed;
    }

    public boolean moveAllowedInGrid(Position p){
        return p.getX() >= 0 && p.getX() < Grid.gridSize && p.getY() >= 0 && p.getY() < Grid.gridSize;
    }

    public Agent moveBlockedByAgent(Position p){
        for (Agent a: this.grid.getAgents()) {
            if(p.equals(a.getCurrentPos())){
                return a;
            }
        }
        return null;
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

    public Direction getDirectionFromPosition(Position start, Position target) throws Exception {
        if(start.getX() < target.getX()){
            return Direction.RIGHT;
        }else if(start.getX() > target.getX()){
            return Direction.LEFT;
        }else if(start.getY() < target.getY()){
            return Direction.DOWN;
        }else if(start.getY() > target.getY()){
            return Direction.UP;
        }else{
            throw new Exception("Direction Introuvable");
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
        }
        return p;
    }

    public Direction randomMoveFromPosition(Position p){
        // @Todo Essayez de lui faire choisir la meilleure position pour se décaler plutot que de l'Aleatoire
        // Récupérer les valeurs de l'énumération
        Direction[] directions = Direction.values();

        // Créer un générateur de nombres aléatoires
        Random random = new Random();

        // Créer un set pour stocker les valeurs de l'énumération déjà testées
        Set<Direction> tested = new HashSet<>();

        // Répéter l'opération jusqu'à ce que toutes les valeurs de l'énumération aient été testées
        while (tested.size() < directions.length) {
            // Générer un nombre aléatoire entre 0 et la taille de l'énumération
            int index = random.nextInt(directions.length);

            // Récupérer la valeur de l'énumération à l'index généré
            Direction direction = directions[index];

            // Vérifier que la valeur n'a pas déjà été testée
            if (!tested.contains(direction)) {
                // Ajouter la valeur au set des valeurs testées
                tested.add(direction);

                if(this.moveAllowedInGrid(this.getPositionFromDirection(direction))){
                    return direction;
                }
            }
        }
        return null;
    }

    public void receive(Message m ){
        this.addMessage(m);

        //Je libère ma place
        this.move(this.randomMoveFromPosition(this.getCurrentPos()));
        this.getMessages().get(0).getSender().setWaiting(false);
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

    private boolean isWaiting() {
        return isWaiting;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "currentPos=" + currentPos +
                '}';
    }
}
