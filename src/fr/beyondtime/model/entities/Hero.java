package fr.beyondtime.model.entities;

import fr.beyondtime.model.interfaces.Fightable;

public class Hero extends Entity implements Fightable {

	private int damageAmount;
	final private Bag bag;
	private double x;
	private double y;

	public static final int DEFAULT_HEALTH = 100;
	public static final int DEFAULT_DAMAGE = 20;

	public Hero(String levelName) {
		super(DEFAULT_HEALTH, "Hero");
		this.damageAmount = DEFAULT_DAMAGE;
		this.bag = new Bag();
		System.out.println("Initialisation du héros avec un inventaire vide");

		// Initialisation de l'inventaire en fonction du niveau
		switch (levelName) {
			case "Préhistoire" -> {
				// Niveau 1 : 2 potions
				this.bag.addItem(new Potion());
				this.bag.addItem(new Potion());
				System.out.println("2 potions ajoutées à l'inventaire pour le niveau Préhistoire");
			}
			case "Égypte Antique" -> {
				// Niveau 2 : 1 épée et 2 potions
				this.bag.addItem(new Sword());
				this.bag.addItem(new Potion());
				this.bag.addItem(new Potion());
				System.out.println("1 épée et 2 potions ajoutées à l'inventaire pour le niveau Égypte Antique");
			}
			case "2nde Guerre Mondiale" -> {
				// Niveau 3 : 1 épée et 4 potions
				this.bag.addItem(new Sword());
				this.bag.addItem(new Potion());
				this.bag.addItem(new Potion());
				this.bag.addItem(new Potion());
				this.bag.addItem(new Potion());
				System.out.println("1 épée et 4 potions ajoutées à l'inventaire pour le niveau 2nde Guerre Mondiale");
			}
			default -> {
				// Pour les maps personnalisées, on commence avec 2 potions
				this.bag.addItem(new Potion());
				this.bag.addItem(new Potion());
				System.out.println("2 potions ajoutées à l'inventaire pour une map personnalisée");
			}
		}

		this.x = 0;
		this.y = 0;
	}

	// Getter pour l'inventaire
	public Bag getBag() {
		return this.bag;
	}

	// Getter pour les dégâts
	@Override
	public int getDamage() {
		return this.damageAmount;
	}

	public void addDamage(int amount) {
		this.damageAmount += amount;
	}

	@Override
	public void attack(Entity entity) {
		entity.removeHealth(this.damageAmount);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
}