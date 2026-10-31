package ru.aston.timoshenko.aleksey.project4.livelock;

public class Printer {
    private Worker owner;


    public Worker getOwner() {
        return owner;
    }

    public synchronized void setOwner(Worker owner) {
        this.owner = owner;
    }

    public void print(String message) {
        System.out.println(message);
    }
}
