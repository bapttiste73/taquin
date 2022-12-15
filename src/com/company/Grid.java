package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Grid {

    public static final int gridSize = 4;

    ArrayList<Agent> agents = new ArrayList<>();
    HashMap<Integer, Position> cases = new HashMap<>();

    public Grid(){
        // Initialisation de la grid
        int nbCell = 0;
        for (int y = 0; y < gridSize; y++) {
            for (int x = 0; x < gridSize; x++) {
                    this.cases.put(nbCell,new Position(x,y));
                    nbCell++;
            }
        }
    }

    public void generateRandomAgents(int nbAgent){
        List<Position> agentsPositions = new ArrayList<Position>(cases.values());
        Collections.shuffle(agentsPositions);
        List<Position> targetPositions = new ArrayList<Position>(cases.values());
        Collections.shuffle(targetPositions);

        for (int i = 0; i < nbAgent; i++) {
            Position start = agentsPositions.remove(agentsPositions.size() - 1);
            Position target = targetPositions.remove(targetPositions.size() - 1);

            Agent a = new Agent(start, target, this);
            this.addAgent(a);


        }
//        Position{x=3, y=3}
//        Position{x=3, y=0}
//        Position{x=4, y=4}
//        --------------
//                Position{x=3, y=4}
//        Position{x=2, y=4}
//        Position{x=3, y=0}

//        Agent a = new Agent(new Position(0,0), new Position(3,0), this);
//        this.addAgent(a);
//
//        Agent b = new Agent(new Position(3,0), new Position(1,0), this);
//        this.addAgent(b);

//        Agent c = new Agent(new Position(4,4), new Position(3,0), this);
//        this.addAgent(c);
    }


    public void addAgent(Agent agent){
        this.agents.add(agent);
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public boolean positionIsAvailable(Position p){
        boolean available = true;
        for(Agent a : this.getAgents()){
            if(p.equals(a.getCurrentPos())){
                available = false;
            }
        }
        return available;
    }

    public void launch(){
        for (Agent a: this.getAgents()) {
            a.calculateMinPath();
            a.run();
        }
    }
}
