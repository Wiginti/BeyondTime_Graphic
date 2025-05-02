package fr.beyondtime.model.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un sac à objets (Inventaire).
 */
public class Bag {
	private final List<Item> items;
	private static final int MAX_ITEMS = 5;

	/** Constructeur du sac */
	public Bag() {
		items = new ArrayList<>();
	}

	/** Ajoute un objet dans le sac. */
	public void addItem(Item item) {
		if (items.size() < MAX_ITEMS) {
			items.add(item);
		}
	}
	
	/** Retirer un objet du sac. */
	public void removeItem(Item item) {
		items.remove(item);
	}

	/** Retourne la liste des items dans le sac. */
	public List<Item> getItems() {
		return items;
	}
}