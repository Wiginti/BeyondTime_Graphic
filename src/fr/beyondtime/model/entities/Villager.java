package fr.beyondtime.model.entities;

public class Villager extends Entity implements Interactable {
	
	public Villager(int defaultHealthpoint, String name) {
		super(defaultHealthpoint, name);
	}

	public static final int DEFAULT_HEALTHPOINT = 50;

}
