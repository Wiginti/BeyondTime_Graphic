package fr.beyondtime.model.entities;

/**
 * Classe abstraite de base pour toutes les entités du jeu (héros, monstres, etc.).
 */
public abstract class Entity {
	/** Points de vie de l'entité. */
	private int health;
	/** Nom de l'entité. */
	private String name;

	/** Constructeur de l'entité son nom et ses points de vie. */
	public Entity(int health, String name) {
		this.health = health;
		this.name = name;
	}
	
	/**
	 * 
	 * @return health
	 */
	public int getHealth() {
		return health;
	}
	
	/** Retirer de la vie à l'entité */
	public void removeHealth(int amount) {
		this.health = Math.max(0, this.health - amount);
	}
	
	/** Mettre amount de vie à une entité */
	/**
	 * 
	 * @param health
	 */
	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}
}