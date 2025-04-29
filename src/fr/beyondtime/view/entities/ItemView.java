package fr.beyondtime.view.entities;

import javafx.scene.image.Image;
import fr.beyondtime.util.ImageLoader; // Potentiellement utile

public class ItemView extends EntityView {

	// Le constructeur n'accepte plus cellSize
	public ItemView(Image entityImage) {
		// Appeler le constructeur parent avec seulement l'image
		super(entityImage); 
		System.out.println("ItemView created."); // Mise à jour du message
	}

	// Ajouter un constructeur par défaut si nécessaire, 
	// mais il faudrait définir une image et une taille par défaut.
	/*
	private static Image defaultItemImage = ImageLoader.loadImage("/path/to/default/item.png");
	private static int defaultCellSize = 50; // Ou récupérer depuis une config
	public ItemView() {
		 super(defaultItemImage, defaultCellSize);
	}
	*/

	// Vous pouvez ajouter un chargement d'image par défaut si nécessaire
	/*
	private static Image itemImage; // Exemple

	static {
		// itemImage = new Image(...);
	}

	public ItemView(Image specificItemImage, int cellSize) {
		super(specificItemImage, cellSize); // Passer cellSize au constructeur parent
		System.out.println("ItemView created with cell size: " + cellSize);
	}

	public ItemView(int cellSize) {
		this(itemImage, cellSize); // Utilise l'image statique (à définir)
	}
	*/
}
