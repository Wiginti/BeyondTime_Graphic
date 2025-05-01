package fr.beyondtime.view.entities;

import javafx.scene.image.Image;

public class MobView extends EntityView {

	// Le constructeur n'accepte plus cellSize
	public MobView(Image entityImage) {
		// Appeler le constructeur parent avec seulement l'image
		super(entityImage); 
		System.out.println("MobView created."); // Mise à jour du message
	}

	// Vous pouvez ajouter un chargement d'image par défaut si nécessaire
	/*
	private static final String MOB_IMAGE_PATH = "/assets/mob.png"; 
	private static Image mobImage;

	static {
		try {
			mobImage = new Image(MobView.class.getResourceAsStream(MOB_IMAGE_PATH));
		} catch (Exception e) {
			System.err.println("Failed to load mob image: " + MOB_IMAGE_PATH);
			e.printStackTrace();
			mobImage = null;
		}
	}

	public MobView() { // Constructeur par défaut utilisant l'image statique
		super(mobImage);
		System.out.println("MobView created.");
	}
	*/
}
