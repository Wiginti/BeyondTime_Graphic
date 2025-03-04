package fr.beyondtime.model.entities;

import java.util.ArrayList;
import java.util.List;

public class Bag {
	private List<String> items;

	public Bag() {
		items = new ArrayList<>();
	}

	public void addItem(String item) {
		items.add(item);
	}

	public void removeItem(String item) {
		items.remove(item);
	}

	public List<String> getItems() {
		return items;
	}
}