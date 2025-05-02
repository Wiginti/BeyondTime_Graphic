package fr.beyondtime.model.entities;

/**
 * Classe représentant un objet consommable (potion).
 */
public class Potion extends Item {
	/** Effet de soin de la potion. */
	private int healthAmount;
	private final static int DEFAULT_HEALTH_AMOUNT = 50;
	private final static int MAX_HEALTH_AMOUNT = 100;
	private final static String DEFAULT_IMAGE_PATH = "/fr/beyondtime/resources/potion.jpg";

	public Potion(int healthAmount) {
		super("Potion de vie", "Restaure " + healthAmount + " points de vie", DEFAULT_IMAGE_PATH);
		this.healthAmount = Math.min(Math.max(healthAmount, 0), MAX_HEALTH_AMOUNT);
	}
	/** Constructeur de la potion. */
	public Potion() {
		this(DEFAULT_HEALTH_AMOUNT);
	}
	
	/** Utilise la potion sur le héro. */
	@Override
	public void use(Hero hero) {
		if (hero == null) {
			throw new IllegalArgumentException("Le héros ne peut pas être null");
		}
		
		int currentHealth = hero.getHealth();
		int maxHealth = Hero.DEFAULT_HEALTH;
		
		if (currentHealth >= maxHealth) {
			System.out.println("La potion n'a pas été utilisée car le héros a déjà toute sa vie");
			return;
		}
		
		int newHealth = Math.min(currentHealth + healthAmount, maxHealth);
		hero.setHealth(newHealth);
		System.out.println("Potion utilisée ! La vie du héros est maintenant de " + newHealth + "/" + maxHealth);
	}

	/**
	 * 
	 * @return healthAmount
	 */
	public int getHealthAmount() {
		return this.healthAmount;
	}

	/**
	 * 
	 * @param healthAmount
	 */
	public void setHealthAmount(int healthAmount) {
		this.healthAmount = Math.min(Math.max(healthAmount, 0), MAX_HEALTH_AMOUNT);
	}

	@Override
	public String toString() {
		return getName() + " (" + healthAmount + " PV)";
	}
}
