package fr.beyondtime.model.entities;

import java.util.List;

public interface Containable {
	
	public abstract List<Item> getItems();
	public void addItem(Item item);
	public void removeItem(Item item);
	public Item getItemByName(String name);
	public void displayItems();
	
	
}
