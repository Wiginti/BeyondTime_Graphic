package fr.beyondtime.model.entities;

import java.util.ArrayList;
import java.util.List;

public class Bag extends Item implements Containable {
	
	private int size;
	
	public static final int MAX_ITEMS = 5;
	private ArrayList<Item> items;
	
	public Bag() {
		this.size = 0;
		this.items = new ArrayList<Item>();
	}
	
	public int getSize() {
		return this.size;
	}
	
	@Override
	public List<Item> getItems() {
		return items;
	}
	
	public void addItem(Item item) {
		if(this.size < Bag.MAX_ITEMS) {
			items.add(item);	
			this.size++;
		}
	}
	
	public void removeItem(Item item) {
		if(items.contains(item)) {
			items.remove(item);
			this.size--;
		}
	}
	
	public void displayItems() {
		try {
			System.out.println("List of items available in your Bag :");
			for(Item item : this.items) {
				System.out.println("- " + item.getClass().getSimpleName());
			}
		} catch (Exception e) {
			System.out.println("There is no item in your Bag !");
		}
	}

	@Override
	public Item getItemByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
