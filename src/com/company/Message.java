package com.company;

public class Message {
    private Agent sender;
    private Position destination;

    public Message(Agent sender, Position destination) {
        this.sender = sender;
        this.destination = destination;
    }

    public Agent getSender() {
        return sender;
    }

    public void setSender(Agent sender) {
        this.sender = sender;
    }

    public Position getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender=" + sender +
                ", destination=" + destination +
                '}';
    }

    public void send(Agent receiver){
        receiver.addMessage(this);
    }

}
