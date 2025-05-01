package fr.beyondtime.model.entities;

public class Monster extends Entity {
    private double x, y;
    private double speed;
    private boolean alive;

    public Monster(String name, int health, double x, double y, double speed) {
        super(health, name); // appel au constructeur de Entity
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.alive = true;
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isAlive() { return alive; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void kill() { this.alive = false; }

    public void move() {
        this.x -= speed;
    }
}
