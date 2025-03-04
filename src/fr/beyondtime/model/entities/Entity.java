package fr.beyondtime.model.entities;

public abstract class Entity {
	private int health;
	private String name;

	public Entity(int health, String name) {
		this.health = health;
		this.name = name;
	}

	public int getHealth() {
		return health;
	}

	public void removeHealth(int amount) {
		this.health = Math.max(0, this.health - amount);
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public String getName() {
		return name;
	}
}