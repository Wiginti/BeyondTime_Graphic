package fr.beyondtime.model.entities;

public abstract class Entity {
	
	private int healthPoint;
	private String name;
	
	//Getter and Setter for Health
	
	public Entity(int defaultHealthpoint, String name) {
		this.healthPoint = defaultHealthpoint;
		this.name = name;
	}

	public int getHealth() {
		return this.healthPoint;
	}
	
	public void removeHealth(int amount) {
		this.healthPoint -= amount;
	}
	
	public void addHealth(int amount) {
		this.healthPoint += amount;
	}
	
	public void setHealth(int amount) {
		this.healthPoint = amount;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void changeName(String newName) {
		this.name = newName;
	}

}
