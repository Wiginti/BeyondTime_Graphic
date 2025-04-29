package fr.beyondtime.model.entities;

import fr.beyondtime.model.interfaces.Fightable;

public class Hero extends Entity implements Fightable {

	private int damageAmount;
	private Bag bag;
	private double x;
	private double y;

	public static final int DEFAULT_HEALTH = 100;
	public static final int DEFAULT_DAMAGE = 25;

	public Hero() {
		super(DEFAULT_HEALTH, "Hero");
		this.damageAmount = DEFAULT_DAMAGE;
		this.bag = new Bag();
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

	// Ajout de la méthode addHealth pour augmenter la santé du héros
	public void addHealth(int amount) {
		setHealth(getHealth() + amount);
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