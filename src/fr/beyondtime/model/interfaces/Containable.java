package fr.beyondtime.model.interfaces;

import java.util.List;

import fr.beyondtime.model.entities.Item;

public interface Containable {
	
	public abstract List<Item> getItems();
	public void addItem(Item item);
	public void removeItem(Item item);
	public Item getItemByName(String name);
	public void displayItems();
	
	
}