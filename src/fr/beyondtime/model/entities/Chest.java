package fr.beyondtime.model.entities;

import java.util.ArrayList;
import java.util.List;

public class Chest extends Item implements Containable {
	
	private int size;
	
	public static final int MAX_ITEMS = 5;

	private ArrayList<Item> items = new ArrayList<Item>();
	
	public Chest() {
		this.size = 0;
	}
	
	@Override
	public List<Item> getItems() {
		return items;
	}
	
	@Override
	public void addItem(Item item) {
		if(this.size < Chest.MAX_ITEMS) {
			items.add(item);	
			this.size++;
		}
	}
	
	public void addItem(List<Item> items) {
		if(items.size() + this.size < Chest.MAX_ITEMS)
		for(Item item : items) {
			items.add(item);
			this.size++;
		}
	}
	
	@Override
	public void removeItem(Item item) {
		if(items.contains(item)) {
			items.remove(item);
			this.size--;
		}
	}
	
	@Override
	public Item getItemByName(String name) {
		for(Item item : this.items) {
			if(item.getClass().getName().equals(name)) {
				return item;
			}
		}
		return null;
	}
	
	@Override
	public void displayItems() {
		try {
			System.out.println("List of items available in this Safe :");
			for(Item item : this.items) {
				System.out.println("- " + item.getClass().getSimpleName());
			}
		} catch (Exception e) {
			System.out.println("There is no item in this Safe");
		}
	}
	
}
