package fr.beyondtime.model.entities;

import java.util.ArrayList;
import java.util.List;

public class Bag extends Item implements Containable {

	private int size;

	public static final int MAX_ITEMS = 5;
	private ArrayList<Item> items;

	public Bag() {
		this.size = 0;
		this.items = new ArrayList<>();
	}

	@Override
	public List<Item> getItems() {
		return this.items;
	}

	public int getSize() {
		return this.size;
	}

	@Override
	public void addItem(Item item) {
		if (this.size < MAX_ITEMS) {
			items.add(item);
			this.size++;
		}
	}

	@Override
	public void removeItem(Item item) {
		if (items.contains(item)) {
			items.remove(item);
			this.size--;
		}
	}

	@Override
	public Item getItemByName(String name) {
		for (Item item : items) {
			if (item.getClass().getSimpleName().equals(name)) {
				return item;
			}
		}
		return null;
	}

	@Override
	public void displayItems() {
		if (items.isEmpty()) {
			System.out.println("Il n’y a pas d’objets dans votre sac !");
			return;
		}
		System.out.println("Objets dans votre sac :");
		for (Item item : items) {
			System.out.println("- " + item.getClass().getSimpleName());
		}
	}

}