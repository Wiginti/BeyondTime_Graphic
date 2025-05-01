package fr.beyondtime.model.entities;

import java.util.ArrayList;
import java.util.List;

public class Bag {
	private final List<Item> items;

	public Bag() {
		items = new ArrayList<>();
	}

	public void addItem(Item item) {
		items.add(item);
	}

	public void removeItem(Item item) {
		items.remove(item);
	}

	public List<Item> getItems() {
		return items;
	}
}