package fr.beyondtime.model.entities;

public interface Interactable {
	
	public default void discuss(String message) {
		System.out.println(message);
	}

}
