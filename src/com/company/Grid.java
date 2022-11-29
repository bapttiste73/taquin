package com.company;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Grid {

    public static final int gridSize = 5;

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
            Position start = agentsPositions.remove(nbAgent);
            Position target = agentsPositions.remove(nbAgent);

            Agent a = new Agent(start, target, this);
            this.addAgent(a);
        }
    }


    public void addAgent(Agent agent){
        this.agents.add(agent);
    }

    public ArrayList<Agent> getAgents() {
        return agents;
    }

    public boolean positionIsAvailable(Position p){
        return false;
    }

    public void launch(){
        for (Agent a: this.getAgents()) {
            a.calculateMinPath();
            a.run();
        }
    }
}
